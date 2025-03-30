package fr.nicknqck.roles.ds.demons;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.slayers.InosukeV2;
import fr.nicknqck.roles.ds.slayers.Tanjiro;
import fr.nicknqck.roles.ds.slayers.ZenItsuV2;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KyogaiDemon extends DemonsRoles implements Listener {

    private boolean solo = false;

    public KyogaiDemon(UUID player) {
        super(player);
    }

    @Override
    public @NonNull DemonType getRank() {
        return DemonType.INFERIEUR;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Kyogai";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Kyogai;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Demon;
    }

    @Override
    public void resetCooldown() {}

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NIGHT);
        addPower(new TambourPower(this), true);
        EventUtils.registerRoleEvent(this);
        addKnowedRole(MuzanV2.class);
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            if (!gameState.getAttributedRole().contains(GameState.Roles.Muzan) || gameState.getDeadRoles().contains(GameState.Roles.Muzan)){
                procSolo();
            }
        }, 20*10);
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .addParticularites(
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                                new TextComponent("§7Si vous arrivez à tué§a Tanjiro§7,§a Inosuke§7 ou§a Zen'Itsu§7 vous obtiendrez l'item§6 Percussion Rapide\n\n" +
                                        "§7Il vous permettra d'activer un§c passif§7 pour une durée de§c 10 secondes\n" +
                                        "§7Ce qui vous permettra de retourner n'importe quel joueur que vous§c frapperez\n\n" +
                                        "§7(1x/10m)")
                        }),
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                                new TextComponent("§7A la mort de§c Muzan§7 vous deviendrez un rôle§e Solitaire§7, vous obtiendrez donc les§c bonus§7 suivant:\n\n" +
                                        "§7Vous obtiendrez l'effet§9 Résistance I§7 de manière§c permanente§7 ainsi que§c 3❤ permanents\n\n" +
                                        "§7Votre§c Tambour§7 aura §c50%§7 de §cchance§7 de retourner tout les joueurs autours de vous (§c30 blocs§7)")
                        })
                ).getText();
    }
    @EventHandler
    private void UHCKillEvent(final UHCPlayerKillEvent event) {
        if (event.isCancel())return;
        if (event.getGamePlayerKiller() == null)return;
        if (event.getGameState().hasRoleNull(event.getVictim().getUniqueId()))return;
        if (!event.getKiller().getUniqueId().equals(getPlayer()))return;
        final RoleBase role = event.getGameState().getGamePlayer().get(event.getVictim().getUniqueId()).getRole();
        if (role instanceof Tanjiro || role instanceof ZenItsuV2 || role instanceof InosukeV2) {
            addPower(new PercussionRapidePower(this), true);
            EventUtils.unregisterEvents(this);
            event.getGamePlayerKiller().sendMessage("§7Vous avez réussis à tué "+role.getName()+"§7, vous obtenez donc finalement la faculté d'utiliser vos§6 Percussion Rapide");
        }
    }
    @EventHandler
    private void UHCDeathEvent(final UHCDeathEvent event) {
        if (event.getRole() instanceof MuzanV2) {
            procSolo();
        }
    }
    private void procSolo() {
        this.solo = true;
        getGamePlayer().sendMessage("§7Vous êtes enfin libéré de l'emprise de§c Muzan§7 vous obtenez l'effet§9 Résistance I§c permanent§7 ainsi que§c 3❤ permanents§7",
                "§7Votre§c Tambour§7 à maintenant§c 50%§7 de§c chance§7 de retourner tout les joueurs autours de vous (§c30 blocs§7)");
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0, false, false), EffectWhen.PERMANENT));
        setMaxHealth(getMaxHealth()+6.0);
        final Player owner = Bukkit.getPlayer(getPlayer());
        if (owner != null) {
            owner.setMaxHealth(getMaxHealth());
            owner.setHealth(owner.getHealth()+6.0);
        }
        setTeam(TeamList.Solo);
    }

    private static class TambourPower extends ItemPower {

        protected TambourPower(@NonNull KyogaiDemon role) {
            super("Tambour", new Cooldown(30), new ItemBuilder(Material.STICK).setName("§cTambour"), role,
                    "§7Vous permet de retourner la personne visée");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final Player target = RayTrace.getTargetPlayer(player, 30.0, null);
            if (target == null) {
                player.sendMessage("§cIl faut viser un joueur !");
                return false;
            }
            boolean skip = false;
            if (((KyogaiDemon) getRole()).solo) {
                if (Main.RANDOM.nextInt(101) <= 50)skip = true;
                final List<Player> aroundPlayers = Loc.getNearbyPlayersExcept(player, 30);
                final StringBuilder stringBuilder = new StringBuilder();
                if (aroundPlayers.size() <= 1) skip = true;
                if (!skip) {
                    for (final Player p : aroundPlayers) {
                        stringBuilder.append("§c").append(p.getDisplayName()).append("§7, ");
                        Loc.inverserDirectionJoueur(p);
                        target.sendMessage("§cKyogai§7 vous a retourné");
                    }
                    player.sendMessage("§7Vous avez retourné les joueurs: "+ stringBuilder.substring(3));
                    return true;
                }
            }
            Loc.inverserDirectionJoueur(target);
            player.sendMessage("§7Vous avez retourné§c "+target.getDisplayName());
            target.sendMessage("§cKyogai§7 vous a retourné");
            return true;
        }
    }
    private static class PercussionRapidePower extends ItemPower implements Listener {

        protected PercussionRapidePower(@NonNull RoleBase role) {
            super("Percussion Rapide", new Cooldown(60*10), new ItemBuilder(Material.NETHER_STAR).setName("§6Percussion Rapide"), role);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            player.sendMessage("§7Vous avez activé vos§6 Percussion Rapide");
            EventUtils.registerRoleEvent(this);
            new PercussionRunnable(this, getRole().getGameState());
            return true;
        }
        @EventHandler
        private void PlayerBattleEvent(final UHCPlayerBattleEvent event) {
            if (event.getDamager().getUuid().equals(this.getRole().getPlayer())) {
                if (event.isPatch())return;
                final Player player = Bukkit.getPlayer(event.getVictim().getUuid());
                if (player != null) {
                    final int random = Main.RANDOM.nextInt(101);
                    if (random > 25)return;
                    Loc.inverserDirectionJoueur(player);
                    event.getDamager().sendMessage("§7Vous avez retourné§c "+player.getDisplayName());
                    player.sendMessage("§cKyogai§7 vous a retourné");
                }
            }
        }
        private static class PercussionRunnable extends BukkitRunnable {

            private final PercussionRapidePower power;
            private final GameState gameState;
            private int timeRemaining;

            public PercussionRunnable(PercussionRapidePower percussionRapidePower, @NonNull GameState gameState) {
                this.power = percussionRapidePower;
                this.gameState = gameState;
                this.timeRemaining = 15;
                power.getRole().getGamePlayer().getActionBarManager().addToActionBar("kyogai.percurapide", "§bTemp de percussion restant:§c "+timeRemaining+"s");
                runTaskTimerAsynchronously(power.getPlugin(), 0, 20);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                this.power.getRole().getGamePlayer().getActionBarManager().updateActionBar("kyogai.percurapide", "§bTemp de percussion restant:§c "+timeRemaining+"s");
                if (this.timeRemaining <= 0) {
                    this.power.getRole().getGamePlayer().sendMessage("§7Vous êtes maintenant trop fatigué pour continué à utiliser vos§6 Percussion Rapide");
                    this.power.getRole().getGamePlayer().getActionBarManager().removeInActionBar("kyogai.percurapide");
                    EventUtils.unregisterEvents(power);
                    cancel();
                    return;
                }
                this.timeRemaining--;
            }
        }
    }
}

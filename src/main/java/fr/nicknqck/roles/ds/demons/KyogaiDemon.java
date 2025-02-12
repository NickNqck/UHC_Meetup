package fr.nicknqck.roles.ds.demons;

import fr.nicknqck.GameState;
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

import java.util.Map;
import java.util.UUID;

public class KyogaiDemon extends DemonsRoles implements Listener {

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
    public TeamList getOriginTeam() {
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

    private static class TambourPower extends ItemPower {

        protected TambourPower(@NonNull RoleBase role) {
            super("Tambour", new Cooldown(30), new ItemBuilder(Material.STICK).setName("§cTambour"), role);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            final Player target = RayTrace.getTargetPlayer(player, 30.0, null);
            if (target == null) {
                player.sendMessage("§cIl faut viser un joueur !");
                return false;
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
        public boolean onUse(Player player, Map<String, Object> map) {
            player.sendMessage("§7Vous avez activé vos§6 Percussion Rapide");
            EventUtils.registerRoleEvent(this);
            new PercussionRunnable(this, getRole().getGameState());
            return false;
        }
        @EventHandler
        private void PlayerBattleEvent(final UHCPlayerBattleEvent event) {
            if (event.getDamager().getUuid().equals(this.getRole().getPlayer())) {
                if (event.isPatch())return;
                final Player player = Bukkit.getPlayer(event.getVictim().getUuid());
                if (player != null) {
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

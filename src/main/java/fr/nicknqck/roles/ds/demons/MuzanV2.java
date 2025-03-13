package fr.nicknqck.roles.ds.demons;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.roles.ds.demons.lune.Kokushibo;
import fr.nicknqck.roles.ds.slayers.NezukoV2;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MuzanV2 extends DemonsRoles implements Listener {

    private boolean killNezuko = false;

    public MuzanV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull DemonType getRank() {
        return DemonType.DEMON;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Muzan";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Muzan;
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
        addKnowedRole(Kokushibo.class);
        getGamePlayer().startChatWith("§cMuzan: ", "!", Kokushibo.class);
        addPower(new RegenPower(this));
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0, false, false), EffectWhen.NIGHT);
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60, 0, false, false), EffectWhen.AT_KILL);
        addPower(new DSGivePower(this));
        addPower(new DSBoostPower(this));
        EventUtils.registerRoleEvent(this);
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .addParticularites(
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {
                                new TextComponent("§7Si vous arrivez à tué§a Nezuko§7 vous obtiendrez l'effet§c Résistance I§7 de manière§c permanente§7, ainsi que l'effet§c Speed I§7 la§c nuit")
                        })
                )
                .getText();
    }

    @EventHandler
    private void UHCKillEvent(@NonNull UHCPlayerKillEvent event) {
        if (event.getGamePlayerKiller() == null)return;
        if (event.getGameState().hasRoleNull(event.getVictim().getUniqueId()))return;
        if (this.killNezuko)return;
        final RoleBase role = event.getGameState().getGamePlayer().get(event.getVictim().getUniqueId()).getRole();
        if (role instanceof NezukoV2) {
            getEffects().remove(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0, false, false), EffectWhen.NIGHT);
            getEffects().remove(new PotionEffect(PotionEffectType.SPEED, 20*60, 0, false, false), EffectWhen.AT_KILL);
            givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
            givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0, false, false), EffectWhen.NIGHT);
            this.killNezuko = true;
            getGamePlayer().sendMessage("§7Vous avez§c tué§a Nezuko§7, vous avez obtenu l'effet§c Résistance I§7 de manière§c permanente§7 et l'effet§b Speed I§7 de§c nuit");
        }
    }

    private static class DSBoostPower extends CommandPower {

        public DSBoostPower(@NonNull RoleBase role) {
            super("/ds boost <joueur>", "boost", null, role, CommandType.DS,
                    "§7Vous permet de donner§c +10%§7 de§c force");
            setMaxUse(1);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 2) {
                final Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage("§b"+args[1]+"§c n'est pas connecté(e) ou n'existe pas");
                    return false;
                }
                if (getRole().getGameState().hasRoleNull(target.getUniqueId())) {
                    player.sendMessage("§b"+args[1]+"§c n'a pas de rôle !");
                    return false;
                }
                if (target.getUniqueId().equals(player.getUniqueId())) {
                    player.sendMessage("§cVous ne pouvez pas donner de votre§4 sang§c à vous même");
                    return false;
                }
                final RoleBase role = getRole().getGameState().getGamePlayer().get(target.getUniqueId()).getRole();
                if (role instanceof NezukoV2 || role.getOriginTeam().equals(TeamList.Demon) || role.getTeam().equals(TeamList.Demon)) {
                    role.addBonusforce(10.0);
                    player.sendMessage("§cVous avez offert de votre sang à§b "+target.getDisplayName());
                    target.sendMessage("§4Muzan§c vous a offert de son sang");
                    return true;
                }
            }
            return false;
        }
    }
    private static class DSGivePower extends CommandPower {

        private DSGivePower(@NonNull RoleBase role) {
            super("/ds give <joueur>", "give", null, role, CommandType.DS,
                    "§7Vous permet de donner le pouvoir de l'§cinfection§7 à un allier§c démon");
            setMaxUse(1);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 2) {
                final Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage("§b"+args[1]+"§c n'est pas connecté(e) ou n'existe pas");
                    return false;
                }
                if (getRole().getGameState().hasRoleNull(target.getUniqueId())) {
                    player.sendMessage("§b"+args[1]+"§c n'a pas de rôle !");
                    return false;
                }
                final RoleBase role = getRole().getGameState().getGamePlayer().get(target.getUniqueId()).getRole();
                if (role instanceof DemonsSlayersRoles) {
                    if (role instanceof NezukoV2 || role.getOriginTeam().equals(TeamList.Demon)) {
                        player.sendMessage("§cVous avez donné le pouvoir de l'infection à§b "+target.getDisplayName());
                        target.sendMessage("§4Muzan§c vous à donnez le pouvoir de l'infection, ne le gâchez pas...");
                        role.addPower(new InfectPower((DemonsRoles) role));
                        return true;
                    }
                }
            }
            return false;
        }
        private static class InfectPower extends CommandPower {

            private InfectPower(@NonNull DemonsRoles role) {
                super("/ds infection <joueur>", "infection", new Cooldown(-500), role, CommandType.DS,
                        "§7Cette§c commande§7 vous permet d'§cinfecter§7 le joueur cibler dans le camp des§c Démons§7 (au bout de§c "+ StringUtils.secondsTowardsBeautiful(Main.getInstance().getGameConfig().getInfectionTime())+"§7)");
                setMaxUse(1);
            }

            @Override
            public boolean onUse(Player player, Map<String, Object> map) {
                final String[] args = (String[]) map.get("args");
                if (args.length == 2) {
                    final Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        player.sendMessage("§cLe joueur viser n'existe pas ou n'est pas connecté");
                        return false;
                    }
                    if (getRole().getGameState().hasRoleNull(target.getUniqueId())) {
                        player.sendMessage("§cLe joueur viser n'a pas de rôle, impossible de l'infecter");
                        return false;
                    }
                    final RoleBase role = getRole().getGameState().getGamePlayer().get(target.getUniqueId()).getRole();
                    final GameState gameState = role.getGameState();
                    new InfectionRunnable(gameState, (DemonsRoles) getRole(), role);
                    return true;
                }
                return false;
            }
            private static final class InfectionRunnable extends BukkitRunnable {

                private final GameState gameState;
                private final DemonsRoles roleInfecteur;
                private final RoleBase roleTarget;
                private int timeRemaining;

                private InfectionRunnable(GameState gameState, DemonsRoles roleInfecteur, RoleBase roleTarget) {
                    this.gameState = gameState;
                    this.roleInfecteur = roleInfecteur;
                    this.roleTarget = roleTarget;
                    this.timeRemaining = Main.getInstance().getGameConfig().getInfectionTime();
                }

                @Override
                public void run() {
                    if (gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                        cancel();
                        return;
                    }
                    if (timeRemaining <= 0) {
                        final Player target = Bukkit.getPlayer(roleTarget.getPlayer());
                        if (target == null)return;
                        if (!target.isOnline())return;
                        procInfection(target);
                        cancel();
                        return;
                    }
                    timeRemaining--;
                }
                @SuppressWarnings("deprecation")
                private void procInfection(final Player target) {
                    target.resetTitle();
                    target.sendTitle("§cVous avez été infecté", "Vous gagnez maintenant avec les Démons");
                    target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*15, 0, false, false));

                    target.sendMessage(target.getName()+" à été infecté");
                    for (final UUID u : gameState.getInGamePlayers()) {
                        final Player z = Bukkit.getPlayer(u);
                        if (z == null)continue;
                        if (gameState.hasRoleNull(u)) continue;
                        if (gameState.getGamePlayer().get(u).getRole().getOriginTeam().equals(TeamList.Demon)) {
                            z.sendMessage("§4Un joueur à été infecté et à rejoins le camp des§c Démons");
                        }
                    }
                    this.roleInfecteur.getGamePlayer().sendMessage(target.getName()+" à été infecté");
                    if (gameState.getGamePlayer().get(target.getUniqueId()).getRole().getTeam() != TeamList.Slayer) {
                        target.sendMessage("Vous avez été §cinfecté§f mais comme vous n'étiez pas du camp§a Slayer§r vous n'avez pas pus être§c infecté§f, vous restez donc dans votre camp d'origine");
                    }
                    if (gameState.getGamePlayer().get(target.getUniqueId()).getRole().getTeam() == TeamList.Slayer) {
                        gameState.getGamePlayer().get(target.getUniqueId()).getRole().setTeam(TeamList.Demon);
                    }
                    target.resetTitle();
                    target.sendMessage("Voici l'identité de votre§c infecteur§f:§c§l "+this.roleInfecteur.getGamePlayer().getPlayerName());
                    target.sendTitle("§cVous avez été infecté", "Vous gagnez maintenant avec les Démons");
                    this.roleTarget.givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NIGHT);
                }
            }
        }
    }
    private static class RegenPower extends Power {

        public RegenPower(@NonNull RoleBase role) {
            super("Pouvoir régénérant", null, role);
            new RegenerationRunnable(role.getGameState(), role.getPlayer(), this);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            return true;
        }
        private static class RegenerationRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final UUID uuid;
            private final RegenPower regenPower;
            private int timeLeft;

            private RegenerationRunnable(GameState gameState, UUID uuid, RegenPower regenPower) {
                this.gameState = gameState;
                this.uuid = uuid;
                this.regenPower = regenPower;
                runTaskTimerAsynchronously(regenPower.getPlugin(), 0, 20);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.timeLeft == 0) {
                    final Player owner = Bukkit.getPlayer(this.uuid);
                    if (owner != null) {
                        if (this.regenPower.checkUse(owner, new HashMap<>())){
                            Bukkit.getScheduler().runTask(this.regenPower.getPlugin(), () -> owner.setHealth(Math.min(owner.getMaxHealth(), owner.getHealth()+1.0)));
                        }
                    }
                    this.timeLeft = 10;
                    return;
                }
                timeLeft--;
            }
        }
    }
}
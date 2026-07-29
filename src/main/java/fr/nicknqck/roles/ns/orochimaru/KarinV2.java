package fr.nicknqck.roles.ns.orochimaru;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.*;
import fr.nicknqck.events.power.PowerTakeInfoEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.builders.OrochimaruRoles;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.*;

public class KarinV2 extends OrochimaruRoles {

    public KarinV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.MOYENNE;
    }

    @Override
    public EChakras[] getChakrasCanHave() {
        return new EChakras[] {
                EChakras.SUITON,
                EChakras.RAITON,
                EChakras.KATON,
                EChakras.DOTON,
                EChakras.FUTON
        };
    }

    @Override
    public String getName() {
        return "Karin";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Karin;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new MorsureItem(this), true);
        addKnowedRole(KimimaroV2.class);
        addPower(new DonItem(this));
        addPower(new InfoObtainPower(this));
    }

    @Nonnull
    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }
    private static class DonItem extends CommandPower {

        public DonItem(@NonNull RoleBase role) {
            super("/ns don <joueur>", "don", new Cooldown(60*3), role, CommandType.NS,
                    "§7L'un des pouvoirs de la personne viser verra son§c cooldown réduit§7 de§c 1 minute§7.");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 2) {
                final Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage("§b"+args[1]+"§c n'existe pas ou n'est pas connecter !");
                    return false;
                }
                final GamePlayer gamePlayer = GamePlayer.of(target.getUniqueId());
                if (gamePlayer == null) {
                    player.sendMessage("§cImpossible de viser ce joueur.");
                    return false;
                }
                if (gamePlayer.getRole() == null || !gamePlayer.isAlive() || !gamePlayer.isOnline()) {
                    player.sendMessage("§cImpossible de viser ce joueur.");
                    return false;
                }
                final List<Power> powerList = new ArrayList<>(gamePlayer.getRole().getPowers());
                if (powerList.isEmpty()) {
                    player.sendMessage("§cImpossible de viser ce joueur, il n'a pas de pouvoir.");
                    return false;
                }
                for (@NonNull final Power power : powerList) {
                    if (power.getCooldown() == null)continue;
                    if (!power.getCooldown().isInCooldown())continue;
                    if (power.getCooldown().getCooldownRemaining() >= 60)continue;
                    player.sendMessage("§7Vous avez réduit le§b cooldown§7 de l'un des§c pouvoirs§7 de§a "+target.getDisplayName());
                    target.sendMessage("§7Le§c cooldown§7 de§b "+(power instanceof ItemPower ? ((ItemPower) power).getItem().getItemMeta().getDisplayName() : power.getName()));
                    return true;
                }
            }
            return false;
        }
    }
    private static class MorsureItem extends ItemPower {

        private int timeLastUseHeal = 0;

        public MorsureItem(@NonNull RoleBase role) {
            super("§dMorsure§r", new Cooldown(5), new ItemBuilder(Material.NETHER_STAR).setName("§dMorsure"), role,
                    "§7Vous permet de vous§d soigner intégralement§7.",
                    "",
                    "§7Si vous utilisez plusieurs fois ce§c pouvoir§7 en moins de§c 60 secondes§7,",
                    "§7vous perdrez§c 1/2❤ permanent§7.");
            new MorsureRunnable(this).runTaskTimerAsynchronously(getPlugin(), 20, 20);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (player.getHealth() == player.getMaxHealth()) {
                    player.sendMessage("§cVous êtes déjà plein de§a vie§c !");
                    return false;
                }
                if (timeLastUseHeal > 0) {
                    getRole().setMaxHealth(getRole().getMaxHealth()-1.0);
                    player.setMaxHealth(getRole().getMaxHealth());
                    player.sendMessage("§7Vous venez de perdre §c1/2❤ permanent suite à votre§d Morsure§7.");
                }
                player.setHealth(player.getMaxHealth());
                timeLastUseHeal = 60;
                return true;
            }
            return false;
        }
        private static class MorsureRunnable extends BukkitRunnable {

            private final MorsureItem morsureItem;

            private MorsureRunnable(MorsureItem morsureItem) {
                this.morsureItem = morsureItem;
            }

            @Override
            public void run() {
                if (!GameState.inGame()) {
                    cancel();
                    return;
                }
                morsureItem.timeLastUseHeal--;
                if (morsureItem.timeLastUseHeal == 0) {
                    this.morsureItem.getRole().getGamePlayer().sendMessage("§dMorsure§7 peut à nouveau être utiliser sans aucun§c contre-coup§7.");
                }
            }
        }
    }
    private static final class InfoObtainPower extends Power {

        private final Map<UUID, Integer> timePassedNearby = new HashMap<>();
        @Getter
        private final KnowRunnable knowRunnable;

        public InfoObtainPower(@NonNull RoleBase role) {
            super("§aIdentification du chakra§r", null, role,
                    "§7En restant proche des autres joueurs vous connaitrez dans quel camp ils sont.",
                    "",
                    "§8 -§c 2 minutes§7 si la personne a pour camp d'origine "+TeamList.Orochimaru.getName(),
                    "§8 -§c 5 minutes§7 pour les autres camp");
            knowRunnable = new KnowRunnable(this);
            this.knowRunnable.runTaskTimerAsynchronously(getPlugin(), 100, 20);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return true;
        }
        private static class KnowRunnable extends BukkitRunnable {

            private final InfoObtainPower infoObtainPower;

            private KnowRunnable(InfoObtainPower infoObtainPower) {
                this.infoObtainPower = infoObtainPower;
            }

            @Override
            public void run() {
                if (!this.infoObtainPower.getRole().getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                final Player owner = Bukkit.getPlayer(this.infoObtainPower.getRole().getPlayer());
                if (owner == null)return;
                for (@NonNull final Player p : Loc.getNearbyPlayersExcept(owner, 20)) {
                    if (this.infoObtainPower.getRole().getGameState().hasRoleNull(p.getUniqueId())) {
                        return;
                    }
                    if (this.infoObtainPower.timePassedNearby.containsKey(p.getUniqueId())) {
                        final GamePlayer gamePlayer = GamePlayer.of(p.getUniqueId());
                        if (gamePlayer == null)continue;
                        if (!gamePlayer.check())continue;
                        if (this.infoObtainPower.timePassedNearby.get(p.getUniqueId()) == 60*2) {
                            if (this.infoObtainPower.checkUse(owner, new HashMap<>())) {
                                @NonNull final PowerTakeInfoEvent powerTakeInfoEvent = new PowerTakeInfoEvent(this.infoObtainPower, gamePlayer, InfoType.TEAM);
                                this.infoObtainPower.getPlugin().getServer().getPluginManager().callEvent(powerTakeInfoEvent);
                                if (!powerTakeInfoEvent.isCancelled()) {
                                    if (powerTakeInfoEvent.getGameTarget().getRole().getOriginTeam().equals(TeamList.Orochimaru)) {
                                        owner.sendMessage("§5"+p.getDisplayName()+"§f est dans le camp§5 Orochimaru");
                                    }
                                } else {
                                    powerTakeInfoEvent.sendCancelMessage(owner);
                                    continue;
                                }
                            }
                        } else if (this.infoObtainPower.timePassedNearby.get(p.getUniqueId()) == 60*5) {
                            if (gamePlayer.getRole().getOriginTeam().equals(TeamList.Orochimaru)) continue;
                            if (this.infoObtainPower.checkUse(owner, new HashMap<>())) {
                                @NonNull final PowerTakeInfoEvent powerTakeInfoEvent = new PowerTakeInfoEvent(this.infoObtainPower, gamePlayer, InfoType.TEAM);
                                this.infoObtainPower.getPlugin().getServer().getPluginManager().callEvent(powerTakeInfoEvent);
                                if (!powerTakeInfoEvent.isCancelled()) {
                                    owner.sendMessage(powerTakeInfoEvent.getGameTarget().getRole().getTeamColor()+p.getDisplayName()+
                                            "§f est dans le camp "
                                            +powerTakeInfoEvent.getGameTarget().getRole().getTeam().getName());
                                } else {
                                    powerTakeInfoEvent.sendCancelMessage(owner);
                                    continue;
                                }
                            }
                        }
                        this.infoObtainPower.timePassedNearby.replace(p.getUniqueId(), this.infoObtainPower.timePassedNearby.get(p.getUniqueId())+1);
                    }else {
                        this.infoObtainPower.timePassedNearby.put(p.getUniqueId(), 1);
                    }
                }
            }
        }
    }
}
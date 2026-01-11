package fr.nicknqck.roles.ns.orochimaru;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.OrochimaruRoles;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class KarinV2 extends OrochimaruRoles {

    private final Map<UUID, Integer> timePassedNearby = new HashMap<>();

    public KarinV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.MOYENNE;
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
        setChakraType(getRandomChakras());
        addKnowedRole(KimimaroV2.class);
        addPower(new DonItem(this));
        new KnowRunnable(this).runTaskTimerAsynchronously(Main.getInstance(), 20, 20);
        super.RoleGiven(gameState);
    }

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
    private static class KnowRunnable extends BukkitRunnable {

        private final KarinV2 karinV2;

        private KnowRunnable(KarinV2 karinV2) {
            this.karinV2 = karinV2;
        }

        @Override
        public void run() {
            if (!karinV2.getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            final Player owner = Bukkit.getPlayer(karinV2.getPlayer());
            if (owner == null)return;
            for (@NonNull final Player p : Loc.getNearbyPlayersExcept(owner, 20)) {
                if (this.karinV2.getGameState().hasRoleNull(p.getUniqueId())) {
                    return;
                }
                if (this.karinV2.timePassedNearby.containsKey(p.getUniqueId())) {
                    int i = this.karinV2.timePassedNearby.get(p.getUniqueId());
                    this.karinV2.timePassedNearby.put(p.getUniqueId(), i+1);
                    if (GamePlayer.of(p.getUniqueId()).getRole().getOriginTeam() == TeamList.Orochimaru) {
                        if (this.karinV2.timePassedNearby.get(p.getUniqueId()) == 60*2) {
                            owner.sendMessage("§5"+p.getDisplayName()+"§f est dans le camp§5 Orochimaru");
                        }
                    }else {
                        if (this.karinV2.timePassedNearby.get(p.getUniqueId()) == 60*5) {
                            final GamePlayer gamePlayer = GamePlayer.of(p.getUniqueId());
                            if (gamePlayer != null){
                                owner.sendMessage(gamePlayer.getRole().getTeamColor()+p.getDisplayName()+
                                        "§f est dans le camp "
                                        +gamePlayer.getRole().getTeam().getName());
                            }
                        }
                    }
                }else {
                    this.karinV2.timePassedNearby.put(p.getUniqueId(), 1);
                }
            }
        }
    }
}
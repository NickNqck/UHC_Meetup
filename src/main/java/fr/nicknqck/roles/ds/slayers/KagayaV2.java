package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.roles.ds.slayers.pillier.PilierRoles;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.packets.ArmorStandUtils;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;

public class KagayaV2 extends SlayerRoles {

    public KagayaV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.AUCUN;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Kagaya";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Kagaya;
    }

    @Override
    public void resetCooldown() {}

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public TextComponent getComponent() {
        AutomaticDesc desc = new AutomaticDesc(this).addEffects(getEffects()).addCustomLine("§7Vous possédez§c 1❤ permanent§7 supplémentaire").setPowers(getPowers());
        return desc.getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new MaladieItem(this), true);
    //    addPower(new ShowHealthPower(this));
        setMaxHealth(getMaxHealth()+2);
        owner.setMaxHealth(getMaxHealth());
        owner.setHealth(owner.getMaxHealth());
    }
    private static class MaladieItem extends ItemPower implements Listener{

        private int stade = 1;
        private final ResiMatesRunnable runnable;

        protected MaladieItem(@NonNull KagayaV2 role) {
            super("§2Maladie", new Cooldown(10), new ItemBuilder(Material.SPIDER_EYE).setName("§2Maladie"), role,
                    "",
                    "Item de test",
                    "");
            EventUtils.registerRoleEvent(this);
            this.runnable = new ResiMatesRunnable(this);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                this.stade++;
                player.sendMessage("§7Votre§2 maladie§7 empire, elle à atteind le§c stade "+stade);
                setStade(this.stade, player);
                return true;
            }
            return false;
        }
        private void setStade(final int state,final Player owner) {
            switch (state) {
                case 2:
                    getRole().setMaxHealth(getRole().getMaxHealth()-4);
                    owner.setMaxHealth(getRole().getMaxHealth());
                    PilierRoles pilier = this.findRandomPillier();
                    if (pilier == null) {
                        owner.sendMessage("§7Malheureusement, aucun§a pilier§7 n'est encore en§c vie§7, votre§c sacrifice§7 a donc été§c inutile.");
                    } else {
                        owner.sendMessage("§7Vous l'avez trouver ! Un§a pilier§7 ! Et en§c vie§7 en plus, il s'agit de§a "+pilier.getName()+"§7 (§a"+pilier.getGamePlayer().getPlayerName()+"§7)");
                        getRole().getKnowedRoles().add(pilier.getClass());
                        this.runnable.pilierUUID = pilier.getPlayer();
                        this.runnable.runTaskTimerAsynchronously(getPlugin(), 0, 20);
                    }
                    getRole().givePotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 0), EffectWhen.PERMANENT);
                    break;
                case 3:
                    getRole().setMaxHealth(getRole().getMaxHealth()-4);
                    owner.setMaxHealth(getRole().getMaxHealth());
                    Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> getRole().addPower(new ShowHealthPower(getRole())), 5);
                    break;
                default:
                    break;
            }
        }
        @EventHandler
        private void onDeath(UHCPlayerKillEvent event) {
            if (event.getGamePlayerKiller() != null) {
                final GamePlayer gameVictim = event.getGameState().getGamePlayer().get(event.getVictim().getUniqueId());
                if (gameVictim == null)return;
                if (gameVictim.getRole() == null)return;
                if (gameVictim.getRole() instanceof PilierRoles) {
                    if (this.stade >= 3) {
                        Player owner = getPlugin().getServer().getPlayer(getRole().getPlayer());
                        if (owner != null) {

                        }
                    }
                }
            }
        }
        private PilierRoles findRandomPillier() {
            final List<GamePlayer> gamePlayerList = new ArrayList<>(getRole().getGameState().getGamePlayer().values());
            if (gamePlayerList.isEmpty())return null;
            Collections.shuffle(gamePlayerList, Main.RANDOM);
            for (final GamePlayer gamePlayer : gamePlayerList) {
                if (gamePlayer.getRole() == null)continue;
                if (gamePlayer.getRole() instanceof PilierRoles) {
                    if (!gamePlayer.isAlive())continue;
                    return (PilierRoles) gamePlayer.getRole();
                }
            }
            return null;
        }
        private static class ResiMatesRunnable extends BukkitRunnable {

            private final KagayaV2 kagaya;
            private final GameState gameState;
            private UUID pilierUUID;

            private ResiMatesRunnable(MaladieItem item) {
                this.kagaya = (KagayaV2) item.getRole();
                this.gameState = kagaya.getGameState();
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.pilierUUID == null) return;
                final Player owner = Bukkit.getPlayer(kagaya.getPlayer());
                if (owner != null) {
                    Player pilier = Bukkit.getPlayer(pilierUUID);
                    if (pilier != null) {
                        final List<Player> aroundPlayers = Loc.getNearbyPlayersExcept(owner, 20);
                        if (aroundPlayers.contains(pilier)) {
                            Bukkit.getScheduler().runTask(Main.getInstance(), () -> owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 0, false, false), true));
                        }
                    }
                }
            }
        }
    }
    private static class ShowHealthPower extends Power implements Listener {

        private final Map<UUID, ArmorStandUtils> armorStands = new HashMap<>();

        public ShowHealthPower(@NonNull RoleBase role) {
            super("§aVoir la vie des joueurs", null, role);
            EventUtils.registerRoleEvent(this);
            for (final UUID uuid : role.gameState.getGamePlayer().keySet()) {
                if (uuid.equals(role.getPlayer()))continue;
                ArmorStandUtils armorStand = new ArmorStandUtils(role.gameState.getGamePlayer().get(uuid).getLastLocation(), "TEST");
                armorStand.display(role.owner);
                this.armorStands.put(uuid, armorStand);
            }
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            return false;
        }
        @EventHandler
        private void PlayerMooveEvent(PlayerMoveEvent event) {
            if (armorStands.containsKey(event.getPlayer().getUniqueId())) {
                Player owner = Bukkit.getPlayer(getRole().getPlayer());
                if (owner == null)return;
                armorStands.get(event.getPlayer().getUniqueId()).rename("§c"+new DecimalFormat("0").format(event.getPlayer().getHealth())+"❤", owner);
                final Location to = new Location(event.getTo().getWorld(), event.getTo().getX(), event.getTo().getY()+0.5, event.getTo().getZ());
                armorStands.get(event.getPlayer().getUniqueId()).teleport(to, owner);
            }
        }
    }
}

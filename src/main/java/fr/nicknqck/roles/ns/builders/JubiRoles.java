package fr.nicknqck.roles.ns.builders;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.entity.bijuv2.BijuBase;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.managers.BijuManager;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.UUID;

public abstract class JubiRoles extends NSRoles implements IUchiwa{

    public JubiRoles(UUID player) {
        super(player);
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Jubi;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new TraqueurPower(this), true);
    }

    @Override
    public @NonNull EUchiwaType getUchiwaType() {
        return EUchiwaType.LEGENDAIRE;
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.GENIE;
    }

    public static class TraqueurPower extends ItemPower implements Listener {

        private final JubiRoles roles;
        private BijuBase traqued = null;
        public TraqueurPower(@NonNull JubiRoles role) {
            super("§dTraqueur de biju", null, new ItemBuilder(Material.COMPASS).setName("§dTraqueur"), role,
                    "§7Vous permet de traquer les§d bijus§7 qui sont activé dans la partie");
            this.roles = role;
            EventUtils.registerEvents(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                PlayerInteractEvent event = (PlayerInteractEvent) args.get("event");
                if (this.roles == null)return false;
                if (!this.getPlugin().getBijuManager().isBijuEnable()) {
                    this.roles.getPowers().remove(this);
                    player.sendMessage("§7Les bijus sont désactiver pendant cette partie.");
                    player.setItemInHand(null);
                    return true;
                }
                Action action = event.getAction();
                if (action.name().contains("RIGHT")) {
                    Inventory inv = Bukkit.createInventory(player, 9, "§7Traqueur de§d Biju");
                    boolean empty = true;
                    for (@NonNull final Class<? extends BijuBase> clazz : Main.getInstance().getBijuManager().getClassBijuMap().keySet()) {
                        if (Main.getInstance().getBijuManager().getBijuEnables().get(clazz)) {
                            inv.addItem(Main.getInstance().getBijuManager().getClassBijuMap().get(clazz).getItemInMenu());
                            empty = false;
                        }
                    }
                    if (empty) {
                        this.roles.getPowers().remove(this);
                        player.sendMessage("§7Les bijus sont désactiver pendant cette partie.");
                        player.setItemInHand(null);
                        return true;
                    }
                    player.openInventory(inv);
                } else {
                    printTraqueMessage(player);
                }
                return true;
            }
            return false;
        }
        @EventHandler
        private void onEndGame(EndGameEvent event) {
            EventUtils.unregisterEvents(this);
        }
        @EventHandler
        private void onInventoryClick(InventoryClickEvent event) {
            if (event.getClickedInventory() == null)return;
            if (event.getClickedInventory().getTitle() == null)return;
            if (!(event.getWhoClicked() instanceof Player))return;
            if (!event.getWhoClicked().getUniqueId().equals(this.roles.getPlayer()))return;
            if (!event.getClickedInventory().getTitle().equals("§7Traqueur de§d Biju"))return;
            if (event.getCurrentItem() == null)return;
            if (!event.getCurrentItem().hasItemMeta())return;
            if (!event.getCurrentItem().getItemMeta().hasDisplayName())return;
            for (@NonNull final BijuBase bijuBase : Main.getInstance().getBijuManager().getBijuSpawnMap().keySet()) {
                if (bijuBase.getItemInMenu().getItemMeta().getDisplayName().equalsIgnoreCase(event.getCurrentItem().getItemMeta().getDisplayName())) {
                    this.traqued = bijuBase;
                    event.getWhoClicked().sendMessage("§7Vous§c traquez§7 maintenant " + bijuBase.getName());
                    printTraqueMessage((Player) event.getWhoClicked());
                    break;
                }
            }
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
        }
        private void printTraqueMessage(Player player) {
            if (this.traqued == null) {
                player.sendMessage("§cVous ne traquez§c aucun§d biju§c actuellement§c !");
                return;
            }
            player.sendMessage("§7Voici tout les informations disponnible sur "+this.traqued.getName());
            player.sendMessage("");
            if (this.traqued.getHote() == null) {
                if (this.traqued.getEntity() == null) {
                    player.sendMessage("§7Il apparaitra en "+getGoodCoord(this.traqued.getOriginSpawn()));
                    player.setCompassTarget(this.traqued.getOriginSpawn());
                    final BijuManager bijuManager = Main.getInstance().getBijuManager();
                    for (@NonNull final BijuBase bijuBase : bijuManager.getBijuSpawnMap().keySet()) {
                        if (bijuBase.getClass().equals(this.traqued.getClass())) {
                            player.sendMessage("§7Il apparaitra dans§c "+
                                    StringUtils.secondsTowardsBeautiful(
                                            Main.getInstance().getBijuManager().getBijuSpawnMap().get(bijuBase)-this.roles.getGameState().getInGameTime()
                                    )+
                                    "§7 c'est à dire à §c"+
                                    StringUtils.secondsTowardsBeautiful(Main.getInstance().getBijuManager().getBijuSpawnMap().get(bijuBase))+"§7.");
                            break;
                        }
                    }
                } else {
                    player.sendMessage("§7Il est actuellement en "+getGoodCoord(this.traqued.getEntity().getLocation()));
                    player.setCompassTarget(this.traqued.getEntity().getLocation());
                }
            } else {
                GamePlayer gamePlayer = (this.traqued.getHote());
                player.sendMessage("§c"+gamePlayer.getPlayerName()+"§7 est en "+getGoodCoord(gamePlayer.getLastLocation()));
            }
        }
        private String getGoodCoord(Location location) {
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            return "§cx§7: §c"+x+"§7,§c y§7:§c "+y+"§7,§c z§7:§c "+z;
        }
    }
}
package fr.nicknqck.events.essential.inventorys;

import fr.nicknqck.GameState;
import fr.nicknqck.HubListener;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.utils.rank.ChatRank;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class HubInventory implements Listener {

    private final GameState gameState;
    @Getter
    private static HubInventory instance;

    public HubInventory(GameState gameState) {
        this.gameState = gameState;
        instance = this;
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryConfig(gameState), Main.getInstance());
    }
    @EventHandler
    private void OnInventoryClicked(InventoryClickEvent event) {
        if (gameState.getServerState() != GameState.ServerStates.InLobby) return;
        if (event.getWhoClicked() instanceof Player) {
            Player player = Bukkit.getPlayer(event.getWhoClicked().getName());
            Inventory inv = event.getClickedInventory();
            InventoryAction action = event.getAction();
            if (inv != null && event.getCurrentItem() != null) {
                if (event.getCurrentItem().isSimilar(GUIItems.getStartGameButton()) && gameState.gameCanLaunch) HubListener.getInstance().StartGame(player);
                final ItemStack item = event.getCurrentItem();
                final boolean mahr = item.isSimilar(GUIItems.getSelectMahrButton());
                final boolean sl = item.isSimilar(GUIItems.getSelectSlayersButton());
                final boolean d = item.isSimilar(GUIItems.getSelectDemonButton());
                final boolean solo = item.isSimilar(GUIItems.getSelectSoloButton());
                final boolean titans = item.isSimilar(GUIItems.getSelectTitanButton());
                final boolean soldat = item.isSimilar(GUIItems.getSelectSoldatButton());
                final boolean akatsuki = item.isSimilar(GUIItems.getSelectAkatsukiButton());
                final boolean orochimaru = item.isSimilar(GUIItems.getSelectOrochimaruButton());
                final boolean shinobi = item.isSimilar(GUIItems.getSelectShinobiButton());
                if (!item.hasItemMeta())return;
                switch(inv.getTitle()) {
                    case "Configuration -> AOT":
                        if (!item.hasItemMeta())return;
                        if (!item.getItemMeta().hasDisplayName())return;
                        if (item.getType() == Material.AIR)return;
                        String name = item.getItemMeta().getDisplayName();
                        if (name.equals("§rCooldown Equipement Tridimentionnel")) {
                            if (action.equals(InventoryAction.PICKUP_ALL)) {
                                Main.getInstance().getGameConfig().getAotConfig().setTridiCooldown(Main.getInstance().getGameConfig().getAotConfig().getTridiCooldown()-1);
                            }else {
                                Main.getInstance().getGameConfig().getAotConfig().setTridiCooldown(Math.max(1, Main.getInstance().getGameConfig().getAotConfig().getTridiCooldown()+1));
                            }
                        }
                        if (name.equals("§rEquipement Tridimentionnel")){
                            gameState.rod = !gameState.rod;
                        }
                        if (name.equals("§r§6Lave§f pour les titans (transformé)")) {
                            Main.getInstance().getGameConfig().setLaveTitans(!Main.getInstance().getGameConfig().isLaveTitans());
                        }
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            player.openInventory(GUIItems.getMahrGui());
                            Main.getInstance().getInventories().updateMahrInventory(player);
                        }
                        Main.getInstance().getInventories().updateAOTConfiguration(player);
                        event.setCancelled(true);
                        break;
                    case "§fRoles§7 ->§6 DemonSlayer":
                        if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
                            if (sl || d || solo) {
                                if (sl) {
                                    player.openInventory(GUIItems.getSlayersSelectGUI());
                                    Main.getInstance().getInventories().updateSlayerInventory(player);
                                }
                                if (d) {
                                    player.openInventory(GUIItems.getDemonSelectGUI());
                                    Main.getInstance().getInventories().updateDemonInventory(player);
                                }
                                if (solo) {
                                    player.openInventory(GUIItems.getDSSoloSelectGUI());
                                    Main.getInstance().getInventories().updateDSSoloInventory(player);
                                }
                            }
                        }else {
                            player.openInventory(GUIItems.getRoleSelectGUI());
                            Main.getInstance().getInventories().updateRoleInventory(player);
                        }
                        event.setCancelled(true);
                        break;
                    case "§fAOT§7 -> §eSolo":
                        if (item.getItemMeta() == null)return;
                        if (item.getItemMeta().getDisplayName() == null)return;
                        if (item.getType() == Material.AIR)return;
                        if (item.isSimilar(GUIItems.getx())) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!ChatRank.isHost(player)) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
                            if (!item.isSimilar(GUIItems.getOrangeStainedGlassPane()) && !solo && !d && !sl && !mahr && !titans && !soldat && !item.isSimilar(GUIItems.getCantStartGameButton()) && !item.isSimilar(GUIItems.getStartGameButton())) {
                                name = item.getItemMeta().getDisplayName();
                                if (action.equals(InventoryAction.PICKUP_ALL)) {
                                    EasyRoleAdder.addRoles(name);
                                } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                    EasyRoleAdder.removeRoles(name);
                                }else {
                                    event.setCancelled(true);
                                }
                            }
                            if (item.isSimilar(GUIItems.getStartGameButton()) && gameState.gameCanLaunch) HubListener.getInstance().StartGame(player);

                        } else {
                            for (UUID u : gameState.getInLobbyPlayers()) {
                                Player p = Bukkit.getPlayer(u);
                                if (p == null)return;
                                if (p == event.getWhoClicked()) {
                                    if (ChatRank.isHost(player)){
                                        p.openInventory(GUIItems.getSelectAOTInventory());
                                        Main.getInstance().getInventories().updateAOTInventory(player);
                                    }
                                    if (!p.isOp() && p.getOpenInventory() != null && p.getInventory() != null) p.closeInventory();
                                }
                            }
                        }
                        for (UUID u : gameState.getInLobbyPlayers()) {
                            Player p = Bukkit.getPlayer(u);
                            if (p == null)return;
                            Main.getInstance().getInventories().updateDSSoloInventory(p);
                        }
                        if (solo) {
                            player.openInventory(GUIItems.getDSSoloSelectGUI());
                            Main.getInstance().getInventories().updateDSSoloInventory(player);
                        }
                        if (mahr) {
                            player.openInventory(GUIItems.getMahrGui());
                            Main.getInstance().getInventories().updateMahrInventory(player);
                        }
                        if (titans) {
                            player.openInventory(GUIItems.getSecretTitansGui());
                            Main.getInstance().getInventories().updateTitansInventory(player);
                        }
                        if (soldat) {
                            player.openInventory(GUIItems.getSecretSoldatGui());
                            Main.getInstance().getInventories().updateSoldatInventory(player);
                        }
                        event.setCancelled(true);
                        break;
                    case "§fRoles§7 ->§6 AOT":
                        if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
                            if (solo || mahr || titans || soldat) {
                                if (solo) {
                                    player.openInventory(GUIItems.getAOTSoloSelectGUI());
                                    Main.getInstance().getInventories().updateAOTSoloInventory(player);
                                }
                                if (mahr) {
                                    player.openInventory(GUIItems.getMahrGui());
                                    Main.getInstance().getInventories().updateMahrInventory(player);
                                }
                                if (titans) {
                                    player.openInventory(GUIItems.getSecretTitansGui());
                                    Main.getInstance().getInventories().updateTitansInventory(player);
                                }
                                if (soldat) {
                                    player.openInventory(GUIItems.getSecretSoldatGui());
                                    Main.getInstance().getInventories().updateSoldatInventory(player);
                                }
                            }
                        }else {
                            player.openInventory(GUIItems.getRoleSelectGUI());
                            Main.getInstance().getInventories().updateRoleInventory(player);
                        }
                        event.setCancelled(true);
                        break;
                    case "§fRoles§7 ->§6 NS":
                        if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
                            if (item.isSimilar(GUIItems.getStartGameButton())) {
                                if (gameState.gameCanLaunch) {
                                    HubListener.getInstance().StartGame(player);
                                }
                            } else {
                                if (solo) {
                                    player.openInventory(GUIItems.getSelectNSSoloInventory());
                                    Main.getInstance().getInventories().updateNSSoloInventory(player);
                                }
                                if (akatsuki) {
                                    player.openInventory(GUIItems.getSelectAkatsukiInventory());
                                    Main.getInstance().getInventories().updateNSAkatsukiInventory(player);
                                }
                                if (orochimaru) {
                                    player.openInventory(GUIItems.getSelectOrochimaruInventory());
                                    Main.getInstance().getInventories().updateNSOrochimaruInventory(player);
                                }
                                if (shinobi) {
                                    player.openInventory(GUIItems.getSelectNSShinobiInventory());
                                    Main.getInstance().getInventories().updateNSShinobiInventory(player);
                                }
                            }
                        }else {
                            player.openInventory(GUIItems.getRoleSelectGUI());
                            Main.getInstance().getInventories().updateRoleInventory(player);
                        }
                        event.setCancelled(true);
                        break;
                    case "§dKrystal UHC":
                        if (item.isSimilar(GUIItems.getSelectSoloButton())) {
                            Main.getInstance().getInventories().openKrystalSoloInventory(player);
                        }
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            player.openInventory(GUIItems.getRoleSelectGUI());
                            Main.getInstance().getInventories().updateRoleInventory(player);
                        }
                        event.setCancelled(true);
                        break;
                    case "§dKrystal UHC§7 ->§e Solo":
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            Main.getInstance().getInventories().openKrystalInventory(player);
                            event.setCancelled(true);
                            return;
                        }
                        if (!item.getType().equals(Material.STAINED_GLASS_PANE)) {
                            if (action.equals(InventoryAction.PICKUP_ALL)) {
                                EasyRoleAdder.addRoles(item.getItemMeta().getDisplayName());
                            } else if (action.equals(InventoryAction.PICKUP_HALF)){
                                EasyRoleAdder.removeRoles(item.getItemMeta().getDisplayName());
                            }
                        }
                        Main.getInstance().getInventories().openKrystalSoloInventory(player);
                        event.setCancelled(true);
                        break;
                    default:
                        break;
                }
                for (UUID u : gameState.getInLobbyPlayers()) {
                    Player p = Bukkit.getPlayer(u);
                    if (p == null)return;
                    Main.getInstance().getInventories().menuUpdater(p);
                }
            }
        }
    }

}
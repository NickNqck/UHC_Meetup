package fr.nicknqck.events.essential.inventorys.rconfig;

import fr.nicknqck.GameState;
import fr.nicknqck.HubListener;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.rank.ChatRank;
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

public class DSRolesConfig implements Listener {

    private final GameState gameState;

    public DSRolesConfig(GameState gameState) {
        this.gameState = gameState;
        EventUtils.registerEvents(this);
    }
    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))return;
        if (gameState.getServerState() != GameState.ServerStates.InLobby) return;
        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getClickedInventory();
        InventoryAction action = event.getAction();
        if (inv != null && event.getCurrentItem() != null) {
            ItemStack item = event.getCurrentItem();
            if (!item.hasItemMeta())return;
            final boolean sl = item.isSimilar(GUIItems.getSelectSlayersButton());
            final boolean d = item.isSimilar(GUIItems.getSelectDemonButton());
            final boolean solo = item.isSimilar(GUIItems.getSelectSoloButton());
            switch (inv.getTitle()) {
                case "DemonSlayer ->§a Slayers":
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
                        if (!item.isSimilar(GUIItems.getGreenStainedGlassPane()) && !item.isSimilar(GUIItems.getSelectSoloButton()) && !item.isSimilar(GUIItems.getSelectDemonButton()) && !item.isSimilar(GUIItems.getCantStartGameButton()) && !item.isSimilar(GUIItems.getStartGameButton())) {
                            String name = item.getItemMeta().getDisplayName();
                            GameState.Roles role = GameState.Roles.valueOf(name);
                            if (action.equals(InventoryAction.PICKUP_ALL)) {
                                gameState.addInAvailableRoles(role, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(role)+1));
                            } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                gameState.addInAvailableRoles(role, Math.max(0, gameState.getAvailableRoles().get(role)-1));
                            }
                        }
                        if (item.isSimilar(GUIItems.getStartGameButton()) && gameState.gameCanLaunch) HubListener.getInstance().StartGame(player);
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

                    } else {
                        player.openInventory(GUIItems.getDemonSlayerInventory());
                        Main.getInstance().getInventories().updateDSInventory(player);
                    }
                    for (UUID u : gameState.getInLobbyPlayers()) {
                        Player p = Bukkit.getPlayer(u);
                        if (p == null)continue;
                        Main.getInstance().getInventories().updateSlayerInventory(p);
                    }
                    event.setCancelled(true);
                    break;
                case "DemonSlayer -> §cDémons":
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
                        if (!item.isSimilar(GUIItems.getRedStainedGlassPane()) && !item.isSimilar(GUIItems.getSelectSoloButton()) && !item.isSimilar(GUIItems.getSelectDemonButton()) && !item.isSimilar(GUIItems.getSelectSlayersButton())&& !item.isSimilar(GUIItems.getCantStartGameButton()) && !item.isSimilar(GUIItems.getStartGameButton())) {
                            String name = item.getItemMeta().getDisplayName();
                            GameState.Roles role = GameState.Roles.valueOf(name);
                            if (action.equals(InventoryAction.PICKUP_ALL)) {
                                gameState.addInAvailableRoles(role, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(role)+1));
                            } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                gameState.addInAvailableRoles(role, Math.max(0, gameState.getAvailableRoles().get(role)-1));
                            }
                        }
                        if (item.isSimilar(GUIItems.getStartGameButton()) && gameState.gameCanLaunch) HubListener.getInstance().StartGame(player);
                    } else {
                        player.openInventory(GUIItems.getDemonSlayerInventory());
                        Main.getInstance().getInventories().updateDSInventory(player);
                    }
                    for (UUID u : gameState.getInLobbyPlayers()) {
                        Player p = Bukkit.getPlayer(u);
                        if (p == null)continue;
                        Main.getInstance().getInventories().updateDemonInventory(p);
                    }
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

                    event.setCancelled(true);
                    break;
                case "DemonSlayer -> §eSolo":
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
                        if (!item.isSimilar(GUIItems.getOrangeStainedGlassPane()) && !solo && !d && !sl && !item.isSimilar(GUIItems.getCantStartGameButton()) && !item.isSimilar(GUIItems.getStartGameButton())) {
                            String name = item.getItemMeta().getDisplayName();
                            GameState.Roles role = GameState.Roles.valueOf(name);
                            if (action.equals(InventoryAction.PICKUP_ALL)) {
                                gameState.addInAvailableRoles(role, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(role)+1));
                            } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                gameState.addInAvailableRoles(role, Math.max(0, gameState.getAvailableRoles().get(role)-1));
                            }
                        }
                        if (item.isSimilar(GUIItems.getStartGameButton()) && gameState.gameCanLaunch) HubListener.getInstance().StartGame(player);

                    } else {
                        for (UUID u : gameState.getInLobbyPlayers()) {
                            Player p = Bukkit.getPlayer(u);
                            if (p == null)continue;
                            if (p == event.getWhoClicked()) {
                                if (ChatRank.isHost(player)) {
                                    p.openInventory(GUIItems.getDemonSlayerInventory());
                                    Main.getInstance().getInventories().updateDSInventory(player);
                                }
                                if (!p.isOp() && p.getOpenInventory() != null && p.getInventory() != null) p.closeInventory();
                            }
                        }
                    }
                    for (UUID u : gameState.getInLobbyPlayers()) {
                        Player p = Bukkit.getPlayer(u);
                        if (p == null)continue;
                        Main.getInstance().getInventories().updateDSSoloInventory(p);
                    }
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
                    event.setCancelled(true);
                    break;
            }
        }
    }

}
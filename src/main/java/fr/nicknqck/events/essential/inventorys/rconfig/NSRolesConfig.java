package fr.nicknqck.events.essential.inventorys.rconfig;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.utils.rank.ChatRank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class NSRolesConfig implements Listener {

    private final GameState gameState;

    public NSRolesConfig(GameState gameState) {
        this.gameState = gameState;
    }
    @EventHandler
    private void oninventoryConfig(InventoryClickEvent event) {
        if (gameState.getServerState() != GameState.ServerStates.InLobby) return;
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            Inventory inv = event.getClickedInventory();
            InventoryAction action = event.getAction();
            if (inv != null && event.getCurrentItem() != null) {
                ItemStack item = event.getCurrentItem();
                if (!item.hasItemMeta())return;
                switch (inv.getTitle()) {
                    case "§aNaruto§7 ->§a Shinobi":
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            player.openInventory(GUIItems.getRoleSelectGUI());
                            Main.getInstance().getInventories().updateRoleInventory(player);
                        } else {
                            if (ChatRank.isHost(player)) {
                                for (GameState.Roles roles : GameState.Roles.values()) {
                                    if (item.getItemMeta().getDisplayName().contains(roles.getItem().getItemMeta().getDisplayName())) {
                                        if (action.equals(InventoryAction.PICKUP_ALL)) {
                                            gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
                                        } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                            gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
                                        }
                                    }
                                }
                            }
                        }
                        event.setCancelled(true);
                        break;
                    case "§aNaruto§7 ->§c Akatsuki":
                        if (!item.hasItemMeta())return;
                        if (!item.getItemMeta().hasDisplayName())return;
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            player.openInventory(GUIItems.getSelectNSInventory());
                            Main.getInstance().getInventories().updateNSInventory(player);
                        } else {
                            if (ChatRank.isHost(player)) {
                                for (GameState.Roles roles : GameState.Roles.values()) {
                                    if (item.getItemMeta().getDisplayName().contains(roles.getItem().getItemMeta().getDisplayName())) {
                                        if (action.equals(InventoryAction.PICKUP_ALL)) {
                                            gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
                                        } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                            gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
                                        }
                                    }
                                }
                            }
                        }
                        event.setCancelled(true);
                        break;
                    case "§aNaruto§7 ->§5 Orochimaru":
                        if (!item.getItemMeta().hasDisplayName())return;
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            player.openInventory(GUIItems.getRoleSelectGUI());
                            Main.getInstance().getInventories().updateRoleInventory(player);
                        } else {
                            if (ChatRank.isHost(player)) {
                                for (GameState.Roles roles : GameState.Roles.values()) {
                                    if (item.getItemMeta().getDisplayName().contains(roles.getItem().getItemMeta().getDisplayName())) {
                                        if (action.equals(InventoryAction.PICKUP_ALL)) {
                                            gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
                                        } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                            gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
                                        }
                                    }
                                }
                            }
                        }
                        event.setCancelled(true);
                        break;
                    case "§aNaruto§7 ->§e Solo":
                        if (!item.hasItemMeta())return;
                        if (!item.getItemMeta().hasDisplayName())return;
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            player.openInventory(GUIItems.getRoleSelectGUI());
                            Main.getInstance().getInventories().updateRoleInventory(player);
                        } else {
                            if (item.isSimilar(GUIItems.getSelectJubiButton())) {
                                player.openInventory(GUIItems.getSelectNSJubiInventory());
                                Main.getInstance().getInventories().updateNSJubiInventory(player);
                                event.setCancelled(true);
                                return;
                            }
                            if (item.isSimilar(GUIItems.getSelectBrumeButton())) {
                                player.openInventory(GUIItems.getSelectNSBrumeInventory());
                                Main.getInstance().getInventories().updateNSBrumeInventory(player);
                                event.setCancelled(true);
                                return;
                            }
                            if (item.isSimilar(GUIItems.getSelectKumogakureButton())){
                                player.openInventory(Bukkit.createInventory(player, 54, "§eSolo§7 ->§6 Kumogakure"));
                                Main.getInstance().getInventories().updateNSKumogakure(player);
                                event.setCancelled(true);
                                return;
                            }
                            if (ChatRank.isHost(player)) {
                                for (GameState.Roles roles : GameState.Roles.values()) {
                                    if (item.getItemMeta().getDisplayName().contains(roles.getItem().getItemMeta().getDisplayName())) {
                                        if (action.equals(InventoryAction.PICKUP_ALL)) {
                                            gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
                                        } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                            gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
                                        }
                                    }
                                }
                            }
                        }
                        event.setCancelled(true);
                        break;
                    case "§eSolo§7 ->§d Jubi":
                        if (!item.hasItemMeta())return;
                        if (!item.getItemMeta().hasDisplayName())return;
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            player.openInventory(GUIItems.getSelectNSSoloInventory());
                            Main.getInstance().getInventories().updateNSSoloInventory(player);
                        } else {
                            if (ChatRank.isHost(player)) {
                                for (GameState.Roles roles : GameState.Roles.values()) {
                                    if (item.getItemMeta().getDisplayName().contains(roles.getItem().getItemMeta().getDisplayName())) {
                                        if (action.equals(InventoryAction.PICKUP_ALL)) {
                                            gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
                                        } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                            gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
                                        }
                                    }
                                }
                            }
                        }
                        event.setCancelled(true);
                        break;
                    case "§eSolo§7 ->§b Zabuza et Haku":
                        if (!item.getItemMeta().hasDisplayName())return;
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            player.openInventory(GUIItems.getSelectNSSoloInventory());
                            Main.getInstance().getInventories().updateNSSoloInventory(player);
                        } else {
                            if (ChatRank.isHost(player)) {
                                for (GameState.Roles roles : GameState.Roles.values()) {
                                    if (item.getItemMeta().getDisplayName().contains(roles.getItem().getItemMeta().getDisplayName())) {
                                        if (action.equals(InventoryAction.PICKUP_ALL)) {
                                            gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
                                        } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                            gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
                                        }
                                    }
                                }
                            }
                        }
                        event.setCancelled(true);
                        break;
                    case "§eSolo§7 ->§6 Kumogakure":
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            player.openInventory(GUIItems.getSelectNSSoloInventory());
                            Main.getInstance().getInventories().updateNSSoloInventory(player);
                        } else {
                            if (ChatRank.isHost(player)) {
                                for (GameState.Roles roles : GameState.Roles.values()) {
                                    if (item.getItemMeta().getDisplayName().contains(roles.getItem().getItemMeta().getDisplayName())) {
                                        if (action.equals(InventoryAction.PICKUP_ALL)) {
                                            gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
                                        } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                            gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
                                        }
                                    }
                                }
                            }
                        }
                        event.setCancelled(true);
                        break;
                }
            }
        }
    }
}

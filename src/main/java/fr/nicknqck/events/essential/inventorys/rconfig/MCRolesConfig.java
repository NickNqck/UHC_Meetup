package fr.nicknqck.events.essential.inventorys.rconfig;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.utils.rank.ChatRank;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MCRolesConfig implements Listener {

    private final GameState gameState;

    public MCRolesConfig(GameState gameState) {
        this.gameState = gameState;
    }
    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        if (gameState.getServerState() != GameState.ServerStates.InLobby) return;
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            Inventory inv = event.getClickedInventory();
            InventoryAction action = event.getAction();
            if (inv != null && event.getCurrentItem() != null) {
                ItemStack item = event.getCurrentItem();
                if (!item.hasItemMeta())return;
                switch (inv.getTitle()) {
                    case "§aMinecraft§7 ->§a Overworld":
                    case "§aMinecraft§7 ->§c Nether":
                        if (!item.getItemMeta().hasDisplayName())return;
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            player.openInventory(GUIItems.getSelectMCInventory());
                            Main.getInstance().getInventories().updateMCInventory(player);
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

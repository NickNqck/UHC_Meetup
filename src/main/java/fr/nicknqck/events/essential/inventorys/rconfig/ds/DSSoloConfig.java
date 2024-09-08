package fr.nicknqck.events.essential.inventorys.rconfig.ds;

import fr.nicknqck.GameState;
import fr.nicknqck.HubListener;
import fr.nicknqck.Main;
import fr.nicknqck.events.essential.inventorys.ConfiguratorRole;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.utils.rank.ChatRank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class DSSoloConfig extends ConfiguratorRole {
    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        GameState gameState = GameState.getInstance();
        Player player = (Player) event.getWhoClicked();
        InventoryAction action = event.getAction();
        if (item.isSimilar(GUIItems.getx())) {
            event.setCancelled(true);
            return;
        }
        if (!ChatRank.isHost(player)) {
            event.setCancelled(true);
            return;
        }
        if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
            if (item.isSimilar(GUIItems.getSelectSlayersButton())) {
                event.setCancelled(true);
                player.openInventory(GUIItems.getSlayersSelectGUI());
                Main.getInstance().getInventories().updateSlayerInventory(player);
                return;
            }
            if (item.isSimilar(GUIItems.getSelectDemonButton())) {
                event.setCancelled(true);
                player.openInventory(GUIItems.getDemonSelectGUI());
                Main.getInstance().getInventories().updateDemonInventory(player);
                return;
            }
            if (item.isSimilar(GUIItems.getSelectSoloButton())) {
                event.setCancelled(true);
                player.openInventory(GUIItems.getDSSoloSelectGUI());
                Main.getInstance().getInventories().updateDSSoloInventory(player);
                return;
            }
            if (!item.isSimilar(GUIItems.getOrangeStainedGlassPane()) && !item.isSimilar(GUIItems.getCantStartGameButton()) && !item.isSimilar(GUIItems.getStartGameButton())) {
                String name = item.getItemMeta().getDisplayName();
                if (action.equals(InventoryAction.PICKUP_ALL)) {
                    addRoles(name);
                } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                    removeRoles(name);
                }
            }
            if (item.isSimilar(GUIItems.getStartGameButton()) && gameState.gameCanLaunch)
                HubListener.getInstance().StartGame(player);

        } else {
            for (UUID u : gameState.getInLobbyPlayers()) {
                Player p = Bukkit.getPlayer(u);
                if (p == null) continue;
                if (p == event.getWhoClicked()) {
                    if (ChatRank.isHost(player)) {
                        p.openInventory(GUIItems.getDemonSlayerInventory());
                        Main.getInstance().getInventories().updateDSInventory(player);
                    }
                    if (!p.isOp() && p.getOpenInventory() != null && p.getInventory() != null)
                        p.closeInventory();
                }
            }
        }
        for (UUID u : gameState.getInLobbyPlayers()) {
            Player p = Bukkit.getPlayer(u);
            if (p == null) continue;
            Main.getInstance().getInventories().updateDSSoloInventory(p);
        }
        event.setCancelled(true);
    }

    @Override
    public String getInvName() {
        return "DemonSlayer -> §eSolo";
    }
}

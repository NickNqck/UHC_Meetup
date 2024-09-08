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

public class DemonsConfig extends ConfiguratorRole {

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        InventoryAction action = event.getAction();
        GameState gameState = GameState.getInstance();
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
                if (action.equals(InventoryAction.PICKUP_ALL)) {
                    addRoles(name);
                } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                    removeRoles(name);
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
        if (item.isSimilar(GUIItems.getSelectSlayersButton())) {
            player.openInventory(GUIItems.getSlayersSelectGUI());
            Main.getInstance().getInventories().updateSlayerInventory(player);
        }
        if (item.isSimilar(GUIItems.getSelectDemonButton())) {
            player.openInventory(GUIItems.getDemonSelectGUI());
            Main.getInstance().getInventories().updateDemonInventory(player);
        }
        if (item.isSimilar(GUIItems.getSelectSoloButton())) {
            player.openInventory(GUIItems.getDSSoloSelectGUI());
            Main.getInstance().getInventories().updateDSSoloInventory(player);
        }
        event.setCancelled(true);
    }

    @Override
    public String getInvName() {
        return "DemonSlayer -> §cDémons";
    }
}

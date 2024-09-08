package fr.nicknqck.events.essential.inventorys.rconfig.ns;

import fr.nicknqck.Main;
import fr.nicknqck.events.essential.inventorys.ConfiguratorRole;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.utils.rank.ChatRank;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class JubiConfig extends ConfiguratorRole {

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryAction action = event.getAction();
        ItemStack item = event.getCurrentItem();
        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
            player.openInventory(GUIItems.getSelectNSSoloInventory());
            Main.getInstance().getInventories().updateNSSoloInventory(player);
        } else {
            if (ChatRank.isHost(player)) {
                String name = item.getItemMeta().getDisplayName();
                if (action.equals(InventoryAction.PICKUP_ALL)) {
                    addRoles(name);
                } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                    removeRoles(name);
                }
            }
        }
        event.setCancelled(true);
    }

    @Override
    public String getInvName() {
        return "§eSolo§7 ->§d Jubi";
    }
}

package fr.nicknqck.events.essential.inventorys.rconfig.ns;

import fr.nicknqck.Main;
import fr.nicknqck.events.essential.inventorys.ConfiguratorRole;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.utils.rank.ChatRank;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ShinobiConfig extends ConfiguratorRole {
    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        InventoryAction action = event.getAction();
        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
            player.openInventory(GUIItems.getSelectNSInventory());
            Main.getInstance().getInventories().updateNSInventory(player);
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
        return "§aNaruto§7 ->§a Shinobi";
    }
}

package fr.nicknqck.events.essential.inventorys.rconfig.ns;

import fr.nicknqck.Main;
import fr.nicknqck.events.essential.inventorys.ConfiguratorRole;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.utils.rank.ChatRank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class NSSoloConfig extends ConfiguratorRole {
    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryAction action = event.getAction();
        ItemStack item = event.getCurrentItem();
        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
            player.openInventory(GUIItems.getSelectNSInventory());
            Main.getInstance().getInventories().updateNSInventory(player);
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
        return "§aNaruto§7 ->§e Solo";
    }
}

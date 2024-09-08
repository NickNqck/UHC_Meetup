package fr.nicknqck.events.essential.inventorys;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface IConfiguratorRole {

    void onInventoryClick(InventoryClickEvent event);
    String getInvName();

}

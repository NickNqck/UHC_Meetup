package fr.nicknqck.utils.betteritem;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.nicknqck.utils.StringUtils;

public class BetterItemListener implements Listener {

    @EventHandler
    public void onBetterItemUse(PlayerInteractEvent event){
        if(event.getItem() == null) return;
        if(BetterItem.getRegisteredItems() == null) return;
        for (BetterItem registeredItem : BetterItem.getRegisteredItems()) {
            if(registeredItem.getItemStack() == null) continue;
            if(registeredItem.isSame(event.getItem())){
                if(!registeredItem.isLeftClick() && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) return;
                if(registeredItem.isUseable()){
                    if (registeredItem.getEventPredicate().test(new BetterItemEvent(event))) {
                        registeredItem.setNextUse();
                    }
                }else {
                    event.getPlayer().sendMessage("§8 » §7Vous êtes en §ccooldown §7pendant encore " + StringUtils.secondsTowardsBeautiful((int) ((registeredItem.getNextUse() - System.currentTimeMillis())/ 1000)));
                }
            }
        }
    }

    @EventHandler
    public void onBetterBlockPlace(BlockPlaceEvent event){
        if(event.getItemInHand() == null) return;
        for (BetterItem registeredItem : BetterItem.getRegisteredItems()) {
            if(registeredItem.getItemStack() == null) continue;
            if(registeredItem.isSame(event.getItemInHand())){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBetterItemDrop(PlayerDropItemEvent event){
        if(event.getItemDrop().getItemStack() == null) return;
        for (BetterItem registeredItem : BetterItem.getRegisteredItems()) {
            if(registeredItem.getItemStack() == null) continue;
            if(registeredItem.isSame(event.getItemDrop().getItemStack())){
                event.getItemDrop().setTicksLived(registeredItem.getTicksLived());
                if(!registeredItem.isDroppable()) event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBetterItemDespawn(ItemDespawnEvent event){
        if(event.getEntity().getItemStack() == null) return;
        for (BetterItem registeredItem : BetterItem.getRegisteredItems()) {
            if(registeredItem.getItemStack() == null) continue;
            if(registeredItem.isSame(event.getEntity().getItemStack())){
                if(!registeredItem.isDespawnable()) event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
	public void OnBlockPlaced(BlockPlaceEvent event) {
    	if (event.getBlock() == null) return;
    	for (BetterItem registerItem : BetterItem.getRegisteredItems()) {
    		if (registerItem.getItemStack() == null) continue;
    		if (registerItem.isSame(event.getItemInHand())) {
    			if (!registerItem.isDroppable())event.setCancelled(true);
    		}
    	}
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getInventory() == null) return;

        ItemStack clicked = event.getCurrentItem();

        if (event.getHotbarButton() != -1) {
            clicked = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
        }

        if(clicked == null) return;
        InventoryType type = event.getWhoClicked().getOpenInventory().getType();
        if(type != InventoryType.CRAFTING && type != InventoryType.CREATIVE){
            for (BetterItem registeredItem : BetterItem.getRegisteredItems()) {
                if(registeredItem.getItemStack() == null) continue;
                if(registeredItem.isSame(clicked)){
                    if(!registeredItem.isMovableOther()){
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

}

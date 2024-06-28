package fr.nicknqck.utils.itembuilder;

import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilderListener implements Listener {
    @Getter
    private final List<ItemStack> cantBeDroppable = new ArrayList<>();
    @Getter
    private static ItemBuilderListener instance;

    public ItemBuilderListener(){
        instance = this;
    }
    @EventHandler
    private void onDrop(PlayerDropItemEvent event){
        if (!cantBeDroppable.isEmpty()){
            if (cantBeDroppable.contains(event.getItemDrop().getItemStack())) {
                event.setCancelled(true);
            }
        }
    }
}
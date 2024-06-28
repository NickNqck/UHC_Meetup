package fr.nicknqck.utils.itembuilder;

import lombok.Getter;
import org.bukkit.event.Listener;
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
}
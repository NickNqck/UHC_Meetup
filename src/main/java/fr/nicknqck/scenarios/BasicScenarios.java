package fr.nicknqck.scenarios;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
@Setter
@Getter
public abstract class BasicScenarios {

    private InventoryAction action;

    public abstract String getName();
    public abstract ItemStack getAffichedItem();
    public abstract void onClick(Player player);
    public boolean isClickGauche(){
        return getAction() == InventoryAction.PICKUP_ALL;
    }
    public boolean isClickDroit(){
        return getAction() == InventoryAction.PICKUP_HALF;
    }
    public boolean isShiftClick(){
        return getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY;
    }
    public boolean isDropClick(){
        return getAction() == InventoryAction.DROP_ONE_SLOT;
    }
}
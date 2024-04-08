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
}
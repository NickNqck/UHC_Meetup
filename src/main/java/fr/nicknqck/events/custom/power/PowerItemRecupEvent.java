package fr.nicknqck.events.custom.power;

import fr.nicknqck.utils.powers.ItemPower;
import lombok.Getter;
import org.bukkit.entity.Item;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PowerItemRecupEvent extends Event implements Cancellable {

    @Getter
    private final Item item;
    @Getter
    private final ItemStack itemStack;
    @Getter
    private final ItemPower itemPower;
    private boolean cancel;


    public PowerItemRecupEvent(Item item, ItemStack itemStack, ItemPower itemPower) {
        super();
        this.item = item;
        this.itemStack = itemStack;
        this.itemPower = itemPower;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
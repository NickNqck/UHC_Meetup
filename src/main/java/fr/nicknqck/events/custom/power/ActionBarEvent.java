package fr.nicknqck.events.custom.power;

import fr.nicknqck.utils.powers.ItemPower;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ActionBarEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();
    @Getter
    private final String key;
    @Getter
    @Setter
    private String value;
    @Getter
    private final ItemPower itemPower;
    @Getter
    private final boolean customText;
    private boolean cancelled = false;

    public ActionBarEvent(String key, String value, ItemPower itemPower, boolean customText) {
        this.key = key;
        this.value = value;
        this.itemPower = itemPower;
        this.customText = customText;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}

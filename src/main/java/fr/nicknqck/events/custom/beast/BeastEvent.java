package fr.nicknqck.events.custom.beast;

import fr.nicknqck.entity.krystalbeast.Beast;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BeastEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final Beast beast;

    public BeastEvent(Beast beast) {
        this.beast = beast;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}

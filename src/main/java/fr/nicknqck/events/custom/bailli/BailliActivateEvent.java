package fr.nicknqck.events.custom.bailli;

import fr.nicknqck.managers.crystaluhc.BailliManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BailliActivateEvent extends Event {

    private final BailliManager bailliManager;

    public BailliActivateEvent(BailliManager bailliManager) {
        this.bailliManager = bailliManager;
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

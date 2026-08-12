package fr.nicknqck.events.custom.biju;

import com.avaje.ebean.validation.NotNull;
import fr.nicknqck.entity.bijuv2.BijuBase;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class BijuEvent extends Event {

    @NotNull
    private final BijuBase biju;

    public BijuEvent(BijuBase biju) {
        this.biju = biju;
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

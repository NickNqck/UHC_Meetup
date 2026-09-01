package fr.nicknqck.events.custom.biju;

import fr.nicknqck.entity.bijuv2.BijuBase;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class BijuDeathEvent extends Event {

    private final Player killer;
    private final Location location;
    private final BijuBase biju;

    public BijuDeathEvent(BijuBase biju, Player killer, Location location) {
        this.biju = biju;
        System.out.println("called "+this);
        this.killer = killer;
        this.location = location;
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

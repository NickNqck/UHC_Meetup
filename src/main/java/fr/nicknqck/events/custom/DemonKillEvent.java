package fr.nicknqck.events.custom;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;
import java.util.UUID;

@Getter
public class DemonKillEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final List<UUID> demons;
    private final String killerName;
    public DemonKillEvent(@NonNull List<UUID> demons,@NonNull String killerName) {
        this.demons = demons;
        this.killerName = killerName;
    }


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}

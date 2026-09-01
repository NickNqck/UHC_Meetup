package fr.nicknqck.events.custom.death;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UHCDeathMessageEvent extends Event {

    private final List<String> deathMessages;
    private final Player victim;
    private final UUID killerUUID;
    private boolean sendToVictim = true;
    private boolean sendToEveryoneElse = true;
    private boolean sendToKiller = true;

    public UHCDeathMessageEvent(List<String> deathMessages, Player victim, UUID killerUUID) {
        this.deathMessages = deathMessages;
        this.victim = victim;
        this.killerUUID = killerUUID;
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

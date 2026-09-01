package fr.nicknqck.events.custom.power;

import fr.nicknqck.utils.powers.Cooldown;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class CooldownUpdateEvent extends Event {

    private final Cooldown cooldown;

    public CooldownUpdateEvent(Cooldown cooldown) {
        this.cooldown = cooldown;
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

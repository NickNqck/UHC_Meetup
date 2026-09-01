package fr.nicknqck.events.custom.power;

import com.avaje.ebean.validation.NotNull;
import fr.nicknqck.utils.powers.Cooldown;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class CooldownFinishEvent extends Event {

    @NotNull
    private final Cooldown cooldown;

    public CooldownFinishEvent(@NonNull final Cooldown cooldown) {
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
package fr.nicknqck.events.ns;

import fr.nicknqck.player.GamePlayer;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

@Getter
public final class GamePlayerBecomeHokageEvent extends Event {

    @NonNull
    private final GamePlayer hokage;

    public GamePlayerBecomeHokageEvent(@Nonnull GamePlayer hokage) {
        this.hokage = hokage;
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
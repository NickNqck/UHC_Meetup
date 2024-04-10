package fr.nicknqck.events.custom;

import fr.nicknqck.GameState;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;



@Getter
public class StartGameEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final GameState gameState;

    public StartGameEvent(GameState gameState) {
        this.gameState = gameState;
    }
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}

package fr.nicknqck.events.custom.time;

import fr.nicknqck.GameState;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class SecondPassEvent extends Event {

    private final GameState gameState;

    public SecondPassEvent(GameState gameState) {
        this.gameState = gameState;
    }

    public boolean isInGame() {
        return this.gameState.getServerState().equals(GameState.ServerStates.InGame);
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
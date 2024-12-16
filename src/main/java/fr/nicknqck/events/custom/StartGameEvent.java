package fr.nicknqck.events.custom;

import fr.nicknqck.GameState;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
public class StartGameEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final GameState gameState;
    private final List<UUID> inGamePlayers = new ArrayList<>();
    private final List<GameState.Roles> igRoles;

    public StartGameEvent(GameState gameState, List<GameState.Roles> rolesList) {
        this.gameState = gameState;
        this.igRoles = rolesList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

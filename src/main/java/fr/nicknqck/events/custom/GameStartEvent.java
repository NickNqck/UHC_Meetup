package fr.nicknqck.events.custom;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.Roles;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
public class GameStartEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final GameState gameState;
    private final List<UUID> inGamePlayers;
    private final List<Roles> igRoles;

    public GameStartEvent(GameState gameState, List<Roles> rolesList) {
        this.gameState = gameState;
        this.igRoles = rolesList;
        this.inGamePlayers = new ArrayList<>(gameState.getInGamePlayers());
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

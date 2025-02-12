package fr.nicknqck.events.custom.assassin;

import fr.nicknqck.GameState;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class ProcAssassinEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final GameState gameState;
    private final GamePlayer assassin;
    private final DemonsRoles role;

    public ProcAssassinEvent(GameState gameState, GamePlayer assassin, DemonsRoles role) {
        this.gameState = gameState;
        this.assassin = assassin;
        this.role = role;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}

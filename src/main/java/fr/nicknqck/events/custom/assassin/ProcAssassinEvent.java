package fr.nicknqck.events.custom.assassin;

import fr.nicknqck.GameState;
import fr.nicknqck.player.GamePlayer;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class ProcAssassinEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final GameState gameState;
    private final GamePlayer assassin;

    public ProcAssassinEvent(GameState gameState, GamePlayer assassin) {
        this.gameState = gameState;
        this.assassin = assassin;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}

package fr.nicknqck.events.custom.assassin;

import fr.nicknqck.GameState;
import fr.nicknqck.player.GamePlayer;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class ChooseAssassinEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final GamePlayer gamePlayer;
    private final GameState gameState;

    public ChooseAssassinEvent(GamePlayer gamePlayer, GameState gameState) {
        this.gamePlayer = gamePlayer;
        this.gameState = gameState;
    }


    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}

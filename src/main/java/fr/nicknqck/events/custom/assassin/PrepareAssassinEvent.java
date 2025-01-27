package fr.nicknqck.events.custom.assassin;

import fr.nicknqck.GameState;
import fr.nicknqck.player.GamePlayer;
import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PrepareAssassinEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final GameState gameState;
    private final List<GamePlayer> gamePlayers;
    private final boolean finish;
    private boolean cancel = false;

    public PrepareAssassinEvent(GameState gameState, List<GamePlayer> gamePlayers, boolean end) {
        this.gameState = gameState;
        this.gamePlayers = gamePlayers;
        this.finish = end;
    }


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }
}

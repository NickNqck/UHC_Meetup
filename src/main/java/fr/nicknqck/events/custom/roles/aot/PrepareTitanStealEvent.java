package fr.nicknqck.events.custom.roles.aot;

import fr.nicknqck.GameState;
import fr.nicknqck.player.GamePlayer;
import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class PrepareTitanStealEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();
    @Getter
    private final TitanForm titanForm;
    @Getter
    private final GameState gameState;
    @Getter
    private final List<GamePlayer> canSteal;
    @Getter
    private final GamePlayer gamePlayer;
    private boolean cancelled = false;

    public PrepareTitanStealEvent(TitanForm titanForm, GameState gameState, List<GamePlayer> canSteal, final GamePlayer gamePlayer) {
        this.titanForm = titanForm;
        this.gameState = gameState;
        this.canSteal = canSteal;
        this.gamePlayer = gamePlayer;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }


    public enum TitanForm {
        ASSAILLANT
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

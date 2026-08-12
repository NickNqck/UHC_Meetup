package fr.nicknqck.events.custom;

import fr.nicknqck.player.GamePlayer;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GamePlayerEatGappleEvent extends Event implements Cancellable {

    private boolean cancelled = false;
    @Getter
    private final GamePlayer gamePlayer;
    @Getter
    private final Player player;

    public GamePlayerEatGappleEvent(GamePlayer gamePlayer, Player player) {
        this.gamePlayer = gamePlayer;
        this.player = player;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
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

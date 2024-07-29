package fr.nicknqck.events.custom;

import fr.nicknqck.GameState;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class UHCDeathEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final GameState gameState;
    public UHCDeathEvent(Player player, GameState gameState) {
        this.player = player;
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

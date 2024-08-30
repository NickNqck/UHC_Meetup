package fr.nicknqck.events.custom.doublejump;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class JumpStartEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    @Getter
    private final Player player;
    @Getter
    @Setter
    private boolean enableDoubleJump = false;
    @Getter
    @Setter
    private double velocity = 2.0;
    @Getter
    @Setter
    private double velocityHight = 1.5;
    public JumpStartEvent(Player player) {
        this.player = player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
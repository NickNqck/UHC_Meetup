package fr.nicknqck.events.custom.doublejump;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class JumpEndEvent extends Event  {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final double velocity;
    private final double velocityHight;
    public JumpEndEvent(Player player, double velocity, double velocityHight) {
        this.player = player;
        this.velocity = velocity;
        this.velocityHight = velocityHight;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

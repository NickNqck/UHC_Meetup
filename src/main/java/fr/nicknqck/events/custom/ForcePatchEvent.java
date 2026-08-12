package fr.nicknqck.events.custom;

import fr.nicknqck.player.GamePlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class ForcePatchEvent extends Event implements Cancellable {

    private double damage;
    private double forcePercentToUse;
    private boolean cancelled = false;
    private final GamePlayer gameDamager;
    private final GamePlayer gameVictim;

    public ForcePatchEvent(double damage, double forcePercentToUse, GamePlayer gameDamager, GamePlayer gameVictim) {
        this.damage = damage;
        this.forcePercentToUse = forcePercentToUse;
        this.gameDamager = gameDamager;
        this.gameVictim = gameVictim;
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
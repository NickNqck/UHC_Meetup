package fr.nicknqck.events.custom.roles.aot;

import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.AotRoles;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class TitanAssailantStealEvent extends Event implements Cancellable {

    private final AotRoles role;
    private final GamePlayer gamePlayer;
    private final Player player;
    private boolean cancelled = false;

    public TitanAssailantStealEvent(AotRoles role, GamePlayer gamePlayer, Player player) {
        this.role = role;
        this.gamePlayer = gamePlayer;
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
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

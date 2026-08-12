package fr.nicknqck.events.custom.roles.aot;

import fr.nicknqck.roles.aot.builders.AotRoles;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PrepareStealCommandEvent extends Event implements Cancellable {

    @Getter
    private final Player player;
    @Getter
    private final AotRoles role;
    private boolean cancel = false;

    public PrepareStealCommandEvent(Player player, AotRoles role) {
        this.player = player;
        this.role = role;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
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

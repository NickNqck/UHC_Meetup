package fr.nicknqck.events.custom.roles.ns;

import fr.nicknqck.roles.ns.akatsuki.KisameV2;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SamehadaUseEvent extends Event implements Cancellable {

    private boolean cancelled = false;
    @Getter
    private final Player kisame;
    @Getter
    private final Player target;
    @Getter
    private final KisameV2 kisameRole;

    public SamehadaUseEvent(Player kisame, Player target, KisameV2 kisameV2) {
        this.kisame = kisame;
        this.target = target;
        this.kisameRole = kisameV2;
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

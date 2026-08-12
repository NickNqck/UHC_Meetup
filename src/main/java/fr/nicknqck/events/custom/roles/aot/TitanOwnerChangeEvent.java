package fr.nicknqck.events.custom.roles.aot;

import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.titans.TitanBase;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class TitanOwnerChangeEvent extends Event implements Cancellable {

    private boolean cancelled = false;
    @Getter
    private final UUID oldUUID;
    @Getter
    private final GamePlayer newGamePlayer;
    @Getter
    private final TitanBase titan;

    public TitanOwnerChangeEvent(@NonNull UUID oldUUID, @NonNull GamePlayer gamePlayer, @NonNull TitanBase titan) {
        super();
        this.oldUUID = oldUUID;
        this.newGamePlayer = gamePlayer;
        this.titan = titan;
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

package fr.nicknqck.events.custom.death;

import fr.nicknqck.player.GamePlayer;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

@Getter
public class UHCTimerDeathEvent extends Event implements Cancellable {

    @NonNull
    private final GamePlayer dieGamePlayer;
    @Nullable
    private final Entity entityKiller;
    @Nonnull
    private final UUID entityKillerUUID;
    private final int timeLeft;
    private boolean cancelled = false;

    public UHCTimerDeathEvent(@NonNull GamePlayer dieGamePlayer, @Nullable Entity entityKiller, @Nonnull UUID entityKillerUUID, int timeLeft) {
        this.dieGamePlayer = dieGamePlayer;
        this.entityKiller = entityKiller;
        this.entityKillerUUID = entityKillerUUID;
        this.timeLeft = timeLeft;
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
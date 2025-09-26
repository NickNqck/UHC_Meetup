package fr.nicknqck.events.custom.roles.aot;

import fr.nicknqck.events.custom.GameEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.titans.TitanBase;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.event.Cancellable;

import java.util.UUID;

public class TitanOwnerChangeEvent extends GameEvent implements Cancellable {

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
}

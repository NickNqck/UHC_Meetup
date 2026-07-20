package fr.nicknqck.events.ns;

import fr.nicknqck.events.custom.GameEvent;
import fr.nicknqck.player.GamePlayer;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.Nonnull;

@Getter
public final class GamePlayerBecomeHokageEvent extends GameEvent {

    @NonNull
    private final GamePlayer hokage;

    public GamePlayerBecomeHokageEvent(@Nonnull GamePlayer hokage) {
        this.hokage = hokage;
    }
}
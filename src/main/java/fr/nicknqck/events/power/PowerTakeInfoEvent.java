package fr.nicknqck.events.power;

import fr.nicknqck.enums.InfoType;
import fr.nicknqck.events.custom.GameEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.utils.powers.Power;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;

public class PowerTakeInfoEvent extends GameEvent implements Cancellable {

    @Getter
    private final Power power;
    @Getter
    @Setter
    private GamePlayer gameTarget;
    @Getter
    private final InfoType infoType;
    private boolean cancelled = false;

    public PowerTakeInfoEvent(Power power, GamePlayer gameTarget, InfoType infoType) {
        this.power = power;
        this.gameTarget = gameTarget;
        this.infoType = infoType;
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
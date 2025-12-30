package fr.nicknqck.events.custom.roles.krystal;

import fr.nicknqck.events.custom.GameEvent;
import fr.nicknqck.roles.krystal.KrystalBase;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;

public class KrystalGiveEvent extends GameEvent implements Cancellable {

    @Getter
    private final KrystalBase role;
    @Setter
    @Getter
    private int amount;
    private boolean cancel = false;

    public KrystalGiveEvent(KrystalBase role, int amount) {
        this.role = role;
        this.amount = amount;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }
}
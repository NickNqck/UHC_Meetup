package fr.nicknqck.events.custom.biju;

import fr.nicknqck.entity.bijuv2.BijuBase;
import lombok.NonNull;
import org.bukkit.event.Cancellable;

public class BijuCheckSpawnEvent extends BijuEvent implements Cancellable {

    private boolean cancel = false;

    public BijuCheckSpawnEvent(@NonNull BijuBase biju) {
        super(biju);
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }
}

package fr.nicknqck.events.custom.biju;

import com.avaje.ebean.validation.NotNull;
import fr.nicknqck.entity.bijuv2.BijuBase;
import fr.nicknqck.events.custom.GameEvent;
import lombok.Getter;

@Getter
public class BijuEvent extends GameEvent {

    @NotNull
    private final BijuBase biju;

    public BijuEvent(BijuBase biju) {
        this.biju = biju;
    }
}

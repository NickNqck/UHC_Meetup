package fr.nicknqck.events.custom.biju;

import fr.nicknqck.entity.bijuv2.BijuBase;
import lombok.Getter;

@Getter
public class BijuSpawnEvent extends BijuEvent {

    public BijuSpawnEvent(BijuBase biju) {
        super(biju);
    }
}
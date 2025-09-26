package fr.nicknqck.events.custom.biju;

import fr.nicknqck.entity.bijuv2.BijuBase;
import lombok.Getter;

@Getter
public class BijuInstantiateEvent extends BijuEvent {

    private final Integer spawnTime;

    public BijuInstantiateEvent(BijuBase biju, Integer spawnTime) {
        super(biju);
        this.spawnTime = spawnTime;
    }
}

package fr.nicknqck.events.custom.roles.aot;

import fr.nicknqck.events.custom.GameEvent;
import fr.nicknqck.titans.TitanBase;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class TitanTransformEvent extends GameEvent {

    private final TitanBase titan;
    private final boolean transforming;

    public TitanTransformEvent(@NonNull final TitanBase titan,final boolean transming) {
        super();
        this.titan = titan;
        this.transforming = transming;
    }
}
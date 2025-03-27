package fr.nicknqck.events.custom.roles.aot;

import fr.nicknqck.events.custom.GameEvent;
import fr.nicknqck.titans.TitanBase;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Getter
public class TitanTransformEvent extends GameEvent {

    private final TitanBase titan;
    private final boolean transforming;
    private final Player player;

    public TitanTransformEvent(@NonNull final TitanBase titan, final boolean transming, Player player) {
        super();
        this.titan = titan;
        this.transforming = transming;
        this.player = player;
    }
}
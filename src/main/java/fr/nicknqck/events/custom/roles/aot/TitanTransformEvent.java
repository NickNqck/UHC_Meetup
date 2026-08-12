package fr.nicknqck.events.custom.roles.aot;

import fr.nicknqck.titans.TitanBase;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class TitanTransformEvent extends Event {

    private final TitanBase titan;
    private final boolean transforming;
    private final Player player;

    public TitanTransformEvent(@NonNull final TitanBase titan, final boolean transming, Player player) {
        super();
        this.titan = titan;
        this.transforming = transming;
        this.player = player;
    }
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
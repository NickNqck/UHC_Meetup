package fr.nicknqck.events.custom.particle;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;
import java.util.UUID;

@Getter
public class ParticleBallGroundEvent extends Event {

    private final String ballName;
    private final UUID shooterUUID;
    private final Location impactLocation;
    @Nullable private final Player hitPlayer;

    /** Constructeur pour impact sur bloc */
    public ParticleBallGroundEvent(String ballName, UUID shooterUUID, Location impactLocation) {
        this(ballName, shooterUUID, impactLocation, null);
    }

    /** Constructeur pour impact sur joueur */
    public ParticleBallGroundEvent(String ballName, UUID shooterUUID, Location impactLocation, @Nullable Player hitPlayer) {
        this.ballName       = ballName;
        this.shooterUUID    = shooterUUID;
        this.impactLocation = impactLocation;
        this.hitPlayer      = hitPlayer;
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
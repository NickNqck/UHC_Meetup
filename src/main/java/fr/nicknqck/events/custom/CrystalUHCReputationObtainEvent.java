package fr.nicknqck.events.custom;

import fr.nicknqck.roles.crystal.CrystalBase;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class CrystalUHCReputationObtainEvent extends Event {

    private final CrystalBase crystalRole;
    private int reputation;

    public CrystalUHCReputationObtainEvent(CrystalBase crystalRole, int reputation) {
        this.crystalRole = crystalRole;
        this.reputation = reputation;
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
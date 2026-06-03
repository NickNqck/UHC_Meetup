package fr.nicknqck.events.custom;

import fr.nicknqck.roles.crystal.CrystalBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrystalUHCReputationObtainEvent extends GameEvent {

    private final CrystalBase crystalRole;
    private int reputation;

    public CrystalUHCReputationObtainEvent(CrystalBase crystalRole, int reputation) {
        this.crystalRole = crystalRole;
        this.reputation = reputation;
    }
}
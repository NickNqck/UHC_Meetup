package fr.nicknqck.roles.ds.builders;

import java.util.UUID;

public abstract class DemonsRoles extends DemonsSlayersRoles {
    public DemonsRoles(UUID player) {
        super(player);
    }
    public abstract DemonType getRank();
}
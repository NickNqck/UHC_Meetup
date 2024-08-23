package fr.nicknqck.roles.ds.builders;

import lombok.NonNull;

import java.util.UUID;

public abstract class DemonsRoles extends DemonsSlayersRoles {
    public DemonsRoles(UUID player) {
        super(player);
    }
    @NonNull
    public abstract DemonType getRank();
}
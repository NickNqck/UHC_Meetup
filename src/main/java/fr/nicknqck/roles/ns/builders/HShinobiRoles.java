package fr.nicknqck.roles.ns.builders;

import java.util.UUID;

public abstract class HShinobiRoles extends ShinobiRoles{

    public HShinobiRoles(UUID player) {
        super(player);
    }

    @Override
    public boolean isCanBeHokage() {
        return true;
    }
}
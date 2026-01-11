package fr.nicknqck.roles.ns.builders;

import java.util.UUID;

public abstract class ChiefAkatsukiRoles extends AkatsukiRoles implements IAkatsukiChief{

    public ChiefAkatsukiRoles(UUID player) {
        super(player);
    }
}

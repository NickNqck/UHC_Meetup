package fr.nicknqck.roles.mc.builders;

import fr.nicknqck.roles.builder.TeamList;

import java.util.UUID;

public abstract class NetherRoles extends UHCMcRoles {
    public NetherRoles(UUID player) {
        super(player);
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Nether;
    }
}


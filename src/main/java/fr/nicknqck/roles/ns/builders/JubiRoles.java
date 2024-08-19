package fr.nicknqck.roles.ns.builders;

import fr.nicknqck.roles.builder.TeamList;

import java.util.UUID;

public abstract class JubiRoles extends UchiwaRoles {
    public JubiRoles(UUID player) {
        super(player);
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Jubi;
    }
}

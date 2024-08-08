package fr.nicknqck.roles.aot.builders;

import fr.nicknqck.roles.builder.TeamList;

import java.util.UUID;

public abstract class MahrRoles extends AotRoles{
    public MahrRoles(UUID player) {
        super(player);
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Mahr;
    }
}

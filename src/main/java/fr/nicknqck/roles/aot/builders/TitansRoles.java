package fr.nicknqck.roles.aot.builders;

import fr.nicknqck.roles.builder.TeamList;

import java.util.UUID;

public abstract class TitansRoles extends AotRoles{
    public TitansRoles(UUID player) {
        super(player);
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Titan;
    }
}

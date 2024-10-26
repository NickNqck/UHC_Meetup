package fr.nicknqck.roles.ds.builders;

import fr.nicknqck.roles.builder.TeamList;

import java.util.UUID;

public abstract class SlayerRoles extends DemonsSlayersRoles{
    public SlayerRoles(UUID player) {
        super(player);
        setCanuseblade(true);
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Slayer;
    }
}

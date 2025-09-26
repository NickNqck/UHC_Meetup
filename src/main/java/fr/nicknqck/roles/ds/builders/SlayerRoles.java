package fr.nicknqck.roles.ds.builders;

import fr.nicknqck.roles.builder.TeamList;
import lombok.NonNull;

import java.util.UUID;

public abstract class SlayerRoles extends DemonsSlayersRoles{
    public SlayerRoles(UUID player) {
        super(player);
        setCanuseblade(true);
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Slayer;
    }
}

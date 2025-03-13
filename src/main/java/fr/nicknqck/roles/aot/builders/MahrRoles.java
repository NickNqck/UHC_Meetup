package fr.nicknqck.roles.aot.builders;

import fr.nicknqck.roles.builder.TeamList;
import lombok.NonNull;

import java.util.UUID;

public abstract class MahrRoles extends AotRoles{
    public MahrRoles(UUID player) {
        super(player);
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Mahr;
    }
}

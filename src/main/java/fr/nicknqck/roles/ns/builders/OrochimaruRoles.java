package fr.nicknqck.roles.ns.builders;

import fr.nicknqck.enums.TeamList;
import lombok.NonNull;

import java.util.UUID;

public abstract class OrochimaruRoles extends NSRoles{
    public OrochimaruRoles(UUID player) {
        super(player);
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Orochimaru;
    }
}

package fr.nicknqck.roles.ns.builders;

import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ns.Intelligence;
import lombok.NonNull;

import java.util.UUID;

public abstract class KumogakureRole extends NSRoles {

    public KumogakureRole(UUID player) {
        super(player);
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Kumogakure;
    }

    @Override
    public Intelligence getIntelligence() {
        return Intelligence.PEUINTELLIGENT;
    }
}
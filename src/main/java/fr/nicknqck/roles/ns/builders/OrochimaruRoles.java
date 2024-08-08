package fr.nicknqck.roles.ns.builders;

import fr.nicknqck.roles.builder.TeamList;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class OrochimaruRoles extends NSRoles{
    public OrochimaruRoles(UUID player) {
        super(player);
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Orochimaru;
    }
}

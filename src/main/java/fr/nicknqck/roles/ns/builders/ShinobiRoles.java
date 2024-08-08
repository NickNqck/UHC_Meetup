package fr.nicknqck.roles.ns.builders;

import fr.nicknqck.roles.builder.TeamList;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class ShinobiRoles extends NSRoles {
    public ShinobiRoles(UUID player) {
        super(player);
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Shinobi;
    }
}

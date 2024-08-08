package fr.nicknqck.roles.aot.builders;

import fr.nicknqck.roles.builder.TeamList;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class SoldatsRoles extends AotRoles{
    public SoldatsRoles(UUID player) {
        super(player);
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Soldat;
    }
}

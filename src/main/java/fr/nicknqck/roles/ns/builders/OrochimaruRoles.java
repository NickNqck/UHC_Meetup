package fr.nicknqck.roles.ns.builders;

import fr.nicknqck.roles.builder.TeamList;
import org.bukkit.entity.Player;

public abstract class OrochimaruRoles extends NSRoles{
    public OrochimaruRoles(Player player) {
        super(player);
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Orochimaru;
    }
}

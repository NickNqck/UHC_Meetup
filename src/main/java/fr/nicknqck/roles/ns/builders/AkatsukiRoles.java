package fr.nicknqck.roles.ns.builders;

import fr.nicknqck.roles.builder.TeamList;
import org.bukkit.entity.Player;

public abstract class AkatsukiRoles extends NSRoles{
    public AkatsukiRoles(Player player) {
        super(player);
    }

    @Override
    public TeamList getTeam() {
        return TeamList.Akatsuki;
    }
}

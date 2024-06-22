package fr.nicknqck.roles.ns.builders;

import fr.nicknqck.roles.builder.TeamList;
import org.bukkit.entity.Player;

public abstract class ShinobiRoles extends NSRoles {
    public ShinobiRoles(Player player) {
        super(player);
    }

    @Override
    public TeamList getTeam() {
        return TeamList.Shinobi;
    }
}

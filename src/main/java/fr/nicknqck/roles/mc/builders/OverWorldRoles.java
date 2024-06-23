package fr.nicknqck.roles.mc.builders;

import fr.nicknqck.roles.builder.TeamList;
import org.bukkit.entity.Player;

public abstract class OverWorldRoles extends UHCMcRoles {
    public OverWorldRoles(Player player) {
        super(player);
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.OverWorld;
    }
}

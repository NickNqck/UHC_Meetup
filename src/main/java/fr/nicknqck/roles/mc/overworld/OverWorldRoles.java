package fr.nicknqck.roles.mc.overworld;

import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import org.bukkit.entity.Player;

public abstract class OverWorldRoles extends RoleBase {
    public OverWorldRoles(Player player) {
        super(player);
    }

    @Override
    public TeamList getTeam() {
        return TeamList.OverWorld;
    }
}

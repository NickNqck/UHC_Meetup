package fr.nicknqck.roles.aot.builders;

import fr.nicknqck.roles.builder.TeamList;
import org.bukkit.entity.Player;

public abstract class MahrRoles extends AotRoles{
    public MahrRoles(Player player) {
        super(player);
    }

    @Override
    public TeamList getTeam() {
        return TeamList.Mahr;
    }
}

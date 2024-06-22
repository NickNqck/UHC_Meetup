package fr.nicknqck.roles.aot.builders;

import fr.nicknqck.roles.builder.TeamList;
import org.bukkit.entity.Player;

public abstract class TitansRoles extends AotRoles{
    public TitansRoles(Player player) {
        super(player);
    }

    @Override
    public TeamList getTeam() {
        return TeamList.Titan;
    }
}

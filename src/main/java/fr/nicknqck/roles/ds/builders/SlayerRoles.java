package fr.nicknqck.roles.ds.builders;

import fr.nicknqck.roles.builder.TeamList;
import org.bukkit.entity.Player;

public abstract class SlayerRoles extends DemonsSlayersRoles{
    public SlayerRoles(Player player) {
        super(player);
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Slayer;
    }
}

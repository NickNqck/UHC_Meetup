package fr.nicknqck.roles.builder;

import org.bukkit.entity.Player;

public abstract class SoloRoles extends RoleBase{
    public SoloRoles(Player player) {
        super(player);
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Solo;
    }
}

package fr.nicknqck.roles.builder;

import org.bukkit.entity.Player;

public abstract class SoloRoles extends RoleBase{
    public SoloRoles(Player player) {
        super(player);
    }

    @Override
    public TeamList getTeam() {
        return TeamList.Solo;
    }
}

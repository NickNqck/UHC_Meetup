package fr.nicknqck.roles.builder;

import org.bukkit.entity.Player;

public abstract class DemonsRoles extends RoleBase{
    public DemonsRoles(Player player) {
        super(player);
    }
    public abstract DemonType getRank();
}
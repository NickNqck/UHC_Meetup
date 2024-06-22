package fr.nicknqck.roles.ds.builders;

import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.DemonType;
import org.bukkit.entity.Player;

public abstract class DemonsRoles extends RoleBase {
    public DemonsRoles(Player player) {
        super(player);
    }
    public abstract DemonType getRank();
}
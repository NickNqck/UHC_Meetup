package fr.nicknqck.roles.ns.builders;

import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Intelligence;
import org.bukkit.entity.Player;

public abstract class NSRoles extends RoleBase {
    public NSRoles(Player player) {
        super(player);
    }
    public abstract Intelligence getIntelligence();
}

package fr.nicknqck.roles.builder;

import fr.nicknqck.roles.ns.Intelligence;
import org.bukkit.entity.Player;

public abstract class NSRoles extends RoleBase{
    public NSRoles(Player player) {
        super(player);
    }
    public abstract Intelligence getIntelligence();
}

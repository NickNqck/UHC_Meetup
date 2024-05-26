package fr.nicknqck.roles.builder;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.ns.Intelligence;
import org.bukkit.entity.Player;

public abstract class NSRoles extends RoleBase{
    public NSRoles(Player player, GameState.Roles roles) {
        super(player, roles);
    }
    public abstract Intelligence getIntelligence();
}

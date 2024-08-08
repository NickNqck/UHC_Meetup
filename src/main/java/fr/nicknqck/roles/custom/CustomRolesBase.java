package fr.nicknqck.roles.custom;

import fr.nicknqck.roles.builder.RoleBase;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class CustomRolesBase extends RoleBase {
    public CustomRolesBase(UUID player) {
        super(player);

    }

    public boolean onCustomCommand(String[] args, Player sender) {
        return false;
    }
}

package fr.nicknqck.roles.mc.builders;

import fr.nicknqck.roles.builder.RoleBase;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class UHCMcRoles extends RoleBase {
    public UHCMcRoles(UUID player) {
        super(player);
    }
    public void onMcCommand(String[] args) {}
}

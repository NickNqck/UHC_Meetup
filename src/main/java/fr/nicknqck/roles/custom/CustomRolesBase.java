package fr.nicknqck.roles.custom;

import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.RoleBase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class CustomRolesBase extends RoleBase {
    public CustomRolesBase(Player player) {
        super(player);
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            player.spigot().sendMessage(getComponent());
        }, 1);
    }

    public boolean onCustomCommand(String[] args, Player sender) {
        return false;
    }
}

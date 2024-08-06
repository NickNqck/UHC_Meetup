package fr.nicknqck.commands.roles;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.custom.CustomRolesBase;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CRolesCommands implements CommandExecutor {
    private final GameState gameState;
    public CRolesCommands(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            if (!gameState.hasRoleNull((Player) commandSender)) {
                Player sender = (Player) commandSender;
                if (gameState.getPlayerRoles().get(sender) instanceof CustomRolesBase) {
                    CustomRolesBase role = (CustomRolesBase) gameState.getPlayerRoles().get(sender);
                    if (role.getGamePlayer().isAlive()) {
                        return role.onCustomCommand(strings, sender);
                    }
                }
            }
        }
        return false;
    }
}

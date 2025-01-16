package fr.nicknqck.commands.roles;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.custom.CustomRolesBase;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Power;
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
            if (!gameState.hasRoleNull(((Player) commandSender).getUniqueId())) {
                Player sender = (Player) commandSender;
                if (gameState.getGamePlayer().get(sender.getUniqueId()).getRole() instanceof CustomRolesBase) {
                    CustomRolesBase role = (CustomRolesBase) gameState.getGamePlayer().get(sender.getUniqueId()).getRole();
                    if (role.getGamePlayer().isAlive()) {
                        if (!role.getPowers().isEmpty()) {
                            for (Power power : role.getPowers()) {
                                if (power instanceof CommandPower) {
                                    ((CommandPower) power).call(strings, CommandPower.CommandType.CUSTOM, sender);
                                }
                            }
                        }
                        return role.onCustomCommand(strings, sender);
                    }
                }
            }
        }
        return false;
    }
}

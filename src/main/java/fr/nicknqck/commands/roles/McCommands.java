package fr.nicknqck.commands.roles;

import fr.nicknqck.roles.mc.builders.UHCMcRoles;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Power;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.nicknqck.GameState;

public class McCommands implements CommandExecutor{
	final GameState gameState;
	public McCommands(final GameState gameState) {
		this.gameState = gameState;
	}

	@Override
	public boolean onCommand(CommandSender send, Command cmd, String arg, String[] args) {
		if (send instanceof Player) {
			Player sender = (Player) send;
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("roles")) {
					sender.sendMessage(gameState.getRolesList());
					return true;
				}
				if (!gameState.hasRoleNull(sender.getUniqueId())) {
					if (args[0].equalsIgnoreCase("me")) {
						gameState.sendDescription(sender);
                    } else {
						if (!(gameState.getGamePlayer().get(sender.getUniqueId()).getRole() instanceof UHCMcRoles))return false;
						UHCMcRoles role = (UHCMcRoles) gameState.getGamePlayer().get(sender.getUniqueId()).getRole();
						if (role.getGamePlayer().isAlive()) {
							if (!role.getPowers().isEmpty()) {
								for (Power power : role.getPowers()) {
									if (power instanceof CommandPower) {
										((CommandPower) power).call(args, CommandPower.CommandType.MC, sender);
									}
								}
							}
							((UHCMcRoles) gameState.getGamePlayer().get(sender.getUniqueId()).getRole()).onMcCommand(args);
						}
                    }
                    return true;
                }
			}
		}
		return true;
	}

}

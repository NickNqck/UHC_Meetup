package fr.nicknqck.commands.roles;

import fr.nicknqck.roles.mc.builders.UHCMcRoles;
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
				if (!gameState.hasRoleNull(sender)) {
					if (args[0].equalsIgnoreCase("me")) {
						gameState.sendDescription(sender);
                    } else {
						if (!(gameState.getPlayerRoles().get(sender) instanceof UHCMcRoles))return false;
						if (gameState.getPlayerRoles().get(sender).getGamePlayer().isAlive()) {
							((UHCMcRoles) gameState.getPlayerRoles().get(sender)).onMcCommand(args);
						}
                    }
                    return true;
                }
			}
		}
		return true;
	}

}
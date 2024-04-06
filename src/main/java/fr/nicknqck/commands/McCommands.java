package fr.nicknqck.commands;

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
						sender.sendMessage(gameState.getDescription(sender));
						return true;
					} else {
						gameState.getPlayerRoles().get(sender).onMcCommand(args);
						return true;
					}
				}
			}
		}
		return true;
	}

}

package fr.nicknqck.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.aot.titans.Titans;

public class AotCommands implements CommandExecutor {
GameState gameState;
	public AotCommands(GameState gameState) {
		this.gameState = gameState;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command label, String arg, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("roles")) {
                    sender.sendMessage(gameState.getRolesList());
                }
				if (gameState.getPlayerRoles().containsKey(player)) {
					if (args[0].equalsIgnoreCase("me") || args[0].equalsIgnoreCase("role")) {
						player.sendMessage(gameState.getDescription(player));
						return true;
					}else {
						if (args[0].equalsIgnoreCase("steal")) {
							for (Titans value : Titans.values()) {
								value.getTitan().onSteal(player, args);
							}
							return true;
						} else {
							if (args[0].equalsIgnoreCase("titan")) {
								for (Titans value : Titans.values()) {
								value.getTitan().onAotTitan(player, args);
							}
							return true;
							} else {
								for (Titans t : Titans.values()) {
								t.getTitan().onSubCommand(player, args);
								}
							gameState.getPlayerRoles().get(player).onAotCommands(arg, args, gameState);
							return true;
							}
						}
					}					
				}else {
				player.sendMessage("§cCommande Introuvable !");
				return true;
				}
			}
		}
		return false;
	}
}
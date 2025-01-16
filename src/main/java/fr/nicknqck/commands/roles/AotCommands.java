package fr.nicknqck.commands.roles;

import fr.nicknqck.roles.aot.builders.AotRoles;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Power;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.aot.builders.titans.Titans;

public class AotCommands implements CommandExecutor {
	private final GameState gameState;
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
				if (!gameState.hasRoleNull(player.getUniqueId())) {
					if (args[0].equalsIgnoreCase("me") || args[0].equalsIgnoreCase("role")) {
						gameState.sendDescription(player);
                    } else {
						if (args[0].equalsIgnoreCase("steal")) {
							for (Titans value : Titans.values()) {
								value.getTitan().onSteal(player, args);
							}
                        } else {
							if (args[0].equalsIgnoreCase("titan")) {
								for (Titans value : Titans.values()) {
									value.getTitan().onAotTitan(player, args);
									return true;
								}
                            } else {
								for (Titans t : Titans.values()) {
									t.getTitan().onSubCommand(player, args);
								}
								if (gameState.getGamePlayer().get(player.getUniqueId()).getRole() instanceof AotRoles) {
									AotRoles aotRoles = (AotRoles) gameState.getGamePlayer().get(player.getUniqueId()).getRole();
									if (aotRoles.getGamePlayer().isAlive()) {
										aotRoles.onAotCommands(arg, args, gameState);
										if (!aotRoles.getPowers().isEmpty()) {
											for (Power power : aotRoles.getPowers()) {
												if (power instanceof CommandPower) {
													((CommandPower) power).call(args, CommandPower.CommandType.AOT, player);
												}
											}
										}
									}
								}
                            }
                        }
                    }
                } else {
					player.sendMessage("§cCommande Introuvable !");
                }
                return true;
            }
		}
		return false;
	}
}
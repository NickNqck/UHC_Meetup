package fr.nicknqck.commands.roles;

import fr.nicknqck.Main;
import fr.nicknqck.roles.aot.builders.AotRoles;
import fr.nicknqck.roles.aot.builders.titans.StealCommand;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Power;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import fr.nicknqck.GameState;

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
						if (args[0].equalsIgnoreCase("info")) {
							if (Main.getInstance().getTitanManager().hasTitan(player.getUniqueId())) {
								player.sendMessage(Main.getInstance().getTitanManager().getDescriptions(player.getUniqueId()));
							}
						}
						if (args[0].equalsIgnoreCase("steal")) {
							if (!gameState.hasRoleNull(player.getUniqueId())) {
								// un seul /aot steal à la fois (il peut y avoir un StealCommand par titan mort à proximité)
								for (Power power : new ArrayList<>(gameState.getGamePlayer().get(player.getUniqueId()).getRole().getPowers())) {
									if (power instanceof StealCommand && power.getUse() < power.getMaxUse()) {
										((CommandPower) power).call(args, CommandPower.CommandType.AOT, player);
										break;
									}
								}
							}
                        } else {
							if (args[0].equalsIgnoreCase("titan")) {
								if (Main.getInstance().getTitanManager().hasTitan(player.getUniqueId())) {
									player.sendMessage(Main.getInstance().getTitanManager().getDescriptions(player.getUniqueId()));
									return true;
								}
                            } else {
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
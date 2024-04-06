package fr.nicknqck.commands.vanilla;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Gamemode implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command command, String arg2, String[] args) {
		if (s.isOp()) {
			if (args.length >= 1) {
				GameMode toChange = null;
				if (args[0].equalsIgnoreCase("spectator") || args[0].equalsIgnoreCase("sp") || args[0].equalsIgnoreCase("3")) {
					toChange = GameMode.SPECTATOR;
					if (args.length == 2) {
						Player target = Bukkit.getPlayer(args[1]);
						if (target != null) {
							target.setGameMode(toChange);
							notifyGameModeChange(s, target, toChange);
							return true;
						}
					} else {
						if (s instanceof Player) {
							Player target = (Player)s;
							target.setGameMode(toChange);
							notifyGameModeChange(s, target, toChange);
							return true;
						}
					}
				}
				if (args[0].equalsIgnoreCase("survival") || args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("0")) {
					toChange = GameMode.SURVIVAL;
					if (args.length == 2) {
						Player target = Bukkit.getPlayer(args[1]);
						if (target != null) {
							target.setGameMode(toChange);
							notifyGameModeChange(s, target, toChange);
							return true;
						}
					} else {
						if (s instanceof Player) {
							Player target = (Player)s;
							target.setGameMode(toChange);
							notifyGameModeChange(s, target, toChange);
							return true;
						}
					}
				}
				if (args[0].equalsIgnoreCase("creative") || args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("1")) {
					toChange = GameMode.CREATIVE;
					if (args.length == 2) {
						Player target = Bukkit.getPlayer(args[1]);
						if (target != null) {
							target.setGameMode(toChange);
							notifyGameModeChange(s, target, toChange);
							return true;
						}
					} else {
						if (s instanceof Player) {
							Player target = (Player)s;
							target.setGameMode(toChange);
							notifyGameModeChange(s, target, toChange);
							return true;
						}
					}
				}
				if (args[0].equalsIgnoreCase("adventure") || args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("2")) {
					toChange = GameMode.ADVENTURE;
					if (args.length == 2) {
						Player target = Bukkit.getPlayer(args[1]);
						if (target != null) {
							target.setGameMode(toChange);
							notifyGameModeChange(s, target, toChange);
							return true;
						}
					} else {
						if (s instanceof Player) {
							Player target = (Player)s;
							target.setGameMode(toChange);
							notifyGameModeChange(s, target, toChange);
							return true;
						}
					}
				}
			} else {
				s.sendMessage("§cUsage: /gamemode <mode> [joueur]");
				return true;
			}
		}
		return false;
	}
	private void notifyGameModeChange(CommandSender sender, Player changed, GameMode mode) {
		String goodName = "";
		if (mode == GameMode.ADVENTURE) {
			goodName = "§cAventure";
		} else if (mode == GameMode.CREATIVE) {
			goodName = "§cCreatif";
		} else if (mode == GameMode.SPECTATOR) {
			goodName = "§cSpectateur";
		} else {
			goodName = "§cSurvie";
		}
		System.out.println(changed.getName()+" is now at GameMode "+mode.name());
		changed.sendMessage("§7Vous êtes maintenant en "+goodName);
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.isOp() && p.getUniqueId() != changed.getUniqueId()) {
				if (sender instanceof Player) {
					p.sendMessage(sender.getName()+"§7 à définit le mode de jeu de§f "+changed.getName()+"§7 sur "+goodName);
				} else {
					p.sendMessage(changed.getName()+"§7 est maintenant en "+goodName);
				}
			}
		}
	}
}
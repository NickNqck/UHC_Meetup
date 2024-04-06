package fr.nicknqck.commands.vanilla;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import fr.nicknqck.GameState;

public class Whitelist implements CommandExecutor, Listener {

	private GameState gameState;
	public Whitelist(GameState s) {
		this.gameState = s;
	}
	@Override
	public boolean onCommand(CommandSender zzzz, Command label, String arg2, String[] args) {
		if (args.length >= 1) {
			if (zzzz instanceof Player) {
				Player sender = (Player)zzzz;
				if (!isGoodPlayer(sender)) {
					sender.sendMessage("§cIl faut être§l HOST§c pour whitelister un joueur");
					return true;
				}
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("on")) {
						if (!wlActivated) {
							wlActivated = true;
							sender.sendMessage("§7La whitelist est maintenant§a activée");
						} else {
							sender.sendMessage("§7§cLa whitelist est déjà activé");
						}
						return true;
					}
					if (args[0].equalsIgnoreCase("off")) {
						if (wlActivated) {
							wlActivated = false;
							sender.sendMessage("§7La whitelist est maintenant§c désactivée");
						} else {
							sender.sendMessage("§cLa whitelist est déjà désactivée");
						}
						return true;
					}
					if (args[0].equalsIgnoreCase("reset")) {
						Whitelisted.clear();
						sender.sendMessage("§7La whitelist à bien été réinitialisé");
						return true;
					}
					if (args[0].equalsIgnoreCase("force")) {
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (!Whitelisted.contains(p.getUniqueId())) {
								p.kickPlayer("§cVous n'êtes pas whitelist !");
							}
						}
						sender.sendMessage("§7Toute les personnes non whitelist on été éjécté.");
						return true;
					}
				}
				if (args.length == 2) {
					Player target = Bukkit.getPlayer(args[1]);
					if (target != null) {
						if (args[0].equalsIgnoreCase("add")) {
							if (!Whitelisted.contains(target.getUniqueId())) {
								sender.sendMessage("§c"+target.getDisplayName()+"§7 à été ajoutée à la whitelist");
								Whitelisted.add(target.getUniqueId());
								return true;
							}
						}
						if (args[0].equalsIgnoreCase("remove")) {
							if (Whitelisted.contains(target.getUniqueId())) {
								sender.sendMessage("§c"+target.getDisplayName()+"§7 à été retirer de la whitelist");
								Whitelisted.remove(target.getUniqueId());
								return true;
							}
						}
					}
				}
			}
		}
		zzzz.sendMessage(new String[] {
				"§7Voici les commandes utilisable:",
				"",
				"§7 -§6 /wl on§7: Permet d'activer la whitelist.",
				"",
				"§7 -§6 /wl off§7: Permet de désactiver la whitelist.",
				"",
				"§7 -§6 /wl add <joueur>§7: Permet d'ajouter un joueur à la whitelist.",
				"",
				"§7 -§6 /wl remove <joueur>§7: Permet de retirer un joueur de la whitelist.",
				"",
				"§7 -§6 /wl reset§7: Permet de réinitialisé la whitelist.",
				"",
				"§7 -§6 /wl force§7: Permet d'éjecté du serveur tout joueur n'étant pas dans la whitelist."
		});
		return true;
	}
	private boolean isGoodPlayer(Player p) {
		if (p.isOp() || gameState.getHost().contains(p)) {
			return true;
		}
		return false;
	}
	private List<UUID> Whitelisted = new ArrayList<>();
	public boolean wlActivated = false;
	@EventHandler
	public void onJoin(AsyncPlayerPreLoginEvent e) {
	//	Player player = Bukkit.getPlayer(e.getUniqueId());
		if (e.getName().contains(" ")) {
			e.setLoginResult(Result.KICK_BANNED);
			e.setKickMessage("§cPseudo incorect désolé !");
		}
		if (!Whitelisted.contains(e.getUniqueId()) && wlActivated && !isGoodPlayer(Bukkit.getPlayer(e.getUniqueId()))) {
			e.setLoginResult(Result.KICK_WHITELIST);
			e.setKickMessage("§cVous n'êtes pas dans la whitelist !");
		}
	}
}
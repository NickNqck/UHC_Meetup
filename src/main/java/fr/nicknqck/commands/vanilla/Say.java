package fr.nicknqck.commands.vanilla;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.nicknqck.GameState;
import fr.nicknqck.utils.CC;

public class Say implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0;i<args.length;i++) {
			sb.append(" ");
			sb.append(args[i]);
		}
		if (s instanceof Player) {
			Player sender = (Player)s;
			if (sender.isOp()) {
				Bukkit.broadcastMessage(" ");
				Bukkit.broadcastMessage("[§cAdmin§f]§c "+sender.getName()+"§f:"+CC.translate(sb.toString()));
				return true;
			}
			if (GameState.getInstance().getHost().contains(sender)) {
				Bukkit.broadcastMessage(" ");
				Bukkit.broadcastMessage("[§cHost§f]§c "+sender.getName()+"§f:"+CC.translate(sb.toString()));
				return true;
			}
		} else {
			Bukkit.broadcastMessage("[CONSOLE] :"+CC.translate(sb.toString()));
			return true;
		}
		return false;
	}
}
package fr.nicknqck.commands.vanilla;

import fr.nicknqck.utils.rank.ChatRank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Say implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(" ");
            sb.append(arg);
        }
		if (s instanceof Player) {
			Player sender = (Player)s;
			Bukkit.broadcastMessage(" ");
			Bukkit.broadcastMessage(ChatRank.getPlayerGrade(sender).getFullPrefix()+sender.getName()+"§f:"+ ChatColor.translateAlternateColorCodes('&', sb.toString()));
		} else {
			Bukkit.broadcastMessage("[CONSOLE] :"+ChatColor.translateAlternateColorCodes('&', sb.toString()));
			return true;
		}
		return false;
	}
}
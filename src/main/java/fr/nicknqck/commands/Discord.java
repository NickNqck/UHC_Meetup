package fr.nicknqck.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Discord implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		ArrayList<String> message = new ArrayList<String>();
        message.add(ChatColor.GOLD + "Voici le discord:");
        message.add(ChatColor.GOLD + "Discord du mdj: " + ChatColor.GRAY + "https://discord.gg/RF3D4Du8VN");
        sender.sendMessage(message.toArray(new String[message.size()]));
        return true;
	}

}

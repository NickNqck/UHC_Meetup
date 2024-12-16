package fr.nicknqck.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Discord implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		sender.sendMessage(new String[] {
                "§6Voici le discord:",
                "§6Discord du mdj: §7https://discord.gg/6dWxCAEsfF"
        });
        return true;
	}

}

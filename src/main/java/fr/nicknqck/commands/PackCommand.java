package fr.nicknqck.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PackCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage(new String[]{
                "",
                "§7Voici le lien du pack de son:",
                "§bhttps://www.clictune.com/ktNr",
                ""
        });
        return true;
    }

}
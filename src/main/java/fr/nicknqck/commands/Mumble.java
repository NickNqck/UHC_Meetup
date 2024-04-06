package fr.nicknqck.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.nicknqck.Main;

public class Mumble implements CommandExecutor {

	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
            if(cmd.getName().equalsIgnoreCase("mumble")) {
                if (args.length == 0) { 
                    sender.sendMessage("§8§m---------------------------------------§r\n \n §7Mumble: §r" + Main.getInstance().getConfig().getString("mumble") + " // " + Main.getInstance().getConfig().getString("port") + "\n \n§8§m---------------------------------------");
                    return true;
                }
                if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
                    Main.getInstance().getConfig().set("mumble", args[1]);
                    Main.getInstance().getConfig().set("port", args[2]);
                    Main.getInstance().saveConfig();
                    sender.sendMessage("Mumble définit avec succès !");
                    return true;
                }
                else {
                    sender.sendMessage("§cErreur, esssayez /mumble set <mumble> <port>");
                    return false;
                }
            }
        return false;
    }
}
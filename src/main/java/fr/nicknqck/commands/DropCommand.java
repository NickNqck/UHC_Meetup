package fr.nicknqck.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.nicknqck.GameListener;

public class DropCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String osef, String[] args) {
		if (sender instanceof Player) {
			if (((Player) sender).getItemInHand() != null) {
				if (!((Player) sender).getItemInHand().getType().equals(Material.AIR)) {
					GameListener.dropItem(((Player) sender).getLocation(), ((Player) sender).getItemInHand());
					((Player) sender).getInventory().remove(((Player) sender).getItemInHand());
				}else {
					sender.sendMessage("Il faut avoir un item en main !");
				}
			}else {
				sender.sendMessage("Il faut avoir un item en main !");
			}
		}else {
			System.out.println("Il faut etre un joueur pour drop un item");
		}
		return true;
	}

}

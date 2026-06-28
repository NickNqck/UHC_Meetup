package fr.nicknqck.commands;

import fr.nicknqck.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemStack;

public class DropCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String osef, String[] args) {
		if (sender instanceof Player) {
			if (((Player) sender).getItemInHand() != null) {
				if (!((Player) sender).getItemInHand().getType().equals(Material.AIR)) {
					final Location loc =  ((Player) sender).getLocation();
					final ItemStack item = ((Player) sender).getItemInHand();
					loc.getWorld().dropItem(loc.clone().add(0.5D, 0.3D, 0.5D), item);
					Main.getInstance().debug("dropped item at x"+loc.getX()+" z"+loc.getZ()+" the item "+item.getType().name()+" x"+item.getAmount());
					((Player) sender).setItemInHand(null);
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

package fr.nicknqck.scenarios.impl;

import fr.nicknqck.scenarios.BasicScenarios;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class AntiDrop extends BasicScenarios {

	private static boolean AntiDrop = true;

	@Override
	public String getName() {
		return "§r§fAnti Drop";
	}

	@Override
	public ItemStack getAffichedItem() {
		return new ItemBuilder(Material.HOPPER).setName(getName()).setLore("§fl'"+getName()+"§f est actuellement: "+ (AntiDrop ? "§aActivé" : "§cDésactivé")).toItemStack();
	}
	@Override
	public void onClick(Player player) {
		if (getAntiDrop()) {
			setAntiDrop(false);
			player.sendMessage(drop() +ChatColor.RED+"Désactivation de l'Anti-Drop");
		} else {
			setAntiDrop(true);
			player.sendMessage(drop()+ChatColor.GREEN+"Activation de l'Anti-Drop");
		}
	}

	public static void setAntiDrop(final boolean b){
		AntiDrop = b;
	}

	public static boolean getAntiDrop() {
		return AntiDrop;
	}
	
	public static String drop() {
        return ChatColor.GOLD+"[ANTI-DROP] "+ChatColor.RESET;
	}
}
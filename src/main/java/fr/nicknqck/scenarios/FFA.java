package fr.nicknqck.scenarios;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FFA {

	
	private static boolean FFA = false;
	
	public static void setFFA(boolean FFAt) {
		FFA = FFAt;
	}
	public static boolean getFFA() {
		return FFA;
	}
	public static String ffa() {
		String a = ChatColor.GOLD+"[FFA] "+ChatColor.RESET;
		return a;
	}
	
	public static ItemStack getFFAButton() {
		ItemStack stack = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14); // Red
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Activer le mode FFA");
		meta.setLore(Arrays.asList(ChatColor.DARK_PURPLE+"Active le mode FFA"));
		stack.setItemMeta(meta);
		return stack;
	}

	public static ItemStack getnotFFAButton() {
		ItemStack stack = new ItemStack(Material.STAINED_CLAY, 1, (byte) 5); // Green
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Désactiver le mode FFA");
		meta.setLore(Arrays.asList(ChatColor.DARK_PURPLE+"Désactive le mode FFA"));
		stack.setItemMeta(meta);
		return stack;
	}
}
package fr.nicknqck.scenarios;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AntiDrop {
	
	private static boolean AntiDrop = true;
	
	public static void setAntiDrop(boolean AntiDropt) {
		AntiDrop = AntiDropt;
	}
	public static boolean getAntiDrop() {
		return AntiDrop;
	}
	
	public static String drop() {
		String a = ChatColor.GOLD+"[ANTI-DROP] "+ChatColor.RESET;
		return a;
	}

	public static ItemStack getAntiDropButton() {
		ItemStack stack = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14); // Red
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Activer l'Anti-Drop");
		meta.setLore(Arrays.asList(ChatColor.DARK_PURPLE+"Active l'Anti-Drop"));
		stack.setItemMeta(meta);
		return stack;
	}

	public static ItemStack getnotAntiDropButton() {
		ItemStack stack = new ItemStack(Material.STAINED_CLAY, 1, (byte) 5); // Green
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Désactiver l'Anti-Drop");
		meta.setLore(Arrays.asList(ChatColor.DARK_PURPLE+"Désactive l'Anti-Drop"));
		stack.setItemMeta(meta);
		return stack;
	}

}

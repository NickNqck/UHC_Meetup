package fr.nicknqck.scenarios;

import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class AntiDrop extends BasicScenarios{

	private static boolean AntiDrop = true;

	@Override
	public String getName() {
		return "Anti-Drop";
	}
	public static void setAntiDrop(boolean b){
		AntiDrop = b;
	}

	public static boolean getAntiDrop() {
		return AntiDrop;
	}
	
	public static String drop() {
        return ChatColor.GOLD+"[ANTI-DROP] "+ChatColor.RESET;
	}

	public static ItemStack getAntiDropButton() {
		ItemStack stack = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14); // Red
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Activer l'Anti-Drop");
		meta.setLore(List.of(ChatColor.DARK_PURPLE + "Active l'Anti-Drop"));
		stack.setItemMeta(meta);
		return stack;
	}

	public static ItemStack getnotAntiDropButton() {
		ItemStack stack = new ItemStack(Material.STAINED_CLAY, 1, (byte) 5); // Green
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Désactiver l'Anti-Drop");
		meta.setLore(List.of(ChatColor.DARK_PURPLE + "Désactive l'Anti-Drop"));
		stack.setItemMeta(meta);
		return stack;
	}

}

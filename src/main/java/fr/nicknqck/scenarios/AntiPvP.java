package fr.nicknqck.scenarios;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AntiPvP {

	private static boolean antipvplobby = true;
	
	public static void setantipvplobby(boolean anti) {
		antipvplobby = anti;
	}
	public static boolean isantipvplobby() {
		return antipvplobby;
	}
	public static ItemStack getlobbypvp() {
		ItemStack stack = new ItemStack(Material.STAINED_CLAY, 1, (byte) 5);
		ItemMeta meta = stack.getItemMeta();
		meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setDisplayName(ChatColor.DARK_PURPLE+"Anti-PvP (Lobby)");
		meta.setLore(Arrays.asList(ChatColor.GOLD+"Désactiver"));
		stack.setItemMeta(meta);
		return stack;
	}

	public static ItemStack getnotlobbypvp() {
		ItemStack stack = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_PURPLE+"Anti-PvP (Lobby)");
		meta.setLore(Arrays.asList(ChatColor.GOLD+"Activer"));
		stack.setItemMeta(meta);
		return stack;
	}
}
package fr.nicknqck.scenarios;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Hastey_Babys  {

	private static boolean HasteyBabys = false;
	
	public static void setHasteyBabys(boolean hastey) {
		HasteyBabys = hastey;
	}
	
	public static boolean isHasteyBabys() {
		return HasteyBabys;
	}
	
	public static String HasteyBabys() {
		String a = ChatColor.GOLD+"[Hastey-Babys] "+ChatColor.RESET;
		return a;
	}
	
	 public static ItemStack getHasteyBabys() {
		  ItemStack stack = new ItemStack(Material.WOOD_PICKAXE, 1);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setLore(Arrays.asList(ChatColor.GOLD+"Activer"));
		  meta.addEnchant(Enchantment.DEPTH_STRIDER, 0, false);
		  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		  meta.setDisplayName(ChatColor.DARK_PURPLE+"Hastey Babys: ");
		  stack.setItemMeta(meta);
		  return stack;
	  }
	 
	 public static ItemStack getnotHasteyBabys() {
		  ItemStack stack = new ItemStack(Material.WOOD_PICKAXE, 1);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setLore(Arrays.asList(ChatColor.GOLD+"Désactiver"));
		  meta.setDisplayName(ChatColor.DARK_PURPLE+"Hastey Babys: ");
		  stack.setItemMeta(meta);
		  return stack;
	  }
	
}
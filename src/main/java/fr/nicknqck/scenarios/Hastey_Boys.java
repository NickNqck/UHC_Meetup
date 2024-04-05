package fr.nicknqck.scenarios;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Hastey_Boys{
	

	public static boolean HasteyBoys = false;
	public static void setHasteyBoys(boolean hasteyboys) {
		HasteyBoys = hasteyboys;
	}
	public static boolean isHasteyBoys() {
		return HasteyBoys;
	}
	
	 public static ItemStack getHasteyBoys() {
		  ItemStack stack = new ItemStack(Material.GOLD_PICKAXE, 1);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setLore(Arrays.asList(ChatColor.GOLD+"Activer"));
		  meta.addEnchant(Enchantment.DEPTH_STRIDER, 0, false);
		  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		  meta.setDisplayName(ChatColor.DARK_PURPLE+"Hastey Boys: ");
		  stack.setItemMeta(meta);
		  return stack;
	  }
	  public static ItemStack getnotHasteyBoys() {
		  ItemStack stack = new ItemStack(Material.GOLD_PICKAXE, 1);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setLore(Arrays.asList(ChatColor.GOLD+"Désactiver"));
		  meta.setDisplayName(ChatColor.DARK_PURPLE+"Hastey Boys: ");
		  stack.setItemMeta(meta);
		  return stack;
	  }
	  public static String hasteyboy() {
			String a = ChatColor.GOLD+"[HASTEY-BOYS] "+ChatColor.RESET;
			return a;
		}
}
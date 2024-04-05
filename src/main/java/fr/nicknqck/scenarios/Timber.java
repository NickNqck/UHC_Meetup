package fr.nicknqck.scenarios;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Timber {


	private static boolean timber = false;

	public static void setTimber(boolean t) {
		timber = t;
	}
	public static boolean isTimber() {
		return timber;
	}
	public static ItemStack itemON() {
		  ItemStack stack = new ItemStack(Material.LOG, 1);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setLore(Arrays.asList(ChatColor.GOLD+"Activer"));
		  meta.addEnchant(Enchantment.DEPTH_STRIDER, 0, false);
		  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		  meta.setDisplayName(ChatColor.DARK_PURPLE+"Timber: ");
		  stack.setItemMeta(meta);
		  return stack;
	  }
	  public static ItemStack itemOFF() {
		  ItemStack stack = new ItemStack(Material.LOG, 1);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setLore(Arrays.asList(ChatColor.GOLD+"Désactiver"));
		  meta.setDisplayName(ChatColor.DARK_PURPLE+"Timber: ");
		  stack.setItemMeta(meta);
		  return stack;
	  }
	  public static String timber() {
		 String a = ChatColor.GOLD+"[TIMBER] "+ChatColor.RESET;
		 return a;
	  }
	
}
package fr.nicknqck.scenarios;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import fr.nicknqck.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DiamondLimit extends BasicScenarios{
	
	private static int maxdiams = 22;
	private static int diamsmined = 0;
	private static boolean alimit = false;
	
	public static void setLimit(boolean a) {
		alimit = a;
	}
	
	public static boolean isLimit() {
		return alimit;
	}
	
	public static void setMaxDiams(int maxd) {
		maxdiams = maxd;
	}
	public static void setdiamsmined(int d) {
		diamsmined = d;
	}
	public static int getdiamsmined() {
		return diamsmined;
	}
	public static int getmaxdiams() {
		return maxdiams;
	}

	@Override
	public String getName() {
		return "§r§fDiamond Limit";
	}

	@Override
	public ItemStack getAffichedItem() {
		return new ItemBuilder(Material.DIAMOND_BLOCK).setLore("§fLa "+getName()+" est actuellement: "+(alimit ? "§aActivé" : "§cDésactivé")).toItemStack();
	}

	@Override
	public void onClick(Player player) {

	}

	public static ItemStack limitON() {
		  ItemStack stack = new ItemStack(Material.DIAMOND_BLOCK, 1);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setLore(Collections.singletonList(ChatColor.GOLD + "Activer"));
		  meta.addEnchant(Enchantment.DEPTH_STRIDER, 0, false);
		  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		  meta.setDisplayName(ChatColor.DARK_PURPLE+"Diamond Limite: ");
		  stack.setItemMeta(meta);
		  return stack;
	  }
	 
	 public static ItemStack limitOFF() {
		  ItemStack stack = new ItemStack(Material.DIAMOND_BLOCK, 1);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setLore(Collections.singletonList(ChatColor.GOLD + "Désactiver"));
		  meta.setDisplayName(ChatColor.DARK_PURPLE+"Diamond Limite: ");
		  stack.setItemMeta(meta);
		  return stack;
	  }	 
	 public static ItemStack ChangeDiamond() {
		  ItemStack stack = new ItemStack(Material.DIAMOND, maxdiams);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setLore(Arrays.asList(ChatColor.GOLD+"Click to Change",
				ChatColor.GOLD+"Minimum: "+ChatColor.DARK_PURPLE+"1",
				  ChatColor.GOLD+"Maximum: "+ChatColor.DARK_PURPLE+"32"));
		  meta.addEnchant(Enchantment.DEPTH_STRIDER, 0, false);
		  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		  meta.setDisplayName(ChatColor.DARK_PURPLE+"Limite de diamant: ");
		  stack.setItemMeta(meta);
		  return stack;
	  }
	 
	 public static String DM() {
			String a = ChatColor.GOLD+"[Diamond-Limite] "+ChatColor.RESET;
			return a;
		}
}
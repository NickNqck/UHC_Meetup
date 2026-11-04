package fr.nicknqck.scenarios.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import fr.nicknqck.scenarios.BasicScenarios;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DiamondLimit extends BasicScenarios {
	
	private static int maxdiams = 22;
	private static final Map<UUID, Integer> diamsMined = new HashMap<>();
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
	public static int getDiamsMined(final UUID uuid) {
		return diamsMined.getOrDefault(uuid, 0);
	}
	public static void addDiamMined(final UUID uuid) {
		diamsMined.put(uuid, getDiamsMined(uuid) + 1);
	}
	public static void resetDiamsMined() {
		diamsMined.clear();
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
		return new ItemBuilder(Material.DIAMOND_BLOCK).setName(getName()).setLore("§fLa "+getName()+" est actuellement: "+(alimit ? "§aActivé" : "§cDésactivé"),"§r§fLe nombre maximal de diamant minable est:§b"+getmaxdiams()).toItemStack();
	}

	@Override
	public void onClick(Player player) {
		if (isClickDroit()){
			DiamondLimit.setLimit(false);
			player.sendMessage(DiamondLimit.DM()+"Désactivation de la Diamond Limite");
		}
		if (isClickGauche()){
			DiamondLimit.setLimit(true);
			player.sendMessage(DiamondLimit.DM()+"Activation de la Diamond Limite");
		}
		if (isShiftClick() && maxdiams < 64){
			maxdiams++;
		}
		if (isDropClick() && maxdiams > 0){
			maxdiams--;
		}
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
package fr.nicknqck.scenarios;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nicknqck.GameState;
import fr.nicknqck.blocks.BlockManager;

public class CutClean {

	
	/*Le reste est dans 
	 * fr.nicknqck.mtpds.blocks.BlockManager.CutClean
	 * 
	 */
	GameState gameState;
	public CutClean(GameState gameState) {this.gameState = gameState;}
	private static boolean CutClean = false;
	
	public static boolean isCutClean() {return CutClean;}
	public static void setCutClean(boolean CutCleant) {CutClean = CutCleant;}
	 public static ItemStack getCutClean() {
		  ItemStack stack = new ItemStack(Material.IRON_PICKAXE, 1);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setLore(Arrays.asList(ChatColor.GOLD+"Activer"));
		  meta.addEnchant(Enchantment.DEPTH_STRIDER, 0, false);
		  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		  meta.setDisplayName(ChatColor.DARK_PURPLE+"CutClean: ");
		  stack.setItemMeta(meta);
		  return stack;
	  }
	  public static ItemStack getnotCutClean() {
		  ItemStack stack = new ItemStack(Material.IRON_PICKAXE, 1);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setLore(Arrays.asList(ChatColor.GOLD+"Désactiver"));
		  meta.setDisplayName(ChatColor.DARK_PURPLE+"CutClean: ");
		  stack.setItemMeta(meta);
		  return stack;
	  }
	  public static ItemStack getXpCharbon(GameState gameState) {
		  ItemStack stack = new ItemStack(Material.COAL, 1);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setDisplayName("Point d'xp en plus pour le §6charbon");
		  meta.setLore(Arrays.asList(""+gameState.xpcharbon,
				  "De base: §6"+BlockManager.xpcharbon));
		  meta.addEnchant(Enchantment.DEPTH_STRIDER, 0, false);
		  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		  stack.setItemMeta(meta);
		  return stack;
	  }
	  public static ItemStack getXpFer(GameState gameState) {
		  ItemStack stack = new ItemStack(Material.IRON_INGOT, 1);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setDisplayName("Point d'xp en plus pour le §6fer");
		  meta.setLore(Arrays.asList(""+gameState.xpfer,
				  "De base: §6"+BlockManager.xpfer));
		  meta.addEnchant(Enchantment.DEPTH_STRIDER, 0, false);
		  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		  stack.setItemMeta(meta);
		  return stack;
	  }
	 public static ItemStack getXpOr(GameState gameState) {
		  ItemStack stack = new ItemStack(Material.GOLD_INGOT, 1);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setDisplayName("Point d'xp en plus pour le §6or");
		  meta.setLore(Arrays.asList(""+gameState.xpor,
				  "De base: §6"+BlockManager.xpor));
		  meta.addEnchant(Enchantment.DEPTH_STRIDER, 0, false);
		  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		  stack.setItemMeta(meta);
		  return stack;
	  }
	 public static ItemStack getXpDiams(GameState gameState) {
		  ItemStack stack = new ItemStack(Material.DIAMOND, 1);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setDisplayName("Point d'xp en plus pour le §6Diamant");
		  meta.setLore(Arrays.asList(""+gameState.xpdiams,
				  "De base: "+BlockManager.xpdiams));
		  meta.addEnchant(Enchantment.DEPTH_STRIDER, 0, false);
		  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		  stack.setItemMeta(meta);
		  return stack;
	  }
	  public static String cutclean() {
			String a = ChatColor.GOLD+"[CutClean] "+ChatColor.RESET;
			return a;
		}
}
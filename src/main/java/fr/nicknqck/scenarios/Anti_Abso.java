package fr.nicknqck.scenarios;

import java.util.Arrays;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Anti_Abso {
	/* Le reste de du code est dans
	 * fr.nicknqck.mtpds.ItemsManager
	 * précisément le OnItemConsumed
	 */
<<<<<<< src/main/java/fr/nicknqck/scenarios/Anti_Abso.java
	//TEST
=======
	@Getter
	@Setter
	private static boolean antiabsoall = false;
	@Getter
	@Setter
	private static boolean antiabsoinvi = true;
	@Getter
	@Setter
	private static boolean antiabsooff = false;
	public static boolean isAntiAbsoOff() {
		return antiabsooff;
	}
	
	
	public static String abso() {
		String abso = ChatColor.GOLD+"[HIDE-ABSO] "+ChatColor.RESET;
		return abso;
	}
	  public static ItemStack getAbsoAll() {
		  ItemStack stack = new ItemStack(Material.GOLDEN_APPLE, 1);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setLore(Arrays.asList(ChatColor.DARK_PURPLE+"Caché pour ",
				  ChatColor.GOLD+"Tout les joueurs"));
		  meta.setDisplayName(ChatColor.DARK_PURPLE+"Particule d'Abso: ");
		  stack.setItemMeta(meta);
		  return stack;
	  }
	  public static ItemStack getAbsoInvisible() {
		  ItemStack stack = new ItemStack(Material.GOLDEN_APPLE, 1);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setLore(Arrays.asList(ChatColor.DARK_PURPLE+"Caché pour ",
				  ChatColor.GOLD+"Les invisibles"));
		  meta.setDisplayName(ChatColor.DARK_PURPLE+"Particule d'Abso: ");
		  stack.setItemMeta(meta);
		  return stack;
	  }
	  public static ItemStack getAbsoOff() {
		  ItemStack stack = new ItemStack(Material.GOLDEN_APPLE, 1);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setLore(Arrays.asList(ChatColor.DARK_PURPLE+"Caché pour ",
				  ChatColor.GOLD+"Personne"));
		  meta.setDisplayName(ChatColor.DARK_PURPLE+"Particule d'Abso: ");
		  stack.setItemMeta(meta);
		  return stack;
	  }

}

package fr.nicknqck.scenarios;

import java.util.Arrays;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.persistence.Basic;

public class Anti_Abso extends BasicScenarios {
	/* Le reste de du code est dans
	 * fr.nicknqck.mtpds.ItemsManager
	 * précisément le OnItemConsumed
	 */

	@Getter
	@Setter
	private static boolean antiabsoall = false;
	@Getter
	@Setter
	private static boolean antiabsoinvi = true;
	@Getter
	@Setter
	private static boolean antiabsooff = false;

	@Override
	public String getName() {
		return "Anti Abso";
	}

	public static String abso() {
        return ChatColor.GOLD+"[HIDE-ABSO] "+ChatColor.RESET;
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

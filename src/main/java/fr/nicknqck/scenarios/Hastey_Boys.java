package fr.nicknqck.scenarios;

import fr.nicknqck.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Hastey_Boys extends BasicScenarios{
	private static boolean HasteyBoys = false;
	public static void setHasteyBoys(boolean hasteyboys) {
		HasteyBoys = hasteyboys;
	}
	public static boolean isHasteyBoys() {
		return HasteyBoys;
	}

	@Override
	public String getName() {
		return "§r§fHastey Boys";
	}

	@Override
	public ItemStack getAffichedItem() {
		return new ItemBuilder(Material.GOLD_PICKAXE).setName(getName()).setLore(getName()+"§f est actuellement: "+(HasteyBoys ? "§aActivé" : "§cDésactivé")).toItemStack();
	}

	@Override
	public void onClick(Player player) {
		if (isHasteyBoys()) {
			setHasteyBoys(false);
			player.sendMessage(hasteyboy()+ChatColor.GREEN+"Désactivation d'Hastey Boys");
		} else {
			setHasteyBoys(true);
			player.sendMessage(hasteyboy()+ChatColor.GREEN+"Activation d'Hastey Boys");
			if (Hastey_Babys.isHasteyBabys()) {
				Hastey_Babys.setHasteyBabys(false);
			}
		}
	}

	public static ItemStack getHasteyBoys() {
		  ItemStack stack = new ItemStack(Material.GOLD_PICKAXE, 1);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setLore(List.of(ChatColor.GOLD + "Activer"));
		  meta.addEnchant(Enchantment.DEPTH_STRIDER, 0, false);
		  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		  meta.setDisplayName(ChatColor.DARK_PURPLE+"Hastey Boys: ");
		  stack.setItemMeta(meta);
		  return stack;
	  }
	  public static ItemStack getnotHasteyBoys() {
		  ItemStack stack = new ItemStack(Material.GOLD_PICKAXE, 1);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setLore(List.of(ChatColor.GOLD + "Désactiver"));
		  meta.setDisplayName(ChatColor.DARK_PURPLE+"Hastey Boys: ");
		  stack.setItemMeta(meta);
		  return stack;
	  }
	  public static String hasteyboy() {
          return ChatColor.GOLD+"[HASTEY-BOYS] "+ChatColor.RESET;
		}
}
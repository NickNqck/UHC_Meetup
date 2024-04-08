package fr.nicknqck.scenarios.impl;

import fr.nicknqck.GameState;
import fr.nicknqck.HubListener;
import fr.nicknqck.blocks.BlockManager;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.scenarios.BasicScenarios;
import fr.nicknqck.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class CutClean extends BasicScenarios {

	
	/*Le reste est dans 
	 * fr.nicknqck.mtpds.blocks.BlockManager.CutClean
	 */
	private static boolean CutClean = false;

	public static boolean isCutClean() {
		return CutClean;
	}

	@Override
	public String getName() {
		return "§r§fCutClean";
	}

	@Override
	public ItemStack getAffichedItem() {
		return new ItemBuilder(Material.IRON_PICKAXE).setName(getName()).setLore("§r§fLe§6 CutClean§f est actuellement: "+(CutClean ? "§aActivé" : "§cDésactivé")).toItemStack();
	}

	@Override
	public void onClick(Player player) {
		if (getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
			player.openInventory(GUIItems.getCutCleanConfigGUI());
			HubListener.getInstance().updateCutCleanInventory(player);
			player.updateInventory();
		} else {
			if (CutClean) {
				CutClean = false;
				player.sendMessage(cutclean()+"Désactivation de CutClean");
			} else {
				CutClean = true;
				player.sendMessage(cutclean()+"Activation de CutClean");
			}
		}
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
          return ChatColor.GOLD+"[CutClean] "+ChatColor.RESET;
		}
}
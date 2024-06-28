package fr.nicknqck.scenarios.impl;

import fr.nicknqck.scenarios.BasicScenarios;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Hastey_Boys extends BasicScenarios {
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
	  public static String hasteyboy() {
          return ChatColor.GOLD+"[HASTEY-BOYS] "+ChatColor.RESET;
		}
}
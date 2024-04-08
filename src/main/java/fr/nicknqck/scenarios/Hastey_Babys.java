package fr.nicknqck.scenarios;

import fr.nicknqck.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Hastey_Babys  extends BasicScenarios{

	private static boolean HasteyBabys = false;
	
	public static void setHasteyBabys(boolean hastey) {
		HasteyBabys = hastey;
	}
	
	public static boolean isHasteyBabys() {
		return HasteyBabys;
	}

	@Override
	public String getName() {
		return "§r§fHastey Babys";
	}

	@Override
	public ItemStack getAffichedItem() {
		return new ItemBuilder(Material.WOOD_PICKAXE).toItemStack();
	}

	@Override
	public void onClick(Player player) {

	}

	public static String HasteyBabys() {
        return ChatColor.GOLD+"[Hastey-Babys] "+ChatColor.RESET;
	}
}
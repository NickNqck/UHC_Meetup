package fr.nicknqck.scenarios.impl;

import fr.nicknqck.scenarios.BasicScenarios;
import fr.nicknqck.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FFA extends BasicScenarios {

	

	private static boolean FFA = false;

	@Override
	public String getName() {
		return "§r§fFFA";
	}

	@Override
	public ItemStack getAffichedItem() {
		return new ItemBuilder(Material.SIGN).setName(getName()).setLore("§r§fLe§6 FFA§f est actuellement: "+(FFA ? "§aActivé" : "§cDésactivé")).toItemStack();
	}

	@Override
	public void onClick(Player player) {
		if (getFFA()) {
			setFFA(false);
			player.sendMessage(ffa() + ChatColor.RED+"Désactivation du mode FFA");
		} else {
			setFFA(true);
			player.sendMessage(ffa() + ChatColor.GREEN+"Activation du mode FFA");
		}
	}
	public static void setFFA(boolean b){
		FFA = b;
	}
	public static boolean getFFA() {
		return FFA;
	}
	public static String ffa() {
        return ChatColor.GOLD+"[FFA] "+ChatColor.RESET;
	}
}
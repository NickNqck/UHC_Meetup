package fr.nicknqck.scenarios.impl;

import fr.nicknqck.scenarios.BasicScenarios;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Hastey_Babys  extends BasicScenarios {

	@Getter
	@Setter
	private static boolean HasteyBabys = false;

	@Override
	public String getName() {
		return "§r§fHastey Babys";
	}

	@Override
	public ItemStack getAffichedItem() {
		return new ItemBuilder(Material.WOOD_PICKAXE).setName(getName()).setLore(getName()+" est actuellement: "+(isHasteyBabys() ? "§aActivé" : "§cDésactivé")).toItemStack();
	}

	@Override
	public void onClick(Player player) {
		if (isHasteyBabys()) {
			setHasteyBabys(false);
			player.sendMessage(HasteyBabys()+"Désactivation de Hastey Babys");
		} else {
			setHasteyBabys(true);
			player.sendMessage(HasteyBabys()+"Activation de Hastey Babys");
			if (Hastey_Boys.isHasteyBoys()) {
				Hastey_Boys.setHasteyBoys(false);
			}
		}
	}

	public static String HasteyBabys() {
        return ChatColor.GOLD+"[Hastey-Babys] "+ChatColor.RESET;
	}
}
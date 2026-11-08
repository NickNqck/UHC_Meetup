package fr.nicknqck.scenarios.impl;

import fr.nicknqck.scenarios.BasicScenarios;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
		return "§r§fAnti Abso";
	}

	@Override
	public ItemStack getAffichedItem() {
		return new ItemBuilder(Material.GOLDEN_APPLE).setAmount(1).setName(getName()).setLore("§eAbsorption§f cachée pour: "+(antiabsoall ? "§fTous les joueurs" : antiabsoinvi ? "§fLes joueurs invisibles" : "§fAucun joueur")).toItemStack();
	}

	@Override
	public void onClick(Player player) {
		if (Anti_Abso.isAntiabsooff()) {
			setAntiabsooff(false);
			setAntiabsoinvi(true);
			player.sendMessage(abso()+"L'absorption est maintenant cachée pour ceux qui sont invisibles");
		} else if (Anti_Abso.isAntiabsoinvi()) {
			setAntiabsoinvi(false);
			setAntiabsoall(true);
			player.sendMessage(abso()+"L'absorption est maintenant cachée pour tous les joueurs");
		} else if (Anti_Abso.isAntiabsoall()) {
			setAntiabsoall(false);
			setAntiabsooff(true);
			player.sendMessage(abso()+"L'absorption n'est cachée pour personne");
		}
	}

	public static String abso() {
        return ChatColor.GOLD+"[HIDE-ABSO] "+ChatColor.RESET;
	}
}
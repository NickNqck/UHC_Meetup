package fr.nicknqck.scenarios;

import fr.nicknqck.utils.ItemBuilder;
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
		return new ItemBuilder(Material.GOLDEN_APPLE).setAmount(1).setName(getName()).setLore("§eAbsorbtion§f caché pour: "+(antiabsoall ? "§fTout les joueurs" : antiabsoinvi ? "§fLes joueurs invisible" : "§fAucun joueur")).toItemStack();
	}

	@Override
	public void onClick(Player player) {
		if (Anti_Abso.isAntiabsooff()) {
			setAntiabsooff(false);
			setAntiabsoinvi(true);
			player.sendMessage(abso()+"L'absorbtion est maintenant caché pour ceux qui sont invisible");
		} else if (Anti_Abso.isAntiabsoinvi()) {
			setAntiabsoinvi(false);
			setAntiabsoall(true);
			player.sendMessage(abso()+"L'absorbtion est maintenant caché pour tout les joueurs");
		} else if (Anti_Abso.isAntiabsoall()) {
			setAntiabsoall(false);
			setAntiabsooff(true);
			player.sendMessage(abso()+"L'absorbtion n'est cachée pour personne");
		}
	}

	public static String abso() {
        return ChatColor.GOLD+"[HIDE-ABSO] "+ChatColor.RESET;
	}
}
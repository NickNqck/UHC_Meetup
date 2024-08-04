package fr.nicknqck.roles.aot.soldats;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.roles.aot.builders.SoldatsRoles;
import fr.nicknqck.roles.desc.AllDesc;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class Eclaireur extends SoldatsRoles {

	public Eclaireur(Player player) {
		super(player);
		gameState.GiveRodTridi(owner);
	}
	@Override
	public Roles getRoles() {
		return Roles.Eclaireur;
	}
	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"Eclaireur",
				"",
				AllDesc.capacite+"Si vous enlever votre armure vous deviendrez invisible et obtiendrez les effets "+AllDesc.Speed+" 2 ansi que noFall",
				"",
				AllDesc.bar
		};
	}

	@Override
	public String getName() {
		return "Eclaireur";
	}

	@Override
	public void Update(GameState gameState) {
		if (gameState.isApoil(owner)) {
			givePotionEffet(owner, PotionEffectType.SPEED, 40, 2, true);
			givePotionEffet(owner, PotionEffectType.INVISIBILITY, 40, 1, true);
			setNoFall(true);
		}else {
			setNoFall(false);
		}
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}
	@Override
	public void resetCooldown() {
	}
}
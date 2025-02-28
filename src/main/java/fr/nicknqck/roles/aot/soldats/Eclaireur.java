package fr.nicknqck.roles.aot.soldats;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.roles.aot.builders.SoldatsRoles;
import fr.nicknqck.roles.desc.AllDesc;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Eclaireur extends SoldatsRoles {

	public Eclaireur(UUID player) {
		super(player);
	}
	@Override
	public @NonNull Roles getRoles() {
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
			OLDgivePotionEffet(owner, PotionEffectType.SPEED, 40, 2, true);
			OLDgivePotionEffet(owner, PotionEffectType.INVISIBILITY, 40, 1, true);
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
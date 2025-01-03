package fr.nicknqck.roles.aot.mahr;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.aot.builders.MahrRoles;
import fr.nicknqck.roles.aot.builders.titans.Titans;
import fr.nicknqck.roles.desc.AllDesc;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Porco extends MahrRoles {

	public Porco(UUID player) {
		super(player);
		gameState.Shifter.add(owner);
		Titans.Machoire.getTitan().getListener().setMachoire(player);
	}
	@Override
	public Roles getRoles() {
		return Roles.Porco;
	}
	@Override
	public void RoleGiven(GameState gameState) {
		canShift = true;
		super.RoleGiven(gameState);
	}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(getItems());
		super.GiveItems();
	}

	@Override
	public String getName() {
		return "Porco";
	}

	@Override
	public String[] Desc() {
		Main.getInstance().getGetterList().getMahrList(owner);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"Porco",
				"",
				"§7Vous possédez le Titan "+Titans.Machoire.getTitan().getName(),
				"",
				AllDesc.bar
		};
	}
	@Override
	public ItemStack[] getItems() {
		return Titans.Machoire.getTitan().Items();
	}
	@Override
	public void resetCooldown() {
	}
}
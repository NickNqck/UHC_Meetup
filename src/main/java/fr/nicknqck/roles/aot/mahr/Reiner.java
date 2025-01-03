package fr.nicknqck.roles.aot.mahr;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.aot.builders.MahrRoles;
import fr.nicknqck.roles.aot.builders.titans.Titans;
import fr.nicknqck.roles.desc.AllDesc;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Reiner extends MahrRoles {

	public Reiner(UUID player) {
		super(player);
		gameState.Shifter.add(owner);
		Titans.Cuirasse.getTitan().getListener().setCuirasse(player);
	}
	@Override
	public Roles getRoles() {
		return Roles.Reiner;
	}
	@Override
	public void RoleGiven(GameState gameState) {
		canShift = true;
		super.RoleGiven(gameState);
	}

	@Override
	public String getName() {
		return "Reiner";
	}

	@Override
	public String[] Desc() {
		Main.getInstance().getGetterList().getMahrList(owner);
		return new String[] {AllDesc.bar,
				AllDesc.role+"§9Reiner",
				AllDesc.objectifteam+"§9Mahrs",
				"",
				"Vous possédez le Titan§9 Cuirassé§6 /aot titan",
				"",
				AllDesc.bar
		};
	}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(getItems());
		super.GiveItems();
	}
	@Override
	public ItemStack[] getItems() {
		return Titans.Cuirasse.getTitan().Items();
	}
	@Override
	public void resetCooldown() {
	}
}
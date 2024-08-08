package fr.nicknqck.roles.aot.mahr;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.aot.builders.MahrRoles;
import fr.nicknqck.roles.aot.builders.titans.Titans;
import fr.nicknqck.roles.desc.AllDesc;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Lara extends MahrRoles {

	public Lara(UUID player) {
		super(player);
		gameState.Shifter.add(owner);
		gameState.GiveRodTridi(owner);
		Titans.WarHammer.getTitan().getListener().setWarHammer(owner.getUniqueId());
	}
	@Override
	public Roles getRoles() {
		return Roles.Lara;
	}
	@Override
	public void RoleGiven(GameState gameState) {
		canShift = true;
		super.RoleGiven(gameState);
	}

	@Override
	public String getName() {
		return "Lara";
	}

	@Override
	public String[] Desc() {
		Main.getInstance().getGetterList().getMahrList(owner);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"Lara",
				"",
				"§7Vous possédez le Titan "+Titans.WarHammer.getTitan().getName(),
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
		return Titans.WarHammer.getTitan().Items();
	}
	@Override
	public void resetCooldown() {
	}
}
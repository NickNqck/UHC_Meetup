package fr.nicknqck.roles.aot.mahr;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.aot.titans.Titans;
import fr.nicknqck.roles.desc.AllDesc;

public class Lara extends RoleBase {

	public Lara(Player player) {
		super(player);
		owner.sendMessage(Desc());
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
		return "§9Lara";
	}

	@Override
	public String[] Desc() {
		gameState.sendShifterList(owner);
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
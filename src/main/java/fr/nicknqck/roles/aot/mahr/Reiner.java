package fr.nicknqck.roles.aot.mahr;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.aot.titans.Titans;
import fr.nicknqck.roles.desc.AllDesc;

public class Reiner extends RoleBase{

	public Reiner(Player player, Roles roles, GameState gameState) {
		super(player, roles, gameState);
		owner.sendMessage(Desc());
		gameState.Shifter.add(owner);
		gameState.GiveRodTridi(owner);
		Titans.Cuirasse.getTitan().getListener().setCuirasse(owner.getUniqueId());
	}
	@Override
	public void RoleGiven(GameState gameState) {
		canShift = true;
		super.RoleGiven(gameState);
	}
	@Override
	public String[] Desc() {
		gameState.sendShifterList(owner);
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
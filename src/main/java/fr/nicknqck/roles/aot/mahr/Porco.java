package fr.nicknqck.roles.aot.mahr;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.aot.titans.Titans;
import fr.nicknqck.roles.desc.AllDesc;

public class Porco extends RoleBase{

	public Porco(Player player, Roles roles) {
		super(player, roles);
		owner.sendMessage(Desc());
		gameState.Shifter.add(owner);
		gameState.GiveRodTridi(owner);
		Titans.Machoire.getTitan().getListener().setMachoire(owner.getUniqueId());
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
	public String[] Desc() {
		gameState.sendShifterList(owner);
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
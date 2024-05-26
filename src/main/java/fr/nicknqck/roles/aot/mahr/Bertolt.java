package fr.nicknqck.roles.aot.mahr;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.aot.titans.TitanListener;
import fr.nicknqck.roles.aot.titans.Titans;
import fr.nicknqck.roles.desc.AllDesc;;

public class Bertolt extends RoleBase{
	public Bertolt(Player player, Roles roles) {
		super(player, roles);
		owner.sendMessage(Desc());
		gameState.Shifter.add(owner);
		gameState.GiveRodTridi(owner);
		TitanListener.getInstance().setColossal(owner.getUniqueId());
	}
	@Override
	public void RoleGiven(GameState gameState) {
		canShift = true;
		super.RoleGiven(gameState);
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, Titans.Colossal.getTitan().Items());
		super.GiveItems();
	}
	@Override
	public String[] Desc() {
		gameState.sendShifterList(owner);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§9Bertolt",
				AllDesc.objectifteam+"§9 Mahr",
				"",
				"Vous possédez un titan§6 /aot titan",
				"",
				AllDesc.bar
		};
	}
	boolean transfo = false;
	@Override
	public ItemStack[] getItems() {
		return Titans.Colossal.getTitan().Items();
	}
	@Override
	public void resetCooldown() {
	}
}
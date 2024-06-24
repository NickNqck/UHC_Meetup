package fr.nicknqck.roles.aot.mahr;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.aot.builders.MahrRoles;
import fr.nicknqck.roles.aot.builders.titans.TitanListener;
import fr.nicknqck.roles.aot.builders.titans.Titans;
import fr.nicknqck.roles.desc.AllDesc;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Bertolt extends MahrRoles {
	public Bertolt(Player player) {
		super(player);
		owner.sendMessage(Desc());
		gameState.Shifter.add(owner);
		gameState.GiveRodTridi(owner);
		TitanListener.getInstance().setColossal(owner.getUniqueId());
	}

	@Override
	public Roles getRoles() {
		return Roles.Bertolt;
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
		Main.getInstance().getGetterList().getMahrList(owner);
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

	@Override
	public String getName() {
		return "§9Bertolt";
	}

	@Override
	public ItemStack[] getItems() {
		return Titans.Colossal.getTitan().Items();
	}
	@Override
	public void resetCooldown() {
	}
}
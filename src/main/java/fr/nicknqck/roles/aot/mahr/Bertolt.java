package fr.nicknqck.roles.aot.mahr;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.aot.builders.MahrRoles;
import fr.nicknqck.roles.aot.builders.titans.TitanListener;
import fr.nicknqck.roles.aot.builders.titans.Titans;
import fr.nicknqck.roles.desc.AllDesc;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Bertolt extends MahrRoles {
	public Bertolt(UUID player) {
		super(player);
		gameState.Shifter.add(owner);
		TitanListener.getInstance().setColossal(player);
	}

	@Override
	public @NonNull Roles getRoles() {
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
		return "Bertolt";
	}

	@Override
	public ItemStack[] getItems() {
		return Titans.Colossal.getTitan().Items();
	}
	@Override
	public void resetCooldown() {
	}
}
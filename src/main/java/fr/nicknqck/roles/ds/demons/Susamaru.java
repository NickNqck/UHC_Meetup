package fr.nicknqck.roles.ds.demons;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.DemonType;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Susamaru extends DemonInferieurRole {

	public Susamaru(UUID player) {
		super(player);
	}

	@Override
	public @NonNull DemonType getRank() {
		return DemonType.DEMON;
	}

	@Override
	public Roles getRoles() {
		return Roles.Susamaru;
	}
	@Override
	public String[] Desc() {
		return AllDesc.Susamaru;
	}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(Items.getSusamaruBow());
		super.GiveItems();
	}
	@Override
	public TeamList getOriginTeam() {
		return TeamList.Demon;
	}
	public boolean Niveau1 = true;
	public boolean Niveau2 = false;
	public int cooldown = -1;
	public int changecd = 0;
	//tout ce qui concerne l'arc est dans fr.nicknqck.mtpds.HomingBow.java
	@Override
	public void Update(GameState gameState) {
		if (cooldown > 0) {
			cooldown--;}
		if (owner.getItemInHand().isSimilar(Items.getSusamaruBow())) {
			sendActionBarCooldown(owner, cooldown);
		}
		if (changecd>=1)changecd--;		
		if (cooldown == 0) {
			owner.sendMessage("§6Ballon §rutilisable des maintenants");
			cooldown-=1;
		}
		super.Update(gameState);
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				Items.getSusamaruBow()
		};
	}
	@Override
	public void resetCooldown() {
		cooldown = 0;
		changecd = 0;
	}

	@Override
	public String getName() {
		return "Susamaru";
	}
}
package fr.nicknqck.roles.ds.demons;

import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.roles.desc.AllDesc;

public class Demon_Simple extends DemonsRoles {

	public Demon_Simple(Player player) {
		super(player);
		setForce(20);
	}

	@Override
	public DemonType getRank() {
		return DemonType.Demon;
	}
	@Override
	public TeamList getOriginTeam() {
		return TeamList.Demon;
	}
	@Override
	public Roles getRoles() {
		return Roles.Demon;
	}
	@Override
	public String[] Desc() {
		return AllDesc.Demon_Simple;
	}

	@Override
	public String getName() {
		return "§cDemon Simple";
	}

	@Override
	public void Update(GameState gameState) {
		if (gameState.nightTime) {
			givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
		} else {
			givePotionEffet(PotionEffectType.WEAKNESS, 60, 1, true);
		}
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer == owner) {
			if (victim != owner) {
				this.setForce(this.getForce() + 3);
				owner.sendMessage("Vous venez de tuer: "+victim.getName()+" vous obtenez donc +§c 3% de Force§f ce qui vous à fait monter jusqu'a: "+ this.getForce()+"%");
			}
		}
	}

	@Override
	public void resetCooldown() {
		
	}
}
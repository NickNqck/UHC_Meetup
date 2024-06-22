package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.desc.AllDesc;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class Akaza extends DemonsRoles {
	
	public Akaza(Player player) {
		super(player);
		owner.sendMessage(Desc());
		this.setForce(20);
	}

	@Override
	public DemonType getRank() {
		return DemonType.LuneSuperieur;
	}
	@Override
	public TeamList getTeam() {
		return TeamList.Demon;
	}
	@Override
	public Roles getRoles() {
		return Roles.Akaza;
	}
	@Override
	public String[] Desc() {
		KnowRole(owner, Roles.Muzan, 1);
		return AllDesc.Akaza;
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}
	private int regencooldown = 0;
	private boolean killkyojuro = false;
	private boolean firstrez = false;
	@Override
	public void Update(GameState gameState) {
		if (regencooldown == 0) {
			regencooldown = 20;
			if (owner.getHealth() != this.getMaxHealth()) {
				if (owner.getHealth() <= (this.getMaxHealth() - 1.0)) {
					owner.setHealth(owner.getHealth() + 1.0);
				} else {
					owner.setHealth(this.getMaxHealth());
				}			
			}
			
		}
		if (regencooldown >= 1) {regencooldown--;}
		if (killkyojuro) {
			if (!firstrez) {
				if (owner.getHealth() <= 4.0) {
					firstrez = true;
					owner.setHealth(getMaxHealth());
					owner.sendMessage("Vous venez d'utiliser votre résurrection.");
				}
			}
		}
		givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 80, 1, true);
	}

	@Override
	public String getName() {
		return "§cAkaza";
	}

	@Override
	public void resetCooldown() {
		regencooldown = 0;
	}
}
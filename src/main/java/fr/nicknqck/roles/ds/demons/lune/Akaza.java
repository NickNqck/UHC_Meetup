package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.demons.Muzan;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Akaza extends DemonsRoles {
	private int regencooldown = 0;
	public Akaza(Player player) {
		super(player);
		givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
		getKnowedRoles().add(Muzan.class);
	}
	@Override
	public DemonType getRank() {
		return DemonType.LuneSuperieur;
	}
	@Override
	public TeamList getOriginTeam() {
		return TeamList.Demon;
	}
	@Override
	public Roles getRoles() {
		return Roles.Akaza;
	}
	@Override
	public String[] Desc() {
		return AllDesc.Akaza;
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}
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
	}
	@Override
	public String getName() {
		return "Akaza";
	}
	@Override
	public void resetCooldown() {
		regencooldown = 0;
	}
}
package fr.nicknqck.roles.ds.demons.lune;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;

public class Akaza extends RoleBase{
	
	public Akaza(Player player, Roles roles) {
		super(player, roles);
		owner.sendMessage(Desc());
		this.setForce(20);
		gameState.addLuneSupPlayers(owner);
		if (!gameState.lunesup.contains(owner))gameState.lunesup.add(owner);
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
	public void resetCooldown() {
		regencooldown = 0;
	}
}
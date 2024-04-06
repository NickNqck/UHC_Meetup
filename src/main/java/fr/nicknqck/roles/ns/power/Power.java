package fr.nicknqck.roles.ns.power;

import org.bukkit.entity.Player;

import fr.nicknqck.GameState;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.cooldown.Cooldown;

public abstract class Power {
	
	private int maxUse = -1;
	private Cooldown cooldown;
	private final String name;
	private int actualUse;
	public Power(String name, Cooldown cd) {
		this.name = name;
		this.actualUse = 0;
		this.cooldown = cd;
	}
	public boolean checkCanUse(Player user) {
		if (GameState.getInstance().hasRoleNull(user)) {
			user.sendMessage("§cIl faut avoir un rôle pour utiliser ce pouvoir.");
			return false;
		}
		int maxUse = this.getMaxUse();
        int use = this.getActualUse();
		if (use >= maxUse && maxUse != -1) {
			user.sendMessage("§cVous avez atteint le maximum d'utilisation de cette capacité.");
			return false;
		}
		Cooldown powerCooldown = this.getCooldown();
        if(powerCooldown != null && powerCooldown.isInCooldown()){
        	user.sendMessage("§cCe pouvoir est en cooldown pour encore "+StringUtils.secondsTowardsBeautiful((int)powerCooldown.getCooldownRemaining()));
        }
		return true;
	}
	public Cooldown getCooldown() {
		return this.cooldown;
	}
	public void addCooldown(int i) {
		this.cooldown.addSeconds(i);
	}
	public String getName() {
		return this.name;
	}
	public int getActualUse() {
		return this.actualUse;
	}
	public void setActualUse(int i) {
		this.actualUse = i;
	}
	public int getMaxUse() {
		return maxUse;
	}
	public void setMaxUse(int i) {
		this.maxUse = i;
	}
}
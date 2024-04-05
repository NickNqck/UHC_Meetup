package fr.nicknqck.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Objective;

import fr.nicknqck.GameState;

public abstract class EventBase {
	
	public GameState gameState = GameState.getInstance();
	private int time;
	private boolean activated = false;
	
	public boolean PlayEvent(int gameTime) {
		if (!isActivated() && gameTime == time) {
			setActivated(true);
			return true;
		}
		return false;
	}
	
	public abstract void OnPlayerKilled(Player player, Player victim, GameState gameState);

	public int getTime() {return time;}

	public void setTime(int time) {
		this.time = time;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public int UpdateScoreboard(Objective objective, Player player, int i) {
		return i;
	}
	public abstract void setupEvent();

	public abstract Events getEvents();

	public abstract int getProba();

	public abstract void onItemInteract(PlayerInteractEvent event, ItemStack itemstack, Player player);

	public abstract void onPlayerKilled(Entity damager, Player player, GameState gameState2);

	public abstract void onSecond();
	public abstract void resetCooldown();

	public abstract void onPlayerDamagedByPlayer(EntityDamageByEntityEvent event, Player player, Entity damageur);

	public abstract void onSubDSCommand(Player sender, String[] args);
}
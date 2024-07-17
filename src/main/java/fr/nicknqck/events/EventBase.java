package fr.nicknqck.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Objective;

import fr.nicknqck.GameState;

public abstract class EventBase {
	
	public GameState gameState = GameState.getInstance();
	@Setter
	@Getter
	private int time;
	@Setter
	@Getter
	private boolean activated = false;
	
	public boolean PlayEvent(int gameTime) {
		if (!isActivated() && gameTime == time) {
			setActivated(true);
			return true;
		}
		return false;
	}
	
	public abstract void OnPlayerKilled(Player player, Player victim, GameState gameState);
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
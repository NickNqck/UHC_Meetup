package fr.nicknqck.events;

import fr.nicknqck.GameState;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public abstract class EventBase {
	
	public GameState gameState = GameState.getInstance();
	@Setter
	@Getter
	private int minTime;
	@Setter
	@Getter
	private boolean activated = false;
	
	public boolean PlayEvent(int gameTime) {
		if (!isActivated() && gameTime == minTime) {
			setActivated(true);
			return true;
		}
		return false;
	}

	public abstract void setupEvent();
	public abstract Events getEvents();
	public abstract int getProba();
	public abstract void onItemInteract(PlayerInteractEvent event, ItemStack itemstack, Player player);
	public abstract void onPlayerKilled(Entity damager, Player player, GameState gameState2);
	public abstract void onSecond();
	public abstract void resetCooldown();
	public abstract void onPlayerDamagedByPlayer(EntityDamageByEntityEvent event, Player player, Entity damageur);
	public abstract boolean onSubDSCommand(Player sender, String[] args);
}
package fr.nicknqck.events.custom;

import fr.nicknqck.GameState;
import fr.nicknqck.player.GamePlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UHCPlayerKillEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	@Getter
	private final Player victim;
	private final Entity damager;
	@Getter
	private final GameState gameState;
	@Setter
	@Getter
	private boolean cancel;
	public UHCPlayerKillEvent(Player player, Entity damager, GameState gameState) {
		this.victim = player;
		this.damager = damager;
		this.gameState = gameState;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList() {
	    return handlers;
	}

	public Entity getKiller() {
		return damager;
	}
	public Player getPlayerKiller(){
		Player killer = null;
		if (getKiller() instanceof Player){
			killer = (Player) getKiller();
		} else if (getKiller() instanceof Projectile){
			if (((Projectile) getKiller()).getShooter() instanceof Player){
				killer = ((Player) ((Projectile) getKiller()).getShooter()).getPlayer();
			}
		}
		return killer;
	}
	public GamePlayer getGamePlayerKiller(){
		if (getPlayerKiller() != null){
			if (!getGameState().hasRoleNull(getPlayerKiller())){
				return getGameState().getPlayerRoles().get(getPlayerKiller()).getGamePlayer();
			}
		}
		return null;
	}
}
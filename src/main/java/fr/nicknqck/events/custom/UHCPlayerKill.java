package fr.nicknqck.events.custom;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nicknqck.GameState;

public class UHCPlayerKill extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Player victim;
	private Entity damager;
	private GameState gameState;
	public UHCPlayerKill(Player player, Entity damager, GameState gameState) {
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
	public GameState getGameState() {
		return gameState;
	}
	public Player getVictim() {
		return victim;
	}
	public Entity getKiller() {
		return damager;
	}
}
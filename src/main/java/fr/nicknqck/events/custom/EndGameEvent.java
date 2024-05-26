package fr.nicknqck.events.custom;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.TeamList;

public class EndGameEvent extends Event{
	
	private static final HandlerList handlers = new HandlerList();
	private GameState gameState;
	private TeamList team;
	public EndGameEvent(GameState gameState, TeamList team) {
		this.gameState = gameState;
		this.team = team;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public TeamList getWiningTeam() {
		return team;
	}
	public GameState getGameState() {
		return gameState;
	}
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
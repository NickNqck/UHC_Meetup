package fr.nicknqck.events.custom;

import lombok.Getter;
import org.bukkit.event.HandlerList;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.TeamList;

@Getter
public class EndGameEvent extends CustomEventBase{
	
	private static final HandlerList handlers = new HandlerList();
	private final GameState gameState;
	private final TeamList team;
	public EndGameEvent(GameState gameState, TeamList team) {
		this.gameState = gameState;
		this.team = team;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
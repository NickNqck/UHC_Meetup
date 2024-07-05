package fr.nicknqck.events.custom;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.TeamList;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class EndGameEvent extends Event {
	
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
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
package fr.nicknqck.events.custom;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.TeamList;
import fr.nicknqck.interfaces.ITeam;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class GameEndEvent extends Event {

	private final GameState gameState;
	private final ITeam team;

	public GameEndEvent(GameState gameState, ITeam team) {
		this.gameState = gameState;
		this.team = team;
	}
	private static final HandlerList handlers = new HandlerList();
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
package fr.nicknqck.events.custom;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.TeamList;
import lombok.Getter;

@Getter
public class GameEndEvent extends GameEvent {

	private final GameState gameState;
	private final TeamList team;

	public GameEndEvent(GameState gameState, TeamList team) {
		this.gameState = gameState;
		this.team = team;
	}

}
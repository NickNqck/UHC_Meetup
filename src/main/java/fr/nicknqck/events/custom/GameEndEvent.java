package fr.nicknqck.events.custom;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.TeamList;
import fr.nicknqck.interfaces.ITeam;
import lombok.Getter;

@Getter
public class GameEndEvent extends GameEvent {

	private final GameState gameState;
	private final ITeam team;

	public GameEndEvent(GameState gameState, ITeam team) {
		this.gameState = gameState;
		this.team = team;
	}

}
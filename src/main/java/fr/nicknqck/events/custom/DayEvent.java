package fr.nicknqck.events.custom;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nicknqck.GameState;

@Getter
public class DayEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final GameState gameState;
	public DayEvent(GameState gameState) {
		this.gameState = gameState;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public List<Player> getInGamePlayersWithRole(){
		List<Player> toReturn = new ArrayList<>(getGameState().getInGamePlayers());
		toReturn.stream().filter(p -> getGameState().hasRoleNull(p)).forEach(toReturn::remove);
		return toReturn;
	}
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
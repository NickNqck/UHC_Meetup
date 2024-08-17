package fr.nicknqck.events.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nicknqck.GameState;

public class NightEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	private final GameState gameState;
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public NightEvent(GameState gameState) {
		this.gameState = gameState;
	}
	public GameState getGameState() {
		return gameState;
	}
	public List<UUID> getInGamePlayersWithRole(){
		List<UUID> toReturn = new ArrayList<>(getGameState().getInGamePlayers());
		toReturn.stream().filter(u -> Bukkit.getPlayer(u) != null).filter(p -> getGameState().hasRoleNull(Bukkit.getPlayer(p))).forEach(toReturn::remove);
		return toReturn;
	}
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
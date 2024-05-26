package fr.nicknqck.events.custom;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nicknqck.roles.builder.TeamList;

public class WinEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	private TeamList WinTeam = null;
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public TeamList getWiningTeam() {
		return WinTeam;
	}
	public WinEvent(TeamList winer) {
		this.WinTeam = winer;
	}
	public String getTeamColor() {
		return WinTeam.getColor();
	}
	public List<Player> getWiners(){
		return WinTeam.getPlayers();
	}
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
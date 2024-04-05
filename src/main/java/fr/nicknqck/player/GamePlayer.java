package fr.nicknqck.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.TeamList;

public class GamePlayer {
//class pour l'instant inutile mais permettra de faire qu'on puisse déco reco en game, (si tu vois sa @ moi je veux savoir ce que tu en pense
	private final HashMap<UUID, RoleBase> GamePlayersRoles = new HashMap<>();
	public GamePlayer() {
	}
	public List<UUID> getGamePlayerUUID(){
		List<UUID> toReturn = new ArrayList<>(GamePlayersRoles.keySet());
		return toReturn;
	}
	public HashMap<UUID, RoleBase> getGamePlayers(){
		return GamePlayersRoles;
	}
	public void putGamePlayer(UUID uuid, RoleBase role) {
		GamePlayersRoles.put(uuid, role);
	}
	public void removeGamePlayer(UUID uuid) {
		GamePlayersRoles.remove(uuid, GamePlayersRoles.get(uuid));
	}
	public RoleBase getRole(UUID uuid) {
		if (GamePlayersRoles.containsKey(uuid)) {
			return GamePlayersRoles.get(uuid);
		}
		return null;
	}
	public TeamList getTeam(UUID uuid) {
		if (GamePlayersRoles.containsKey(uuid)) {
			return GamePlayersRoles.get(uuid).getTeam();
		}
		return null;
	}
}
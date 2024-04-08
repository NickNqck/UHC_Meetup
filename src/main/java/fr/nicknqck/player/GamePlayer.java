package fr.nicknqck.player;

import fr.nicknqck.Main;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.TeamList;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class GamePlayer {
//class pour l'instant inutile mais permettra de faire qu'on puisse déco reco en game, (si tu vois sa @ moi je veux savoir ce que tu en pense
	private final HashMap<UUID, RoleBase> GamePlayersRoles = new HashMap<>();

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
	public void stun(UUID uuid, double seconds, Location loc){
		Player target = Bukkit.getPlayer(uuid);
		if (target != null){
			Location gLoc;
			if (loc == null){
				gLoc = target.getLocation().clone();
			} else {
				gLoc = loc.clone();
			}
			new BukkitRunnable() {
				double tickRemaining = 20*seconds;
				@Override
				public void run() {
					if (tickRemaining == 0){
						cancel();
						return;
					}
					target.teleport(gLoc);
					tickRemaining--;
				}
			}.runTaskTimer(Main.getInstance(), 0, 1);
		}
	}
}
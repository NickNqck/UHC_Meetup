package fr.nicknqck.player;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

@Getter
public class GamePlayer {
//class pour l'instant inutile mais permettra de faire qu'on puisse déco reco en game, (si tu vois sa @ moi je veux savoir ce que tu en pense
	private final UUID uuid;
	public GamePlayer(UUID gamePlayer){
		this.uuid = gamePlayer;
	}
	public void stun(double seconds){
		Player target = Bukkit.getPlayer(getUuid());
		if (target != null){
			Location gLoc = target.getLocation().clone();
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
	public static GamePlayer get(UUID uuid){
		return GameState.getInstance().getPlayerRoles().get(Bukkit.getPlayer(uuid)).getGamePlayer();
	}
}
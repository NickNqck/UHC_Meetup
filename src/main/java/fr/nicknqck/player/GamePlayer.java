package fr.nicknqck.player;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.RoleBase;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public class GamePlayer {
//class pour l'instant inutile mais permettra de faire qu'on puisse déco reco en game, (si tu vois sa @ moi je veux savoir ce que tu en pense
	private final UUID uuid;
	@Setter
	private boolean isAlive;
	@Setter
	private boolean canRevive = false;
	@Getter
	@Setter
	private RoleBase role;
	public GamePlayer(UUID gamePlayer){
		this.uuid = gamePlayer;
		setAlive(true);
	}
	public static GamePlayer get(UUID uuid){
		return GameState.getInstance().getPlayerRoles().get(Bukkit.getPlayer(uuid)).getGamePlayer();
	}
	public static GamePlayer get(Player player){
		return GameState.getInstance().getGamePlayer().get(player.getUniqueId());
	}
}
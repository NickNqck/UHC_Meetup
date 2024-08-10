package fr.nicknqck.player;

import fr.nicknqck.roles.builder.RoleBase;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.UUID;

@Getter
public class GamePlayer {

	private final UUID uuid;
	@Setter
	private boolean isAlive;
	@Setter
	private boolean canRevive = false;
	@Setter
	private RoleBase role;
	@Setter
	private Location deathLocation;
	public GamePlayer(UUID gamePlayer){
		this.uuid = gamePlayer;
		setAlive(true);
		setCanRevive(false);
	}
}
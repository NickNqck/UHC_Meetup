package fr.nicknqck.player;

import com.avaje.ebean.validation.NotNull;
import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.RoleBase;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.UUID;

@Getter
public class GamePlayer {

	private final UUID uuid;
	@Setter
	private boolean isAlive;
	@Setter
	private boolean canRevive = false;
	@NotNull
	@NonNull
	@Setter
	private RoleBase role;
	@Setter
	private Location deathLocation;
	@Setter
	private int timeDisconnectLeft = 60;
	private final String playerName;
	@Nullable
	private DiscRunnable discRunnable;
	@NotNull
	@NonNull
	@Setter
	private Location lastLocation;
	@NotNull
	@NonNull
	@Setter
	private ItemStack[] lastInventoryContent;
	public GamePlayer(Player gamePlayer){
		this.uuid = gamePlayer.getUniqueId();
		this.playerName = gamePlayer.getName();
		this.lastLocation = gamePlayer.getLocation();
		this.lastInventoryContent = gamePlayer.getInventory().getContents();
		setAlive(true);
		setCanRevive(false);
	}
	public void onQuit() {
		this.discRunnable = new DiscRunnable(this);
		this.discRunnable.runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
	}
	public void onJoin() {
		if (this.discRunnable != null) {
			this.discRunnable.cancel();
			this.discRunnable = null;
		}
	}
	private static class DiscRunnable extends BukkitRunnable {
		private final GamePlayer gamePlayer;
		private DiscRunnable(GamePlayer gamePlayer) {
			this.gamePlayer = gamePlayer;
		}
		@Override
		public void run() {
			gamePlayer.setTimeDisconnectLeft(gamePlayer.getTimeDisconnectLeft()-1);
			if (gamePlayer.getTimeDisconnectLeft() == 0) {
				Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
					GameState.getInstance().getInGamePlayers().remove(gamePlayer.getUuid());
					Main.getInstance().getDeathManager().DisconnectKillHandler(gamePlayer);
					gamePlayer.discRunnable = null;
				});
				cancel();
			}
		}
	}
}
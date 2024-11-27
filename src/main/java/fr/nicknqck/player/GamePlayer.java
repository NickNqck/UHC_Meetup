package fr.nicknqck.player;

import com.avaje.ebean.validation.NotNull;
import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.scoreboard.PersonalScoreboard;
import fr.nicknqck.utils.packets.NMSPacket;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class GamePlayer {

	private final UUID uuid;
	@Setter
	private boolean isAlive;
	@Setter
	private boolean canRevive = false;
	@org.bukkit.craftbukkit.libs.jline.internal.Nullable
	@Setter
	private RoleBase role;
	@Setter
	private Location deathLocation;
	@Setter
	private int timeDisconnectLeft = 60*5;
	private final String playerName;
	@Nullable
	private DiscRunnable discRunnable;
	@NotNull
	@Setter
	private Location lastLocation;
	@NotNull
	@NonNull
	@Setter
	private ItemStack[] lastInventoryContent;
	private final PersonalScoreboard scoreboard;
	@Setter
	@Nullable
    private GamePlayer killer;
	private final ActionBarManager actionBarManager;
	public GamePlayer(Player gamePlayer){
		this.uuid = gamePlayer.getUniqueId();
		this.playerName = gamePlayer.getName();
		this.lastLocation = gamePlayer.getLocation();
		this.lastInventoryContent = gamePlayer.getInventory().getContents();
		this.scoreboard = Main.getInstance().getScoreboardManager().getScoreboards().get(gamePlayer.getUniqueId());
		this.actionBarManager = new ActionBarManager(this);
		setAlive(true);
		setCanRevive(false);
	}
	public void onQuit() {
		this.discRunnable = new DiscRunnable(this);
		this.discRunnable.runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
	}
	public void onJoin(Player player) {
		if (this.discRunnable != null) {
			player.sendMessage(this.discRunnable.toSend);
			this.discRunnable.cancel();
			this.discRunnable = null;
		}
	}
	public void stun(int tick) {
		Player player = Bukkit.getPlayer(getUuid());
		if (player == null)return;
		new BukkitRunnable() {
			private int ticks = tick;
			private final Location stunLocation = player.getLocation();
			@Override
			public void run() {
				if (ticks == 0 || !isAlive || !GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
					cancel();
					return;
				}
				Player player = Bukkit.getPlayer(getUuid());
				if (player == null)return;
				if (!player.getWorld().equals(stunLocation.getWorld())) {
					cancel();
					return;
				}
				player.teleport(stunLocation);
				ticks--;

			}
		}.runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
	}
	public void sendMessage(final String... messages) {
		Player owner = Bukkit.getPlayer(getUuid());
		if (owner != null) {
			owner.sendMessage(messages);
		} else {
			this.discRunnable.setMessagesToSend(messages);
		}
	}
    private static class DiscRunnable extends BukkitRunnable {

		private final GamePlayer gamePlayer;
		private final GameState gameState;
		private String[] toSend = new String[0];

		private DiscRunnable(GamePlayer gamePlayer) {
			this.gamePlayer = gamePlayer;
			this.gameState = GameState.getInstance();
		}

		private void setMessagesToSend(final String[] messages) {
			this.toSend = messages;
		}

		@Override
		public void run() {
			if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
				cancel();
				return;
			}
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
	private static class ActionBarManager {

		@Getter
		private final GamePlayer gamePlayer;
		private final Map<String, String> actionBars;
		@Getter
		private final ActionBarRunnable actionBarRunnable;

        private ActionBarManager(GamePlayer gamePlayer) {
            this.gamePlayer = gamePlayer;
			this.actionBars = new LinkedHashMap<>();
			this.actionBarRunnable = new ActionBarRunnable(this);
        }

		public void addToActionBar(final String key, final String value) {
			if (actionBars.containsKey(key)) {
				throw new Error("[ActionBarManager] Error ! key: "+key+" is already inside the Map");
			}
			this.actionBars.put(key, value);
		}

		public void updateActionBar(final String key, final String value) {
			if (actionBars.containsKey(key)) {
				this.actionBars.put(key, value);
			} else {
				throw new Error("[ActionBarManager] Error ! key: "+key+" isn't inside the Map");
			}
		}
		public void removeInActionBar(final String key) {
			if (actionBars.containsKey(key)) {
				final String value = actionBars.get(key);
				actionBars.remove(key, value);
			} else {
				throw new Error("[ActionBarManager] Error ! key: "+key+" isn't inside the Map");
			}
		}
		private static class ActionBarRunnable extends BukkitRunnable {

			private final GameState gameState;
			private final ActionBarManager actionBarManager;

            private ActionBarRunnable(ActionBarManager actionBarManager) {
                this.actionBarManager = actionBarManager;
				this.gameState = GameState.getInstance();
            }

            @Override
			public void run() {
				if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
					cancel();
					return;
				}
				if (this.actionBarManager.actionBars.isEmpty())return;
				final Player player = Bukkit.getPlayer(this.actionBarManager.getGamePlayer().getUuid());
				if (player == null)return;
				StringBuilder string = new StringBuilder();
				for (final String value : this.actionBarManager.actionBars.values()) {
					 string.append(value).append(" §7|§r ");
				}
				NMSPacket.sendActionBar(player, string.toString());
			}
		}

    }
}
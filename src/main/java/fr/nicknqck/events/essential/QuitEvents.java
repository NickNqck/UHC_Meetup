package fr.nicknqck.events.essential;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitEvents implements Listener{
	private final GameState gameState;
	public QuitEvents() {
		this.gameState = GameState.getInstance();
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void OnDisconnect(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Main.getInstance().getScoreboardManager().onLogout(player);
		if (gameState.getServerState() == ServerStates.InLobby) {
			String quitMessage;
			gameState.delInLobbyPlayers(player);
			player.saveData();
			quitMessage = ChatColor.LIGHT_PURPLE+player.getDisplayName()+ChatColor.RED+" à quitté le Lobby "+ChatColor.GRAY+"["+gameState.getInLobbyPlayers().size()+"/"+gameState.getroleNMB()+"]";
			event.setQuitMessage(quitMessage);
		}
		gameState.updateGameCanLaunch();
	    if (gameState.getServerState() == ServerStates.InGame) {
	        String quitMessage = "";
	        if (gameState.getInGamePlayers().contains(player)) {
	        GameListener.getInstance().DeathHandler(player, player, 9999.0, gameState);
	        quitMessage = "(Game) "+ChatColor.LIGHT_PURPLE + player.getDisplayName() + ChatColor.RED + " A quitté(e) la partie" + ChatColor.RED + " Il meurt donc suite à la déconnexion";
	        gameState.delInGamePlayers(player);
	        } else if (gameState.getInLobbyPlayers().contains(player)){
	        gameState.delInLobbyPlayers(player);
	        quitMessage = "(Lobby) "+ChatColor.LIGHT_PURPLE + player.getDisplayName() + ChatColor.RED + " A quitté(e) la partie";
	        } else if (gameState.getInSpecPlayers().contains(player)) {
	        gameState.delInSpecPlayers(player);
	        quitMessage = "(Spec) "+ChatColor.LIGHT_PURPLE + player.getDisplayName() + ChatColor.RED + " A quitté(e) la partie";
	        }
	        event.setQuitMessage(quitMessage);
	        System.out.println("Le joueur: " + player.getName() + " c'est deconnecter il est donc mort suite a ceci");
			GameListener.detectWin(gameState);
	    }
	}
}

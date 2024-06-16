package fr.nicknqck.roles.ds.demons;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.TeamList;

public class Assassin {
	@SuppressWarnings("deprecation")
	public void start(GameState gameState) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			 if (gameState.Assassin == null) {
	             gameState.Assassin = gameState.canBeAssassin.get(0);
	             gameState.Assassin.sendMessage("Vous êtes l'assassin vous possédez désormais§c 2"+Main.RH()+" supplémentaire de manière permanente, de plus faite attention au rôle de§a Tanjiro§f qui obtiendra un bonus s'il vous tue.");
	             gameState.Assassin.resetTitle();
	             gameState.Assassin.sendTitle("§c§lVous êtes l'§4§lAssassin", "§cVous obtenez donc 2"+Main.RH()+"§c supplémentaires !");
	             gameState.getPlayerRoles().get(gameState.Assassin).setMaxHealth(gameState.getPlayerRoles().get(gameState.Assassin).getMaxHealth()+4.0);
	             gameState.Assassin.setMaxHealth(gameState.getPlayerRoles().get(gameState.Assassin).getMaxHealth());
	             gameState.Assassin.setHealth(gameState.Assassin.getMaxHealth());
	             System.out.println(gameState.Assassin.getName()+" is now the Assassin of the game");
	             System.out.println("Ending Assassin System");
	             if (gameState.getServerState() != ServerStates.InGame)return;
	             for (Player p : gameState.getInGamePlayers()) {
	            	 if (!gameState.hasRoleNull(p)) {
	            		 if (gameState.getPlayerRoles().get(p).hasTeam(p)) {	            			 
	            			 if (gameState.getPlayerRoles().get(p).getTeam() == TeamList.Demon || gameState.getPlayerRoles().get(p).getRoles() == Roles.Tanjiro) {
		            			 p.sendMessage("§cL'Assassin à été désigné");
		            		 }
	            		 }
	            	 }
	             }
	         }
		}, 20L *gameState.TimingAssassin);
	}
}
package fr.nicknqck.roles.ds.demons;

import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
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
		Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
			 if (gameState.Assassin == null) {
	             gameState.Assassin = gameState.canBeAssassin.get(0);
				 if (gameState.Assassin == null) return;
	             gameState.Assassin.sendMessage("Vous êtes l'assassin vous possédez désormais§c 2"+AllDesc.coeur+" supplémentaire de manière permanente, de plus faite attention au rôle de§a Tanjiro§f qui obtiendra un bonus s'il vous tue.");
	             gameState.Assassin.resetTitle();
	             gameState.Assassin.sendTitle("§c§lVous êtes l'§4§lAssassin", "§cVous obtenez donc 2"+ AllDesc.coeur+"§c supplémentaires !");
				 RoleBase role = gameState.getPlayerRoles().get(gameState.Assassin);
	             role.setMaxHealth(role.getMaxHealth()+4.0);
	             gameState.Assassin.setMaxHealth(role.getMaxHealth());
	             gameState.Assassin.setHealth(role.getMaxHealth());
	             System.out.println(gameState.Assassin.getName()+" is now the Assassin of the game");
	             System.out.println("Ending Assassin System");
	             if (gameState.getServerState() != ServerStates.InGame)return;
	             for (Player p : gameState.getInGamePlayers()) {
	            	 if (!gameState.hasRoleNull(p)) {
	            		 if (gameState.getPlayerRoles().get(p).hasTeam(p)) {	            			 
	            			 if (gameState.getPlayerRoles().get(p).getOriginTeam() == TeamList.Demon || gameState.getPlayerRoles().get(p).getRoles() == Roles.Tanjiro) {
		            			 p.sendMessage("§cL'Assassin à été désigné");
		            		 }
	            		 }
	            	 }
	             }
	         }
		}, 20L *gameState.TimingAssassin);
	}
}
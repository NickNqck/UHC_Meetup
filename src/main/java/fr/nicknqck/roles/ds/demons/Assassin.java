package fr.nicknqck.roles.ds.demons;

import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.slayers.Tanjiro;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;

import java.util.Collections;
import java.util.UUID;

public class Assassin {
	@SuppressWarnings("deprecation")
	public void start(GameState gameState) {
		Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
			 if (gameState.Assassin == null) {
				 if (gameState.getServerState() != ServerStates.InGame)return;
				 Collections.shuffle(gameState.canBeAssassin, Main.RANDOM);
	             gameState.Assassin = gameState.canBeAssassin.get(0);
				 if (gameState.Assassin == null) return;
	             gameState.Assassin.sendMessage("Vous êtes l'assassin vous possédez désormais§c 2"+AllDesc.coeur+" supplémentaire de manière permanente, de plus faite attention au rôle de§a Tanjiro§f qui obtiendra un bonus s'il vous tue.");
	             gameState.Assassin.resetTitle();
	             gameState.Assassin.sendTitle("§c§lVous êtes l'§4§lAssassin", "§cVous obtenez donc 2"+ AllDesc.coeur+"§c supplémentaires !");
				 RoleBase role = gameState.getPlayerRoles().get(gameState.Assassin);
	             role.setMaxHealth(role.getMaxHealth()+4.0);
	             gameState.Assassin.setMaxHealth(role.getMaxHealth());
	             gameState.Assassin.setHealth(role.getMaxHealth());
				 role.setSuffixString(role.getSuffixString()+"§7 (§cAssassin§7)§r");
	             System.out.println(gameState.Assassin.getName()+" is now the Assassin of the game");
	             System.out.println("Ending Assassin System");
	             for (UUID u : gameState.getInGamePlayers()) {
					 Player p = Bukkit.getPlayer(u);
					 if (p == null)continue;
	            	 if (!gameState.hasRoleNull(p)) {
	            		 if (gameState.getGamePlayer().get(p.getUniqueId()).getRole().getTeam() != null) {
	            			 if (gameState.getPlayerRoles().get(p) instanceof DemonsRoles || gameState.getPlayerRoles().get(p) instanceof Tanjiro) {
		            			 p.sendMessage("§cL'Assassin à été désigné");
		            		 }
	            		 }
	            	 }
	             }
	         }
		}, 20L *gameState.TimingAssassin);
	}
}
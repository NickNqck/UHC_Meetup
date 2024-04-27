package fr.nicknqck.scoreboard;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.TeamList;
import fr.nicknqck.scenarios.impl.FFA;
import fr.nicknqck.utils.ArrowTargetUtils;
import fr.nicknqck.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

/*
 * This file is part of SamaGamesAPI.
 *
 * SamaGamesAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SamaGamesAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SamaGamesAPI.  If not, see <http://www.gnu.org/licenses/>.
 */
public class PersonalScoreboard {
    private final Player player;
    private final UUID uuid;
    private final ObjectiveSign objectiveSign;
    private final GameState gameState;
    PersonalScoreboard(Player player, GameState gameState){
        this.player = player;
        this.gameState = gameState;
        uuid = player.getUniqueId();
        objectiveSign = new ObjectiveSign("sidebar", Main.getInstance().PLUGIN_NAME);
        
        reloadData();
        objectiveSign.addReceiver(player);
    }

    public void reloadData(){}

    public void setLines(String ip){
    	String premsg = this.gameState.premsg;
    	if (this.gameState.getServerState() == ServerStates.InLobby) {
    		 	objectiveSign.setDisplayName(this.gameState.msgBoard);
    	        objectiveSign.setLine(0, premsg+"§1");
    	        objectiveSign.setLine(1, premsg+"§fJoueurs: §c"+this.gameState.getInLobbyPlayers().size()+"§r/§6"+this.gameState.getroleNMB());
    	        if (gameState.getMdj() == null) {
    	        	objectiveSign.setLine(2, premsg+"§fMDJ: Aucun");
    	        } else {
    	        	objectiveSign.setLine(2, premsg+"§fMDJ: "+gameState.getMdj().getItem().getItemMeta().getDisplayName());
    	        }
    	        if (FFA.getFFA()) {objectiveSign.setLine(3, premsg+"§fFFA: §6Activer");
 			   } else {objectiveSign.setLine(3, premsg+"§fFFA: §6Désactiver");}
    	        if (player.isOp()) {
    	        	objectiveSign.setLine(4, premsg+"§fGrade: §cAdmin");
    	        } else {
    	        	if (this.gameState.getHost().contains(player.getUniqueId())) {
    	        		objectiveSign.setLine(4, premsg+"§fGrade: §cHost");
    	        	}else {
    	        		objectiveSign.setLine(4, premsg+"§fGrade: Aucun");
    	        	}
    	        }
    	        objectiveSign.setLine(5, premsg+"§2");
    	        objectiveSign.setLine(6, premsg+ip);
    	        objectiveSign.removeScore("§c");
    	}else if (this.gameState.getServerState() == ServerStates.InGame){
    		objectiveSign.setDisplayName(this.gameState.msgBoard);
    		
    		objectiveSign.setLine(0, "§c");
    		if (!this.gameState.hasRoleNull(player)) {
    			RoleBase role = this.gameState.getPlayerRoles().get(player);
    			if (this.gameState.getPlayerRoles().get(player).getTeam() != null) {
    				objectiveSign.setLine(1, premsg+"§fRôle: "+role.getTeam().getColor()+role.type.name());
    			}else {
    				objectiveSign.setLine(1, premsg+"§fRôle: "+role.type.name());
    			}
    			
    		}
    		objectiveSign.setLine(2, premsg+"§fJoueurs:§c "+this.gameState.getInGamePlayers().size());
    		objectiveSign.setLine(3, premsg+"§fGroupe:§6 "+this.gameState.getGroupe());
    		objectiveSign.setLine(4, "§a");
    		objectiveSign.setLine(5, premsg+"§fDurée: "+StringUtils.secondsTowardsBeautifulinScoreboard(this.gameState.getInGameTime()));
    		if (this.gameState.getPvP()) {
    			objectiveSign.setLine(6, premsg+"§fPvP:§c Activée");
    		} else {
				objectiveSign.setLine(6, premsg+"§fPvP:§c "+ StringUtils.secondsTowardsBeautifulinScoreboard(this.gameState.getActualPvPTimer()));
			}
    		if (this.gameState.getInGameTime() < this.gameState.shrinkTimer) {
    			int time = this.gameState.shrinkTimer-this.gameState.getInGameTime();
				objectiveSign.setLine(7, premsg+"§fBordure: §c"+StringUtils.secondsTowardsBeautifulinScoreboard(time));
    		} else {
    			objectiveSign.setLine(7, premsg+"§fBordure:§c Activé");
    		}
    		if (this.gameState.nightTime) {
    			objectiveSign.setLine(8, premsg+"§9Nuit§r: "+StringUtils.secondsTowardsBeautifulinScoreboard(this.gameState.t));
    		}else {
				objectiveSign.setLine(8, premsg+"§eJour§r: "+StringUtils.secondsTowardsBeautifulinScoreboard(this.gameState.t));
			}
    		objectiveSign.setLine(9, "§0");
    		if (!this.gameState.hasRoleNull(player)) {
    			objectiveSign.setLine(10, premsg+"Kills:§6 "+this.gameState.getPlayerKills().get(player).size());
    		}
    		objectiveSign.setLine(11, premsg+"§fCentre: §6"+ArrowTargetUtils.calculateArrow(player, new Location(player.getWorld(), 0, player.getWorld().getHighestBlockYAt(new Location(player.getWorld(), 0, 0, 0)), 0))+this.gameState.getDecimalFormat("0").format(player.getLocation().distance(new Location(player.getWorld(), 0, player.getWorld().getHighestBlockYAt(new Location(player.getWorld(), 0, 0, 0)), 0))));
    		if (this.gameState.roletab) {
    			if (this.gameState.roleTimer < this.gameState.getInGameTime()) {
    				if (!this.gameState.hasRoleNull(player)) {
    					if (this.gameState.getPlayerRoles().get(player).getTeam() != null) {
    						this.gameState.changeTabPseudo(this.gameState.getPlayerRoles().get(player).getTeam().getColor()+this.gameState.getPlayerRoles().get(player).type.name()+" "+player.getDisplayName(), player);	
						}else {
							this.gameState.changeTabPseudo(this.gameState.getPlayerRoles().get(player).type.name()+" "+player.getDisplayName(), player);
						}
    				}
    			}
    		}
    		
    		if (gameState.getJubiCrafter() != null) {
    			if (gameState.getJubiCrafter().getUniqueId().equals(player.getUniqueId())) {
    				gameState.changeTabPseudo("§dJubi "+player.getDisplayName(), player);
    			} else if (gameState.getPlayerRoles().get(player).getTeam().equals(TeamList.Jubi)) {
    				gameState.changeTabPseudo("§d "+player.getDisplayName(), player);
    			}
    		}
    	}
    	if (gameState.getServerState() != ServerStates.GameEnded && gameState.getServerState() != ServerStates.InGame && gameState.getServerState() != ServerStates.InLobby) {
    		objectiveSign.setLine(0, "§c");
    		objectiveSign.setLine(1, "§a");
    		objectiveSign.setLine(2, "Scoreboard is glitched");
    		objectiveSign.setLine(3, "§6");
    		objectiveSign.setLine(4, "§b");
    		objectiveSign.setLine(5, "§1");
			objectiveSign.setLine(6, "§r");
			objectiveSign.setLine(7, "§m");
			objectiveSign.setLine(8, "§l");
    		objectiveSign.setLine(9, "§0");
    		objectiveSign.setLine(10, "§2");
    		objectiveSign.setLine(11, "§3");
    	}
        objectiveSign.updateLines();
    }
    public void onLogout(){
        objectiveSign.removeReceiver(Bukkit.getServer().getOfflinePlayer(uuid));
        System.out.println("removing "+Bukkit.getPlayer(uuid).getName()+" from PersonalScoreboard");
    }
}
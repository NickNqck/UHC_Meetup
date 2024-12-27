package fr.nicknqck.scoreboard;

import fr.nicknqck.Border;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.scenarios.impl.FFA;
import fr.nicknqck.utils.ArrowTargetUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.rank.ChatRank;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
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
	@Getter
	private final Map<UUID, String> colors;

    PersonalScoreboard(Player player, GameState gameState){
        this.player = player;
        this.gameState = gameState;
        uuid = player.getUniqueId();
		this.colors = new HashMap<>();
        objectiveSign = new ObjectiveSign("sidebar", Main.getInstance().PLUGIN_NAME);
        
        reloadData();
        objectiveSign.addReceiver(player);
    }

    public void reloadData(){}

    public void setLines(String ip){
    	String premsg = "§7§l ┃ §r";
		ChatRank.updateRank(this.player);
    	if (this.gameState.getServerState() == ServerStates.InLobby) {
    		 	objectiveSign.setDisplayName(this.gameState.msgBoard);
    	        objectiveSign.setLine(0, premsg+"§1");
    	        objectiveSign.setLine(1, premsg+"§fJoueurs: §c"+this.gameState.getInLobbyPlayers().size()+"§r/§6"+this.gameState.getroleNMB());
				objectiveSign.setLine(2, premsg+"§fMDJ: "+gameState.getMdj().getItem().getItemMeta().getDisplayName());
    	        if (FFA.getFFA()) {objectiveSign.setLine(3, premsg+"§fFFA: §6Activer");
 			   } else {objectiveSign.setLine(3, premsg+"§fFFA: §6Désactiver");}
				objectiveSign.setLine(4, premsg+"§fGrade: "+ChatRank.getPlayerGrade(this.player).getFullPrefix());
    	        objectiveSign.setLine(5, premsg+"§2");
    	        objectiveSign.setLine(6, premsg+ip);
    	        objectiveSign.removeScore("§c");
    	} else if (this.gameState.getServerState() == ServerStates.InGame){
    		objectiveSign.setDisplayName(this.gameState.msgBoard);
    		
    		objectiveSign.setLine(0, "§c");
    		if (!this.gameState.hasRoleNull(player.getUniqueId())) {
    			RoleBase role = this.gameState.getPlayerRoles().get(player);
				String roleName = role.getName();
				String iRole = "§fRôle: ";
				if (premsg.length()+iRole.length()+ roleName.length() + role.getTeam().getColor().length() >= 40) {
					roleName = role.getRoles().name();
				}
    			if (this.gameState.getPlayerRoles().get(player).getTeam() != null) {
    				objectiveSign.setLine(1, premsg+iRole+role.getTeam().getColor()+roleName);
    			}else {
    				objectiveSign.setLine(1, premsg+iRole+roleName);
    			}
    		}
    		objectiveSign.setLine(2, premsg+"§fJoueurs:§c "+this.gameState.getInGamePlayers().size());
    		objectiveSign.setLine(3, premsg+"§fGroupe:§6 "+this.gameState.getGroupe());
    		objectiveSign.setLine(4, "§a");
    		objectiveSign.setLine(5, premsg+"§fDurée: "+StringUtils.secondsTowardsBeautifulinScoreboard(this.gameState.getInGameTime()));
    		if (Main.getInstance().getGameConfig().isPvpEnable()) {
    			objectiveSign.setLine(6, premsg+"§fPvP:§c Activée");
    		} else {
				objectiveSign.setLine(6, premsg+"§fPvP:§c "+ StringUtils.secondsTowardsBeautifulinScoreboard(this.gameState.getActualPvPTimer()));
			}
    		if (this.gameState.getInGameTime() < Border.getTempReduction()) {
    			int time = Border.getTempReduction()-this.gameState.getInGameTime();
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
    		if (!this.gameState.hasRoleNull(player.getUniqueId())) {
    			objectiveSign.setLine(10, premsg+"Kills:§6 "+this.gameState.getPlayerKills().get(player.getUniqueId()).size());
    		}
    		objectiveSign.setLine(11, premsg+"§fCentre: §6"+ArrowTargetUtils.calculateArrow(player, new Location(player.getWorld(), 0, player.getWorld().getHighestBlockYAt(new Location(player.getWorld(), 0, 0, 0)), 0))+new DecimalFormat("0").format(player.getLocation().distance(new Location(player.getWorld(), 0, player.getWorld().getHighestBlockYAt(new Location(player.getWorld(), 0, 0, 0)), 0))));
    		if (this.gameState.roletab) {
    			if (this.gameState.roleTimer < this.gameState.getInGameTime()) {
    				if (!this.gameState.hasRoleNull(player.getUniqueId())) {
    					if (this.gameState.getPlayerRoles().get(player).getOriginTeam() != null) {
    						this.gameState.changeTabPseudo(this.gameState.getPlayerRoles().get(player).getOriginTeam().getColor()+this.gameState.getPlayerRoles().get(player).getRoles().name()+" "+player.getDisplayName(), player);
						}else {
							this.gameState.changeTabPseudo(this.gameState.getPlayerRoles().get(player).getRoles().name()+" "+player.getDisplayName(), player);
						}
    				}
    			}
    		}
    		if (gameState.getJubiCrafter() != null) {
    			if (gameState.getJubiCrafter().getUniqueId().equals(player.getUniqueId())) {
    				gameState.changeTabPseudo("§dJubi "+player.getDisplayName(), player);
    			} else if (gameState.getPlayerRoles().get(player).getOriginTeam().equals(TeamList.Jubi)) {
    				gameState.changeTabPseudo("§d "+player.getDisplayName(), player);
    			}
    		}
    	}
        objectiveSign.updateLines();
    }
    public void onLogout(){
        objectiveSign.removeReceiver(Bukkit.getServer().getOfflinePlayer(uuid));
        System.out.println("removing "+Bukkit.getPlayer(uuid).getName()+" from PersonalScoreboard");
    }
	public void changeDisplayName(final Player sender, final Player target, final String color) {
        this.getColors().remove(target.getUniqueId());
		this.getColors().put(target.getUniqueId(), color);
		setCustomName(sender, target, color);
	}
	/*@param
        - player = Le mec qui verra le pseudo
        - target = Le mec à qui on veut changer le pseudo
        - customName = Le nom qui sera afficher
     */
	private void setCustomName(Player player, Player target, String customName) {
		Scoreboard scoreboard = player.getScoreboard();
		Team team = scoreboard.getTeam(target.getName());
		if (team == null) {
			team = scoreboard.registerNewTeam(target.getName());
		}
		team.setPrefix(customName);
		team.setCanSeeFriendlyInvisibles(false);
		team.addEntry(target.getName());
	}
}
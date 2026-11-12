package fr.nicknqck.scoreboard;

import fr.nicknqck.Border;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.scoreboard.ScoreBoardUpdateEvent;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.scenarios.impl.FFA;
import fr.nicknqck.utils.ArrowTargetUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.rank.ChatRank;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

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
@Getter
public class PersonalScoreboard {

    private final Player player;
    private final UUID uuid;
    private final ObjectiveSign objectiveSign;
    private final GameState gameState;

    /*
     * setLines() est appelé hors du thread principal (exécuteur du ScoreboardManager).
     * Les lignes y sont préparées, puis le ScoreBoardUpdateEvent et l'envoi des lignes
     * se font sur le thread principal (runTask). Tant que cette tâche n'est pas terminée,
     * les rafraîchissements suivants sont ignorés : les lignes ne sont donc jamais
     * modifiées par un thread pendant que l'autre les lit.
     */
    private final AtomicBoolean updatePending = new AtomicBoolean(false);
    /*
     * Hauteur du bloc le plus haut en (0, 0) : lue uniquement sur le thread principal
     * (au plus une fois par seconde) et mise en cache pour la ligne "Centre".
     */
    private volatile String centerYWorld = null;
    private volatile int centerY = 0;
    private long centerYTime = 0L;

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
        if (!Main.getInstance().isEnabled()) return;
        // Un rafraîchissement est encore en attente sur le thread principal : on saute celui-ci
        if (!updatePending.compareAndSet(false, true)) return;
        boolean scheduled = false;
        try {
            buildLines(ip);
            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                try {
                    if (this.gameState.getServerState() == ServerStates.InGame && this.player.isOnline()) {
                        refreshCenterY();
                    }
                    Bukkit.getPluginManager().callEvent(new ScoreBoardUpdateEvent(this.objectiveSign.lines, this.objectiveSign.scores, this));
                    objectiveSign.updateLines();
                } finally {
                    updatePending.set(false);
                }
            });
            scheduled = true;
        } finally {
            // Erreur pendant la préparation ou plugin en cours d'arrêt : on libère le verrou
            if (!scheduled) updatePending.set(false);
        }
    }

    /** Prépare les lignes du scoreboard (appelé hors du thread principal). */
    private void buildLines(String ip){
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
    			RoleBase role = this.gameState.getGamePlayer().get(player.getUniqueId()).getRole();
				String roleName = role.getName();
				String iRole = "§fRôle: ";
				if (premsg.length()+iRole.length()+ roleName.length() + role.getTeam().getColor().length() >= 40) {
					roleName = role.getRoles().getItem().getItemMeta().getDisplayName();
				}
				if (premsg.length()+iRole.length()+role.getRoles().getItem().getItemMeta().getDisplayName().length() >= 40) {
					roleName = String.valueOf(role.getRoles());
				}
    			if (role.getTeam() != null) {
    				objectiveSign.setLine(1, premsg+iRole+role.getTeam().getColor()+roleName);
    			}else {
    				objectiveSign.setLine(1, premsg+iRole+roleName);
    			}
    		}
    		objectiveSign.setLine(2, premsg+"§fJoueurs:§c "+this.gameState.getInGamePlayers().size());
    		objectiveSign.setLine(3, premsg+"§fGroupe:§6 "+Main.getInstance().getGameConfig().getGroupe());
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
    			objectiveSign.setLine(8, premsg+"§9Nuit§r: "+StringUtils.secondsTowardsBeautifulinScoreboard(this.gameState.getDayTimer()));
    		}else {
				objectiveSign.setLine(8, premsg+"§eJour§r: "+StringUtils.secondsTowardsBeautifulinScoreboard(this.gameState.getDayTimer()));
			}
    		objectiveSign.setLine(9, "§0");
    		if (!this.gameState.hasRoleNull(player.getUniqueId())) {
    			final fr.nicknqck.player.GamePlayer gamePlayer = gameState.getGamePlayer().get(player.getUniqueId());
    			objectiveSign.setLine(10, premsg+"K/A:§6 "+gamePlayer.getKillAmounts()+"§7/§6"+gamePlayer.getAssistAmounts());
    		}
    		final Location center = new Location(player.getWorld(), 0, getCenterY(player.getWorld()), 0);
    		objectiveSign.setLine(11, premsg+"§fCentre: §6"+ArrowTargetUtils.calculateArrow(player, center)+new DecimalFormat("0").format(player.getLocation().distance(center)));
    		if (this.gameState.roletab) {
    			if (this.gameState.roleTimer < this.gameState.getInGameTime()) {
    				if (!this.gameState.hasRoleNull(player.getUniqueId())) {
						final RoleBase role = this.gameState.getGamePlayer().get(player.getUniqueId()).getRole();
						Main.getInstance().getCustomTabManager().setPrefixForAll(role.getPlayer(), role.getOriginTeam().getColor()+role.getName()+" §r");
                    }
    			}
    		}
    	}
    }
    /**
     * Retourne la hauteur en cache du bloc le plus haut en (0, 0) dans {@code world},
     * sans toucher au monde depuis ce thread. Tant qu'aucune valeur n'est connue pour
     * ce monde (premier rafraîchissement), la hauteur du joueur est utilisée.
     */
    private int getCenterY(World world) {
        return world.getName().equals(centerYWorld) ? centerY : player.getLocation().getBlockY();
    }

    /**
     * Met à jour la hauteur en cache du centre (0, 0) du monde du joueur.
     * Appelé uniquement sur le thread principal, au plus une fois par seconde par monde :
     * le monde est donc forcément celui où se trouve le joueur à cet instant (chargé).
     */
    private void refreshCenterY() {
        final World world = this.player.getWorld();
        final long now = System.currentTimeMillis();
        if (world.getName().equals(centerYWorld) && now - centerYTime < 1000L) return;
        centerY = world.getHighestBlockYAt(0, 0);
        centerYWorld = world.getName();
        centerYTime = now;
    }

    public void onLogout(){
        objectiveSign.removeReceiver(Bukkit.getServer().getOfflinePlayer(uuid));
        System.out.println("removing "+Bukkit.getPlayer(uuid).getName()+" from PersonalScoreboard");
    }
}
package fr.nicknqck.player;

import fr.nicknqck.roles.builder.TeamList;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class PlayerInfo {

    private final Map<String, Integer> rolesPlayed = new LinkedHashMap<>();
    private final Map<TeamList, Integer> teamPlayed = new LinkedHashMap<>();
    private final Map<UUID, Integer> playerKills = new LinkedHashMap<>();
    private final UUID uuid;
    private int joinCount = 0;
    private int entitiesKilled = 0;
    private int arrowsShot = 0;
    private int amountTeamChange = 0;
    private int gameWin = 0;
    private int gameLoose = 0;
    private int gamePlayed = 0;
    private int totalKills = 0;
    private int deaths = 0;
    private int timePlayed = 0;

    public PlayerInfo(UUID uuid) {
        this.uuid = uuid;
    }

    public void incrementJoin() { joinCount++; }
    public void incrementKills() { entitiesKilled++; }
    public void incrementArrows() { arrowsShot++; }
    public void addAmountTeamChange() { this.amountTeamChange++;}
    public void addTimePlayed() {this.timePlayed++;}
    public void addGameWin() {
        this.gameWin++;
    }
    public void addGameLoose() {
        this.gameLoose++;
    }
    public void addGamePlayed() {
        System.out.println("Added gamePlayed");
        this.gamePlayed++;
    }

    public void incrementRolePlayed(String roleName) {
        rolesPlayed.put(roleName, rolesPlayed.getOrDefault(roleName, 0) + 1);
    }
    public void addTeamPlayed(final TeamList teamList) {
        this.teamPlayed.put(teamList, this.teamPlayed.getOrDefault(teamList, 0)+1);
    }
    public void addKill(UUID victimUUID) {
        playerKills.put(victimUUID, playerKills.getOrDefault(victimUUID, 0) + 1);
        totalKills++;
    }
    public void addDeath() {
        deaths++;
    }
    public String getMostKilledPlayerName() {
        return playerKills.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(entry.getKey());
                    String name = (offlinePlayer != null && offlinePlayer.getName() != null) ? offlinePlayer.getName() : "Inconnu";
                    int count = entry.getValue();
                    double percentage = (totalKills == 0) ? 0 : (count * 100.0 / totalKills);
                    return name + " (" + String.format("%.1f", percentage) + "%)";
                })
                .orElse("Aucun");
    }

    public String getKDRatio() {
        if (deaths == 0) return totalKills + "/0";//Si on n'est jamais mort on return notre nombre de kill / 0
        return totalKills +"/"+this.deaths+ " (" + String.format("%.2f", totalKills / (double) deaths) + ")";
    }

}
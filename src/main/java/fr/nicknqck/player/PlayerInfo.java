package fr.nicknqck.player;

import fr.nicknqck.roles.builder.TeamList;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class PlayerInfo {

    private final Map<String, Integer> rolesPlayed = new LinkedHashMap<>();
    private final Map<TeamList, Integer> teamPlayed = new LinkedHashMap<>();
    private int joinCount = 0;
    private int quitCount = 0;
    private int entitiesKilled = 0;
    private int arrowsShot = 0;
    private int amountTeamChange = 0;

    public void incrementJoin() { joinCount++; }
    public void incrementQuit() { quitCount++; }
    public void incrementKills() { entitiesKilled++; }
    public void incrementArrows() { arrowsShot++; }
    public void addAmountTeamChange() { this.amountTeamChange++;}

    public void incrementRolePlayed(String roleName) {
        rolesPlayed.put(roleName, rolesPlayed.getOrDefault(roleName, 0) + 1);
    }
    public void addTeamPlayed(final TeamList teamList) {
        this.teamPlayed.put(teamList, this.teamPlayed.getOrDefault(teamList, 0)+1);
    }
}

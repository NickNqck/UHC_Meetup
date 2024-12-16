package fr.nicknqck.scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;

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
public class ScoreboardManager {
    @Getter
    private final Map<UUID, PersonalScoreboard> scoreboards;
    @SuppressWarnings({ "unused", "rawtypes" })
	private final ScheduledFuture glowingTask;
    @SuppressWarnings({ "unused", "rawtypes" })
	private final ScheduledFuture reloadingTask;
    private int ipCharIndex;
    private int cooldown;
    private final GameState gameState;
    public ScoreboardManager(GameState gameState) {
    	this.gameState = gameState;
        scoreboards = new HashMap<>();
        ipCharIndex = 0;
        cooldown = 0;

        glowingTask = Main.getInstance().getScheduledExecutorService().scheduleAtFixedRate(() ->
        {
            String ip = colorIpAt();
            for (PersonalScoreboard scoreboard : scoreboards.values())
                Main.getInstance().getExecutorMonoThread().execute(() -> scoreboard.setLines(ip));
        }, 80, 80, TimeUnit.MILLISECONDS);

        reloadingTask = Main.getInstance().getScheduledExecutorService().scheduleAtFixedRate(() ->
        {
            for (PersonalScoreboard scoreboard : scoreboards.values())
                Main.getInstance().getExecutorMonoThread().execute(scoreboard::reloadData);
        }, 1, 1, TimeUnit.SECONDS);
    }

    public void onDisable() {
    	if (scoreboards != null) {
    		if (!scoreboards.isEmpty()) {
    			scoreboards.values().forEach(PersonalScoreboard::onLogout);
    		}
    	}
    }

    public void onLogin(Player player) {
        if (scoreboards.containsKey(player.getUniqueId())) {
        	onLogout(player);
        }
        scoreboards.put(player.getUniqueId(), new PersonalScoreboard(player, gameState));
        System.out.println("put "+player.getName()+" for PersonalScoreboard");
    }
    
    public void onEnable() {
    	scoreboards.clear();
    	for (Player p : Bukkit.getOnlinePlayers()) {
    		scoreboards.put(p.getUniqueId(), new PersonalScoreboard(p, gameState));
    	}
    }

    public void onLogout(Player player) {
        if (scoreboards.containsKey(player.getUniqueId())) {
            scoreboards.get(player.getUniqueId()).onLogout();
            scoreboards.remove(player.getUniqueId());
        }
    }

    public void update(Player player) {
        if (scoreboards.containsKey(player.getUniqueId())) {
            scoreboards.get(player.getUniqueId()).reloadData();
        }
    }

    private String colorIpAt() {
        String ip = "discord.gg/6dWxCAEsfF";

        if (cooldown > 0) {
            cooldown--;
            return ChatColor.AQUA + ip;
        }

        StringBuilder formattedIp = new StringBuilder();

        if (ipCharIndex > 0) {
            formattedIp.append(ip.substring(0, ipCharIndex - 1));
            formattedIp.append(ChatColor.BLUE).append(ip.substring(ipCharIndex - 1, ipCharIndex));
        } else {
            formattedIp.append(ip.substring(0, ipCharIndex));
        }

        formattedIp.append(ChatColor.DARK_BLUE).append(ip.charAt(ipCharIndex));

        if (ipCharIndex + 1 < ip.length()) {
            formattedIp.append(ChatColor.BLUE).append(ip.charAt(ipCharIndex + 1));

            if (ipCharIndex + 2 < ip.length())
                formattedIp.append(ChatColor.AQUA).append(ip.substring(ipCharIndex + 2));

            ipCharIndex++;
        } else {
            ipCharIndex = 0;
            cooldown = 50;
        }

        return ChatColor.AQUA + formattedIp.toString();
    }

}
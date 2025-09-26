package fr.nicknqck.managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayersNameManager {

    private final File file;
    private final FileConfiguration config;

    public PlayersNameManager(File dataFolder) {
        this.file = new File(dataFolder, "playersname.yml");

        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    Bukkit.getLogger().info("Successfuly created file playersname.yml");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    // Met à jour le nom du joueur à sa connexion
    public void savePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        config.set(uuid.toString(), name);
        saveFile();
    }

    // Récupère le nom connu pour un UUID
    public String getPlayerName(UUID uuid) {
        String name = config.getString(uuid.toString());
        return name != null ? name : "Inconnu";
    }

    // Sauvegarde le fichier
    private void saveFile() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

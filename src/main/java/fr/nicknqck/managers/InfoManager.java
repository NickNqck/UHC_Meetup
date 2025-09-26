package fr.nicknqck.managers;

import com.google.gson.Gson;
import fr.nicknqck.player.PlayerInfo;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.*;

public class InfoManager {

    private final File playerDataFolder;
    private final Gson gson = new Gson();
    private final Map<UUID, PlayerInfo> data = new HashMap<>();

    public InfoManager(File dataFolder) {
        this.playerDataFolder = new File(dataFolder, "playerdata");
        if (!playerDataFolder.exists()) {
            if (!playerDataFolder.mkdirs()) {
                Bukkit.getLogger().info("[InfoManager] couldn't create dataFolder");
            } else {
                Bukkit.getLogger().info("[InfoManager] Successfully created dataFolder !");
            }
        }
        loadAll();
    }

    // Obtenir les infos d'un joueur
    public PlayerInfo getPlayerInfo(UUID uuid) {
        if (!this.data.containsKey(uuid)) {
            load(uuid);
        }
        return data.get(uuid);
    }

    // Enregistrer une info spécifique
    public void save(UUID uuid) {
        PlayerInfo info = data.get(uuid);
        if (info == null) return;

        File file = new File(playerDataFolder, uuid.toString() + ".json");
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(info, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Charger une info spécifique
    public void load(UUID uuid) {
        File file = new File(playerDataFolder, uuid.toString() + ".json");
        if (!file.exists()) {
            data.put(uuid, new PlayerInfo(uuid));
            save(uuid);
            return;
        }

        try (Reader reader = new FileReader(file)) {
            PlayerInfo info = gson.fromJson(reader, PlayerInfo.class);
            if (info != null) {
                data.put(uuid, info);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Charger tous les fichiers
    private void loadAll() {
        File[] files = playerDataFolder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return;

        for (File file : files) {
            String name = file.getName().replace(".json", "");
            try {
                UUID uuid = UUID.fromString(name);
                load(uuid);
            } catch (IllegalArgumentException e) {
                System.out.println("Fichier invalide ignoré : " + name);
            }
        }
    }
    public String getMostPlayedRoleInfo(final UUID uuid) {
        final PlayerInfo info = getPlayerInfo(uuid);
        if (info.getRolesPlayed().isEmpty()) return "Aucun rôle joué pour le moment.";

        String mostPlayedRole = null;
        int maxCount = 0;
        int total = 0;

        for (Map.Entry<String, Integer> entry : info.getRolesPlayed().entrySet()) {
            int count = entry.getValue();
            total += count;

            if (count > maxCount) {
                mostPlayedRole = entry.getKey();
                maxCount = count;
            }
        }

        if (mostPlayedRole == null || total == 0) return "Aucun rôle joué pour le moment.";

        double percentage = (maxCount * 100.0) / total;
        return "§7Rôle le plus joué : §e" + mostPlayedRole + " §7(" + maxCount + " fois, " + String.format("%.2f", percentage) + "%)";
    }
}
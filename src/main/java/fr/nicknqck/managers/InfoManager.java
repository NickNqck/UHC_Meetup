package fr.nicknqck.managers;

import com.google.gson.Gson;
import fr.nicknqck.player.PlayerInfo;

import java.io.*;
import java.util.*;

public class InfoManager {

    private final File playerDataFolder;
    private final Gson gson = new Gson();
    private final Map<UUID, PlayerInfo> data = new HashMap<>();

    public InfoManager(File dataFolder) {
        this.playerDataFolder = new File(dataFolder, "playerdata");
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
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
            data.put(uuid, new PlayerInfo());
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
}
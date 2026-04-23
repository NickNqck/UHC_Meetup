package fr.nicknqck.managers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import fr.nicknqck.Main;
import fr.nicknqck.enums.TeamList;
import fr.nicknqck.interfaces.ITeam;
import fr.nicknqck.player.PlayerInfo;
import org.bukkit.Bukkit;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class InfoManager {

    private final File playerDataFolder;
    private final Gson gson;
    private final Map<UUID, PlayerInfo> data = new HashMap<>();

    /**
     * Registre des implémentations connues de ITeam.
     * TeamList est toujours en première position → prioritaire en cas de conflit de nom.
     */
    private static final List<Class<? extends Enum<?>>> ITEAM_IMPLEMENTATIONS = new ArrayList<>();
    static {
        // TeamList est enregistrée en dur en tête de liste — elle est toujours prioritaire
        ITEAM_IMPLEMENTATIONS.add(TeamList.class);
    }

    /**
     * Permet à un plugin tiers d'enregistrer son propre enum ITeam.
     * Si un nom de valeur existe déjà dans TeamList, TeamList restera prioritaire
     * et un message de debug sera émis dans la console.
     *
     * @param clazz L'enum implémentant ITeam à enregistrer
     * @throws IllegalArgumentException si la classe n'implémente pas ITeam
     */
    public static void registerITeamImplementation(Class<? extends Enum<?>> clazz) {
        if (!ITeam.class.isAssignableFrom(clazz))
            throw new IllegalArgumentException(clazz.getName() + " n'implémente pas ITeam");

        // Ne pas enregistrer TeamList deux fois
        if (clazz.equals(TeamList.class)) return;

        // Détecter les conflits de noms avec les implémentations déjà enregistrées
        for (Enum<?> newConstant : clazz.getEnumConstants()) {
            for (Class<? extends Enum<?>> existing : ITEAM_IMPLEMENTATIONS) {
                for (Enum<?> existingConstant : existing.getEnumConstants()) {
                    if (existingConstant.name().equals(newConstant.name())) {
                        Main.getInstance().debug(
                                "[InfoManager] Conflit de nom ITeam détecté : '"
                                        + newConstant.name()
                                        + "' existe dans " + existing.getSimpleName()
                                        + " ET dans " + clazz.getSimpleName()
                                        + ". " + existing.getSimpleName() + " est prioritaire."
                        );
                    }
                }
            }
        }

        // Ajout en queue → TeamList (index 0) reste toujours prioritaire
        ITEAM_IMPLEMENTATIONS.add(clazz);
        Main.getInstance().debug("[InfoManager] ITeam enregistrée : " + clazz.getSimpleName());
    }

    public InfoManager(File dataFolder) {
        this.gson = buildGson();

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

    // ── Gson avec support ITeam ───────────────────────────────────────────────

    private static Gson buildGson() {
        return new GsonBuilder()
                // Sérialisation : ITeam → son nom enum (ex: "Akatsuki")
                .registerTypeHierarchyAdapter(ITeam.class, (JsonSerializer<ITeam>) (src, typeOfSrc, ctx) ->
                        new JsonPrimitive(src.name())
                )
                // Désérialisation : nom enum → implémentation ITeam connue
                // TeamList est toujours en tête de liste → prioritaire en cas de conflit
                .registerTypeHierarchyAdapter(ITeam.class, (JsonDeserializer<ITeam>) (json, typeOfT, ctx) -> {
                    final String name = json.getAsString();
                    for (Class<? extends Enum<?>> clazz : ITEAM_IMPLEMENTATIONS) {
                        for (Enum<?> constant : clazz.getEnumConstants()) {
                            if (constant.name().equals(name)) {
                                return (ITeam) constant;
                            }
                        }
                    }
                    Main.getInstance().debug(
                            "[InfoManager] Aucune ITeam trouvée pour la valeur '" + name
                                    + "'. Vérifiez que l'implémentation est bien enregistrée via registerITeamImplementation()."
                    );
                    return null;
                })
                // Les clés Map<ITeam,?> sont sérialisées en String via name()
                // et relues comme ITeam via TeamList.valueOf
                .registerTypeAdapterFactory(new ITeamMapKeyAdapterFactory())
                .create();
    }

    /**
     * Factory qui gère spécifiquement Map<ITeam, Integer> :
     * Gson sérialise les clés de Map non-String sous forme d'objet JSON par défaut,
     * ce qui est incompatible avec ITeam (interface). On force la clé en String.
     */
    private static class ITeamMapKeyAdapterFactory implements TypeAdapterFactory {

        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            // On ne prend en charge que Map<ITeam, Integer>
            if (!Map.class.isAssignableFrom(type.getRawType())) return null;

            final TypeAdapter<Map<String, Integer>> stringMapAdapter = gson.getAdapter(new TypeToken<Map<String, Integer>>(){});

            @SuppressWarnings("unchecked")
            final TypeAdapter<T> adapter = (TypeAdapter<T>) new TypeAdapter<Map<ITeam, Integer>>() {

                @Override
                public void write(com.google.gson.stream.JsonWriter out, Map<ITeam, Integer> value) throws IOException {
                    if (value == null) { out.nullValue(); return; }
                    // Convertit Map<ITeam, Integer> → Map<String, Integer>
                    Map<String, Integer> stringMap = new LinkedHashMap<>();
                    for (Map.Entry<ITeam, Integer> entry : value.entrySet()) {
                        stringMap.put(entry.getKey().name(), entry.getValue());
                    }
                    stringMapAdapter.write(out, stringMap);
                }

                @Override
                public Map<ITeam, Integer> read(com.google.gson.stream.JsonReader in) throws IOException {
                    Map<String, Integer> stringMap = stringMapAdapter.read(in);
                    if (stringMap == null) return new LinkedHashMap<>();
                    // Convertit Map<String, Integer> → Map<ITeam, Integer>
                    Map<ITeam, Integer> result = new LinkedHashMap<>();
                    for (Map.Entry<String, Integer> entry : stringMap.entrySet()) {
                        try {
                            ITeam found = null;
                            for (Class<? extends Enum<?>> clazz : ITEAM_IMPLEMENTATIONS) {
                                for (Enum<?> constant : clazz.getEnumConstants()) {
                                    if (constant.name().equals(entry.getKey())) {
                                        found = (ITeam) constant;
                                        break;
                                    }
                                }
                                if (found != null) break;
                            }
                            if (found != null) {
                                result.put(found, entry.getValue());
                            } else {
                                Main.getInstance().debug(
                                        "[InfoManager] Clé Map<ITeam> inconnue ignorée : '" + entry.getKey()
                                                + "'. Vérifiez que l'implémentation est bien enregistrée via registerITeamImplementation()."
                                );
                            }
                        } catch (Exception e) {
                            Main.getInstance().debug("[InfoManager] Erreur lecture clé ITeam : " + entry.getKey() + " — " + e.getMessage());
                        }
                    }
                    return result;
                }
            };

            // On n'applique l'adapter que si la Map contient des clés ITeam
            // (détection via la présence du type générique exact)
            try {
                java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) type.getType();
                Type keyType = pt.getActualTypeArguments()[0];
                if (keyType instanceof Class && ITeam.class.isAssignableFrom((Class<?>) keyType)) {
                    return adapter;
                }
            } catch (Exception ignored) {}

            return null;
        }
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    public PlayerInfo getPlayerInfo(UUID uuid) {
        if (!this.data.containsKey(uuid)) {
            load(uuid);
        }
        return data.get(uuid);
    }

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

    // ── Méthodes métier ───────────────────────────────────────────────────────

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

    public void resetData(UUID uuid) {
        if (!this.data.containsKey(uuid)) return;
        PlayerInfo actual = getPlayerInfo(uuid);
        int i = actual.getResetAmount();
        this.data.put(uuid, new PlayerInfo(uuid));
        actual = getPlayerInfo(uuid);
        i++;
        actual.setResetAmount(i);
        save(uuid);
    }
}
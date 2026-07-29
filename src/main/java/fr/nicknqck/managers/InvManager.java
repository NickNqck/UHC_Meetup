package fr.nicknqck.managers;

import fr.nicknqck.Main;
import fr.nicknqck.utils.event.EventUtils;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class InvManager implements Listener {

    private final File invFolder;
    @Getter
    private final List<UUID> configuring;

    public InvManager() {
        // Création de l'objet File pointant vers le dossier "invs"
        this.invFolder = new File(Main.getInstance().getDataFolder(), "invs");
        this.configuring = new ArrayList<>();
        // Si le dossier n'existe pas, on le crée
        if (!this.invFolder.exists()) {
            this.invFolder.mkdirs();
        }
        EventUtils.registerEvents(this);
    }

    /**
     * Sauvegarde l'inventaire complet d'un joueur dans un fichier <UUID>.yml
     * @param player Le joueur dont on veut sauvegarder l'inventaire
     */
    public void saveInventory(Player player) {
        File file = new File(invFolder, player.getUniqueId().toString() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        // Sauvegarde du dernier pseudo connu
        config.set("pseudo", player.getName());

        // On nettoie l'ancienne sauvegarde de l'inventaire s'il y en avait une
        config.set("inventaire", null);

        PlayerInventory inv = player.getInventory();
        // En 1.8.8, getSize() retourne 36, et l'armure va de 36 à 39.
        // On boucle jusqu'à 40 (exclu) pour prendre l'inventaire et l'armure.
        for (int i = 0; i < 40; i++) {
            ItemStack item = inv.getItem(i);

            // On ne sauvegarde que s'il y a un item valide (différent de null et de l'air)
            if (item != null && item.getType() != Material.AIR) {
                config.set("inventaire." + i, item);
            }
        }

        // Sauvegarde du fichier
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
            Main.getInstance().getLogger().severe("Impossible de sauvegarder l'inventaire de " + player.getName());
        }
    }

    /**
     * Restaure l'inventaire d'un joueur depuis son fichier <UUID>.yml
     * @param player Le joueur dont on veut restaurer l'inventaire
     */
    public void restoreInventory(Player player) {
        File file = new File(invFolder, player.getUniqueId().toString() + ".yml");

        // Si le joueur n'a pas de fichier de sauvegarde, on ne fait rien
        if (!file.exists()) {
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection invSection = config.getConfigurationSection("inventaire");

        if (invSection != null) {
            // On vide l'inventaire actuel avant de lui rendre ses items
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);

            // On parcourt les clés (qui sont les numéros des slots)
            for (String key : invSection.getKeys(false)) {
                try {
                    int slot = Integer.parseInt(key);
                    ItemStack item = invSection.getItemStack(key);

                    if (item != null) {
                        player.getInventory().setItem(slot, item);
                    }
                } catch (NumberFormatException e) {
                    Main.getInstance().getLogger().warning("Erreur de lecture du slot " + key + " pour " + player.getName());
                }
            }
            // On met à jour l'inventaire du joueur pour éviter les bugs visuels (typique de la 1.8)
            player.updateInventory();
        }
    }
    /**
     * Vérifie si un joueur possède déjà un inventaire sauvegardé.
     * @param player Le joueur à vérifier
     * @return true si un fichier de sauvegarde existe, sinon false
     */
    public boolean hasSavedInventory(Player player) {
        File file = new File(invFolder, player.getUniqueId().toString() + ".yml");
        return file.exists();
    }
    /**
     * Récupère l'inventaire sauvegardé d'un joueur sous forme de Map triée.
     * @param player Le joueur dont on veut récupérer l'inventaire
     * @return Une Map contenant le numéro du slot en clé et l'ItemStack en valeur, triée par ordre croissant des slots.
     */
    public Map<Integer, ItemStack> getSavedInventoryAsMap(Player player) {
        // Utilisation d'un TreeMap pour garantir que les clés soient triées du plus petit au plus grand
        Map<Integer, ItemStack> inventoryMap = new TreeMap<>();
        File file = new File(invFolder, player.getUniqueId().toString() + ".yml");

        // Si le fichier n'existe pas, on retourne une Map vide
        if (!file.exists()) {
            return inventoryMap;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection invSection = config.getConfigurationSection("inventaire");

        if (invSection != null) {
            // On parcourt les clés de la configuration (les numéros de slots)
            for (String key : invSection.getKeys(false)) {
                try {
                    int slot = Integer.parseInt(key);
                    ItemStack item = invSection.getItemStack(key);

                    if (item != null) {
                        inventoryMap.put(slot, item);
                    }
                } catch (NumberFormatException e) {
                    Main.getInstance().getLogger().warning("Erreur de lecture du slot " + key + " pour " + player.getName());
                }
            }
        }

        return inventoryMap;
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onLeave(@NonNull final PlayerQuitEvent event) {
        this.configuring.remove(event.getPlayer().getUniqueId());
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(@NonNull final PlayerJoinEvent event) {
        this.configuring.remove(event.getPlayer().getUniqueId());
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(@NonNull final PlayerInteractEvent event) {
        if (!this.configuring.contains(event.getPlayer().getUniqueId())) return;
        event.setCancelled(true);
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(@NonNull final PlayerItemConsumeEvent event) {
        if (!this.configuring.contains(event.getPlayer().getUniqueId())) return;
        event.setCancelled(true);
    }
}
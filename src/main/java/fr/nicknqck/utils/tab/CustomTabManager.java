package fr.nicknqck.utils.tab;

import fr.nicknqck.Main;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Gestionnaire de tab list personnalisées.
 * <p>
 * Chaque joueur possède sa propre {@link PlayerTab} avec :
 *  - Prefix/suffix/color personnalisés par cible
 *  - Option de conserver les joueurs déconnectés dans la tab
 * <p>
 * Utilisation :
 * <pre>
 *   CustomTabManager tab = Main.getInstance().getCustomTabManager();
 *
 *   // Mettre une couleur rouge pour tous les viewers sur le joueur "target"
 *   tab.setColorForAll(target.getUniqueId(), ChatColor.RED);
 *
 *   // Pour un viewer spécifique uniquement
 *   tab.setPrefix(viewer.getUniqueId(), target.getUniqueId(), "§c[Host] ");
 *
 *   // Garder le joueur dans le tab même après déconnexion
 *   tab.setKeepWhenOffline(viewer.getUniqueId(), target.getUniqueId(), true);
 * </pre>
 */
@SuppressWarnings("unused")
public class CustomTabManager implements Listener {

    /** Map viewer UUID → son tab personnalisé */
    private final Map<UUID, PlayerTab> playerTabs = new HashMap<>();

    public CustomTabManager() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
        // Initialiser les joueurs déjà en ligne au démarrage du plugin
        for (final Player online : Bukkit.getOnlinePlayers()) {
            initTab(online);
        }
    }

    // ── API publique ─────────────────────────────────────────────────────────

    /**
     * Définit le préfixe d'une cible dans le tab d'un viewer spécifique.
     */
    public void setPrefix(@NonNull UUID viewerUUID, @NonNull UUID targetUUID, @NonNull String prefix) {
        final PlayerTab tab = getOrCreate(viewerUUID);
        tab.setPrefix(targetUUID, prefix);
        tab.apply();
    }

    /**
     * Définit le suffixe d'une cible dans le tab d'un viewer spécifique.
     */
    public void setSuffix(@NonNull UUID viewerUUID, @NonNull UUID targetUUID, @NonNull String suffix) {
        final PlayerTab tab = getOrCreate(viewerUUID);
        tab.setSuffix(targetUUID, suffix);
        tab.apply();
    }

    /**
     * Définit la couleur du nom d'une cible dans le tab d'un viewer spécifique.
     */
    public void setColor(@NonNull UUID viewerUUID, @NonNull UUID targetUUID, @NonNull ChatColor color) {
        final PlayerTab tab = getOrCreate(viewerUUID);
        tab.setColor(targetUUID, color);
        tab.apply();
    }

    /**
     * Active/désactive la persistance dans le tab après déconnexion,
     * pour un viewer spécifique.
     */
    public void setKeepWhenOffline(@NonNull UUID viewerUUID, @NonNull UUID targetUUID, boolean keep) {
        getOrCreate(viewerUUID).setKeepWhenOffline(targetUUID, keep);
    }

    // ── Raccourcis "pour tous" ────────────────────────────────────────────────

    /**
     * Applique un préfixe à une cible dans le tab de TOUS les viewers.
     */
    public void setPrefixForAll(@NonNull UUID targetUUID, @NonNull String prefix) {
        for (final PlayerTab tab : playerTabs.values()) {
            tab.setPrefix(targetUUID, prefix);
            tab.apply();
        }
    }

    /**
     * Applique un suffixe à une cible dans le tab de TOUS les viewers.
     */
    public void setSuffixForAll(@NonNull UUID targetUUID, @NonNull String suffix) {
        for (final PlayerTab tab : playerTabs.values()) {
            tab.setSuffix(targetUUID, suffix);
            tab.apply();
        }
    }

    /**
     * Applique une couleur à une cible dans le tab de TOUS les viewers.
     */
    public void setColorForAll(@NonNull UUID targetUUID, @NonNull ChatColor color) {
        for (final PlayerTab tab : playerTabs.values()) {
            tab.setColor(targetUUID, color);
            tab.apply();
        }
    }

    /**
     * Active/désactive la persistance offline pour TOUS les viewers.
     */
    public void setKeepWhenOfflineForAll(@NonNull UUID targetUUID, boolean keep) {
        for (final PlayerTab tab : playerTabs.values()) {
            tab.setKeepWhenOffline(targetUUID, keep);
        }
    }

    /**
     * Réinitialise le tab d'un viewer spécifique.
     *
     * @param viewerUUID       le viewer dont on réinitialise le tab
     * @param includeOffline   si {@code true}, les entrées offline (keepWhenOffline)
     *                         sont aussi supprimées du tab ; sinon elles sont conservées
     */
    public void resetTab(@NonNull UUID viewerUUID, boolean includeOffline) {
        final PlayerTab tab = playerTabs.get(viewerUUID);
        if (tab == null) return;

        for (final UUID targetUUID : new ArrayList<>(tab.getEntries().keySet())) {
            final TabEntry entry = tab.getEntries().get(targetUUID);
            if (!includeOffline && !entry.isOnline()) continue;
            tab.removeEntry(targetUUID);
        }

        tab.apply();
    }

    /**
     * Réinitialise le tab de TOUS les viewers connectés.
     *
     * @param includeOffline   si {@code true}, les entrées offline sont aussi supprimées
     */
    public void resetAllTabs(boolean includeOffline) {
        for (final UUID viewerUUID : playerTabs.keySet()) {
            resetTab(viewerUUID, includeOffline);
        }
    }

    // ── Listeners ────────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR)
    private void onJoin(PlayerJoinEvent event) {
        final Player joined = event.getPlayer();
        initTab(joined);

        // Ajouter le nouveau joueur dans le tab de tous les viewers existants
        for (final Map.Entry<UUID, PlayerTab> entry : playerTabs.entrySet()) {
            if (entry.getKey().equals(joined.getUniqueId())) continue;
            final TabEntry tabEntry = new TabEntry(joined.getUniqueId(), joined.getName());
            tabEntry.setGameProfile(((CraftPlayer) joined).getHandle().getProfile());
            entry.getValue().upsertEntry(tabEntry);
            entry.getValue().apply();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onQuit(PlayerQuitEvent event) {
        final UUID quitUUID = event.getPlayer().getUniqueId();

        // Marquer le joueur offline dans tous les tabs qui le contiennent
        for (final PlayerTab tab : playerTabs.values()) {
            tab.markOffline(quitUUID);
        }

        // Supprimer le tab du joueur qui part (ses données viewers disparaissent)
        playerTabs.remove(quitUUID);
    }

    // ── Initialisation ────────────────────────────────────────────────────────

    /**
     * Crée le {@link PlayerTab} d'un joueur et y ajoute tous les joueurs en ligne.
     */
    private void initTab(Player player) {
        final PlayerTab tab = new PlayerTab(player.getUniqueId());

        for (final Player online : Bukkit.getOnlinePlayers()) {
            final TabEntry entry = new TabEntry(online.getUniqueId(), online.getName());
            entry.setGameProfile(((CraftPlayer) online).getHandle().getProfile());
            tab.upsertEntry(entry);
        }

        playerTabs.put(player.getUniqueId(), tab);
        tab.apply();
    }

    private PlayerTab getOrCreate(@NonNull UUID viewerUUID) {
        return playerTabs.computeIfAbsent(viewerUUID, uuid -> {
            final Player viewer = Bukkit.getPlayer(uuid);
            if (viewer != null) initTab(viewer);
            return playerTabs.getOrDefault(uuid, new PlayerTab(uuid));
        });
    }
}
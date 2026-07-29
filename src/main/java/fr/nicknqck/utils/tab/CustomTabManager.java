package fr.nicknqck.utils.tab;

import fr.nicknqck.Main;
import lombok.Getter;
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
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Gestionnaire de tab list personnalisées.
 * <p>
 * Chaque joueur possède sa propre {@link PlayerTab} avec :
 *  - Prefix/suffix/color personnalisés par cible, persistants même après
 *    déconnexion/reconnexion du joueur (viewer ou cible)
 *  - Option de conserver les joueurs déconnectés visibles dans la tab
 *  - Masquage automatique du pseudo des joueurs invisibles (aux yeux des autres),
 *    avec restauration fidèle du prefix/suffix/color propres à chaque viewer
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

    /** Intervalle (en ticks) entre chaque vérification d'invisibilité */
    private static final long INVISIBILITY_CHECK_INTERVAL = 10L;

    /** Map viewer UUID → son tab personnalisé (données "live" affichées) */
    @Getter
    private final Map<UUID, PlayerTab> playerTabs = new HashMap<>();

    /** UUID des joueurs actuellement invisibles (pour détecter les changements d'état) */
    private final Set<UUID> invisiblePlayers = new HashSet<>();

    /**
     * Cache PERSISTANT des personnalisations (prefix/suffix/color/keepWhenOffline),
     * indépendant de la connexion du viewer ou de la cible. Contrairement à
     * {@link #playerTabs} (qui est recréé à chaque connexion), ce cache n'est
     * jamais vidé automatiquement : il survit à n'importe quelle déconnexion.
     * <p>
     * Structure : viewerUUID → (targetUUID → snapshot de la personnalisation voulue)
     */
    private final Map<UUID, Map<UUID, TabEntry>> customizationCache = new HashMap<>();
@Getter
    private BukkitRunnable invisibilityTask;

    public CustomTabManager() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
        // Initialiser les joueurs déjà en ligne au démarrage du plugin
        for (final Player online : Bukkit.getOnlinePlayers()) {
            initTab(online);
        }
        startInvisibilityWatcher();
    }

    // ── API publique ─────────────────────────────────────────────────────────

    /**
     * Définit le préfixe d'une cible dans le tab d'un viewer spécifique.
     * La valeur est aussi mise en cache et survivra à une déconnexion/reconnexion.
     */
    public void setPrefix(@NonNull UUID viewerUUID, @NonNull UUID targetUUID, @NonNull String prefix) {
        cachePrefix(viewerUUID, targetUUID, prefix);
        final PlayerTab tab = getOrCreate(viewerUUID);
        tab.setPrefix(targetUUID, prefix);
        tab.apply();
    }

    /**
     * Définit le suffixe d'une cible dans le tab d'un viewer spécifique.
     * La valeur est aussi mise en cache et survivra à une déconnexion/reconnexion.
     */
    public void setSuffix(@NonNull UUID viewerUUID, @NonNull UUID targetUUID, @NonNull String suffix) {
        cacheSuffix(viewerUUID, targetUUID, suffix);
        final PlayerTab tab = getOrCreate(viewerUUID);
        tab.setSuffix(targetUUID, suffix);
        tab.apply();
    }

    /**
     * Définit la couleur du nom d'une cible dans le tab d'un viewer spécifique.
     * La valeur est aussi mise en cache et survivra à une déconnexion/reconnexion.
     */
    public void setColor(@NonNull UUID viewerUUID, @NonNull UUID targetUUID, @NonNull ChatColor color) {
        cacheColor(viewerUUID, targetUUID, color);
        final PlayerTab tab = getOrCreate(viewerUUID);
        tab.setColor(targetUUID, color);
        tab.apply();
    }

    /**
     * Active/désactive la persistance dans le tab après déconnexion,
     * pour un viewer spécifique. La valeur est aussi mise en cache.
     */
    public void setKeepWhenOffline(@NonNull UUID viewerUUID, @NonNull UUID targetUUID, boolean keep) {
        cacheKeepWhenOffline(viewerUUID, targetUUID, keep);
        getOrCreate(viewerUUID).setKeepWhenOffline(targetUUID, keep);
    }

    // ── Raccourcis "pour tous" ────────────────────────────────────────────────

    /**
     * Applique un préfixe à une cible dans le tab de TOUS les viewers (connectés
     * ou non). La valeur est mise en cache pour chaque viewer connu, et sera
     * réappliquée automatiquement aux futurs viewers/reconnexions.
     */
    public void setPrefixForAll(@NonNull UUID targetUUID, @NonNull String prefix) {
        for (final Map.Entry<UUID, PlayerTab> entry : playerTabs.entrySet()) {
            cachePrefix(entry.getKey(), targetUUID, prefix);
            entry.getValue().setPrefix(targetUUID, prefix);
            entry.getValue().apply();
        }
    }

    /**
     * Applique un suffixe à une cible dans le tab de TOUS les viewers.
     */
    public void setSuffixForAll(@NonNull UUID targetUUID, @NonNull String suffix) {
        for (final Map.Entry<UUID, PlayerTab> entry : playerTabs.entrySet()) {
            cacheSuffix(entry.getKey(), targetUUID, suffix);
            entry.getValue().setSuffix(targetUUID, suffix);
            entry.getValue().apply();
        }
    }

    /**
     * Applique une couleur à une cible dans le tab de TOUS les viewers.
     */
    public void setColorForAll(@NonNull UUID targetUUID, @NonNull ChatColor color) {
        for (final Map.Entry<UUID, PlayerTab> entry : playerTabs.entrySet()) {
            cacheColor(entry.getKey(), targetUUID, color);
            entry.getValue().setColor(targetUUID, color);
            entry.getValue().apply();
        }
    }

    /**
     * Active/désactive la persistance offline pour TOUS les viewers.
     */
    public void setKeepWhenOfflineForAll(@NonNull UUID targetUUID, boolean keep) {
        for (final Map.Entry<UUID, PlayerTab> entry : playerTabs.entrySet()) {
            cacheKeepWhenOffline(entry.getKey(), targetUUID, keep);
            entry.getValue().setKeepWhenOffline(targetUUID, keep);
        }
    }

    /**
     * Réinitialise le tab d'un viewer spécifique.
     * Vide aussi le cache persistant de personnalisation pour ce viewer,
     * donc les cibles concernées repartiront sur des valeurs par défaut
     * même après une future reconnexion.
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

        customizationCache.remove(viewerUUID);
        tab.apply();
    }

    /**
     * Réinitialise le tab de TOUS les viewers connectés (et leur cache persistant).
     *
     * @param includeOffline   si {@code true}, les entrées offline sont aussi supprimées
     */
    public void resetAllTabs(boolean includeOffline) {
        for (final UUID viewerUUID : new ArrayList<>(playerTabs.keySet())) {
            resetTab(viewerUUID, includeOffline);
        }
    }

    // ── Cache persistant ─────────────────────────────────────────────────────

    private TabEntry getOrCreateCacheEntry(UUID viewerUUID, UUID targetUUID) {
        return customizationCache
                .computeIfAbsent(viewerUUID, k -> new HashMap<>())
                .computeIfAbsent(targetUUID, k -> new TabEntry(targetUUID, ""));
    }

    private void cachePrefix(UUID viewerUUID, UUID targetUUID, String prefix) {
        getOrCreateCacheEntry(viewerUUID, targetUUID).setPrefix(prefix);
    }

    private void cacheSuffix(UUID viewerUUID, UUID targetUUID, String suffix) {
        getOrCreateCacheEntry(viewerUUID, targetUUID).setSuffix(suffix);
    }

    private void cacheColor(UUID viewerUUID, UUID targetUUID, ChatColor color) {
        getOrCreateCacheEntry(viewerUUID, targetUUID).setColor(color);
    }

    private void cacheKeepWhenOffline(UUID viewerUUID, UUID targetUUID, boolean keep) {
        getOrCreateCacheEntry(viewerUUID, targetUUID).setKeepWhenOffline(keep);
    }

    /**
     * Construit une {@link TabEntry} pour {@code target} dans le tab de {@code viewerUUID},
     * en réappliquant automatiquement le prefix/suffix/color/keepWhenOffline
     * précédemment enregistrés dans le cache persistant, s'ils existent.
     */
    private TabEntry buildEntry(UUID viewerUUID, Player target) {
        final TabEntry entry = new TabEntry(target.getUniqueId(), target.getName());
        entry.setGameProfile(((CraftPlayer) target).getHandle().getProfile());

        final Map<UUID, TabEntry> viewerCache = customizationCache.get(viewerUUID);
        final TabEntry cached = (viewerCache != null) ? viewerCache.get(target.getUniqueId()) : null;

        if (cached != null) {
            entry.setPrefix(cached.getPrefix());
            entry.setSuffix(cached.getSuffix());
            entry.setColor(cached.getColor());
            entry.setKeepWhenOffline(cached.isKeepWhenOffline());
        }

        return entry;
    }

    // ── Invisibilité ─────────────────────────────────────────────────────────

    /**
     * Démarre la tâche qui surveille l'état d'invisibilité de chaque joueur
     * et masque/affiche son pseudo dans le tab des AUTRES joueurs en conséquence.
     * Le joueur invisible continue de se voir lui-même dans son propre tab.
     */
    private void startInvisibilityWatcher() {
        invisibilityTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    final UUID uuid = player.getUniqueId();
                    final boolean isInvisible = player.hasPotionEffect(PotionEffectType.INVISIBILITY);
                    final boolean wasInvisible = invisiblePlayers.contains(uuid);

                    if (isInvisible == wasInvisible) continue; // pas de changement d'état

                    if (isInvisible) {
                        invisiblePlayers.add(uuid);
                        hideFromOtherTabs(player);
                    } else {
                        invisiblePlayers.remove(uuid);
                        showInOtherTabs(player);
                    }
                }
            }
        };
        invisibilityTask.runTaskTimer(Main.getInstance(), INVISIBILITY_CHECK_INTERVAL, INVISIBILITY_CHECK_INTERVAL);
    }

    /**
     * Retire le pseudo de {@code player} du tab de tous les AUTRES viewers.
     * Le prefix/suffix/color reste intact dans le cache persistant, donc
     * {@link #showInOtherTabs} pourra les restaurer fidèlement.
     * Le tab de {@code player} lui-même n'est pas modifié : il se voit toujours.
     */
    private void hideFromOtherTabs(Player player) {
        final UUID targetUUID = player.getUniqueId();

        for (final Map.Entry<UUID, PlayerTab> mapEntry : playerTabs.entrySet()) {
            if (mapEntry.getKey().equals(targetUUID)) continue; // le joueur se voit toujours lui-même
            mapEntry.getValue().removeEntry(targetUUID);
            mapEntry.getValue().apply();
        }
    }

    /**
     * Réaffiche le pseudo de {@code player} dans le tab de tous les AUTRES viewers,
     * en restaurant pour chacun d'eux le prefix/suffix/color/keepWhenOffline
     * enregistré dans le cache persistant.
     */
    private void showInOtherTabs(Player player) {
        final UUID targetUUID = player.getUniqueId();

        for (final Map.Entry<UUID, PlayerTab> mapEntry : playerTabs.entrySet()) {
            final UUID viewerUUID = mapEntry.getKey();
            if (viewerUUID.equals(targetUUID)) continue;

            final PlayerTab tab = mapEntry.getValue();
            tab.upsertEntry(buildEntry(viewerUUID, player));
            tab.apply();
        }
    }

    // ── Listeners ────────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR)
    private void onJoin(PlayerJoinEvent event) {
        final Player joined = event.getPlayer();
        initTab(joined);

        // Le joueur qui rejoint n'est jamais invisible à la connexion.
        // On l'ajoute dans le tab de tous les viewers existants, en réappliquant
        // leur personnalisation persistante (prefix/suffix/color) s'ils en avaient une.
        for (final Map.Entry<UUID, PlayerTab> entry : playerTabs.entrySet()) {
            if (entry.getKey().equals(joined.getUniqueId())) continue;
            entry.getValue().upsertEntry(buildEntry(entry.getKey(), joined));
            entry.getValue().apply();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onQuit(PlayerQuitEvent event) {
        final UUID quitUUID = event.getPlayer().getUniqueId();

        // Marquer le joueur offline dans tous les tabs qui le contiennent.
        // Le cache persistant (customizationCache) n'est PAS touché ici :
        // le prefix/suffix/color restent enregistrés pour sa reconnexion future.
        for (final PlayerTab tab : playerTabs.values()) {
            tab.markOffline(quitUUID);
        }

        // Le joueur ne fait plus partie du suivi d'invisibilité
        invisiblePlayers.remove(quitUUID);

        // On supprime uniquement le tab "live" du joueur qui part (sa propre vue),
        // PAS son cache de personnalisation ni les personnalisations que les
        // autres viewers ont sur lui : ceux-ci sont conservés indéfiniment.
        playerTabs.remove(quitUUID);
    }

    // ── Initialisation ────────────────────────────────────────────────────────

    /**
     * Crée le {@link PlayerTab} d'un joueur et y ajoute tous les joueurs en ligne,
     * en réappliquant pour chacun le prefix/suffix/color persistant s'il existe
     * (y compris si {@code player} se reconnecte après une déconnexion).
     * Les joueurs actuellement invisibles ne sont pas ajoutés (sauf soi-même).
     */
    private void initTab(Player player) {
        final PlayerTab tab = new PlayerTab(player.getUniqueId());

        for (final Player online : Bukkit.getOnlinePlayers()) {
            // Ne pas ajouter les joueurs invisibles dans le tab des autres
            if (!online.getUniqueId().equals(player.getUniqueId())
                    && invisiblePlayers.contains(online.getUniqueId())) {
                continue;
            }

            tab.upsertEntry(buildEntry(player.getUniqueId(), online));
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
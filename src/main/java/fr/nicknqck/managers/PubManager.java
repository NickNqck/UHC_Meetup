package fr.nicknqck.managers;

import fr.nicknqck.Main;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


//Code généré par Claude Sonnet 4.6
public class PubManager {

    private final LinkedHashMap<Integer, String> registry = new LinkedHashMap<>();
    private PubRunnable runnable;

    // ── Démarrage / Arrêt ────────────────────────────────────────────────────

    /**
     * Démarre le runnable de diffusion.
     * Si un runnable est déjà en cours, il est annulé avant de redémarrer.
     */
    public void start() {
        if (registry.isEmpty()) {
            Main.getInstance().getLogger().warning("[PubManager] Aucun message enregistré, le runnable ne démarrera pas.");
            return;
        }
        if (runnable != null) {
            try { runnable.cancel(); } catch (Exception ignored) {}
        }
        runnable = new PubRunnable(this);
        runnable.runTaskTimerAsynchronously(Main.getInstance(), 100, 20);
    }

    /**
     * Arrête le runnable de diffusion.
     */
    public void stop() {
        if (runnable != null) {
            try { runnable.cancel(); } catch (Exception ignored) {}
            runnable = null;
        }
    }

    // ── Ajout ────────────────────────────────────────────────────────────────

    /**
     * Ajoute un message et lui attribue le prochain numéro disponible.
     *
     * @param message le message à enregistrer
     * @return le numéro attribué
     */
    public synchronized int add(@NonNull String message) {
        final int number = registry.size() + 1;
        registry.put(number, message);
        return number;
    }

    // ── Suppression ──────────────────────────────────────────────────────────

    /**
     * Supprime le message associé au numéro donné, puis renumérotise
     * toutes les entrées suivantes (ex: 4→3, 5→4, etc.).
     *
     * @param number le numéro à supprimer
     * @return le message supprimé, ou {@code null} s'il n'existait pas
     */
    public synchronized String remove(int number) {
        if (!registry.containsKey(number)) return null;

        final String removed = registry.remove(number);

        final LinkedHashMap<Integer, String> updated = new LinkedHashMap<>();
        for (Map.Entry<Integer, String> entry : registry.entrySet()) {
            final int key = entry.getKey();
            updated.put(key > number ? key - 1 : key, entry.getValue());
        }

        registry.clear();
        registry.putAll(updated);

        return removed;
    }

    /**
     * Supprime la première occurrence du message donné et renumérotise.
     *
     * @param message le message à supprimer
     * @return {@code true} si le message a été trouvé et supprimé
     */
    public synchronized boolean removeByMessage(@NonNull String message) {
        for (Map.Entry<Integer, String> entry : registry.entrySet()) {
            if (entry.getValue().equals(message)) {
                remove(entry.getKey());
                return true;
            }
        }
        return false;
    }

    // ── Lecture ──────────────────────────────────────────────────────────────

    public synchronized String get(int number) {
        return registry.get(number);
    }

    public synchronized boolean contains(int number) {
        return registry.containsKey(number);
    }

    public synchronized boolean containsMessage(@NonNull String message) {
        return registry.containsValue(message);
    }

    public synchronized int size() {
        return registry.size();
    }

    public synchronized boolean isEmpty() {
        return registry.isEmpty();
    }

    public synchronized Set<Map.Entry<Integer, String>> entries() {
        return registry.entrySet();
    }

    public synchronized Collection<String> messages() {
        return registry.values();
    }

    public synchronized void clear() {
        registry.clear();
    }

    @Override
    public synchronized String toString() {
        return registry.toString();
    }

    // ── Runnable interne ─────────────────────────────────────────────────────

    private static final int MIN_INTERVAL = 60;       // 1 minute
    private static final int MAX_INTERVAL = 60 * 10;  // 10 minutes

    private static final class PubRunnable extends BukkitRunnable {

        private final PubManager manager;
        private int time   = 0;
        private int target = 0;
        private int current;

        private PubRunnable(@NonNull PubManager manager) {
            this.manager = manager;
            this.current = Main.RANDOM.nextInt(manager.size());
            this.target  = 30;
        }

        @Override
        public void run() {
            if (manager.isEmpty()) return;

            if (time == target) {
                // Les index de registry commencent à 1
                final String message = manager.get(current + 1);
                if (message != null) {
                    Bukkit.broadcastMessage("\n" + message);
                }
                target  = time + randomInterval();
                current = nextIndex(current, manager.size());
                Main.getInstance().debug("Prochaine pub: "+current+" ("+manager.get(current+1)+") dans: "+ StringUtils.secondsTowardsBeautiful(this.target-this.time)+", le runnable tourne depuis: "+StringUtils.secondsTowardsBeautiful(this.time));
                return;
            }
            time++;
        }

        private static int randomInterval() {
            return RandomUtils.getRandomInt(MIN_INTERVAL, MAX_INTERVAL);
        }

        /**
         * Retourne un index différent de {@code except}, entre 0 et {@code size - 1}.
         * Corrige le bug de PubRunnable original où nextInt était fixé à 3.
         */
        private static int nextIndex(int except, int size) {
            if (size == 1) return 0;
            int next = Main.RANDOM.nextInt(size);
            while (next == except) {
                next = Main.RANDOM.nextInt(size);
            }
            return next;
        }
    }
}
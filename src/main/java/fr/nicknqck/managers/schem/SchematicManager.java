package fr.nicknqck.managers.schem;

import fr.nicknqck.Main;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire centralisé des schematics du plugin.
 *
 * <p>Responsabilités :
 * <ul>
 *   <li>Créer le dossier {@code plugins/<PluginName>/schems/} s'il n'existe pas.</li>
 *   <li>Détecter et charger automatiquement tous les fichiers {@code .schematic} présents.</li>
 *   <li>Notifier via {@link Main#debug(String)} si aucun schematic n'est trouvé.</li>
 *   <li>Exposer les schematics chargés via {@link #getSchematic(String)}.</li>
 *   <li>Permettre le rechargement à chaud via {@link #reload()}.</li>
 * </ul>
 *
 * <p><strong>Utilisation typique :</strong>
 * <pre>{@code
 * // Dans Main#onEnable()
 * SchematicManager schematicManager = new SchematicManager(this);
 *
 * // Récupérer un schematic par son nom (sans extension)
 * Schematic arena = schematicManager.getSchematic("arena");
 * if (arena != null) {
 *     arena.paste(spawnLocation, true);
 * }
 * }</pre>
 */
public class SchematicManager {

    /** Map <nom_sans_extension, Schematic> des schematics chargés. */
    private final Map<String, Schematic> schematics = new HashMap<String, Schematic>();

    /** Référence au dossier {@code <dataFolder>/schems/}. */
    private final File schemsFolder;

    /** Référence au plugin propriétaire (pour les paths et le scheduler). */
    @Getter
    private final Plugin plugin;

    // ──────────────────────────────────────────────────────────────────────────
    // Constructeur
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Initialise le gestionnaire.
     *
     * <p>Crée le dossier {@code schems/} si nécessaire, puis lance le chargement
     * de tous les fichiers {@code .schematic} présents.
     *
     * @param plugin L'instance du plugin principal. Ne doit pas être null.
     */
    public SchematicManager(Plugin plugin) {
        this.plugin = plugin;
        this.schemsFolder = new File(plugin.getDataFolder(), "schems");

        // Création du dossier si absent
        if (!schemsFolder.exists()) {
            if (schemsFolder.mkdirs()) {
                Main.getInstance().debug(
                        "[SchematicManager] Dossier 'schems' créé automatiquement dans : "
                                + schemsFolder.getAbsolutePath()
                );
            } else {
                Main.getInstance().debug(
                        "[SchematicManager] ERREUR : impossible de créer le dossier 'schems' dans : "
                                + schemsFolder.getAbsolutePath()
                );
            }
        }

        loadAll();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Chargement
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Scanne le dossier {@code schems/} et charge tous les fichiers {@code .schematic}.
     *
     * <p>Les schematics déjà en mémoire sont supprimés avant le chargement (comportement
     * identique lors d'un appel à {@link #reload()}).
     *
     * <p>Cas couverts :
     * <ul>
     *   <li>Dossier vide ou aucun {@code .schematic} → message de debug explicite.</li>
     *   <li>Fichier corrompu / format invalide → loggé individuellement, les autres sont
     *       chargés normalement.</li>
     * </ul>
     */
    private void loadAll() {
        schematics.clear();

        // listFiles() peut retourner null si schemsFolder n'est pas un répertoire
        File[] files = schemsFolder.listFiles(new java.io.FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name != null && name.toLowerCase().endsWith(".schematic");
            }
        });

        // ── Cas : aucun schematic trouvé ──────────────────────────────────────
        if (files == null || files.length == 0) {
            Main.getInstance().debug(
                    "[SchematicManager] Aucun schematic (.schematic) trouvé dans le dossier '"
                            + schemsFolder.getAbsolutePath()
                            + "'. Veuillez y déposer des fichiers .schematic avant de lancer une partie."
            );
            return;
        }

        // ── Chargement de chaque fichier ──────────────────────────────────────
        int loaded = 0;
        int failed = 0;

        for (File file : files) {
            String name = stripExtension(file.getName());
            try {
                Schematic schematic = Schematic.load(file);
                schematics.put(name, schematic);
                Main.getInstance().debug(
                        "[SchematicManager] ✔ Schematic chargé : '"
                                + name + "' ("
                                + schematic.getWidth()  + "x"
                                + schematic.getHeight() + "x"
                                + schematic.getLength() + " — "
                                + schematic.getTotalBlocks() + " blocs)"
                );
                loaded++;
            } catch (Exception e) {
                Main.getInstance().debug(
                        "[SchematicManager] ✘ Échec du chargement de '"
                                + file.getName() + "' : " + e.getMessage()
                );
                // Stack trace complète dans la console pour faciliter le diagnostic
                e.printStackTrace();
                failed++;
            }
        }

        // ── Bilan final ───────────────────────────────────────────────────────
        Main.getInstance().debug(
                "[SchematicManager] Chargement terminé : "
                        + loaded + " réussi(s), " + failed + " échec(s) sur "
                        + files.length + " fichier(s) trouvé(s)."
        );
    }

    // ──────────────────────────────────────────────────────────────────────────
    // API publique
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Récupère un schematic chargé par son nom (sans extension {@code .schematic}).
     *
     * <p>Exemple : pour le fichier {@code schems/arena_uhc.schematic},
     * utiliser {@code getSchematic("arena_uhc")}.
     *
     * @param name Nom du schematic (insensible à la casse <em>non</em> géré —
     *             le nom est celui exact du fichier sans extension).
     * @return L'objet {@link Schematic} correspondant, ou {@code null} s'il n'est pas chargé.
     */
    public Schematic getSchematic(String name) {
        return schematics.get(name);
    }

    /**
     * Vérifie si un schematic est actuellement chargé.
     *
     * @param name Nom du schematic (sans extension).
     * @return {@code true} si le schematic est disponible.
     */
    public boolean hasSchematic(String name) {
        return schematics.containsKey(name);
    }

    /**
     * Retourne une vue non-modifiable de tous les schematics actuellement chargés.
     *
     * <p>La clé est le nom sans extension, la valeur l'objet {@link Schematic}.
     *
     * @return Map immuable des schematics.
     */
    public Map<String, Schematic> getSchematics() {
        return Collections.unmodifiableMap(schematics);
    }

    /**
     * Retourne le nombre de schematics actuellement chargés.
     *
     * @return Nombre de schematics en mémoire.
     */
    public int getLoadedCount() {
        return schematics.size();
    }

    /**
     * Recharge tous les schematics depuis le disque.
     *
     * <p>Vide la map en mémoire, puis rescanne et recharge le dossier {@code schems/}.
     * Pratique pour recharger des fichiers ajoutés sans redémarrer le serveur.
     */
    public void reload() {
        Main.getInstance().debug("[SchematicManager] Rechargement des schematics en cours...");
        loadAll();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Utilitaires privés
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Supprime l'extension {@code .schematic} (ou toute autre extension)
     * du nom d'un fichier.
     *
     * @param filename Nom complet du fichier (ex. {@code "arena_uhc.schematic"}).
     * @return Nom sans extension (ex. {@code "arena_uhc"}).
     */
    private String stripExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex > 0) ? filename.substring(0, dotIndex) : filename;
    }
}

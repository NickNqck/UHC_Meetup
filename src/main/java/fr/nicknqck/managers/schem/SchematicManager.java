package fr.nicknqck.managers.schem;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Gestionnaire centralisé des schematics d'un plugin.
 *
 * <p>Responsabilités :
 * <ul>
 *   <li>Créer le dossier {@code <dataFolder>/schems/} s'il n'existe pas.</li>
 *   <li>Détecter et charger automatiquement tous les fichiers {@code .schematic} présents.</li>
 *   <li>Notifier via le {@link SchematicLogger} fourni si aucun schematic n'est trouvé.</li>
 *   <li>Exposer les schematics chargés via {@link #getSchematic(String)}.</li>
 *   <li>Permettre le rechargement à chaud via {@link #reload()}.</li>
 * </ul>
 *
 * <p><strong>Utilisation dans UHC-Meetup :</strong>
 * <pre>{@code
 * SchematicManager schematicManager = new SchematicManager(this, Main.getInstance()::debug);
 * }</pre>
 *
 * <p><strong>Utilisation depuis un plugin tiers (soft-depend + classpath) :</strong>
 * <pre>{@code
 * SchematicManager schematicManager = new SchematicManager(this, msg -> getLogger().info(msg));
 * Schematic arena = schematicManager.getSchematic("arena");
 * if (arena != null) {
 *     arena.paste(spawnLocation, true);
 * }
 * }</pre>
 */
public class SchematicManager {

    /** Map <nom_sans_extension, Schematic> des schematics chargés. */
    private final Map<String, Schematic> schematics = new HashMap<>();

    /** Référence au dossier {@code <dataFolder>/schems/}. */
    private final File schemsFolder;

    /** Référence au plugin propriétaire (pour les paths et le scheduler). */
    @Getter
    private final Plugin plugin;

    /** Callback de logging, jamais null (par défaut {@link SchematicLogger#NOOP}). */
    private final SchematicLogger logger;

    // ──────────────────────────────────────────────────────────────────────────
    // Constructeurs
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Initialise le gestionnaire sans logging (silencieux).
     *
     * @param plugin L'instance du plugin propriétaire. Ne doit pas être null.
     */
    public SchematicManager(@NonNull Plugin plugin) {
        this(plugin, SchematicLogger.NOOP);
    }

    /**
     * Initialise le gestionnaire.
     *
     * <p>Crée le dossier {@code schems/} si nécessaire, puis lance le chargement
     * de tous les fichiers {@code .schematic} présents dans
     * {@code <plugin.getDataFolder()>/schems/}.
     *
     * @param plugin L'instance du plugin propriétaire. Ne doit pas être null.
     * @param logger Callback de logging optionnel. Si {@code null}, {@link SchematicLogger#NOOP}
     *               est utilisé (aucun log émis).
     */
    public SchematicManager(@NonNull Plugin plugin, SchematicLogger logger) {
        this.plugin = plugin;
        this.logger = (logger != null) ? logger : SchematicLogger.NOOP;
        this.schemsFolder = new File(plugin.getDataFolder(), "schems");

        // Création du dossier si absent
        if (!schemsFolder.exists()) {
            if (schemsFolder.mkdirs()) {
                this.logger.log(
                        "[SchematicManager] Dossier 'schems' créé automatiquement dans : "
                                + schemsFolder.getAbsolutePath()
                );
            } else {
                this.logger.log(
                        "[SchematicManager] ERREUR : impossible de créer le dossier 'schems' dans : "
                                + schemsFolder.getAbsolutePath()
                );
            }
        }
        extractBundledSchematics();
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
     *   <li>Dossier vide ou aucun {@code .schematic} → message de log explicite.</li>
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
            logger.log(
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
                Schematic schematic = Schematic.load(file, logger);
                schematics.put(name, schematic);
                logger.log(
                        "[SchematicManager] ✔ Schematic chargé : '"
                                + name + "' ("
                                + schematic.getWidth()  + "x"
                                + schematic.getHeight() + "x"
                                + schematic.getLength() + " — "
                                + schematic.getTotalBlocks() + " blocs)"
                );
                loaded++;
            } catch (Exception e) {
                logger.log(
                        "[SchematicManager] ✘ Échec du chargement de '"
                                + file.getName() + "' : " + e.getMessage()
                );
                // Stack trace complète dans la console pour faciliter le diagnostic
                e.printStackTrace();
                failed++;
            }
        }

        // ── Bilan final ───────────────────────────────────────────────────────
        logger.log(
                "[SchematicManager] Chargement terminé : "
                        + loaded + " réussi(s), " + failed + " échec(s) sur "
                        + files.length + " fichier(s) trouvé(s)."
        );
    }
    /**
     * Extrait tous les .schematic embarqués dans le jar (src/main/resources/schems/)
     * vers le dossier {@code <dataFolder>/schems/} sur le disque, en écrasant
     * systématiquement les fichiers déjà présents (les schematics embarqués font foi).
     *
     * <p>Nécessite d'exécuter le plugin depuis un jar réellement construit
     * (via {@code gradle shadowJar} par ex.) — ne fonctionne pas si le plugin
     * tourne depuis des classes non-jarées (run exploded depuis l'IDE).
     */
    private void extractBundledSchematics() {
        File jarFile = getPluginJarFile();
        if (jarFile == null || !jarFile.isFile()) {
            logger.log("[SchematicManager] Impossible de localiser le jar du plugin, extraction des schematics embarqués ignorée.");
            return;
        }

        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();
            int extracted = 0;

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                if (entry.isDirectory()) continue;
                if (!name.startsWith("schems/") || !name.toLowerCase().endsWith(".schematic")) continue;

                String fileName = name.substring("schems/".length());
                if (fileName.isEmpty()) continue;

                File target = new File(schemsFolder, fileName);

                try (InputStream in = jar.getInputStream(entry)) {
                    Files.copy(in, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    extracted++;
                } catch (IOException e) {
                    logger.log("[SchematicManager] Échec de l'extraction de '" + fileName + "' : " + e.getMessage());
                }
            }

            if (extracted > 0) {
                logger.log("[SchematicManager] " + extracted + " schematic(s) embarqué(s) extrait(s)/mis à jour dans '"
                        + schemsFolder.getAbsolutePath() + "'.");
            }
        } catch (IOException e) {
            logger.log("[SchematicManager] Erreur lors de l'ouverture du jar du plugin : " + e.getMessage());
        }
    }

    /**
     * Localise le fichier jar physique du plugin propriétaire, pour pouvoir
     * en lire le contenu via {@link JarFile}.
     */
    private File getPluginJarFile() {
        try {
            return new File(plugin.getClass()
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI());
        } catch (Exception e) {
            return null;
        }
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
        logger.log("[SchematicManager] Rechargement des schematics en cours...");
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
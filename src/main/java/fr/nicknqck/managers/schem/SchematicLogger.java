package fr.nicknqck.managers.schem;

/**
 * Callback de logging optionnel utilisé par {@link SchematicManager} et {@link Schematic}.
 *
 * <p>Permet à chaque plugin consommateur (UHC-Meetup ou un plugin tiers dépendant
 * du jar) de rediriger les logs vers son propre système, sans dépendance
 * codée en dur vers {@code fr.nicknqck.Main}.
 *
 * <p>Exemple d'utilisation dans UHC-Meetup :
 * <pre>{@code
 * SchematicManager manager = new SchematicManager(this, Main.getInstance()::debug);
 * }</pre>
 *
 * <p>Exemple d'utilisation dans un plugin tiers :
 * <pre>{@code
 * SchematicManager manager = new SchematicManager(this, msg -> getLogger().info(msg));
 * }</pre>
 */
@FunctionalInterface
public interface SchematicLogger {

    /** Logger "silencieux" par défaut, utilisé si aucun callback n'est fourni. */
    SchematicLogger NOOP = message -> { /* aucun log */ };

    void log(String message);
}
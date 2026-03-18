package fr.nicknqck.utils.particles;

import fr.nicknqck.Main;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class TornadoWaveEffect {

    // ── Paramètres — bras sortants ───────────────────────────────────────────
    private static final int    STREAM_COUNT    = 6;     // nombre de bras sortants
    private static final int    CIRCLE_DENSITY  = 48;    // points du cercle initial
    private static final double RADIUS          = 1.0;   // rayon du cercle de départ (blocs)
    private static final double SPEED           = 0.7;   // blocs/tick des bras sortants
    private static final double MAX_DISTANCE    = 30.0;  // distance max des bras (= rayon du grand cercle rose)
    private static final double CURVE_FACTOR    = 0.10;  // courbure par bloc parcouru (rads/bloc)

    // ── Paramètres — particules entrantes ────────────────────────────────────
    private static final double INWARD_SPEED    = 0.7;   // blocs/tick vers le centre
    private static final int    SPAWN_PER_GAP   = 1;     // nouvelles particules par zone de gap par tick
    private static final double GAP_SPREAD      = 0.70;  // fraction de l'espace entre deux bras couvert

    // ── Paramètres — communs ─────────────────────────────────────────────────
    private static final double HIT_RADIUS      = 0.9;   // hitbox de collision (blocs)
    private static final int    FIRE_DURATION   = 240;   // durée du feu (ticks, 12 sec)
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Lance l'effet :
     *  • Cercle de flammes autour du joueur (ceinture horizontale)
     *  • {@code STREAM_COUNT} bras de particules qui partent en spirale vers l'extérieur
     *  • Dans les zones entre les bras, des particules convergent depuis le bord extérieur vers le centre
     *
     * @param plugin  Instance du plugin
     * @param caster  Joueur déclencheur
     */
    public static void launch(Plugin plugin, Player caster) {

        Location origin = caster.getLocation().clone().add(0, 1.0, 0);
        Random   rng    = new Random();

        // ── 1. Cercle initial ────────────────────────────────────────────────
        for (int i = 0; i < CIRCLE_DENSITY; i++) {
            double a = (2 * Math.PI * i) / CIRCLE_DENSITY;
            spawnFlame(origin.clone().add(Math.cos(a) * RADIUS, 0, Math.sin(a) * RADIUS));
        }

        // ── 2. Init bras sortants ────────────────────────────────────────────
        double[] baseAngles = new double[STREAM_COUNT];
        double[] distances  = new double[STREAM_COUNT];
        boolean[] alive     = new boolean[STREAM_COUNT];

        for (int i = 0; i < STREAM_COUNT; i++) {
            baseAngles[i] = (2 * Math.PI * i) / STREAM_COUNT;
            distances[i]  = 0.0;
            alive[i]      = true;
        }

        // Angles des centres des zones de gap (milieu entre chaque paire de bras)
        double gapHalfWidth = (Math.PI / STREAM_COUNT) * GAP_SPREAD;
        double[] gapCenters = new double[STREAM_COUNT];
        for (int i = 0; i < STREAM_COUNT; i++) {
            gapCenters[i] = baseAngles[i] + Math.PI / STREAM_COUNT;
        }

        // ── 3. Particules entrantes : liste de [angle, rayon courant] ────────
        // Chaque entrée = double[]{ angle, currentRadius }
        List<double[]> inwardParticles = new ArrayList<>();

        double outerRadius = RADIUS + MAX_DISTANCE; // rayon du grand cercle rose

        // ── 4. Tâche principale ──────────────────────────────────────────────
        new BukkitRunnable() {

            @Override
            public void run() {

                // ── A. Bras sortants ─────────────────────────────────────────
                boolean anyOutwardAlive = false;

                for (int i = 0; i < STREAM_COUNT; i++) {
                    if (!alive[i]) continue;
                    anyOutwardAlive = true;

                    distances[i] += SPEED;
                    if (distances[i] >= MAX_DISTANCE) {
                        alive[i] = false;
                        continue;
                    }

                    double d            = distances[i];
                    double currentAngle = baseAngles[i] + d * CURVE_FACTOR;
                    double currentR     = RADIUS + d;

                    Location pos = origin.clone().add(
                            Math.cos(currentAngle) * currentR, 0, Math.sin(currentAngle) * currentR);

                    spawnFlame(pos);
                    checkAndBurn(plugin, pos, caster);

                    // Point intermédiaire pour la collision
                    double dMid = d - SPEED * 0.5;
                    checkAndBurn(plugin, origin.clone().add(
                            Math.cos(baseAngles[i] + dMid * CURVE_FACTOR) * (RADIUS + dMid),
                            0,
                            Math.sin(baseAngles[i] + dMid * CURVE_FACTOR) * (RADIUS + dMid)), caster);
                }

                // ── B. Spawn de nouvelles particules entrantes ───────────────
                // On continue à en spawner tant que des bras sortants sont actifs
                if (anyOutwardAlive) {
                    for (double gapCenter : gapCenters) {
                        for (int s = 0; s < SPAWN_PER_GAP; s++) {
                            // Angle aléatoire dans la zone du gap
                            double angle = gapCenter + (rng.nextDouble() - 0.5) * gapHalfWidth * 2;
                            inwardParticles.add(new double[]{ angle, outerRadius });
                        }
                    }
                }

                // ── C. Déplacer les particules entrantes ─────────────────────
                Iterator<double[]> iter = inwardParticles.iterator();
                while (iter.hasNext()) {
                    double[] p = iter.next();

                    p[1] -= INWARD_SPEED; // réduire le rayon = avancer vers le centre

                    // Arrivée au centre → disparition
                    if (p[1] <= RADIUS) {
                        iter.remove();
                        continue;
                    }

                    Location pos = origin.clone().add(
                            Math.cos(p[0]) * p[1], 0, Math.sin(p[0]) * p[1]);

                    spawnFlame(pos);
                    checkAndBurn(plugin, pos, caster);
                }

                // ── D. Fin de l'effet ────────────────────────────────────────
                // On attend que TOUS les bras et TOUTES les particules entrantes soient terminés
                if (!anyOutwardAlive && inwardParticles.isEmpty()) {
                    cancel();
                }
            }

        }.runTaskTimer(plugin, 1L, 1L);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /** Joueurs actuellement soumis au feu persistant (évite les tâches en double). */
    private static final Set<UUID> burningPlayers = new HashSet<>();

    private static void checkAndBurn(Plugin plugin, Location loc, Player caster) {
        for (Entity entity : loc.getWorld().getNearbyEntities(loc, HIT_RADIUS, HIT_RADIUS, HIT_RADIUS)) {
            if (entity instanceof Player && !entity.equals(caster)) {
                applyPersistentFire(plugin, (Player) entity, caster);
            }
        }
    }

    /**
     * Enflamme le joueur pendant {@code FIRE_DURATION} ticks sans possibilité de s'éteindre.
     * Les fire ticks sont rafraîchis chaque tick, annulant tout tentative d'extinction
     * (eau, pluie, lait, etc.).
     * Une seule tâche est lancée par joueur même s'il est touché plusieurs fois.
     */
    private static void applyPersistentFire(Plugin plugin, Player target, final Player caster) {
        if (burningPlayers.contains(target.getUniqueId())) return;
        caster.sendMessage(Main.getInstance().getNAME()+"§c "+target.getDisplayName()+"§7 a été§c toucher§7 par vos§6 flammes§7.");
        burningPlayers.add(target.getUniqueId());

        new BukkitRunnable() {
            int elapsed = 0;

            @Override
            public void run() {
                // Joueur déconnecté ou durée écoulée → on arrête
                if (!target.isOnline() || elapsed >= FIRE_DURATION) {
                    burningPlayers.remove(target.getUniqueId());
                    cancel();
                    return;
                }
                // Refresh chaque tick → impossible de s'éteindre
                target.setFireTicks(FIRE_DURATION - elapsed);
                elapsed++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private static void spawnFlame(Location loc) {
        loc.getWorld().spigot().playEffect(
                loc, Effect.FLAME,
                0, 0,
                0f, 0f, 0f,
                0f, 1, 48
        );
    }
}

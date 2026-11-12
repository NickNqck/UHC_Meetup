package fr.nicknqck.managers;

import fr.nicknqck.GameState;
import fr.nicknqck.events.custom.GameEndEvent;
import fr.nicknqck.events.custom.death.FinalDeathEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.utils.event.EventUtils;
import lombok.NonNull;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Système d'assistances.
 * <p>
 * Un joueur A obtient une assistance lorsqu'il a infligé des dégâts à un joueur B
 * dans les {@link #ASSIST_WINDOW_MILLIS 30 secondes} précédant la mort de B,
 * à condition que B ait été tué par un joueur (autre que A).
 * <p>
 * Toute source de dégâts rattachable à un joueur est prise en compte : coup direct,
 * projectile tiré par le joueur, animal apprivoisé du joueur, TNT allumée par le joueur,
 * ou dégâts infligés via {@code damage(x, joueur)}. Pour une source que Bukkit ne peut
 * pas rattacher (invocation sans propriétaire, pouvoir qui modifie directement la vie…),
 * un pouvoir peut appeler {@link #recordDamage(UUID, UUID)} lui-même.
 */
public class AssistManager implements Listener {

    public static final long ASSIST_WINDOW_MILLIS = 30_000L;

    /** victime -> (attaquant -> horodatage du dernier dégât infligé) */
    private final Map<UUID, Map<UUID, Long>> lastDamages = new HashMap<>();

    public AssistManager() {
        EventUtils.registerEvents(this);
    }

    /**
     * Enregistre qu'un joueur a infligé des dégâts à un autre joueur à l'instant présent.
     * À appeler depuis le thread principal.
     */
    public void recordDamage(@NonNull final UUID attacker, @NonNull final UUID victim) {
        if (attacker.equals(victim)) return;
        if (!GameState.inGame()) return;
        this.lastDamages.computeIfAbsent(victim, k -> new HashMap<>()).put(attacker, System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onDamage(final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        final UUID attacker = resolvePlayerSource(event.getDamager());
        if (attacker == null) return;
        recordDamage(attacker, event.getEntity().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onFinalDeath(final FinalDeathEvent event) {
        final UUID victim = event.getPlayer().getUniqueId();
        final Map<UUID, Long> attackers = this.lastDamages.remove(victim);
        if (attackers == null || attackers.isEmpty()) return;

        // Pas d'assistance si la victime n'a pas été tuée par un joueur
        final UUID killer = resolvePlayerSource(event.getEntityKiller());
        if (killer == null) return;

        // Comme pour les kills : la victime doit avoir un rôle
        final GamePlayer deadPlayer = GamePlayer.of(victim);
        if (deadPlayer == null || deadPlayer.getRole() == null) return;

        final long now = System.currentTimeMillis();
        for (final Map.Entry<UUID, Long> entry : attackers.entrySet()) {
            final UUID attacker = entry.getKey();
            if (attacker.equals(killer)) continue;
            if (now - entry.getValue() > ASSIST_WINDOW_MILLIS) continue;
            final GamePlayer assistant = GamePlayer.of(attacker);
            if (assistant == null) continue;
            assistant.addAssist();
        }
    }

    @EventHandler
    private void onGameEnd(final GameEndEvent event) {
        this.lastDamages.clear();
    }

    /**
     * Retrouve le joueur responsable d'une entité source de dégâts, ou {@code null}
     * si aucun joueur ne peut y être rattaché.
     */
    @Nullable
    public static UUID resolvePlayerSource(@Nullable final Entity source) {
        if (source == null) return null;
        if (source instanceof Player) {
            return source.getUniqueId();
        }
        if (source instanceof Projectile) {
            final ProjectileSource shooter = ((Projectile) source).getShooter();
            return shooter instanceof Entity ? resolvePlayerSource((Entity) shooter) : null;
        }
        if (source instanceof Tameable) {
            final AnimalTamer owner = ((Tameable) source).getOwner();
            return owner != null ? owner.getUniqueId() : null;
        }
        if (source instanceof TNTPrimed) {
            final Entity igniter = ((TNTPrimed) source).getSource();
            return igniter != source ? resolvePlayerSource(igniter) : null;
        }
        return null;
    }
}

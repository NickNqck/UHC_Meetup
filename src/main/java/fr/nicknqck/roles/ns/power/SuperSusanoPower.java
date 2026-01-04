package fr.nicknqck.roles.ns.power;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import hm.zelha.particlesfx.particles.ParticleDustColored;
import hm.zelha.particlesfx.shapers.ParticleCircle;
import hm.zelha.particlesfx.shapers.ParticleLine;
import hm.zelha.particlesfx.util.Color;
import hm.zelha.particlesfx.util.LocationSafe;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SuperSusanoPower extends ItemPower {

    @NonNull
    @Getter
    private final SuperSusanoRunnable susanoRunnable;
    @Getter
    private final SubSusanoPower subSusanoPower;

    public SuperSusanoPower(@NonNull RoleBase role, @Nullable SubSusanoPower subSusanoPower, String... description) {
        super("§c§lSusanô§r", new Cooldown(60*20), new ItemBuilder(Material.NETHER_STAR).setName("§c§lSusanô"), role, description);
        this.subSusanoPower = subSusanoPower;
        this.susanoRunnable = new SuperSusanoRunnable(this);
        if (subSusanoPower != null) {
            role.addPower(subSusanoPower);
            subSusanoPower.setShowInDesc(false);
            setWorkWhenInCooldown(true);
        }
    }

    @Override
    public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
        if (getInteractType().equals(InteractType.INTERACT)) {
            final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
            if (event != null) {
                if (event.getAction().name().contains("LEFT")) {
                    if (this.subSusanoPower != null) {
                        if (!this.susanoRunnable.running) {
                            player.sendMessage("§cIl faut d'abord§a activer§c le§c§l Susanô§r§c.");
                            return false;
                        }
                        this.subSusanoPower.checkUse(player, map);
                        return false;
                    }
                }
                if (getCooldown().isInCooldown()) {
                    sendCooldown(player);
                    return false;
                }
                this.susanoRunnable.start();
                player.sendMessage("§7[§6UHC-Meetup§7] Vous avez§a activer§7 votre§c§l Susanô§7.");
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*5, 0, false, false), EffectWhen.NOW);
                return true;
            }
        }
        return false;
    }
    public static final class SuperSusanoRunnable extends BukkitRunnable {

        private final SuperSusanoPower superSusanoPower;
        private final ParticleDustColored purple = new ParticleDustColored(Color.PURPLE);
        private ParticleLine particleLine;
        private ParticleCircle circle;
        private ParticleCircle circle2;
        private ParticleCircle circle3;

        private int timeLeft = 60*5*20;
        private boolean running = false;

        public SuperSusanoRunnable(SuperSusanoPower superSusanoPower) {
            this.superSusanoPower = superSusanoPower;
        }

        @Override
        public void run() {
            if (!GameState.inGame()) {
                cancel();
                return;
            }
            if (this.timeLeft <= 0) {
                stop();
                return;
            }
            final Player owner = Bukkit.getPlayer(this.superSusanoPower.getRole().getPlayer());
            if (owner == null)return;
            this.superSusanoPower.getRole().getGamePlayer().getActionBarManager().updateActionBar("common.susano", "§bTemps restant (§cSusanô§b):§c "+ StringUtils.secondsTowardsBeautiful(this.timeLeft/20));
            this.timeLeft--;
            activate(owner);
        }
        public synchronized void start() {
            if (running)return;
            this.timeLeft = 60*5*20;
            runTaskTimerAsynchronously(Main.getInstance(), 0, 2);
            this.running = true;
        }
        public synchronized void stop() {
            if (!running)return;
            cancel();
            this.timeLeft = 0;
            this.running = false;
            this.superSusanoPower.getRole().getGamePlayer().getActionBarManager().removeInActionBar("common.susano");
            if (this.circle2 != null) {
                this.circle2.stop();
            }
            if (this.circle != null) {
                this.circle.stop();
            }
            if (this.circle3 != null) {
                this.circle3.stop();
            }
            if (this.particleLine != null) {
                this.particleLine.stop();
            }
            this.superSusanoPower.getRole().getGamePlayer().sendMessage("§7[§6UHC-Meetup§7] Votre§c§l Susanô§7 a été §c désactiver§7.");
            if (this.superSusanoPower.subSusanoPower != null) {
                this.superSusanoPower.subSusanoPower.onSusanoEnd();
            }
        }
        private void activate(@NonNull final Player player) {
            final List<LocationSafe> list = new ArrayList<>();
            final Location b = Loc.getLocationBehindPlayer(player, 1.0);//position du baton qui lie les demi-cercles
            final Location a = Loc.getLocationBehindPlayer(player, 0.1);///Position pour les demi-cercles à l'arrière du joueur
            for (double y = player.getLocation().getY(); y <= (player.getEyeLocation().getY() + 1); y++) {
                final Location clone = b.clone();
                clone.setY(y);
                list.add(new LocationSafe(clone));
            }
            final ParticleLine line = new ParticleLine(purple, 10, list.toArray(new LocationSafe[0]));
            line.display();
            if (this.particleLine != null) {
                this.particleLine.stop();
            }
            this.particleLine = line;
            @NonNull final ParticleCircle circle = new ParticleCircle(
                    this.purple,
                    new LocationSafe(a),
                    1,
                    1,
                    0,
                    player.getLocation().getYaw(),
                    0,
                    12)
                    .setLimit(40);
            if (this.circle != null) {
                this.circle.stop();
            }
            this.circle = circle;
            @NonNull final ParticleCircle circle2 = new ParticleCircle(
                    this.purple,
                    new LocationSafe(a.clone().add(0.0, 1.0, 0.0)),
                    1,
                    1,
                    0,
                    player.getLocation().getYaw(),
                    0,
                    12)
                    .setLimit(40);
            if (this.circle2 != null) {
                this.circle2.stop();
            }
            this.circle2 = circle2;
            @NonNull final ParticleCircle circle3 = new ParticleCircle(
                    this.purple,
                    new LocationSafe(a.clone().add(0.0, 2.0, 0.0)),
                    1,
                    1,
                    0,
                    player.getLocation().getYaw(),
                    0,
                    12)
                    .setLimit(40);
            if (this.circle3 != null) {
                this.circle3.stop();
            }
            this.circle3 = circle3;
        }
    }
}
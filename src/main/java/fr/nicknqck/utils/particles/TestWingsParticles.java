package fr.nicknqck.utils.particles;

import fr.nicknqck.Main;
import hm.zelha.particlesfx.particles.ParticleDustColored;
import hm.zelha.particlesfx.particles.parents.ColorableParticle;
import hm.zelha.particlesfx.shapers.ParticleImage;
import hm.zelha.particlesfx.shapers.parents.Shape;
import hm.zelha.particlesfx.util.LocationSafe;
import hm.zelha.particlesfx.util.ParticleShapeCompound;
import hm.zelha.particlesfx.util.ShapeDisplayMechanic;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public final class TestWingsParticles {

    @Getter
    private static final Map<UUID, TestWingParticle> map = new HashMap<>();

    public static void start(@NonNull final Player player) {
        map.put(player.getUniqueId(), new TestWingParticle(player));
    }
    public static void stop(@NonNull final UUID uuid) {
        map.remove(uuid);
    }
    private static final class TestWingParticle extends BukkitRunnable{

        private final ParticleShapeCompound wings = new ParticleShapeCompound();
        private final UUID uuid;
        private boolean wingFlapping = true;
        private int time = 60;
        private boolean start = false;
        private final ParticleImage particleImage;

        private TestWingParticle(final Player player) {
            this.uuid = player.getUniqueId();
            String path = Main.getInstance().getDataFolder().getAbsolutePath();
            File wingFile = new File(path, "wing.png");
            particleImage = new ParticleImage(new ParticleDustColored(),
                    new LocationSafe(player.getWorld(),
                            player.getLocation().getX(),
                            player.getLocation().getY()+0.5,
                            player.getLocation().getZ()),
                    wingFile,
                    3.15,
                    4,
                    250
            );
            createNewWings();
            runTaskTimer(Main.getInstance(), 1, 2);
        }
        private void createNewWings() {
            wings.addShape(this.particleImage);
            wings.addShape(wings.getShape(0).clone());
            wings.stop();
            wings.getShape(0).setAxisRotation(-90, 0, 10);
            wings.getShape(1).setAxisRotation(-90, 180, 10 + (-20));
            ShapeDisplayMechanic damageMechanic = ((particle, current, addition, count) -> {
                if (500 < System.currentTimeMillis()) return;

                ColorableParticle p = ((ColorableParticle) particle);
                if (p.getColor() == null)return;
                p.getColor().setBlue(0);
                p.getColor().setGreen(0);
            });
            wings.addMechanic(ShapeDisplayMechanic.Phase.AFTER_ROTATION, damageMechanic);
        }

        @Override
        public void run() {
            if (!start) {
                start = true;
                wings.start();
            }
            time--;
            if (time <= 0) {
                wingFlapping = !wingFlapping;
                time = 60;
            }
            final Player player = Bukkit.getPlayer(this.uuid);
            if (player != null) {
                for (Shape wingsShape : wings.getShapes()) {
                    if (wingsShape == null)continue;
                    if (!(wingsShape instanceof ParticleImage))continue;
                    Location loc = player.getLocation();
                    loc.setX(loc.getX()+Math.cos(Math.toRadians(-player.getEyeLocation().getYaw()+90)));
                    loc.setZ(loc.getZ()+Math.sin(Math.toRadians(player.getEyeLocation().getYaw()-90)));
                    loc.setPitch(0);
                    final LocationSafe locationSafe = new LocationSafe(loc.add(0.0, 1.0, 0.0));
                    ((ParticleImage) wingsShape).setCenter(locationSafe);
                    this.particleImage.setCenter(locationSafe);
                }
                wings.getShape(0).setAxisRotation(-90, player.getLocation().getYaw(), 10);
                wings.getShape(1).setAxisRotation(-90, getBackYaw(player), 10 + (-20));
            }
            if (wingFlapping) {
                wings.move(0, -0.035, 0.015);

                for (int k = 0; k <= 1; k++) {
                    wings.getShape(k).rotate(0.5, 0, 0.5 - k);
                }
            } else {
                wings.move(0, 0.035, -0.015);

                for (int k = 0; k <= 1; k++) {
                    wings.getShape(k).rotate(-0.5, 0, -0.5 + k);
                }
            }
        }
        public float getBackYaw(Player player) {
            float yaw = player.getLocation().getYaw();
            yaw += 180f;

            // Normalisation optionnelle
            if (yaw < -180) yaw += 360;
            if (yaw > 180) yaw -= 360;

            return yaw;
        }

    }
}
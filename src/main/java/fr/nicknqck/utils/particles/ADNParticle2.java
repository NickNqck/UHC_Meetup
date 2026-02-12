package fr.nicknqck.utils.particles;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import static java.lang.Math.*;
import static java.lang.Math.PI;
import static java.lang.Math.sin;

public class ADNParticle2 {

    @Setter
    @Getter
    private Location center;
    private final int r;
    private final int g;
    private final int b;
    private final double radius;     // rayon
    private final double height;    // hauteur
    private final double yStep = 0.2;      // espace vertical entre particules
    private final double speed = 0.3;      // vitesse rotation
    private double angleOffset = 0;  // pour animer la rotation
    @Getter
    private ADNRunnable adnRunnable;
    @Getter
    private boolean running = false;

    public ADNParticle2(Location center, int r, int g, int b) {
        this.center = center;
        this.r = r;
        this.g = g;
        this.b = b;
        this.radius = 2.5;
        this.height = 15.0;
    }
    public ADNParticle2(Location center, int r, int g, int b, double radius, double hauteur) {
        this.center = center;
        this.r = r;
        this.g = g;
        this.b = b;
        this.radius = radius;
        this.height = hauteur;
    }
    public void start() {
        this.adnRunnable = new ADNRunnable(this);
        running = true;
    }
    public static class ADNRunnable extends BukkitRunnable {

        private final ADNParticle2 adnParticle2;
        @Getter
        @Setter
        private boolean goCancel = false;

        public ADNRunnable(ADNParticle2 adnParticle2) {
            this.adnParticle2 = adnParticle2;
            runTaskTimerAsynchronously(Main.getInstance(), 0, 3);
        }

        @Override
        public void run() {
            // Vérifie si la partie est en cours
            if (!GameState.inGame()) {
                goCancel = true;
            }
            if (isGoCancel()) {
                cancel();
                return;
            }

            this.adnParticle2.angleOffset += 0.1; // ADN tourne doucement

            for (double y = 0; y <= this.adnParticle2.height; y += this.adnParticle2.yStep) {
                double angle = y * this.adnParticle2.speed + this.adnParticle2.angleOffset;

                // Spirale 1
                double x1 = this.adnParticle2.center.getX() + this.adnParticle2.radius * cos(angle);
                double z1 = this.adnParticle2.center.getZ() + this.adnParticle2.radius * sin(angle);
                double y1 = this.adnParticle2.center.getY() + y;

                // Spirale 2 (décalée de PI)
                double x2 = this.adnParticle2.center.getX() + this.adnParticle2.radius * cos(angle + PI);
                double z2 = this.adnParticle2.center.getZ() + this.adnParticle2.radius * sin(angle + PI);
                double y2 = this.adnParticle2.center.getY() + y;

                // Particules avec la couleur choisie
                MathUtil.spawnParticle(new Location(this.adnParticle2.center.getWorld(), x1, y1, z1), this.adnParticle2.r, this.adnParticle2.g, this.adnParticle2.b);
                MathUtil.spawnParticle(new Location(this.adnParticle2.center.getWorld(), x2, y2, z2), this.adnParticle2.r, this.adnParticle2.g, this.adnParticle2.b);
            }
        }
    }
}
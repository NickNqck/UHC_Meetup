package fr.nicknqck.utils.particles;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.nicknqck.Main;
import fr.nicknqck.utils.MathL;
import fr.nicknqck.utils.VectorUtils;
import fr.nicknqck.utils.WorldUtils;
import net.minecraft.server.v1_8_R3.EnumParticle;

public class WingsEffect extends ParticleEffect {

    private final EnumParticle particle;

    public WingsEffect(int timeInTicks, EnumParticle effect) {
        super(timeInTicks);
        this.particle = effect;
    }

    @Override
    public void start(Player player) {
        new BukkitRunnable() {
            int ticks;

            @SuppressWarnings("unused")
			double var;


            @Override
            public void run() {

                if (player.getGameMode() == GameMode.SPECTATOR) cancel();

                if (ticks > getTimeInTicks())
                    cancel();

                var += Math.PI / 16;

                Location location = player.getLocation();

                for (double t = 0; t < Math.PI * 2; t += Math.PI / 48) {
                    double offset = (Math.pow(Math.E, MathL.cos(t)) - 2 * MathL.cos(t * 4) - Math.pow(MathL.sin(t / 12), 5)) / 2;
                    double x = MathL.sin(t) * offset;
                    double y = MathL.cos(t) * offset;
                    Vector v = VectorUtils.rotateAroundAxisY(new Vector(x, y, -0.3), -Math.toRadians(location.getYaw()));
                    WorldUtils.spawnParticle(location.clone().add(v.getX(), v.getY() + 0.5, v.getZ()), WingsEffect.this.particle);
                }


                ticks++;
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

}

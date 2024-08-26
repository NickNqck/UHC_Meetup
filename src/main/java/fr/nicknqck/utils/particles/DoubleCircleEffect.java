package fr.nicknqck.utils.particles;

import fr.nicknqck.GameState;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicknqck.Main;
import fr.nicknqck.utils.WorldUtils;
import net.minecraft.server.v1_8_R3.EnumParticle;

public class DoubleCircleEffect extends ParticleEffect {

    private final EnumParticle particle;
    @Nullable
    private float red;
    @Nullable
    private float green;
    @Nullable
    private float blue;
    public DoubleCircleEffect(int timeInTicks, EnumParticle effect) {
        super(timeInTicks);
        this.particle = effect;
    }
    public DoubleCircleEffect(int timeInTicks, EnumParticle effect, float r, float g, float b) {
        super(timeInTicks);
        this.particle = effect;
        this.red = r;
        this.green = g;
        this.blue = b;
    }

    @Override
    public void start(Player player) {
        new BukkitRunnable() {
            int ticks;

            double var;


            @Override
            public void run() {

                if (player.getGameMode() == GameMode.SPECTATOR) cancel();
                if (GameState.getInstance().getServerState() != GameState.ServerStates.InGame) ticks = getTimeInTicks()+5;
                if (ticks > getTimeInTicks())
                    cancel();

                var += Math.PI / 16;

                Location loc = player.getLocation();
                Location firstCircle = loc.clone().add(Math.cos(var), Math.sin(var) + 1, Math.sin(var));
                Location secondCircle = loc.clone().add(Math.cos(var + Math.PI), Math.sin(var) + 1, Math.sin(var + Math.PI));
                if (green == 0.0f && red == 0.0f && blue == 0.0f) {
                    WorldUtils.spawnParticle(firstCircle, particle);
                    WorldUtils.spawnParticle(secondCircle, particle);
                } else {
                    MathUtil.spawnColoredParticle(firstCircle, particle, red, green, blue);
                    MathUtil.spawnColoredParticle(secondCircle, particle, red, green, blue);
                }
                ticks++;
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
    }

}

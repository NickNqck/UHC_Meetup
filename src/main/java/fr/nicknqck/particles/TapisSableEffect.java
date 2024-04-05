package fr.nicknqck.particles;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicknqck.Main;
import fr.nicknqck.utils.WorldUtils;
import net.minecraft.server.v1_8_R3.EnumParticle;

public class TapisSableEffect extends ParticleEffect {

	private final EnumParticle particle;

	private final int r, g, b;

	public TapisSableEffect(int timeInTicks, EnumParticle effect, int r, int g, int b) {
		super(timeInTicks);
		this.particle = effect;
		this.r = r;
		this.g = g;
		this.b = b;
	}

	@Override
	public void start(Player player) {
		new BukkitRunnable() {
			int ticks;

			@Override
			public void run() {
				
				if(player.getGameMode() == GameMode.SPECTATOR) cancel();
				
				if (ticks > getTimeInTicks())
					cancel();
				
				Location location = player.getLocation();

				List<Location> particlesLocations = WorldUtils.generateCylinder(location, 5);

				for (Location particleLocation : particlesLocations) {
					WorldUtils.spawnColoredParticle(particleLocation, particle, r, g, b);
				}

				ticks++;
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
	}

}

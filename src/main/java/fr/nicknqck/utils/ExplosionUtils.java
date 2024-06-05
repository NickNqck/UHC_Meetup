package fr.nicknqck.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicknqck.Main;

public class ExplosionUtils {
	
	public static void createExplosion(Location loc) {
		TNTPrimed tnt = (TNTPrimed) loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
		tnt.setFuseTicks(0);
	}
	public static void createExplosionAfter(Location loc, int tick) {
		new BukkitRunnable() {
			int i = 0;
			@Override
			public void run() {
				if (i >= tick) {
					Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
						createExplosion(loc);
					});
					cancel();
					return;
				}
				i++;
			}
		}.runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
	}
	public static void createExplosion(Location loc, double damage, double range, UUID... imun) {
		createExplosion(loc);
		if (imun != null) {
			List<UUID> i = new ArrayList<>();
			for (UUID u : imun) {
				i.add(u);
			}
			for (Player p : Loc.getNearbyPlayers(loc, range)) {
				if (!i.contains(p.getUniqueId())) {
					p.damage(damage);
				}
			}
		} else {
			for (Player p : Loc.getNearbyPlayers(loc, range)) {
				p.damage(damage);
			}
		}
	}
	public static void createExplosion(Location loc, double damage, double range) {
		createExplosion(loc, damage, range, UUID.fromString(""));
	}
}
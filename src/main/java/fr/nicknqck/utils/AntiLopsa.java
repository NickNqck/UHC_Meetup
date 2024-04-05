package fr.nicknqck.utils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.nicknqck.Main;

public class AntiLopsa {
	
    public static void startWorldBorderChecker() {
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Location playerLocation = player.getLocation();
                    World world = player.getWorld();
                    double worldBorderSize = world.getWorldBorder().getSize() / 2;

                    if (Math.abs(playerLocation.getX()) >= worldBorderSize || Math.abs(playerLocation.getZ()) >= worldBorderSize) {
                        if (player.getGameMode() == GameMode.SPECTATOR) {
                            player.teleport(new Location(world, 0, world.getHighestBlockYAt(0, 0), 0));
                            player.sendMessage("[§cAnti-Lopsa§r] Teleportation au 0 0");
                        } else {
                        	double newX = Math.max(-worldBorderSize + 1, Math.min(worldBorderSize - 1, playerLocation.getX()));
                            double newZ = Math.max(-worldBorderSize + 1, Math.min(worldBorderSize - 1, playerLocation.getZ()));
                            
                            Location teleportLocation = new Location(world, newX, world.getHighestBlockYAt((int)newX, (int)newZ), newZ);
                            player.teleport(teleportLocation);
                            player.sendMessage("Vous avez été automatiquement téléporter à l'intérieur de la bordure !");
                            Vector direction = new Vector(0, 0, 0).subtract(teleportLocation.toVector()).normalize();
                            player.teleport(player.getLocation().setDirection(direction));
                        }
                    }
                }
            }
        }, 0L, 20L);
    }
}
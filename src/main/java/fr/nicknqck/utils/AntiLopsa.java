package fr.nicknqck.utils;

import fr.nicknqck.GameState;
import fr.nicknqck.player.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.nicknqck.Main;

import java.util.ArrayList;
import java.util.List;

public class AntiLopsa {
	
    public static void startWorldBorderChecker() {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (!GameState.inGame()) {
                    cancel();
                    return;
                }
                final List<Player> playerList = new ArrayList<>(Bukkit.getOnlinePlayers());
                for (Player player : playerList) {
                    final GamePlayer gamePlayer = GamePlayer.of(player.getUniqueId());
                    if (gamePlayer == null) {
                        Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), () -> {
                            GamePlayer value = new GamePlayer(player);
                            value.setAlive(false);
                            GameState.getInstance().getGamePlayer().put(player.getUniqueId(), value);});
                        continue;
                    }
                    final Location playerLocation = gamePlayer.getLastLocation();
                    final World world = playerLocation.getWorld();
                    double worldBorderSize = world.getWorldBorder().getSize() / 2;

                    if (Math.abs(playerLocation.getX()) >= worldBorderSize || Math.abs(playerLocation.getZ()) >= worldBorderSize) {
                        if (player.getGameMode() == GameMode.SPECTATOR) {
                            Bukkit.getScheduler().runTask(Main.getInstance(), () -> player.teleport(new Location(world, 0, world.getHighestBlockYAt(0, 0), 0)));
                            player.sendMessage("[§cAnti-Lopsa§r] Teleportation au 0 0");
                        } else {
                            double newX = Math.max(-worldBorderSize + 1, Math.min(worldBorderSize - 1, playerLocation.getX()));
                            double newZ = Math.max(-worldBorderSize + 1, Math.min(worldBorderSize - 1, playerLocation.getZ()));

                            Location teleportLocation = new Location(world, newX, world.getHighestBlockYAt((int)newX, (int)newZ), newZ);
                            player.sendMessage("Vous avez été automatiquement téléporter à l'intérieur de la bordure !");
                            Vector direction = new Vector(0, 0, 0).subtract(teleportLocation.toVector()).normalize();
                            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                                player.teleport(teleportLocation);
                                player.teleport(player.getLocation().setDirection(direction));
                            });
                        }
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 1, 20);
    }
}
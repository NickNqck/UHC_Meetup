package fr.nicknqck.player;

import fr.nicknqck.Main;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class StunManager implements Listener {

    @Getter
    private static final Map<UUID, Boolean> stuns = new HashMap<>();
    @EventHandler
    private void onDamage(EntityDamageEvent e) {
        if (stuns.containsKey(e.getEntity().getUniqueId())){
            if (stuns.containsKey(e.getEntity().getUniqueId())){
                if (stuns.get(e.getEntity().getUniqueId())){
                    e.setDamage(0.0);
                    e.setCancelled(true);
                }
            }
        }
    }
    public static void stun(UUID uuid, double seconds, boolean damage){
        Player target = Bukkit.getPlayer(uuid);
        if (target != null){
            getStuns().put(target.getUniqueId(), damage);
            final Location gLoc = target.getLocation().clone();
            new BukkitRunnable() {
                private double tickRemaining = 20*seconds;
                @Override
                public void run() {
                    if (tickRemaining == 0){
                        getStuns().remove(target.getUniqueId(), damage);
                        cancel();
                        return;
                    }
                    target.teleport(gLoc);
                    tickRemaining--;
                }
            }.runTaskTimer(Main.getInstance(), 0, 1);
        }
    }
}
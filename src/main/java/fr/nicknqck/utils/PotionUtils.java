package fr.nicknqck.utils;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionUtils implements Listener {
    private static final ArrayList<Player> noFall = new ArrayList<>();
    private static final ArrayList<Player> invincible = new ArrayList<>();
    private static final ArrayList<Player> freeze = new ArrayList<>();
    public static void effectGive(Player p, PotionEffectType e, int t, int l){
        p.addPotionEffect(new PotionEffect(e, t * 20, l, true, false));
    }

    public static void effectRemove(Player p, PotionEffectType e){
        p.removePotionEffect(e);
    }

    public static void effetGiveNofall(Player p){
        noFall.add(p);
    }

    public static void effetRemoveNofall(Player p) {
        noFall.remove(p);
    }

    public static void effetGiveInvincible(Player p){
        invincible.add(p);
    }

    public static void effetRemoveInvincible(Player p) {
        invincible.remove(p);
    }

    public static void effetGiveFreeze(Player p){
        freeze.add(p);
    }

    public static void effetRemoveFreeze(Player p) {
        freeze.remove(p);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() == DamageCause.FALL) {
            Player p = (Player) event.getEntity();
            if (noFall.contains(p) || invincible.contains(p)) {
                event.setCancelled(true);
                System.out.println("cancelled damage of "+p.getName());
            }
        }else {
        	if (event.getEntity() instanceof Player) {
        		Player p = (Player) event.getEntity();
        		if (invincible.contains(p)) {
                    event.setCancelled(true);
                }
        	}
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        Player p = e.getPlayer();
        if(freeze.contains(p)) {
            p.teleport(e.getFrom());
        }
    }
    public static ArrayList<Player> getNoFalls(){
    	return noFall;
    }
}
package fr.nicknqck.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.nicknqck.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

public class PropulserUtils implements Listener {

	private final Player p;
	private String toPlayer;
	private boolean sound = false;
	private final int distance;
	private boolean NF = false;
	private final List<UUID> inNF = new ArrayList<>();
	public PropulserUtils(Player user, int distance) {
		this.p = user;
		this.distance = distance;
	}
	public PropulserUtils soundToPlay(String sound) {
		this.toPlayer = sound;
		this.sound = true;
		return this;
	}
	public PropulserUtils setNoFall(boolean b){
		NF = b;
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
		return this;
	}
	@EventHandler
	private void onDamage(EntityDamageEvent e){
		if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)){
			if (inNF.contains(e.getEntity().getUniqueId())){
				inNF.remove(e.getEntity().getUniqueId());
				e.setCancelled(true);
			}
		}
	}

	public List<UUID> getPropulsedUUID(){
		List<UUID> toReturn = new ArrayList<>();
		for (Entity entity : p.getNearbyEntities(distance, distance, distance)) {
			if (entity.getUniqueId() != null) {
				toReturn.add(entity.getUniqueId());
			}
		}
		return toReturn;
	}
	public void applyPropulsion() {
		for (Entity entity : p.getNearbyEntities(distance, distance, distance)) {
            Vector direction = entity.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().multiply(10);
            direction.add(new Vector(0, .8, .8));
            entity.setVelocity(direction);
			if (NF){
				inNF.add(entity.getUniqueId());
			}
            if (entity instanceof Player && sound) {
            	playSound((Player) entity, toPlayer);
            }
        }
	}
	public void applyPropulsion(Player onlyTarget) {
		Loc.inverserDirectionJoueur(onlyTarget);
		Vector direction = onlyTarget.getLocation().getDirection().multiply(10);
        direction.setY(1.8);
        onlyTarget.setVelocity(direction);
		if (NF){
			inNF.add(onlyTarget.getUniqueId());
		}
        if (sound) {
        	playSound(onlyTarget, toPlayer);
        }
	}
	private void playSound(Player p, String sound) {
		p.getWorld().setGameRuleValue("sendCommandFeedback", "false");
		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		Bukkit.dispatchCommand(console, "playSound "+sound+" "+p.getName()+ " "+p.getLocation().getBlockX()+" "+p.getLocation().getBlockY()+" "+p.getLocation().getBlockZ());
		p.getWorld().setGameRuleValue("sendCommandFeedback", "true");
	}
}
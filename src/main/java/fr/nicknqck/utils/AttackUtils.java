package fr.nicknqck.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class AttackUtils implements Listener{
	
	public static List<UUID> CantAttack = new ArrayList<>();
	public static  List<UUID> CantReceveAttack = new ArrayList<>();
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onTape(EntityDamageByEntityEvent e) {
		if (CantAttack.contains(e.getDamager().getUniqueId())) {
				e.setCancelled(true);
		}
		if (CantReceveAttack.contains(e.getEntity().getUniqueId())) {
			e.setCancelled(true);
		}
		if (e.getEntity() instanceof Projectile) {
			Projectile proj = (Projectile) e.getEntity();
			if (proj.getShooter() instanceof Entity) {
				Entity shooter = (Entity) proj.getShooter();
				if (CantAttack.contains(shooter.getUniqueId())) {
					e.setCancelled(true);
				}
			}
		}
	}
}
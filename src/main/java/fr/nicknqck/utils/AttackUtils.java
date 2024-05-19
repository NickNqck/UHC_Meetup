package fr.nicknqck.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import fr.nicknqck.events.custom.EndGameEvent;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class AttackUtils implements Listener{
	
	public static List<UUID> CantAttack = new ArrayList<>();
	public static  List<UUID> CantReceveAttack = new ArrayList<>();
	@Getter
	private static final HashMap<UUID, UUID[]> cantAttackNobody = new HashMap<>();
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onTape(EntityDamageByEntityEvent e) {
		if (CantAttack.contains(e.getDamager().getUniqueId())) {
				e.setCancelled(true);
		}
		if (CantReceveAttack.contains(e.getEntity().getUniqueId())) {
			e.setCancelled(true);
		}
		if (!cantAttackNobody.isEmpty()){
			if (cantAttackNobody.containsKey(e.getDamager().getUniqueId())){
				for (UUID u : cantAttackNobody.get(e.getDamager().getUniqueId())){
					if (e.getEntity().getUniqueId().equals(u)){
						e.setCancelled(true);
						break;
					}
				}
			}
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
	public static void setCantAttack(Player cantAttack, UUID... cantBeAttacked){
		cantAttackNobody.put(cantAttack.getUniqueId(), cantBeAttacked);
	}
	@EventHandler
	private void onEndGame(EndGameEvent event){
		CantAttack.clear();
		CantReceveAttack.clear();
		cantAttackNobody.clear();
	}
}
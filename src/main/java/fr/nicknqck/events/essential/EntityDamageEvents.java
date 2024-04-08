package fr.nicknqck.events.essential;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.scenarios.impl.AntiPvP;
import fr.nicknqck.utils.AttackUtils;

public class EntityDamageEvents implements Listener{

	
	private final GameState gameState = GameState.getInstance();
	
	@EventHandler(priority = EventPriority.NORMAL)
	private void OnDamagedEntity(EntityDamageEvent event) {
		if (AttackUtils.CantReceveAttack.contains(event.getEntity().getUniqueId())) {
			event.setCancelled(true);
		}
		if (gameState.getServerState() == ServerStates.InLobby) {
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				boolean canceled = false;
				if (event.getCause() == DamageCause.ENTITY_ATTACK) {
					if (AntiPvP.isAntipvplobby()) {
						event.setCancelled(true);
						canceled = true;
					}
				}else {
					canceled = true;
					event.setCancelled(true);
				}
				
				if (player.getHealth()-event.getDamage() <= 0 && !canceled) {
					player.setHealth(20.0);
					player.setFoodLevel(20);
					player.teleport(new Location(player.getWorld(), 0, 151, 0));
					player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
					event.setCancelled(true);
				}
			}
		} else {
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				Player killer = player.getKiller();
				Double damage = event.getFinalDamage();
				for (Player p : gameState.getInGamePlayers()) {
					if (!gameState.hasRoleNull(p)) {
						gameState.getPlayerRoles().get(p).onALLPlayerDamage(event, player);
					}
				}
				for (Chakras ch : Chakras.values()) {
					ch.getChakra().onEntityDamage(event, player);
				}
				if (event.getCause() == DamageCause.FALL) {
					if (gameState.getPlayerRoles().containsKey(player)) {
						if (gameState.getPlayerRoles().get(player).isHasNoFall()) {
							event.setCancelled(true);
						} else {
							if (player.getWorld().equals(Main.getInstance().nakime)) {
								event.setDamage(event.getDamage()/4);
							}
						}
					}
				}
				if (event.getCause() == DamageCause.LIGHTNING) {
					if (gameState.getPlayerRoles().containsKey(player)) {
						event.setCancelled(true);
					}
				}
				if (gameState.shutdown.contains(player)) {
					event.setCancelled(true);
				}
				if (event.getCause() == DamageCause.BLOCK_EXPLOSION || event.getCause() == DamageCause.ENTITY_EXPLOSION) {
					if (gameState.getPlayerRoles().containsKey(player)) {
						if (gameState.getPlayerRoles().get(player).onReceveExplosionDamage()) {
							event.setDamage(0);
							event.setCancelled(true);
						}
						for (Player p : gameState.getInGamePlayers()) {
							if (!gameState.hasRoleNull(p)) {
								if (!event.isCancelled()) {
									gameState.getPlayerRoles().get(p).onAllPlayerDamageByExplosion(event, event.getCause(), p);
								} else {
									break;
								}
							}
						}
					}
				}
				if (gameState.getPlayerRoles().containsKey(player)) {
					if (gameState.getPlayerRoles().get(player).isInvincible()) {
						event.setCancelled(true);
						return;
					}
				}
				if (gameState.getInSleepingPlayers().contains(player) || gameState.getInObiPlayers().contains(player)) {
					event.setCancelled(true);
				}
				if ((player.getHealth()-damage) <= 0) {
					if (gameState.getInGamePlayers().contains(player)) {
						if (gameState.getPlayerRoles().containsKey(player)) {
							if (gameState.getPlayerRoles().get(player).isCanRespawn()) {
								for (Player p : Bukkit.getOnlinePlayers()) {
									p.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
								}
								gameState.getPlayerRoles().get(player).PlayerKilled(killer, player, gameState);
							}							
						} else {
							GameListener.RandomTp(player, gameState);
						}
					}
				}
			}
		}
	}
}
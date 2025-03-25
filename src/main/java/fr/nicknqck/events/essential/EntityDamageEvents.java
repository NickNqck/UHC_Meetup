package fr.nicknqck.events.essential;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.scenarios.impl.AntiPvP;
import fr.nicknqck.utils.AttackUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.UUID;

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
				double damage = event.getFinalDamage();
				for (UUID u : gameState.getInGamePlayers()) {
					Player p = Bukkit.getPlayer(u);
					if (p == null)continue;
					if (!gameState.hasRoleNull(p.getUniqueId())) {
						gameState.getGamePlayer().get(p.getUniqueId()).getRole().onALLPlayerDamage(event, player);
					}
				}
				for (Chakras ch : Chakras.values()) {
					ch.getChakra().onEntityDamage(event, player);
				}
				if (event.getCause() == DamageCause.FALL) {
					if (!gameState.hasRoleNull(player.getUniqueId())) {
						if (gameState.getGamePlayer().get(player.getUniqueId()).getRole().isHasNoFall()) {
							event.setCancelled(true);
						} else {
							if (player.getWorld().getName().equals("nakime")) {
								event.setDamage(event.getDamage()/4);
							}
						}
					}
				}
				if (event.getCause() == DamageCause.LIGHTNING) {
					if (!gameState.hasRoleNull(player.getUniqueId())) {
						event.setCancelled(true);
					}
				}
				if (gameState.shutdown.contains(player)) {
					event.setCancelled(true);
				}
				if (!gameState.hasRoleNull(player.getUniqueId())) {
					if (gameState.getGamePlayer().get(player.getUniqueId()).getRole().isInvincible()) {
						event.setCancelled(true);
						return;
					}
				}
				if (gameState.getInSleepingPlayers().contains(player) || gameState.getInObiPlayers().contains(player)) {
					event.setCancelled(true);
				}
				if ((player.getHealth()-damage) <= 0) {
					if (gameState.getInGamePlayers().contains(player.getUniqueId())) {
						if (!gameState.hasRoleNull(player.getUniqueId())) {
							if (gameState.getGamePlayer().get(player.getUniqueId()).getRole().isCanRespawn()) {
								for (final Player p : Bukkit.getOnlinePlayers()) {
									p.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
								}
								gameState.getGamePlayer().get(player.getUniqueId()).getRole().PlayerKilled(killer, player, gameState);
							}							
						} else {
							GameListener.RandomTp(player);
						}
					}
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void EntityDamage(EntityDamageByEntityEvent e){
		if (e.getEntity() instanceof Player){
			if (e.getDamager() instanceof Player){
				Player damager = (Player)e.getDamager();
				if (damager.getLocation().getY() <= 124){
					if (damager.getWorld().getName().equals("nakime") && damager.getWorld().equals(e.getEntity().getWorld())){
						e.setDamage(0.0);
						e.setCancelled(true);
					}
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageEvent e){
		if (e.getEntity() instanceof Player){
			Player p = (Player)e.getEntity();
			if (p.getLocation().getY() <= 124){
				if (p.getWorld().getName().equals("nakime")){
					e.setDamage(0.0);
					e.setCancelled(true);
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void OnDamagedEntityByEntity(EntityDamageByEntityEvent event) {
		if (gameState.getServerState() == ServerStates.InGame) {
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				Entity damageur = event.getDamager();
				double damage = event.getFinalDamage();
				if (damageur instanceof Player) {
					Player damager = (Player) event.getDamager();
					if (!gameState.hasRoleNull(damager.getUniqueId())) {
						gameState.getGamePlayer().get(damager.getUniqueId()).getRole().ItemUseAgainst(damager.getItemInHand(), player, gameState);
						gameState.getGamePlayer().get(damager.getUniqueId()).getRole().neoItemUseAgainst(damager.getItemInHand(), player, gameState, damager);
						/*
						 * (damager).getItemInHand() = ItemStack item
						 * player = Player victim
						 */
						if (player != null) {
							Player attacker = (Player) damageur;
							if (gameState.shutdown.contains(attacker)) {
								event.setCancelled(true);
							}
							gameState.getGamePlayer().get(player.getUniqueId()).getRole().neoAttackedByPlayer(attacker, gameState);
						}
					}
				}
				assert player != null;
				if (player.getHealth()-damage <= 0) {
					if (event.getCause() != DamageCause.FALL) {
						if (gameState.getInGamePlayers().contains(player.getUniqueId())) {
							if (!gameState.hasRoleNull(player.getUniqueId())) {
								if (gameState.getGamePlayer().get(player.getUniqueId()).getRole().isCanRespawn()) {
									assert damageur instanceof Player;
									gameState.getGamePlayer().get(player.getUniqueId()).getRole().PlayerKilled((Player)damageur, player, gameState);
									event.setCancelled(true);
								}
							}
						}
					} else {
						if (!gameState.hasRoleNull(player.getUniqueId())) {
							if (gameState.getGamePlayer().get(player.getUniqueId()).getRole().isHasNoFall()) {
								event.setDamage(0);
								event.setCancelled(true);
							}
						}
					}
				}
			}
		} else {//else du serverstates.ingame
			if (AntiPvP.isAntipvplobby()) {
				event.setCancelled(true);
			}
		}
	}
}
package fr.nicknqck;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.demons.Susamaru;

public class HomingBow implements Listener {
	GameState gameState;
	
	public HomingBow(GameState gameState) {
		this.gameState = gameState;
	}
	   
	private boolean truc = false;

	@EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
		Projectile projectile = event.getEntity();
		if (projectile.getShooter() instanceof Player) {
			Player shooter = (Player) projectile.getShooter();
			if (!gameState.hasRoleNull(shooter)) {
				gameState.getPlayerRoles().get(shooter).onProjectileLaunch(event, shooter);
			}
		}
        // Vérifier si le projectile est une flèche
        if (projectile instanceof Arrow) {
            Arrow arrow = (Arrow) projectile;
            
            // Vérifier si le tireur est un joueur
            if (arrow.getShooter() instanceof Player) {
                Player shooter = (Player) arrow.getShooter();
                if (!gameState.hasRoleNull(shooter)) {
                	gameState.getPlayerRoles().get(shooter).onProjectileLaunch(event.getEntity(), shooter);
                    if (isSpecialPlayer(shooter) && shooter.getItemInHand().equals(Items.getSusamaruBow())) {
                 	   truc = true;
                 	   RoleBase role = gameState.getPlayerRoles().get(shooter);
                 	   Susamaru sam = (Susamaru) role;
                 	   if (sam.Niveau2) {
                 		   if (sam.cooldown <= 0) {
                               sam.cooldown = 60;
                     		   arrow.setVelocity(arrow.getVelocity().multiply(1.5)); // Increase arrow speed

                                shooter.setAllowFlight(true); // Allow the player to fly
                                shooter.setFlying(true); // Enable flight mode

                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        if (!arrow.isValid() || arrow.isOnGround()) {
                                            this.cancel();
                                            shooter.setAllowFlight(false); // Disable flight after arrow lands
                                            shooter.setFlying(false); // Disable flight mode
                                            Location arrowLocation = arrow.getLocation();
                                            shooter.teleport(arrowLocation); // Teleport player to arrow location
                                            Vector arrowVelocity = arrow.getVelocity().multiply(1.5); // Increase arrow speed
                                            arrow.setVelocity(arrowVelocity);
                                            arrow.setVelocity(arrowVelocity.multiply(1 / 1.5)); // Reset arrow velocity to normal
                                        } else {
                                            Vector playerDirection = arrow.getLocation().toVector().subtract(shooter.getLocation().toVector());
                                            playerDirection.setY(0).normalize(); // Calculate the direction vector

                                            double distance = 1.5; // Distance behind the arrow
                                            Vector teleportLocation = arrow.getLocation().toVector().add(playerDirection.multiply(-distance));
                                            shooter.teleport(teleportLocation.toLocation(shooter.getWorld())); // Teleport player behind the arrow
                                        }
                                    }
                                }.runTaskTimer(Main.getInstance(), 0L, 1L);
                 		   } else {
            					sam.owner.sendMessage("Vous n'avez pas réussis à suivre votre §6Ballon§r il était en cooldown (actuellement "+sam.cooldown+"s)");
            				}
                 	   }
                    } else {
                 	   truc = false;
                    }
                }
            }
        }
    }
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if (event.getEntity().getShooter() instanceof Player) {
			Player shooter = (Player) event.getEntity().getShooter();
			if (!gameState.hasRoleNull(shooter)) {
				gameState.getPlayerRoles().get(shooter).onProjectileHit(event, shooter);
			}
		}
	    if (event.getEntity() instanceof Arrow) {
	        Arrow arrow = (Arrow) event.getEntity();
	        if (arrow.getShooter() instanceof Player) {
	            Player shooter = (Player) arrow.getShooter();
	            if (!gameState.hasRoleNull(shooter)) {
	            	gameState.getPlayerRoles().get(shooter).onProjectileHit(event.getEntity(), shooter);
		            if (isSpecialPlayer(shooter)) {
		                if (truc) {
		                    RoleBase role = gameState.getPlayerRoles().get(shooter);
		                    Susamaru sam = (Susamaru) role;
		                    if (sam.Niveau1) {
		                        if (sam.cooldown <= 0) {
		                            Location impactLocation = arrow.getLocation();
		                            World world = impactLocation.getWorld();
		                            world.createExplosion(impactLocation, 4.0f, false); // Créer une explosion sans dégâts
		                            sam.cooldown = 15;
		                            // Informer les joueurs se trouvant dans le rayon de l'explosion
		                            double radius = 5.0; // Rayon de la zone d'explosion
		                            for (Player player : world.getPlayers()) {
		                                if (player.getLocation().distance(impactLocation) <= radius) {
		                                    player.sendMessage("Vous avez été touché par l'§6Éxplosion§r de§c Susamaru");
		                                    if (player.getHealth() > 4.0) {
		                                    	player.setHealth(player.getHealth()-4.0);
		                                    } else {
		                                    	player.setHealth(1.0);
		                                    }
		                                }
		                            }
		                        } else {
		                            sam.owner.sendMessage("Votre §6Ballon§r n'a pas créé d'explosion car il était en cooldown (actuellement "+sam.cooldown+"s)");
		                        }                				
		                    }
		                    if (sam.Niveau2) {
                                if (shooter.getGameMode() != org.bukkit.GameMode.CREATIVE) {
		                            shooter.setAllowFlight(false); // Disable flight if the arrow hits a block
		                            shooter.setFlying(false);
		                        } else {
		                            shooter.sendMessage("Pourquoi tu triches ?");
		                        }
		                    }
		                }
		            }	
	            }
	        }
	    }
	}
	private boolean isSpecialPlayer(Player player) {
		if (gameState.getServerState().equals(ServerStates.InGame)) {
			if (gameState.getInGamePlayers().contains(player)) {
				if (!gameState.hasRoleNull(player)) {
					return gameState.getPlayerRoles().get(player) instanceof Susamaru;
				}
			}
		}
		return false;
	}
}
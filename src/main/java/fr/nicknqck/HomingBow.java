package fr.nicknqck;

import lombok.NonNull;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class HomingBow implements Listener {
	private final GameState gameState;
	
	public HomingBow(GameState gameState) {
		this.gameState = gameState;
	}

	@EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
		@NonNull final Projectile projectile = event.getEntity();
        if (projectile instanceof Arrow) {
            Arrow arrow = (Arrow) projectile;
            if (arrow.getShooter() instanceof Player) {
                Player shooter = (Player) arrow.getShooter();
                if (!gameState.hasRoleNull(shooter.getUniqueId())) {
                	gameState.getGamePlayer().get(shooter.getUniqueId()).getRole().onProjectileLaunch(event.getEntity(), shooter);
                }
            }
        }
    }
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if (event.getEntity().getShooter() instanceof Player) {
			Player shooter = (Player) event.getEntity().getShooter();
			if (!gameState.hasRoleNull(shooter.getUniqueId())) {
				gameState.getGamePlayer().get(shooter.getUniqueId()).getRole().onProjectileHit(event, shooter);
			}
		}
	    if (event.getEntity() instanceof Arrow) {
	        Arrow arrow = (Arrow) event.getEntity();
	        if (arrow.getShooter() instanceof Player) {
	            Player shooter = (Player) arrow.getShooter();
	            if (!gameState.hasRoleNull(shooter.getUniqueId())) {
	            	gameState.getGamePlayer().get(shooter.getUniqueId()).getRole().onProjectileHit(event.getEntity(), shooter);
	            }
	        }
	    }
	}
}
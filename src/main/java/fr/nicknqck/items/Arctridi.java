package fr.nicknqck.items;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;

public class Arctridi implements Listener{
	private final GameState gameState;
	public Arctridi(GameState gameState) {
		this.gameState = gameState;
	}
	
	@EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            ItemStack bow = event.getBow();
            if (gameState.getPlayerRoles().containsKey(player)) {
            	if (!bow.getType().equals(Material.BOW))return;
            	if (bow.isSimilar(gameState.EquipementTridi())) {
                    Arrow arrow = (Arrow) event.getProjectile();
                    arrow.setMetadata("teleportArrow "+gameState.getPlayerRoles().get(player).roleID, new FixedMetadataValue(Main.getInstance(), player.getLocation()));
                    System.out.println(arrow.getMetadata("teleportArrow "+gameState.getPlayerRoles().get(player).roleID));
                }
            } else {
            	event.setCancelled(true);
            }
        }
    }
	private final List<Player> noFall = new ArrayList<>();
	@EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
    	if (gameState.getServerState() != ServerStates.InGame)return;
    	if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player) {
    		if (event.getEntity().hasMetadata("teleportArrow "+gameState.getPlayerRoles().get((Player)event.getEntity().getShooter()).roleID)) {
                Arrow arrow = (Arrow) event.getEntity();
                Player player = (Player) arrow.getShooter();
                if (gameState.getPlayerRoles().get(player).ArcTridiCooldown() <= 0 && !gameState.getPlayerRoles().get(player).isTransformedinTitan) {
                	if (gameState.getPlayerRoles().get(player).gazAmount >0) {
                		Vector arrowVelocity = arrow.getVelocity();
                        Vector playerVelocity = player.getLocation().getDirection().setY(0).normalize().multiply(1.5); // Adjust the teleport distance
                        Vector finalVelocity = arrowVelocity.add(playerVelocity);
                        noFall.add(player);
            			Location initLoc = player.getLocation();
                        gameState.getPlayerRoles().get(player).onArcTridi(player, gameState);
                        player.teleport(arrow.getLocation().add(0, 1, 0).setDirection(finalVelocity));
                        gameState.getPlayerRoles().get(player).actualTridiCooldown = gameState.TridiCooldown;
                        Location endLoc = player.getLocation();//Like initLoc but after TP
                        double distance = initLoc.distance(endLoc);
                        double gazToRemove = distance/8;
                        if (distance > 0 ){
                        	gameState.getPlayerRoles().get(player).gazAmount-=gazToRemove;
                        	DecimalFormat df = new DecimalFormat("0.0");
                        	player.sendMessage("§7Vous avez perdu§c "+df.format(gazToRemove)+"%§7 de gaz, il ne vous reste que §c"+df.format(gameState.getPlayerRoles().get(player).gazAmount)+"%");
                        	event.getEntity().removeMetadata("teleportArrow "+gameState.getPlayerRoles().get((Player)event.getEntity().getShooter()).roleID, Main.getInstance());
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> noFall.remove(player), 20*5);
                	}
                } else {
                	if (gameState.getPlayerRoles().get(player).getActualCooldownArc() > 0) {
                		gameState.getPlayerRoles().get(player).sendCooldown(player, gameState.getPlayerRoles().get(player).getActualCooldownArc());
                	}
                	if (gameState.getPlayerRoles().get(player).isTransformedinTitan) {
                		player.sendMessage(gameState.EquipementTridi().getItemMeta().getDisplayName()+"§7 n'a pas supporté votre poid de Titan");
                	}
                }
            }	
    	}
    }
    
 
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
    	if (e.getCause() == DamageCause.FALL && e.getEntity() instanceof Player) {
    		if (noFall.contains((Player)e.getEntity())) {
    			e.setCancelled(true);
    		}
    	}
    }
}
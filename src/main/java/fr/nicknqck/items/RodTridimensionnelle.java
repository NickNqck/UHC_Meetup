package fr.nicknqck.items;

import java.text.DecimalFormat;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.PotionUtils;

public class RodTridimensionnelle implements Listener {
    public static final ItemStack getItem() {
        return new ItemBuilder(Material.FISHING_ROD).setName("§f§lEquipement Tridimensionnel").addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().setLore("§7Utilisation plus réaliste de l'§lEquipement Tridimentionnel").setUnbreakable(true).toItemStack();
    }
    GameState gameState;
    public RodTridimensionnelle(GameState gameState) {
    	this.gameState = gameState;
	}
    
    @EventHandler
    public void onProjectile(ProjectileLaunchEvent event) {
        if (event.getEntity() == null || !(event.getEntity() instanceof FishHook) || event
                .getEntity().getShooter() == null || !(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        FishHook hook = (FishHook)event.getEntity();
        if (hook.isOnGround()) {
        	event.setCancelled(true);
        }
        Player player = (Player) event.getEntity().getShooter();
        if (!gameState.hasRoleNull(player)) {
        	RoleBase role = gameState.getPlayerRoles().get(player);
        	if (role.type.equals(GameState.Roles.KillerBee)) {
        		if (role.isCanTentacule()) {
        			FishHook fishHook = (FishHook) event.getEntity();
        	        Location eyeLocation = player.getEyeLocation().clone();
        	        fishHook.setVelocity(eyeLocation.getDirection().multiply(2.5D));
        	        (new LaunchFishHook(fishHook, player, false)).runTaskTimer(Main.getInstance(), 1L, 1L);
        	        return;
        		} else {
					event.setCancelled(true);
					return;
				}
        	}
        	if (player.getItemInHand().isSimilar(getItem())) {
        		if (role.gazAmount > 0) {
            		if (role.actualTridiCooldown <= 0) {
            			if (!role.isTransformedinTitan) {
            				FishHook fishHook = (FishHook) event.getEntity();
                	        Location eyeLocation = player.getEyeLocation().clone();
                	        fishHook.setVelocity(eyeLocation.getDirection().multiply(2.5D));
                	        (new LaunchFishHook(fishHook, player, true)).runTaskTimer(Main.getInstance(), 1L, 1L);
            			}else {
            				player.sendMessage("§7Cette "+getItem().getItemMeta().getDisplayName()+"§7 est trop petit pour votre corp de titan, vous ne pouvez pas l'utiliser");
            				event.setCancelled(true);
            			}
            		}else {
            			role.sendCooldown(player, role.ArcTridiCooldown());
            			event.setCancelled(true);
            		}
            	} else {
            		player.sendMessage("§7Vous n'avez pas asser de gaz pour utiliser§l l'Equipement Tridimentionnel");
            		event.setCancelled(true);
            	}
        	}
        }
    }
    public class LaunchFishHook extends BukkitRunnable {
        private final Player player;
        private final FishHook fishHook;
        private final boolean bool;
        public LaunchFishHook(FishHook fishHook, Player player, boolean rodTridi) {
            this.fishHook = fishHook;
            this.player = player;
            this.bool = rodTridi;
        }

        public void run() {
            if (this.player == null || !this.player.isOnline() || this.fishHook.isDead()) {
                cancel();
                return;
            }
            double velocityX = Math.abs(this.fishHook.getVelocity().getX());
            double velocityY = Math.abs(this.fishHook.getVelocity().getY());
            double velocityZ = Math.abs(this.fishHook.getVelocity().getZ());
            if (velocityX >= 0.1D || velocityY >= 0.1D || velocityZ >= 0.1D) {
                return;
            }
            cancel();
            Location loc = this.fishHook.getLocation();
            PotionUtils.effetGiveNofall(player);
	        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                PotionUtils.effetRemoveNofall(player);
            }, 20*5);
            (new AttractFishHook(this.fishHook, this.player, loc, player.getLocation(), bool)).runTaskTimer(Main.getInstance(), 0L, 1L);
        }
    }
    public static int generateRandomNumber() {
        Random random = new Random();
        int min = 1;  // La valeur minimale (inclusive)
        int max = 10;  // La valeur maximale (inclusive)

        // Génère un nombre aléatoire entre min et max
        int randomNumber = random.nextInt((max - min) + 1) + min;

        return randomNumber;
    }
    public static double generateRandomDouble() {
    	Random random = new Random();
    	
    	double randomNumber = random.nextDouble();
    	return generateRandomNumber()+randomNumber;
    }
    public class AttractFishHook extends BukkitRunnable {
        private final Player player;
        private final FishHook fishHook;
        private final Location loc;
        private final Location initLoc;
        private final boolean bool;
        public AttractFishHook(FishHook fishHook, Player player, Location location, Location initLoc, boolean rodTridi) {
            this.fishHook = fishHook;
            this.player = player;
            this.loc = location;
            this.initLoc = initLoc;
            this.bool = rodTridi;
        }
        public void run() {
            if (this.player == null || !this.player.isOnline() || this.player.getWorld() != this.fishHook.getWorld()) {
                cancel();
                return;
            }
            this.fishHook.setVelocity(new Vector(0, 0, 0));
            double vectorX = this.loc.getX() - this.player.getLocation().getX();
            double vectorY = this.loc.getY() - this.player.getLocation().getY();
            double vectorZ = this.loc.getZ() - this.player.getLocation().getZ();
            Vector v = (new Vector(vectorX, vectorY, vectorZ)).add(new Vector(0, 3, 0)).multiply(0.02D);
            if (this.player.getLocation().distance(this.fishHook.getLocation()) > 10.0D) {
                v.multiply(0.85D+gameState.getPlayerRoles().get(this.player).RodSpeedMultipliyer);
            }
            this.player.setVelocity(this.player.getVelocity().add(v));
            if (!this.fishHook.isDead() && this.player.getLocation().distance(this.fishHook.getLocation()) >= 3.0D) {
                return;
            }
            Vector current = this.player.getVelocity();
            if (!getItem().isSimilar(this.player.getItemInHand())) {
                current.multiply(0.3D);
                current.setY(0.5D);
            } else {
                current.setY(0.75D);
            }
            this.player.setVelocity(current);
            cancel();
            double r = loc.distance(initLoc)/2;
            if (bool) {
            	if (gameState.getPlayerRoles().get(this.player).gazAmount - r <= 0) {
                	gameState.getPlayerRoles().get(this.player).gazAmount = 0;
                }else{
                	gameState.getPlayerRoles().get(this.player).gazAmount -= r;
                }
                DecimalFormat df = new DecimalFormat("0.0");
                this.player.sendMessage("§7Vous avez perdu§c "+df.format(r)+"%§7 de gaz, il ne vous en reste plus que§c "+df.format(gameState.getPlayerRoles().get(this.player).gazAmount)+"%");  
                gameState.getPlayerRoles().get(this.player).actualTridiCooldown = gameState.TridiCooldown;
                return;
            } else {
				gameState.getPlayerRoles().get(player).onTentaculeEnd(r);
				return;
			}
        }
    }
}
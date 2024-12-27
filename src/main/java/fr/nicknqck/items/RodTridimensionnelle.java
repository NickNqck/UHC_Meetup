package fr.nicknqck.items;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.aot.builders.AotRoles;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.shinobi.KillerBee;
import fr.nicknqck.utils.PotionUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
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

import java.text.DecimalFormat;

public class RodTridimensionnelle implements Listener {
    public static ItemStack getItem() {
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
        if (!gameState.hasRoleNull(player.getUniqueId())) {
        	RoleBase roleBase = gameState.getPlayerRoles().get(player);
        	if (roleBase instanceof KillerBee) {
        		if (((KillerBee) roleBase).isCanTentacule()) {
        			FishHook fishHook = (FishHook) event.getEntity();
        	        Location eyeLocation = player.getEyeLocation().clone();
        	        fishHook.setVelocity(eyeLocation.getDirection().multiply(2.5D));
        	        (new LaunchFishHook(fishHook, player, false)).runTaskTimer(Main.getInstance(), 1L, 1L);
                } else {
					event.setCancelled(true);
                }
                return;
            }
        	if (player.getItemInHand().isSimilar(getItem())) {
                if (!(roleBase instanceof AotRoles))return;
                AotRoles role = (AotRoles) roleBase;
        		if (role.gazAmount > 0) {
            		if (role.getActualTridiCooldown() <= 0) {
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
            			role.sendCooldown(player, role.getActualTridiCooldown());
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
	        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> PotionUtils.effetRemoveNofall(player), 20*5);
            (new AttractFishHook(this.fishHook, this.player, loc, player.getLocation(), bool)).runTaskTimer(Main.getInstance(), 0L, 1L);
        }
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
            if (gameState.hasRoleNull(player.getUniqueId())) {
                cancel();
                return;
            }
            this.fishHook.setVelocity(new Vector(0, 0, 0));
            double vectorX = this.loc.getX() - this.player.getLocation().getX();
            double vectorY = this.loc.getY() - this.player.getLocation().getY();
            double vectorZ = this.loc.getZ() - this.player.getLocation().getZ();
            Vector v = (new Vector(vectorX, vectorY, vectorZ)).add(new Vector(0, 3, 0)).multiply(0.02D);
            if (this.player.getLocation().distance(this.fishHook.getLocation()) > 10.0D) {
                double speedMultiplier = 0;
                if (gameState.getPlayerRoles().get(player) instanceof AotRoles){
                    speedMultiplier = ((AotRoles) gameState.getPlayerRoles().get(player)).RodSpeedMultipliyer;
                } else if (gameState.getPlayerRoles().get(player) instanceof KillerBee) {
                    speedMultiplier = 0;
                }
                v.multiply(0.85D+speedMultiplier);
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
                if (!(gameState.getPlayerRoles().get(player) instanceof AotRoles))return;
                AotRoles role = (AotRoles) gameState.getPlayerRoles().get(player);
            	if (role.gazAmount - r <= 0) {
                	role.gazAmount = 0;
                }else{
                	role.gazAmount -= r;
                }
                DecimalFormat df = new DecimalFormat("0.0");
                this.player.sendMessage("§7Vous avez perdu§c "+df.format(r)+"%§7 de gaz, il ne vous en reste plus que§c "+df.format(role.gazAmount)+"%");
                role.setActualTridiCooldown(gameState.TridiCooldown);
            } else {
                if (gameState.getPlayerRoles().get(player) instanceof KillerBee) {
                    ((KillerBee) gameState.getPlayerRoles().get(player)).onTentaculeEnd(r);
                }
            }
        }
    }
}
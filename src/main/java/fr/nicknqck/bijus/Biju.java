package fr.nicknqck.bijus;

import fr.nicknqck.Border;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Biju {
	public abstract void setupBiju(GameState gameState);	
	public abstract void spawnBiju();
	public abstract LivingEntity getLivingEntity();
	public abstract String getName();
	public abstract void onDeath(LivingEntity entity, List<ItemStack> drops);
	public abstract void getItemInteraction(PlayerInteractEvent event, Player player);
	public abstract ItemStack getItem();
	public abstract ItemStack getItemInMenu();
	public abstract Location getSpawn();
	public abstract int getTimeSpawn();
	public abstract UUID getHote();
	public abstract void setHote(UUID u);
	public abstract void onBijuDamage(EntityDamageEvent event);
	public static boolean isOutsideOfBorder(Location location) {
        WorldBorder border = location.getWorld().getWorldBorder();
        double x = location.getX();
        double z = location.getZ();
        double size = border.getSize() / 2;
        return ((x > size || (-x) > size) || (z > size || (-z) > size));
    }
	public Location moveToOrigin(Location location) {
	    double x = location.getX();
	    double z = location.getZ();
	        if (x < 0.0) {
	        	return new Location(location.getWorld(), x+1, location.getWorld().getHighestBlockYAt(location.getBlockX()+1, location.getBlockZ()), z, location.getYaw(), location.getPitch());
	        }
	        if (x > 0.0) {
	        	return new Location(location.getWorld(), x-1, location.getWorld().getHighestBlockYAt(location.getBlockX()-1, location.getBlockZ()), z, location.getYaw(), location.getPitch());
	        }
	        if (z < 0.0) {
	        	return new Location(location.getWorld(), x, location.getWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockZ()+1), z+1, location.getYaw(), location.getPitch());
	        }else {
	        	return new Location(location.getWorld(), x, location.getWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockZ()-1), z-1, location.getYaw(), location.getPitch());
			}
	}
	public Location getRandomSpawn(){
		Location spawn = new Location(Main.getInstance().getWorldManager().getGameWorld(), 0.0, Main.getInstance().getWorldManager().getGameWorld().getHighestBlockYAt(0, 0), 0.0);
		if (Main.isDebug()){
			System.err.println("Final Spawn:     "+spawn);
		}
		int x = -999999;
		int z = -999999;
		System.out.println("-Border: "+-Border.getMaxBijuSpawn());
		while (x == 0 || x < -948015) {
			int rdm = Main.RANDOM.nextInt(Border.getMaxBijuSpawn()+1);
			if (rdm >= Border.getMaxBijuSpawn()){
				x = -999999;
			} else {
				if (rdm <= Border.getMinBijuSpawn()){
					x = -999999;
				} else {
					x = rdm;
				}
			}
		}
		while (z == 0 || z < -948015){
			int rdm = Main.RANDOM.nextInt(Border.getMaxBijuSpawn()+1);
			if (rdm >= Border.getMaxBijuSpawn()){
				z = -999999;
			} else {
				if (rdm <= Border.getMinBijuSpawn()){
					z = -999999;
				} else {
					z = rdm;
				}
			}
		}
		if (RandomUtils.getOwnRandomProbability(50.0)) x = -x;
		if (RandomUtils.getOwnRandomProbability(50.0)) z = -z;
		spawn.setX(x);
		spawn.setZ(z);
		spawn.setY(spawn.getWorld().getHighestBlockYAt(x, z));
		return spawn;
	}
	public UUID getMaster() {
        UUID toReturn = null;
        for (UUID u : GameState.getInstance().getInGamePlayers()) {
			Player p = Bukkit.getPlayer(u);
			if (p == null)continue;
            if (hasBiju(p) && getBiju(p) == this) {
                toReturn = u;
                break;
            }
        }
        return toReturn;
    }
	public void sendCooldown(Player target,int cooldown) {
		target.sendMessage("Cooldown: "+StringUtils.secondsTowardsBeautiful(cooldown));
	}
	public static boolean hasBiju(Player player) {
        return getBiju(player) != null;
    }
	
	public static boolean hisMaster(Player player) {
		if (!GameState.getInstance().hasRoleNull(player)) {
			if (GameState.getInstance().getPlayerRoles().get(player).getRoles().equals(Roles.KillerBee) || GameState.getInstance().getPlayerRoles().get(player).getRoles().equals(Roles.Naruto)) {
				return true;
			}
		}
		for (Bijus bijus : Bijus.values()) {
			if (bijus.getBiju().getHote() != null) {
				if (bijus.getBiju().getHote().equals(player.getUniqueId())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static Bijus getBijus(Player player) {
        Bijus toReturn = null;
        for (Bijus biju : Bijus.values()) {
            for (ItemStack content : player.getInventory().getContents()) {
                if (content.equals(biju.getBiju().getItem())) {
                    toReturn = biju;
                    break;
                }
            }
        }
        return toReturn;
    }
	public static boolean NobodyHaveBiju(Bijus bijus) {
		boolean tr = false;
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!p.getGameMode().equals(GameMode.SPECTATOR)) {
				if (p.getInventory().contains(bijus.getBiju().getItem())) {
					tr = true;
					System.out.println(p.getName()+" has "+bijus.name());
					break;
				}
			}
		}
		return tr;
	}
	public boolean hasBijus(Player player) {
		List<Bijus> bijus = new ArrayList<>();
		for (Bijus value : Bijus.values()) {
			if (player.getInventory().contains(value.getBiju().getItem())) {
				bijus.add(value);
			}
		}
        return !bijus.isEmpty();
	}
    public static Biju getBiju(Player player) {
        Biju toReturn = null;
        for (Bijus biju : Bijus.values()) {
            for (ItemStack content : player.getInventory().getContents()) {
                if (content != null && content.isSimilar(biju.getBiju().getItem())) {
                    toReturn = biju.getBiju();
                    break;
                }
            }
        }
        return toReturn;
    }
	public abstract void onItemRecup(PlayerPickupItemEvent e, Player player);
	public abstract void onSecond(GameState gameState);
	public abstract void onTap(EntityDamageByEntityEvent event, Player attacker, Player defender);
	public abstract void onAPlayerDie(Player player, GameState gameState, Entity killer);
	public abstract void onBucketEmpty(PlayerBucketEmptyEvent event, Player player);
	public abstract void onProjectileHit(ProjectileHitEvent e, Bijus bijus, Projectile projectile);
	public abstract Bijus getBijus();
	public abstract void resetCooldown();
	public abstract boolean onDrop(PlayerDropItemEvent event, Player player, ItemStack item);
	public abstract void onJubiInvoc(Player invoquer);
}
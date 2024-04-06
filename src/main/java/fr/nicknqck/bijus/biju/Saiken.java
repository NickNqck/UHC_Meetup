package fr.nicknqck.bijus.biju;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.bijus.Biju;
import fr.nicknqck.bijus.Bijus;
import fr.nicknqck.items.Items;
import fr.nicknqck.utils.CC;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.RandomUtils;
import net.minecraft.server.v1_8_R3.EntityLiving;

public class Saiken extends Biju {

    private Slime slime;
    private Location spawn;

    @Override
    public LivingEntity getLivingEntity() {
        return slime;
    }
    private UUID Hote = null;
    @Override
    public UUID getHote() {
    	return Hote;
    }
    @Override
    public void setHote(UUID u) {
    	this.Hote = u;
    }
    private GameState gameState;
    @Override
    public void setupBiju(GameState gameState) {
        World world = Main.getInstance().gameWorld;
        changeSpawn();
        this.gameState = gameState;
        new SaikenRunnable().runTaskTimer(Main.getInstance(), 0L, 20L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			System.out.println("Saiken will be spawn in world: "+world.getName()+" at x: "+spawn.getBlockX()+", y: "+spawn.getBlockY()+", z: "+spawn.getBlockZ());
		}, 20);
    }
    private void changeSpawn() {
		spawn = null;
		spawn = GameListener.generateRandomLocation(GameState.getInstance(), Bukkit.getWorld("world"));
		spawn = new Location(spawn.getWorld(), spawn.getX(), spawn.getWorld().getHighestBlockYAt(spawn), spawn.getZ());
	}
    @Override
    public String getName() {
        return "§5Saiken";
    }

    @Override
    public void spawnBiju() {
        this.slime = (Slime) Main.getInstance().gameWorld.spawnEntity(this.spawn, EntityType.SLIME);
        slime.setCustomName(this.getName());
        slime.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
        slime.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false));
        slime.setMaxHealth(2D * 100D);
        slime.setSize(8);
        slime.setHealth(slime.getMaxHealth());
        slime.setCustomNameVisible(true);
        EntityLiving nmsEntity = ((CraftLivingEntity) slime).getHandle();
        ((CraftLivingEntity) nmsEntity.getBukkitEntity()).setRemoveWhenFarAway(false);
        new BukkitRunnable() {
			
			@Override
			public void run() {
				if (slime == null) {
					cancel();
					return;
				}
				if (!slime.isDead()) {
					if (spawn == null) return;
					if (isOutsideOfBorder(getLivingEntity().getLocation())) {
    					spawn = moveToOrigin(spawn);
    					getLivingEntity().teleport(spawn);
    				}
			        if (slime.getLocation().distance(spawn) >= 30) {
			        	slime.teleport(slime);
			        }
				}
			}
		}.runTaskTimer(Main.getInstance(), 0, 20);
    }

    @EventHandler
    public void onDamageBiju(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Slime && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Slime slime = (Slime) event.getDamager();
            if (slime != null && slime.getUniqueId().equals(this.slime.getUniqueId())) {
                int value = (int) (Math.random() * 100);
                if (value <= 25) {
                    slime.setHealth(Math.min(slime.getHealth() + 2D, slime.getHealth()));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 2, 0));
                    player.getWorld().createExplosion(player.getLocation(), 1.5F);
                    player.sendMessage(CC.prefix(getName() + " &fvous a touché..."));
                }
            }
        }
    }

    @Override
    public Location getSpawn() {
        return spawn;
    }

    @Override
    public ItemStack getItem() {
        return Items.Saiken();
    }

    @Override
    public void getItemInteraction(PlayerInteractEvent event, Player player) {
    	if (getHote() == null) {
    		player.sendMessage("§7Vous n'êtes pas l'hôte de "+getName());
    		return;
    	}
    	if (getHote().equals(player.getUniqueId())) {
    		if (getListener().getSaikenCooldown() > 0) {
    			sendCooldown(player, getListener().getSaikenCooldown());
                return;
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20 * 60, 0, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 5 * 20 * 60, 0, false, false));
            getListener().setSaikenUser(player.getUniqueId());
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            	getListener().setSaikenUser(null);
            }, 20*60*5);
            getListener().setSaikenCooldown(15 * 60);
    	}
    }

    private int TimeSpawn = RandomUtils.getRandomInt(GameState.getInstance().TimeSpawnBiju, 60*5)+60;
    @Override
    public int getTimeSpawn() {
    	return TimeSpawn;
    }
    public class SaikenRunnable extends BukkitRunnable {

        int timer = 0;
        int spawn = getTimeSpawn();

        @Override
        public void run() {
            timer++;
            if (gameState.getServerState() != ServerStates.InGame || !gameState.BijusEnable || !getBijus().isEnable()) {
            	cancel();
            	return;
            }
            if (this.timer == spawn - 30) {
                Bukkit.broadcastMessage(CC.prefix(getName() + " &fva apparaître dans &a30 &fsecondes."));
            }

            if (this.timer == spawn) {
                spawnBiju();
                Bukkit.broadcastMessage(CC.prefix(getName() + " &fvient d'apparaître."));
                cancel();
            }
        }
    }
	@Override
	public void onDeath(LivingEntity entity, List<ItemStack> drops) {
		if (this.slime != null && entity.getUniqueId().equals(this.slime.getUniqueId())) {
			Player k = null;
        	if (entity.getKiller() instanceof Arrow) {
        		Arrow arrow = (Arrow) entity.getKiller();
        		if (arrow.getShooter() instanceof Player) {
        			Player launcher = (Player) arrow.getShooter();
        			k = launcher;
        		}
        	}
        	if (entity.getKiller() instanceof Player) {
				Player killer = entity.getKiller();
				k = killer;
			}
        	if (k != null) {
        		if (!gameState.hasRoleNull(k)) {
        			gameState.getPlayerRoles().get(k).giveItem(k, true, getItem());
        			Bukkit.broadcastMessage("§a" + getName() + " §fa été tué.");
        			if (hisMaster(k)) {
        				k.sendMessage("§7Vous avez récupéré "+getName());
        			} else {
        				k.sendMessage("§7Vous êtes devenue l'hôte de "+getName());
        				Hote = k.getUniqueId();
        			}
        		} else {
					spawnBiju();
					return;
				}
        	} else {
        		slime.setSize(0);
        		drops.clear();
        		spawnBiju();
        		return;
        	}
        	slime.setSize(0);
            this.slime = null;
            drops.clear();
            new BukkitRunnable() {
				int i = 0;
				@Override
				public void run() {
					i++;
					if (GameState.getInstance().getServerState() != ServerStates.InGame) {
						cancel();
						return;
					}
					if (getHote() != null) {
						cancel();
					}
					if (i == 60*5) {
						if (!NobodyHaveBiju(getBijus())) {
							spawnBiju();
		                    Bukkit.broadcastMessage(CC.prefix(getName() + " &fvient de réapparaître."));
		                } else {
		                	cancel();
		                }
					}
				}
			}.runTaskTimer(Main.getInstance(), 0, 20);
			return;
        }
	}

	@Override
	public ItemStack getItemInMenu() {
		return new ItemBuilder(Material.INK_SACK).setDurability(15).setLore(getBijus().isEnable() ? "§aActivé" : "§cDésactivé").setName(getName()).toItemStack();
	}

	@Override
	public void onBijuDamage(EntityDamageEvent event) {}

	@Override
	public void onItemRecup(PlayerPickupItemEvent e, Player player) {
		if (e.getItem().getItemStack().isSimilar(getItem())) {
			if (getHote() != null) {
				player.sendMessage("§7Vous avez récupérer "+getName());
			} else {
				if (!hisMaster(player)) {
					player.sendMessage("§7Vous êtes devenue l'hôte de: "+getName());
					Hote = player.getUniqueId();
					resetCooldown();
				} else {
					player.sendMessage("§7Vous avez récupérer "+getName());
				}
			}
		}
	}

	@Override
	public void onSecond(GameState gameState) {
		
	}

	@Override
	public void onTap(EntityDamageByEntityEvent event, Player attacker, Player defender) {}

	@Override
	public void onAPlayerDie(Player player, GameState gameState, Entity killer) {
		if (getHote() != null) {
			if (player.getUniqueId().equals(getHote())) {
				player.sendMessage("§7Vous n'êtes plus l'hôte de "+getName());
				Hote = null;
				new BukkitRunnable() {
					int i = 0;
					@Override
					public void run() {
						i++;
						if (gameState.getServerState() != ServerStates.InGame) {
							cancel();
						}
						if (getHote() != null) {
							cancel();
						}
						if (!NobodyHaveBiju(getBijus())) {
							cancel();
						}
						if (i == 60*5) {
							spawnBiju();
							Bukkit.broadcastMessage(getName()+"§7 vient de réaparaitre");
						}
					}
				}.runTaskTimer(Main.getInstance(), 0, 20);
			}
		}
	}

	@Override
	public void onBucketEmpty(PlayerBucketEmptyEvent event, Player player) {}

	@Override
	public void onProjectileHit(ProjectileHitEvent e, Bijus bijus, Projectile projectile) {}

	@Override
	public Bijus getBijus() {
		return Bijus.Saiken;
	}

	@Override
	public void resetCooldown() {
	}
	@Override
	public boolean onDrop(PlayerDropItemEvent event, Player player, ItemStack item) {
		if (getHote() != null) {
			if (player.getUniqueId().equals(getHote())) {
				return true;
			}
		}
		player.sendMessage("§7Vous jeté "+getName());
		return false;
	}
	@Override
	public void onJubiInvoc(Player invoquer) {
		// TODO Auto-generated method stub
		
	}
}

package fr.nicknqck.bijus.biju;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.bijus.Biju;
import fr.nicknqck.bijus.BijuListener;
import fr.nicknqck.bijus.Bijus;
import fr.nicknqck.items.Items;
import fr.nicknqck.utils.CC;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import net.minecraft.server.v1_8_R3.EntityLiving;

public class Matatabi extends Biju{

    private Blaze blaze;
    private Location spawn;
    private GameState gameState;
    @Override
    public LivingEntity getLivingEntity() {
        return blaze;
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
    @Override
    public void getItemInteraction(PlayerInteractEvent event, Player player) {
    	if (getHote() == null) {
    		player.sendMessage("§7Vous n'êtes pas l'hôte de "+getName());
    		return;
    	}
    	if (getHote().equals(player.getUniqueId())) {
    		if (BijuListener.getInstance().getMatatabiCooldown() > 0) {
    			sendCooldown(player, getListener().getMatatabiCooldown());
                return;
            }
            player.sendMessage("§7Activation de "+getName());
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 5 * 20 * 60, 0, false, false), true);
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 5 * 20 * 60, 0, false, false), true);
            BijuListener.getInstance().setMatatabiFire(player.getUniqueId());
            BijuListener.getInstance().setMatatabiCooldown(20 * 60);
    	}
    }

    @Override
    public void setupBiju(GameState gameState) {
        World world = Main.getInstance().gameWorld;
        this.gameState = gameState;
        new MatatabiRunnable().runTaskTimer(Main.getInstance(), 0L, 20L);
        changeSpawn();
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			System.out.println("Matatabi will be spawn in world: "+world.getName()+" at x: "+spawn.getBlockX()+", y: "+spawn.getBlockY()+", z: "+spawn.getBlockZ());
		}, 20);
    }
    
    public void changeSpawn() {
		spawn = null;
		spawn = GameListener.generateRandomLocation(GameState.getInstance(), Bukkit.getWorld("world"));
		spawn = new Location(spawn.getWorld(), spawn.getX(), spawn.getWorld().getHighestBlockYAt(spawn), spawn.getZ());
	}
    
    @Override
    public String getName() {
        return "§aMatatabi";
    }

    @Override
    public void spawnBiju() {
        this.blaze = (Blaze) Main.getInstance().gameWorld.spawnEntity(this.spawn, EntityType.BLAZE);
        blaze.setCustomName(this.getName());
        blaze.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false));
        blaze.setMaxHealth(2D * 100D);
        blaze.setHealth(blaze.getMaxHealth());
        blaze.setCustomNameVisible(true);
        EntityLiving nmsEntity = ((CraftLivingEntity) blaze).getHandle();
        ((CraftLivingEntity) nmsEntity.getBukkitEntity()).setRemoveWhenFarAway(false);

        new BukkitRunnable() {
            int timeAttack = 60;

            @Override
            public void run() {
            	if (blaze == null) {
            		cancel();
            		return;
            	}
                if (!blaze.isDead()) {
                	if (spawn == null) return;
                	if (isOutsideOfBorder(getLivingEntity().getLocation())) {
    					spawn = moveToOrigin(spawn);
    					getLivingEntity().teleport(spawn);
    				}
			        if (blaze.getLocation().distance(spawn) > 30) {
			        	blaze.teleport(spawn);
			        }
                    if (timeAttack == 0) {
                        for (Entity entity : blaze.getNearbyEntities(15, 15, 15)) {
                            if (!(entity instanceof Player)) continue;
                            Player player = (Player) entity;
                            player.setFireTicks(20 * 16);
                            player.sendMessage(CC.prefix(getName() + " &fvient de vous mettre en &cfeu&f."));
                        }
                        timeAttack = 10;
                    }
                } else {
                    cancel();
                }
                timeAttack--;

            }
        }.runTaskTimer(Main.getInstance(), 20, 20);

    }

    @Override
    public Location getSpawn() {
        return spawn;
    }
    @Override
    public ItemStack getItem() {
        return Items.Matatabi();
    }
    private final int TimeSpawn = RandomUtils.getRandomInt(GameState.getInstance().getMinTimeSpawnBiju(), GameState.getInstance().getMaxTimeSpawnBiju())+60;
    @Override
    public int getTimeSpawn() {
    	return TimeSpawn;
    }
    public class MatatabiRunnable extends BukkitRunnable {
    	int timer = 0;
        int spawn = getTimeSpawn();
        public MatatabiRunnable() {
         	System.out.println("Spawn Matatabi at "+StringUtils.secondsTowardsBeautiful(spawn));
		}
        @Override
        public void run() {
        	timer++;
        	if (gameState.getServerState() != ServerStates.InGame || !gameState.BijusEnable || !getBijus().isEnable()) {
            	cancel();
            	return;
            }
            if (this.timer == (spawn) - 30) {
                Bukkit.broadcastMessage(getName()+" §fva apparaître dans §a30 §fsecondes.");
            }
            if (this.timer == (spawn)) {
                spawnBiju();
                Bukkit.broadcastMessage(getName()+" §fvient d'apparaître.");
                cancel();
            }
        }
    }
	@Override
	public void onDeath(LivingEntity entity, List<ItemStack> drops) {
		if (blaze != null && entity.getUniqueId() == blaze.getUniqueId()) {
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
        		spawnBiju();
        		return;
        	}
            this.blaze = null;
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
		}
	}

	@Override
	public ItemStack getItemInMenu() {
		return new ItemBuilder(Material.INK_SACK).setDurability((short) 4).setName(getName()).addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().setLore(getBijus().isEnable() ? "§aActivé" : "§cDésactivé").toItemStack();
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
		if (BijuListener.getInstance().getMatatabiCooldown() == 60*15) {
			BijuListener.getInstance().setMatatabiFire(null);
			Bukkit.getPlayer(getMaster()).sendMessage("§7Vous n'êtes plus sous l'effet de "+getName());
		}
		if (getListener().getMatatabiCooldown() == 0 && getHote() != null) {
			Bukkit.getPlayer(getHote()).sendMessage(getName()+"§7 est à nouveau utilisable	");
		}
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
		return Bijus.Matatabi;
	}
	@Override
	public void resetCooldown() {
		getListener().setMatatabiCooldown(0);
		getListener().setMatatabiFire(null);
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
	}
}
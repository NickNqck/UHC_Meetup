package fr.nicknqck.entity.bijus.biju;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.entity.bijus.Biju;
import fr.nicknqck.entity.bijus.BijuListener;
import fr.nicknqck.entity.bijus.Bijus;
import fr.nicknqck.items.Items;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
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

import java.util.List;
import java.util.UUID;

public class SonGoku extends Biju {

    private MagmaCube magma_cube;
    private Location spawn;

    @Override
    public LivingEntity getLivingEntity() {
        return magma_cube;
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
    		if (BijuListener.getInstance().getSonGokuCooldown() > 0) {
                sendCooldown(player, BijuListener.getInstance().getSonGokuCooldown());
                return;
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20 * 60, 0, false, false), true);
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 5 * 20 * 60, 0, false, false), true);
            BijuListener.getInstance().setSonGokuUser(player.getUniqueId());
            BijuListener.getInstance().setSonGokuCooldown(60*20);
    	}
    }
    private GameState gameState;
    @Override
    public void setupBiju(GameState gameState) {
        this.spawn = getRandomSpawn();
        this.gameState = gameState;
        new SonGokuRunnable().runTaskTimer(Main.getInstance(), 0L, 20L);
        World world = spawn.getWorld();
        System.out.println("Son Goku will be spawn in world: "+world.getName()+" at x: "+spawn.getBlockX()+", y: "+spawn.getBlockY()+", z: "+spawn.getBlockZ());
    }
    @Override
    public String getName() {
        return "§cSon Gokû";
    }
    @Override
    public Location getSpawn() {
        return spawn;
    }
    @Override
    public void spawnBiju() {
        this.magma_cube = (MagmaCube) this.spawn.getWorld().spawnEntity(this.spawn, EntityType.MAGMA_CUBE);
        magma_cube.setCustomName(this.getName());
        magma_cube.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
        magma_cube.setMaxHealth(2);
        magma_cube.setHealth(magma_cube.getMaxHealth());
        magma_cube.setCustomNameVisible(true);
        magma_cube.setSize(8);
        EntityLiving nmsEntity = ((CraftLivingEntity) magma_cube).getHandle();
        ((CraftLivingEntity) nmsEntity.getBukkitEntity()).setRemoveWhenFarAway(false);
        new BukkitRunnable() {
			
			@Override
			public void run() {
				if (magma_cube != null) {
					if (spawn == null) return;
					if (isOutsideOfBorder(getLivingEntity().getLocation())) {
    					spawn = moveToOrigin(spawn);
    					getLivingEntity().teleport(spawn);
    				}
			        if (magma_cube.getLocation().distance(spawn) >= 30) {
			        	magma_cube.teleport(spawn);
			        }
				}
			}
		}.runTaskTimer(Main.getInstance(), 0, 20);
    }

    @Override
    public ItemStack getItem() {
        return Items.SonGoku();
    }

    private final int TimeSpawn = RandomUtils.getRandomInt(Main.getInstance().getGameConfig().getNarutoConfig().getMinTimeSpawnBiju(), Main.getInstance().getGameConfig().getNarutoConfig().getMaxTimeSpawnBiju())+60;
    @Override
    public int getTimeSpawn() {
    	return TimeSpawn;
    }
    public class SonGokuRunnable extends BukkitRunnable {

        int timer = 0;
        int spawn = getTimeSpawn();

        public SonGokuRunnable() {
        	System.out.println("Spawn Son Goku at "+StringUtils.secondsTowardsBeautiful(spawn+TimeSpawn));
		}
        
        @Override
        public void run() {
            timer++;
            if (gameState.getServerState() != ServerStates.InGame || !Main.getInstance().getBijuManager().isBijuEnable() || !isEnable()) {
            	cancel();
            	return;
            }
            if (this.timer == (spawn) - 30) {
                Bukkit.broadcastMessage((getName() + " §fva apparaître dans §a30 §fsecondes."));
            }

            if (this.timer == (spawn)) {
                spawnBiju();
                Bukkit.broadcastMessage((getName() + " §fvient d'apparaître."));
                cancel();
            }
        }
    }

	@Override
	public void onDeath(LivingEntity entity, List<ItemStack> drops) {
		if(this.magma_cube != null && entity.getUniqueId().equals(this.magma_cube.getUniqueId())) {
			Player k = null;
        	if (entity.getKiller() instanceof Arrow) {
        		Arrow arrow = (Arrow) entity.getKiller();
        		if (arrow.getShooter() instanceof Player) {
                    k = (Player) arrow.getShooter();
        		}
        	}
        	if (entity.getKiller() != null) {
                k = entity.getKiller();
			}
        	if (k != null) {
        		if (!gameState.hasRoleNull(k.getUniqueId())) {
        			gameState.getGamePlayer().get(k.getUniqueId()).getRole().giveItem(k, true, getItem());
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
        		magma_cube.setSize(0);
        		drops.clear();
        		spawnBiju();
        		return;
        	}
        	magma_cube.setSize(0);
            this.magma_cube = null;
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
		                    Bukkit.broadcastMessage((getName() + " §fvient de réapparaître."));
		                } else {
		                	cancel();
		                }
					}
				}
			}.runTaskTimer(Main.getInstance(), 0, 20);
        }
	}

	@Override
	public void onBijuDamage(EntityDamageEvent event) {
		if (this.magma_cube == null)return;
		if (event.getEntity().getUniqueId().equals(magma_cube.getUniqueId())) {
			event.setDamage(event.getDamage()*10);
		}
	}

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
		if (BijuListener.getInstance().getSonGokuCooldown() <= 60*15) {
			BijuListener.getInstance().setSonGokuUser(null);
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
	public void onBucketEmpty(PlayerBucketEmptyEvent event, Player player) {
		 if(this.magma_cube != null && !this.magma_cube.isDead() && this.magma_cube.getLocation().distance(event.getPlayer().getLocation()) <= 30) {
	       event.setCancelled(true);
	       event.getPlayer().sendMessage(("§cVous ne pouvez pas utiliser votre lave ou votre seau d'eau à côté de Son Gokû."));
	     }		
	}

	@Override
	public void onProjectileHit(ProjectileHitEvent e, Bijus bijus, Projectile projectile) {}

	@Override
	public ItemStack getItemInMenu() {
		return new ItemBuilder(Material.INK_SACK).setDyeColor(DyeColor.ORANGE).setName(getName()).setLore(isEnable() ? "§r§aActivé" : "§r§cDésactivé").addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().toItemStack();
	}

	@Override
	public Bijus getBijus() {
		return Bijus.SonGoku;
	}
	@Override
	public void resetCooldown() {
		BijuListener.getInstance().setSonGokuCooldown(0);
		BijuListener.getInstance().setSonGokuUser(null);
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
package fr.nicknqck.bijus.biju;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.bijus.Biju;
import fr.nicknqck.bijus.BijuListener;
import fr.nicknqck.bijus.Bijus;
import fr.nicknqck.items.Items;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.RandomUtils;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
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
        World world = Main.getInstance().getWorldManager().getGameWorld();
		this.spawn = getRandomSpawn();
        this.gameState = gameState;
        new SaikenRunnable().runTaskTimer(Main.getInstance(), 0L, 20L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> System.out.println("Saiken will be spawn in world: "+world.getName()+" at x: "+spawn.getBlockX()+", y: "+spawn.getBlockY()+", z: "+spawn.getBlockZ()), 20);
    }
    @Override
    public String getName() {
        return "§5Saiken";
    }

    @Override
    public void spawnBiju() {
        this.slime = (Slime) Main.getInstance().getWorldManager().getGameWorld().spawnEntity(this.spawn, EntityType.SLIME);
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
    		if (BijuListener.getInstance().getSaikenCooldown() > 0) {
    			sendCooldown(player, BijuListener.getInstance().getSaikenCooldown());
                return;
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20 * 60, 0, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 5 * 20 * 60, 0, false, false));
            BijuListener.getInstance().setSaikenUser(player.getUniqueId());
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> BijuListener.getInstance().setSaikenUser(null), 20*60*5);
            BijuListener.getInstance().setSaikenCooldown(15 * 60);
    	}
    }

    private final int TimeSpawn = RandomUtils.getRandomInt(GameState.getInstance().getMinTimeSpawnBiju(), GameState.getInstance().getMaxTimeSpawnBiju())+60;
    @Override
    public int getTimeSpawn() {
    	return TimeSpawn;
    }
    public class SaikenRunnable extends BukkitRunnable {

        private int timer = 0;
        private final int spawn = getTimeSpawn();

        @Override
        public void run() {
            timer++;
            if (gameState.getServerState() != ServerStates.InGame || !gameState.BijusEnable || !isEnable()) {
            	cancel();
            	return;
            }
            if (this.timer == spawn - 30) {
                Bukkit.broadcastMessage((getName() + " §fva apparaître dans §a30 §fsecondes."));
            }

            if (this.timer == spawn) {
                spawnBiju();
                Bukkit.broadcastMessage((getName() + " §fvient d'apparaître."));
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
                    k = (Player) arrow.getShooter();
        		}
        	}
        	if (entity.getKiller() != null) {
                k = entity.getKiller();
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
	public ItemStack getItemInMenu() {
		return new ItemBuilder(Material.INK_SACK).setDurability(15).setLore(isEnable() ? "§aActivé" : "§cDésactivé").setName(getName()).toItemStack();
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

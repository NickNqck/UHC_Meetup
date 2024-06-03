package fr.nicknqck.bijus.biju;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.bijus.Biju;
import fr.nicknqck.bijus.BijuListener;
import fr.nicknqck.bijus.Bijus;
import fr.nicknqck.items.Items;
import fr.nicknqck.utils.*;
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

public class Chomei extends Biju {

    private Ghast ghast;
    private Location spawn;

    @Override
    public LivingEntity getLivingEntity() {
        return ghast;
    }
    @Override
    public void setHote(UUID u) {
    	this.Hote = u;
    }
    private GameState gameState;
    @Override
    public void setupBiju(GameState gameState) {
        World world = Main.getInstance().gameWorld;
        this.gameState = gameState;
        spawn = getRandomSpawn();
        new ChomeiRunnable().runTaskTimer(Main.getInstance(), 0L, 20L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> System.out.println("Chomei will be spawn in world: "+world.getName()+" at x: "+spawn.getBlockX()+", y: "+spawn.getBlockY()+", z: "+spawn.getBlockZ()), 20);
    }
    @Override
    public String getName() {
        return "§aChômei";
    }

    @Override
    public void spawnBiju() {
        this.ghast = (Ghast) Main.getInstance().gameWorld.spawnEntity(this.spawn, EntityType.GHAST);
        ghast.setCustomName(this.getName());
        ghast.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
        ghast.setMaxHealth(2D * 100D);
        ghast.setHealth(ghast.getMaxHealth());
        ghast.setCustomNameVisible(true);
        EntityLiving nmsEntity = ((CraftLivingEntity) ghast).getHandle();
        ((CraftLivingEntity) nmsEntity.getBukkitEntity()).setRemoveWhenFarAway(false);
        new BukkitRunnable() {
			int i = 0;
			@Override
			public void run() {
				i++;
				if (ghast == null) {
					cancel();
					return;
				}
				if (ghast.isDead()) {
					cancel();
					return;
				}
				if (i == 15) {
					int e = 0;
					for (Player p : Loc.getNearbyPlayers(ghast, 100)) {
						p.playSound(ghast.getLocation(), Sound.GHAST_MOAN, 1, 10);
						e++;
					}
					i = -e;
				}
				if (spawn == null) return;
				if (isOutsideOfBorder(ghast.getLocation())) {
					spawn = moveToOrigin(spawn);
					ghast.teleport(spawn);
				}
				if (ghast.getLocation().distance(spawn) >= 30) {
					ghast.teleport(spawn);
				}
			}
		}.runTaskTimer(Main.getInstance(), 0, 20);
    }

    private UUID Hote = null;
    @Override
    public void getItemInteraction(PlayerInteractEvent event, Player player) {
        if (Hote == null) {
        	player.sendMessage("§7Vous n'êtes pas l'hôte de "+getName());
        	return;
        }
        if (player.getUniqueId().equals(Hote)) {
        	if(BijuListener.getInstance().getChomeiCooldown() > 0) {
                sendCooldown(player, BijuListener.getInstance().getChomeiCooldown());
                return;
            }
        	player.setAllowFlight(true);
            player.setFlying(true);
            this.chomeiUser = player;
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*5, 1, false, false), true);
            PotionUtils.effetGiveNofall(player);
            BijuListener.getInstance().setChomeiCooldown(20*60);
        }
    }
    private Player chomeiUser;
    
    @Override
	public void onProjectileHit(ProjectileHitEvent e, Bijus bijus, Projectile projectile) {
    	if (bijus != Bijus.Chomei)return;
		if (projectile instanceof Fireball) {
			Fireball f = (Fireball) projectile;
			if (f.getShooter() instanceof Ghast) {
				Ghast ghast = (Ghast) f.getShooter();
				if (this.ghast == null)return;
				if (ghast != this.ghast)return;
				Location location = f.getLocation();
				Creeper creep = (Creeper) location.getWorld().spawnEntity(location, EntityType.CREEPER);
				creep.setPowered(true);
			}
		}
	}
    @Override
   public ItemStack getItem() {
        return Items.Chomei();
    }
    @Override
    public Location getSpawn() {
        return spawn;
    }
    private final int TimeSpawn = RandomUtils.getRandomInt(GameState.getInstance().getMinTimeSpawnBiju(), GameState.getInstance().getMaxTimeSpawnBiju())+60;
    @Override
    public int getTimeSpawn() {
    	return TimeSpawn;
    }
    public class ChomeiRunnable extends BukkitRunnable {

        int timer = 0;
        int spawn = getTimeSpawn();
        
        public ChomeiRunnable() {
        	System.out.println("Spawn Chomei at "+StringUtils.secondsTowardsBeautiful(spawn));
		}

        @Override
        public void run() {
            timer++;
            if (gameState.getServerState() != ServerStates.InGame || !gameState.BijusEnable || !getBijus().isEnable()) {
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
    public UUID getHote() {
    	return Hote;
    }
    @Override
	public void onDeath(LivingEntity entity, List<ItemStack> drops) {
        if (this.ghast != null && entity.getUniqueId().equals(this.ghast.getUniqueId())) {
			Player k = getPlayer(entity);
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
            this.ghast = null;
            drops.clear();
            new BukkitRunnable() {
				int i = 0;
				@Override
				public void run() {
					i++;
					if (GameState.getInstance().getServerState() != ServerStates.InGame) {
						cancel();
						System.out.println("cancelled Chomei");
						return;
					}
					if (getHote() != null) {
						cancel();
						System.out.println("cancelled Chomei");
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

	private static Player getPlayer(LivingEntity entity) {
		Player k = null;
		if (entity.getKiller() instanceof Arrow) {
			Arrow arrow = (Arrow) entity.getKiller();
			if (arrow.getShooter() instanceof Player) {
                k = (Player) arrow.getShooter();
			}
		}
		if (entity.getKiller() instanceof Fireball) {
			Fireball ball = (Fireball) entity.getKiller();
			if (ball.getShooter() instanceof Player) {
				k = ((Player) ball.getShooter());
			}
		}
		if (entity.getKiller() != null) {
            k = entity.getKiller();
		}
		return k;
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
	public void onSecond(GameState gameState) {
		if (BijuListener.getInstance().getChomeiCooldown() == (60*19)+50) {
			if (chomeiUser != null) {
				chomeiUser.sendMessage("§aVous ne pouvez plus volé !");
				chomeiUser.setFlying(false);
				chomeiUser.setAllowFlight(false);
				chomeiUser = null;
			}
		}
		if (getListener().getChomeiCooldown() == 0 && getHote() != null) {
			Bukkit.getPlayer(getHote()).sendMessage(getName()+"§7 est à nouveau utilisable");
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
	public ItemStack getItemInMenu() {
		return new ItemBuilder(Material.INK_SACK).setDurability(10).setName(getName()).setLore(getBijus().isEnable() ? "§aActivé" : "§cDésactivé").addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().toItemStack();
	}

	@Override
	public Bijus getBijus() {
		return Bijus.Chomei;
	}
	@Override
	public void resetCooldown() {
		getListener().setChomeiCooldown(0);
	}
	@Override
	public void onJubiInvoc(Player invoquer) {
		
	}
}
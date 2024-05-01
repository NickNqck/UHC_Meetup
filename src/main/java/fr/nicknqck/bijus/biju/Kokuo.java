package fr.nicknqck.bijus.biju;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
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
import fr.nicknqck.bijus.HorseInvoker;
import fr.nicknqck.items.Items;
import fr.nicknqck.utils.CC;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;

public class Kokuo extends Biju {

    private Horse horse;
    private Location spawn;

    @Override
    public LivingEntity getLivingEntity() {
        return horse;
    }
    private UUID Hote = null;
    private GameState gameState;
    @Override
    public UUID getHote() {
    	return Hote;
    }
    public void changeSpawn() {
		spawn = null;
		spawn = GameListener.generateRandomLocation(GameState.getInstance(), Bukkit.getWorld("world"));
		spawn = new Location(spawn.getWorld(), spawn.getX(), spawn.getWorld().getHighestBlockYAt(spawn), spawn.getZ());
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
    	if (player.getUniqueId().equals(getHote())) {
    		if (BijuListener.getInstance().getKokuoCooldown() > 0) {
                sendCooldown(player, BijuListener.getInstance().getKokuoCooldown());
                return;
            }
            player.sendMessage("§7Vous venez d'activé: "+getName());
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20 * 60, 1, false, false), true);
            BijuListener.getInstance().setKokuoUser(player.getUniqueId());
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
            	BijuListener.getInstance().setKokuoUser(null);
            }, 5*20*60);
            BijuListener.getInstance().setKokuoCooldown(20 * 60);
    	}
    }

    @Override
    public void setupBiju(GameState gameState) {
    	changeSpawn();
    	this.gameState = gameState;
        World world = Main.getInstance().gameWorld;
        new KokuoRunnable().runTaskTimer(Main.getInstance(), 0L, 20L);
        System.out.println("Kokuo will be spawn in world: "+world.getName()+" at x: "+spawn.getBlockX()+", y: "+spawn.getBlockY()+", z: "+spawn.getBlockZ());
    }

    @Override
    public String getName() {
        return "§bKokuô";
    }

    @Override
    public void spawnBiju() {
    	this.horse = HorseInvoker.invokeKokuo(horse, this.spawn, getName());
    	new BukkitRunnable() {
			@Override
			public void run() {
				if (horse != null) {
					if (spawn == null) return;
					if (isOutsideOfBorder(getLivingEntity().getLocation())) {
    					spawn = moveToOrigin(spawn);
    					getLivingEntity().teleport(spawn);
    				}
			        if (horse.getLocation().distance(spawn) >= 30) {
			        	horse.teleport(spawn);
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
        return Items.Kokuo();
    }
    private final int TimeSpawn = RandomUtils.getRandomInt(GameState.getInstance().TimeSpawnBiju, 60*5)+60;
    @Override
    public int getTimeSpawn() {
    	return TimeSpawn;
    }
    public class KokuoRunnable extends BukkitRunnable {

		int timer = 0;
		int spawn = getTimeSpawn();
        public KokuoRunnable() {
        	System.out.println("Spawn Kokuo at "+StringUtils.secondsTowardsBeautiful(spawn+GameState.getInstance().TimeSpawnBiju));
		}
        @Override
        public void run() {
        	timer++;
        	if (gameState.getServerState() != ServerStates.InGame || !gameState.BijusEnable || !getBijus().isEnable()) {
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
		if(this.horse != null && entity.getUniqueId().equals(this.horse.getUniqueId())) {
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
            this.horse = null;
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
		if (BijuListener.getInstance().getKokuoCooldown() == 60*15) {
			for (Player p : GameState.getInstance().getInGamePlayers()) {
				for (Bijus value : Bijus.values()) {
					if (value.getBiju().getName().equals(getName())) {//je vérifie si le nom du bijus trouvé dans le for est celui de Isobu
						if (value.getBiju().getMaster().equals(p.getUniqueId())) {
							p.sendMessage("Vous n'êtes plus sous l'effet de "+getName());
							GameState.getInstance().getPlayerRoles().get(p).setMaxHealth(GameState.getInstance().getPlayerRoles().get(p).getMaxHealth()-4);
						}
					}
				}
			}
		}
		if (getListener().getKokuoCooldown() ==0 && getHote() != null) {
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
	public void onProjectileHit(ProjectileHitEvent e, Bijus bijus, Projectile projectile) {}
	@Override
	public ItemStack getItemInMenu() {
		return new ItemBuilder(Material.INK_SACK).setName(getName()).setDyeColor(DyeColor.GRAY).addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().setLore(getBijus().isEnable() ? "§r§aActivé" : "§r§cDésactivé").toItemStack();
	}
	@Override
	public Bijus getBijus() {
		return Bijus.Kokuo;
	}
	@Override
	public void resetCooldown() {
		getListener().setKokuoCooldown(0);
		getListener().setKokuoUser(null);
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
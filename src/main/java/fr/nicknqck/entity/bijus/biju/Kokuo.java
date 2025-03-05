package fr.nicknqck.entity.bijus.biju;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.entity.bijus.Biju;
import fr.nicknqck.entity.bijus.BijuListener;
import fr.nicknqck.entity.bijus.Bijus;
import fr.nicknqck.entity.bijus.HorseInvoker;
import fr.nicknqck.items.Items;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import org.bukkit.*;
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
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> BijuListener.getInstance().setKokuoUser(null), 5*20*60);
            BijuListener.getInstance().setKokuoCooldown(20 * 60);
    	}
    }

    @Override
    public void setupBiju(GameState gameState) {
    	this.spawn=getRandomSpawn();
    	this.gameState = gameState;
        World world = Main.getInstance().getWorldManager().getGameWorld();
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
    private final int TimeSpawn = RandomUtils.getRandomInt(GameState.getInstance().getMinTimeSpawnBiju(), GameState.getInstance().getMaxTimeSpawnBiju())+60;
    @Override
    public int getTimeSpawn() {
    	return TimeSpawn;
    }
    public class KokuoRunnable extends BukkitRunnable {

		int timer = 0;
		int spawn = getTimeSpawn();
        public KokuoRunnable() {
        	System.out.println("Spawn Kokuo at "+StringUtils.secondsTowardsBeautiful(spawn+getTimeSpawn()));
		}
        @Override
        public void run() {
        	timer++;
        	if (gameState.getServerState() != ServerStates.InGame || !Main.getInstance().getGameConfig().isBijusEnable() || !isEnable()) {
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
			for (UUID u : GameState.getInstance().getInGamePlayers()) {
				Player p = Bukkit.getPlayer(u);
				if (p == null)continue;
				for (Bijus value : Bijus.values()) {
					if (value.getBiju().getName().equals(getName())) {//je vérifie si le nom du bijus trouvé dans le for est celui de Isobu
						if (value.getBiju().getMaster().equals(p.getUniqueId())) {
							p.sendMessage("Vous n'êtes plus sous l'effet de "+getName());
							GameState.getInstance().getGamePlayer().get(p.getUniqueId()).getRole().setMaxHealth(GameState.getInstance().getGamePlayer().get(p.getUniqueId()).getRole().getMaxHealth()-4);
						}
					}
				}
			}
		}
		if (BijuListener.getInstance().getKokuoCooldown() ==0 && getHote() != null) {
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
		return new ItemBuilder(Material.INK_SACK).setName(getName()).setDyeColor(DyeColor.GRAY).addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().setLore(isEnable() ? "§r§aActivé" : "§r§cDésactivé").toItemStack();
	}
	@Override
	public Bijus getBijus() {
		return Bijus.Kokuo;
	}
	@Override
	public void resetCooldown() {
		BijuListener.getInstance().setKokuoCooldown(0);
		BijuListener.getInstance().setKokuoUser(null);
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
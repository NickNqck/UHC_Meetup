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
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
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
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import net.minecraft.server.v1_8_R3.EntityLiving;

public class Isobu extends Biju{

	private Guardian guardian;
	private Location spawn;
	private GameState gameState;
	@Override
	public void setupBiju(GameState gameState) {
		World world = Main.getInstance().getWorldManager().getGameWorld();
		this.gameState = gameState;
		new IsobuRunnable().runTaskTimer(Main.getInstance(), 0L, 20L);
		this.spawn = getRandomSpawn();
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> System.out.println("Isobu will be spawn in world: "+world.getName()+" at x: "+spawn.getBlockX()+", y: "+spawn.getBlockY()+", z: "+spawn.getBlockZ()), 20);
	}
	@Override
    public void setHote(UUID u) {
    	this.Hote = u;
    }
	@Override
	public LivingEntity getLivingEntity() {
		return this.guardian;
	}
	@Override
	public Location getSpawn() {
		return this.spawn;
	}
	@Override
	public String getName() {
		return "§eIsobu";
	}
	@Override
	public void spawnBiju() {
		this.guardian = (Guardian) Bukkit.getWorld(spawn.getWorld().getName()).spawnEntity(spawn, EntityType.GUARDIAN);
		guardian.setCustomName("§eIsobu");
		guardian.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false), true);
		guardian.setMaxHealth(2D * 100D);
        guardian.setElder(true);
        guardian.setHealth(guardian.getMaxHealth());
        guardian.setCustomNameVisible(true);
        EntityLiving nmsEntity = ((CraftLivingEntity) guardian).getHandle();
        ((CraftLivingEntity) nmsEntity.getBukkitEntity()).setRemoveWhenFarAway(false);
        GameListener.SendToEveryone(getName()+"§7 est apparue !");
        new BukkitRunnable() {
        	int i = 0;
            @Override
            public void run() {
            	if (guardian == null)return;
            	i++;
                if (!guardian.isDead()) {
                    if (i == 10) {
                        Guardian little_guardian = (Guardian) guardian.getWorld().spawnEntity(guardian.getLocation(), EntityType.GUARDIAN);
                        little_guardian.setElder(false);
                        little_guardian.setMaxHealth(10);
                        little_guardian.setHealth(little_guardian.getMaxHealth());
                        little_guardian.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));

                    }
                    if (guardian != null) {
                        if (isOutsideOfBorder(getLivingEntity().getLocation())) {
        					spawn = moveToOrigin(spawn);
        					getLivingEntity().teleport(spawn);
        				}
    			        if (guardian.getLocation().distance(spawn) >= 30) {
    			        	guardian.teleport(spawn);
    			        }
    				}
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
	}
	@Override
	public void onBijuDamage(EntityDamageEvent event) {
		if (guardian != null && !guardian.isDead() && event.getEntity().getUniqueId().equals(guardian.getUniqueId())) {
            int random = (int) (Math.random() * 25);
            if (random <= 0) {
                event.setCancelled(true);
            }
        }
	}
	@Override
	public void onDeath(LivingEntity entity, List<ItemStack> drops) {
		if (this.guardian != null && entity.getUniqueId().equals(this.guardian.getUniqueId())) {
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
        			for (Player p : Bukkit.getOnlinePlayers()) {
        				if (p.hasPotionEffect(PotionEffectType.SLOW_DIGGING)) {
        					p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
        				}
        			}
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
            this.guardian = null;
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
						if (!NobodyHaveBiju(getBijus()) && guardian == null) {
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
	public ItemStack getItem() {
		return Items.Isobu();
	}
	public class IsobuRunnable extends BukkitRunnable {
        int timer = 0;
        int spawn = getTimeSpawn();
        public IsobuRunnable() {
        	System.out.println("Spawn Isobu at "+StringUtils.secondsTowardsBeautiful(spawn));
		}
        @Override
        public void run() {
        	timer++;
        	if (gameState.getServerState() != ServerStates.InGame || !Main.getInstance().getGameConfig().isBijusEnable() || !isEnable()) {
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
	public void getItemInteraction(PlayerInteractEvent event, Player player) {
		if (getHote() == null) {
			player.sendMessage("§7Vous n'êtes pas l'hôte de "+getName());
			return;
		}
		if (getHote().equals(player.getUniqueId())) {
			if (BijuListener.getInstance().getIsobuCooldown() > 0) {
	        	sendCooldown(player, BijuListener.getInstance().getIsobuCooldown());
	            return;
	        }
	        GameState.getInstance().getPlayerRoles().get(player).giveHealedHeartatInt(player, 2);
	        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5 * 20 * 60, 0, false, false), true);
	        player.sendMessage("Vous venez d'activé "+getName());
	        BijuListener.getInstance().setIsobuDamage(player.getUniqueId());
	        BijuListener.getInstance().setIsobuCooldown(20 * 60);
		}
    }
	@Override
	public void onSecond(GameState gameState) {
		if (BijuListener.getInstance().getIsobuCooldown() == 60*15) {
			for (UUID u : GameState.getInstance().getInGamePlayers()) {
				Player p = Bukkit.getPlayer(u);
				if (p == null)continue;
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
		if (BijuListener.getInstance().getIsobuCooldown() == 0 && getHote() != null) {
			Bukkit.getPlayer(getHote()).sendMessage(getName()+"§7 est à nouveau utilisable !");
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
		return new ItemBuilder(Material.INK_SACK).setName(getName()).setLore(isEnable() ? "§aActivé" : "§cDésactivé").addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().toItemStack();
	}
	@Override
	public Bijus getBijus() {
		return Bijus.Isobu;
	}
	@Override
	public void resetCooldown() {
		BijuListener.getInstance().setIsobuCooldown(0);
		BijuListener.getInstance().setIsobuDamage(null);
	}
	private final int TimeSpawn = RandomUtils.getRandomInt(GameState.getInstance().getMinTimeSpawnBiju(), GameState.getInstance().getMaxTimeSpawnBiju())+60;
	@Override
	public int getTimeSpawn() {
		return TimeSpawn;
	}
	private UUID Hote = null;
	@Override
	public UUID getHote() {
		return Hote;
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
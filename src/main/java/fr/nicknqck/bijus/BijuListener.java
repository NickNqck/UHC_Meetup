package fr.nicknqck.bijus;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
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
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.utils.CC;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.particles.MathUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;

public class BijuListener implements Listener{
	@Getter
	@Setter
	private int isobuCooldown = 0;
	@Setter
	@Getter
	private UUID isobuDamage = null;
	
	@Getter
	@Setter
	private int kokuoCooldown = 0;
    @Getter
	@Setter
	private UUID kokuoUser = null;
    
    @Setter
	@Getter
	private int sonGokuCooldown = 0;
    @Setter
	@Getter
	private UUID sonGokuUser = null;
    
    @Getter
	@Setter
	private int matatabiCooldown = 0;
    @Setter
	@Getter
	private UUID matatabiFire = null;
    
    @Setter
	@Getter
	private int chomeiCooldown = 0;
    
    @Getter
	@Setter
	private int saikenCooldown = 0;
    @Setter
	@Getter
	private UUID saikenUser = null;
	
	public void runnableTask(GameState gameState) {
        if (isobuCooldown >= 0) isobuCooldown--;
        if (kokuoCooldown >= 0)kokuoCooldown--;
        if (sonGokuCooldown >= 0) sonGokuCooldown--;
        if (chomeiCooldown >= 0)chomeiCooldown--;
        if (matatabiCooldown >= 0) matatabiCooldown--;
        if (saikenCooldown >= 0)saikenCooldown--;
        if (JubiCooldown >= 0)JubiCooldown--;
        for (Bijus b : Bijus.values()) {
        	b.getBiju().onSecond(gameState);
        }
    }
	@Getter
	private static BijuListener instance;
	public BijuListener() {
		instance = this;
		resetCooldown();
	}
	public void resetCooldown() {
		isobuCooldown = 0;
		kokuoCooldown = 0;
		kokuoUser = null;
		isobuDamage = null;
		sonGokuUser = null;
		sonGokuCooldown = 0;
		chomeiCooldown = 0;
		matatabiCooldown = 0;
		matatabiFire = null;
		setSaikenUser(null);
		setSaikenCooldown(0);

		setChomeiCooldown(0);
		
		setIsobuCooldown(0);
		setIsobuDamage(null);
		
		setKokuoCooldown(0);
		setKokuoUser(null);
		
		setMatatabiCooldown(0);
		setMatatabiFire(null);
		
		setSonGokuCooldown(0);
		setSonGokuUser(null);
		JubiCooldown = 0;
	}
	private int JubiCooldown = 0;
	public ItemStack JubiItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§dJûbi").setLore("§7Vous permet d'invoquer la§c puissance§7 du§d Jûbi").toItemStack();
	}

	@EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.isCancelled()) {
        	if (event.getItem() != null) {
        		if (event.getItem().isSimilar(JubiItem())) {
        			event.setCancelled(true);
        			if (JubiCooldown > 0) {
        				event.getPlayer().sendMessage("§7Il vous reste§c "+StringUtils.secondsTowardsBeautiful(JubiCooldown)+"§7 de cooldown sur§d Jûbi");
        				return;
        			}
        			if (GameState.getInstance().getJubiCrafter() == null) {
        				event.getPlayer().sendMessage("§7Vous n'êtes pas§c l'hôte§7 de§d Jûbi");
        				return;
        			}
        			if (GameState.getInstance().getJubiCrafter().getUniqueId() != event.getPlayer().getUniqueId()) {
        				event.getPlayer().sendMessage("§7Vous n'êtes pas§c l'hôte§7 de§d Jûbi");
        				return;
        			}
        			if (!GameState.getInstance().hasRoleNull(event.getPlayer())) {
        				RoleBase role = GameState.getInstance().getPlayerRoles().get(event.getPlayer());
        				role.givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 20*300, 1, true);
        				role.givePotionEffet(PotionEffectType.SPEED, 20*300, 2, true);
        				role.givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 20*300, 1, true);
        				role.givePotionEffet(PotionEffectType.JUMP, 20*300, 4, true);
        				role.givePotionEffet(PotionEffectType.FIRE_RESISTANCE, 20*300, 1, true);
        				role.givePotionEffet(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 1, false);
        				role.giveHealedHeartatInt(5.0);
        				setIsobuDamage(role.owner.getUniqueId());
        				setKokuoUser(role.owner.getUniqueId());
        				setMatatabiFire(role.owner.getUniqueId());
        				setSaikenUser(role.owner.getUniqueId());
        				setSonGokuUser(role.owner.getUniqueId());
        				JubiCooldown = 1200;
        				GameListener.SendToEveryone("");
        				GameListener.SendToEveryone("§c§lLe récéptacle de§d§l Jûbi§c§l invoque sa puissance !");
        				GameListener.SendToEveryone("");
        				new BukkitRunnable() {
							int i = 300;
							@Override
							public void run() {
								i--;
								if (i == 0 || GameState.getInstance().getServerState() != ServerStates.InGame || !GameState.getInstance().getInGamePlayers().contains(event.getPlayer())) {
									if (GameState.getInstance().getJubiCrafter() != null) {
										GameState.getInstance().getJubiCrafter().sendMessage("§7Vous n'êtes plus sous l'effet du§d Jûbi");
										GameState.getInstance().getJubiCrafter().removePotionEffect(PotionEffectType.REGENERATION);
									}
									cancel();
								}
							}
						}.runTaskTimer(Main.getInstance(), 0, 20);
                    }
        		} else {
        			Bijus bijus = null;
                	for (Bijus value : Bijus.values()) {
                        if (value.getBiju().getItem().equals(event.getItem())) {
                            bijus = value;
                            break;
                        }
                    }
                    if (bijus == null) return;
                    bijus.getBiju().getItemInteraction(event, event.getPlayer());
				}
        	}
        }
    }
	@EventHandler
	public void onHit(ProjectileHitEvent e) {
		for (Bijus value : Bijus.values()){
			value.getBiju().onProjectileHit(e, value, e.getEntity());
		}
	}
	@EventHandler
	public void onRecupItem(PlayerPickupItemEvent e) {
		Bijus bijus = null;
		for (Bijus value : Bijus.values()) {
            if (value.getBiju().getItem().equals(e.getItem().getItemStack())) {
                bijus = value;
                break;
            }
        }
        if (bijus == null) return;
        bijus.getBiju().onItemRecup(e, e.getPlayer());
	}
	@EventHandler
	public void onDeath(EntityDeathEvent e) {
		for (Player p : GameState.getInstance().getInGamePlayers()) {
			if (!GameState.getInstance().hasRoleNull(p)) {
				if (GameState.getInstance().getPlayerRoles().get(p).onEntityDeath(e, e.getEntity())) {
					return;
				}
			}
		}
		Bijus bijus = null;
		for (Bijus value : Bijus.values()) {
			if (value.getBiju().getLivingEntity() != null) {
				if (e.getEntity().getUniqueId().equals(value.getBiju().getLivingEntity().getUniqueId())) {
					bijus = value;
				}
			}
		}
		if (bijus == null)return;
		bijus.getBiju().onDeath(e.getEntity(), e.getDrops());
	}
	@EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Bijus bijus = null;
        for (Bijus value : Bijus.values()) {
            if (value.getBiju().getItem().isSimilar(event.getItemDrop().getItemStack())) {
                bijus = value;
                break;
            }
        }
        if (bijus != null) {
        	if (bijus.getBiju().onDrop(event, event.getPlayer(), event.getItemDrop().getItemStack())) {
        		event.getPlayer().sendMessage("§7Vous ne pouvez pas jeté un Biju...");
            	event.setCancelled(true);
        	}
        }
    }
	@EventHandler
    public void onDamage(EntityDamageEvent event) {
		for (Bijus value : Bijus.values()) {
			if (event.getEntity().getName().equals(value.getBiju().getName())) {
				value.getBiju().onBijuDamage(event);
			}
		}
        if (getIsobuDamage() != null) {
        	if (event.getEntity().getUniqueId().equals(getIsobuDamage())) {
            	
                if (RandomUtils.getOwnRandomProbability(20)) {
                    event.setCancelled(true);
                    event.getEntity().sendMessage("§7Vous avez eu de la chance, ce coup a été §cannulé§f.");
                }
            }
        }
        if (getSonGokuUser() != null) {
        	if (event.getEntity().getUniqueId().equals(getSonGokuUser())) {
            	if (event.getCause().equals(DamageCause.FALL)) {
            		for (Player p : Loc.getNearbyPlayersExcept(event.getEntity(), 30)) {
            			p.damage(Bukkit.getPlayer(getSonGokuUser()).getFallDistance(), Bukkit.getPlayer(getSonGokuUser()));
            		}
            		event.setCancelled(true);
            	}
            }
        }
    }
	 @EventHandler
	    public void onDamageOnEntity(EntityDamageByEntityEvent event) {
	        if(!(event.getEntity() instanceof Player)) return;
	        if(!(event.getDamager() instanceof Player)) return;
	        for (Bijus value : Bijus.values()) {
	        	value.getBiju().onTap(event, (Player)event.getDamager(), (Player)event.getEntity());
	        }
	        if (getMatatabiFire() != null) {
	        	if (event.getDamager().getUniqueId().equals(getMatatabiFire())) {
		                event.getEntity().setFireTicks(60);
		                event.getEntity().sendMessage(CC.prefix("&cVous avez été touché par Matatabi"));
		                event.getDamager().sendMessage(CC.prefix("&fVous avez enflammé &c" + event.getEntity().getName()));
		        }
	        }
	        
	        if (getSonGokuUser() != null) {
	        	if (event.getDamager().getUniqueId().equals(getSonGokuUser())) {
		                if(RandomUtils.getOwnRandomProbability(15)) {
		                    event.getEntity().sendMessage(("§cVous avez été touché par§l Son Gokû"));
		                    event.getDamager().sendMessage(("Vous avez enflammé §c" + event.getEntity().getName()));
		                    event.getEntity().setFireTicks(60);
		                }
		        }
	        }
	        if (getKokuoUser() != null) {
	        	if (event.getDamager().getUniqueId().equals(getKokuoUser())) {
		            if (RandomUtils.getOwnRandomProbability(10)) {
		                ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5*20, 0, false, false), true);
		                ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5*20, 0, false, false), true);
		                event.getEntity().sendMessage(("§cVous avez été touché par Kokuo"));
		                event.getDamager().sendMessage("Vous avez infligé Blindness à "+event.getEntity().getName());
		            }
		        }
	        }
	        
	        if (getSaikenUser() != null) {
	        	if (event.getDamager().getUniqueId().equals(getSaikenUser())) {
		            if(RandomUtils.getOwnRandomProbability(15)) {
		                ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5*20, 0, false, false), true);
		                event.getEntity().setVelocity(event.getEntity().getLocation().getDirection().multiply(1.6));
		                MathUtil.sendParticle(EnumParticle.EXPLOSION_HUGE, event.getEntity().getLocation());
		                event.getEntity().sendMessage(CC.prefix("&cVous avez été touché par Saiken"));
		            }
		        }
	        }
	        
	    }
	 @EventHandler(priority = EventPriority.HIGHEST)
	 public void onEmpty(PlayerBucketEmptyEvent event) {
		 if(this.sonGokuUser == null) return;
	     if(Bukkit.getPlayer(this.sonGokuUser) == null) return;
	     Player player = Bukkit.getPlayer(this.sonGokuUser);
	     if(player.getLocation().distance(event.getBlockClicked().getLocation()) <= 15 && event.getItemStack().getType() == Material.LAVA_BUCKET) {
	    	 event.setCancelled(true);
	         player.sendMessage(("§cVous ne pouvez pas poser des seaux de lave à côté de l'utilisateur de§l Son Gokû."));
	     }
	     for (Bijus value : Bijus.values()) {
	    	 value.getBiju().onBucketEmpty(event, player);
	     }
	 }

}
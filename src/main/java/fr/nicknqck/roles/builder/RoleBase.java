package fr.nicknqck.roles.builder;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.bijus.Bijus;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.titans.Titans;
import fr.nicknqck.roles.ds.demons.lune.Nakime;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.packets.NMSPacket;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import fr.nicknqck.utils.raytrace.BoundingBox;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.*;

public abstract class RoleBase implements IRole {

	public Player owner;
	@Getter
	private Double maxHealth = 20.0;
	@Setter
	@Getter
	private boolean canRespawn = false;
	@Getter
	private boolean hasNoFall = false;
	@Setter
	@Getter
	private Roles oldRole = null;
	@Getter
	private boolean powerEnabled = true;
	@Getter
	@Setter
	private boolean invincible = false;
	@Setter
	@Getter
	private double resi = 0;
	private double Bonusforce = 0;
	private double Bonusresi = 0;
	@Getter
	@NonNull
	public GameState gameState = GameState.getInstance();
	@Getter
	@Setter
	private GamePlayer gamePlayer;
	@Getter
	private TeamList team;
	@Getter
	private final Map<PotionEffect, EffectWhen> effects = new HashMap<>();
	@Getter
	@Setter
	private String suffixString = "";
	@Getter
	private final List<Class<? extends RoleBase>> knowedRoles = new ArrayList<>();
	@Getter
	private final List<String> messageOnDescription = new ArrayList<>();
	@Getter
	private final List<Power> powers = new ArrayList<>();

	public abstract String[] Desc();

	public int roleID = 0;
	public String StringID = "";
	private UUID uuidOwner;
	public RoleBase(UUID player) {
		if (this.gameState == null){
			this.gameState = GameState.getInstance();
		}
		Player owner = Bukkit.getPlayer(player);
		if (owner != null) {
			this.owner = owner;
			owner.setWalkSpeed(0.2f);
			owner.resetPlayerTime();
			owner.resetMaxHealth();
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                owner.sendMessage(ChatColor.BOLD + "Camp: " + this.getTeam().getColor() + StringUtils.replaceUnderscoreWithSpace(this.getTeam().name()));
                System.out.println(owner.getName() +" Team: "+ this.getTeam());
                if (this.getRoles() != null) {
					System.out.println(owner.getName() +" Role: "+ getRoles().name());
				}
			}, 20);
			this.uuidOwner = owner.getUniqueId();
			owner.sendMessage("");
			owner.setAllowFlight(false);
			owner.setFlying(false);
			owner.setGameMode(GameMode.SURVIVAL);
			roleID = RandomUtils.getRandomDeviationValue(1, -500000, 500000);
			System.out.println(owner.getName()+", RoleID: "+roleID);
			StringID = RandomUtils.generateRandomString(24);
			System.out.println(owner.getName()+", StringID: "+StringID);
			Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> gameState.sendDescription(owner), 15);
			new BukkitRunnable() {

				@Override
				public void run() {
					if (gameState.getServerState() != ServerStates.InGame) {
						cancel();
						return;
					}
					Player owner = Bukkit.getPlayer(getPlayer());
					if (owner == null) return;
					if (owner.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE) && getResi() < 20) {
						setResi(20);
					}
				}
			}.runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
			setTeam(getOriginTeam());
		}
	}
	@Override
	public UUID getPlayer() {
		return uuidOwner;
	}
	public void addPower(Power power) {
	/*	TKGPowerAddToPlayerEvent event = new TKGPowerAddToPlayerEvent(this.plugin.getUhcapi(), power, this.getTKGPlayer());
		this.plugin.getServer().getPluginManager().callEvent(event);*/
		this.powers.add(power);
	}
	public void addPower(ItemPower itemPower, boolean give) {
		this.addPower(itemPower);
		Player player = Bukkit.getPlayer(getPlayer());
		if (give && player != null) {
			giveItem(player, false, itemPower.getItem());
		}
	}
	public void sendActionBarCooldown(Player player, int cooldown) {
		if (cooldown > 0) {
			NMSPacket.sendActionBar(player, "Cooldown: "+StringUtils.secondsTowardsBeautiful(cooldown));
		}else {
			NMSPacket.sendActionBar(player, getItemNameInHand(player)+" Utilisable");
		}
	}
	public void sendCustomActionBar(Player player, String msg) {NMSPacket.sendActionBar(player, msg);}
	public void sendMessageAfterXseconde(Player player, String message, int seconde) {
		Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> player.sendMessage(message), 20L *seconde);
	}
	public String getTeamColor() {
		return getTeam().getColor();
	}
	public void givePotionEffet(Player player, PotionEffectType type, int time, int level, boolean force) {
		Bukkit.getScheduler().runTask(Main.getInstance(), () -> player.addPotionEffect(new PotionEffect(type, time, level-1, false, false), force));
	}
	public void givePotionEffet(PotionEffectType type, int time, int level, boolean force) {givePotionEffet(owner, type, time, level, force);}
	public String getItemNameInHand(Player player) {return player.getItemInHand().getItemMeta().getDisplayName()+"§r";}
	public void sendCooldown(Player player, int cooldown) {player.sendMessage("Cooldown: "+StringUtils.secondsTowardsBeautiful(cooldown));}
	public void setTeam(TeamList team) {
		if (this.team != null) {
			this.team.getList().remove(this.owner);
		}
        this.team = team;
		this.team.addPlayer(this.owner);	
	}
	public double getBonusForce() {return Bonusforce;}
	public void setBonusForce(double Bonusforce) {this.Bonusforce = Bonusforce;}
	public void addBonusforce(double Bonusforce) {
		setBonusForce(getBonusForce() + Bonusforce);
	}
	double allresi = getResi()+getBonusForce();
	public double getAllResi() {return allresi;}

	public void addresi(double resi) {setResi(getResi() + resi);}
	public double getBonusResi() {return Bonusresi;}
	public void setBonusResi(double Bonusresi) {this.Bonusresi = Bonusresi;}
	public void addBonusResi(double Bonusresi) {setBonusResi(getBonusResi() + Bonusresi);}
	public void GiveItems() {}
	public void RoleGiven(GameState gameState) {}
	public void Update(GameState gameState) {    //Update every 1s (20ticks)
		allresi = getResi() + getBonusResi();
		if (owner != null) {
			owner.setMaxHealth(getMaxHealth());
			if (!owner.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
				setResi(0.0);
			}
		if (!Objects.equals(owner.getWorld().getGameRuleValue("doMobSpawning"), "false"))
			owner.getWorld().setGameRuleValue("doMobSpawning", "false");
		if (!Objects.equals(owner.getWorld().getGameRuleValue("doFireTick"), "false")){
			owner.getWorld().setGameRuleValue("doFireTick", "false");
		}
		if (owner.getWorld().hasStorm()) owner.getWorld().setStorm(false);
		if (owner.getWorld().isThundering()) owner.getWorld().setThundering(false);
		}
	}
	public boolean ItemUse(ItemStack item, GameState gameState) {return false;}
	public void ItemUseAgainst(ItemStack item, Player victim, GameState gameState) {}
	public void OpenFormInventory(GameState gameState) {}
	public void FormChoosen(ItemStack item, GameState gameState) {}
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {OnAPlayerDie(victim, gameState, killer);}
	public void setNoFall(boolean hasNoFall) {this.hasNoFall = hasNoFall;}

	public void setMaxHealth(Double maxHealth) {this.maxHealth = maxHealth; owner.setMaxHealth(maxHealth);}

	public void setPower(boolean powerEnabled) {this.powerEnabled = powerEnabled;}

	public void neoAttackedByPlayer(Player attacker, GameState gameState) {}

	public void onDay(GameState gameState) {}
	public void onNight(GameState gameState) {}
	public void addSpeedAtInt(Player player, float speedpercent) {player.setWalkSpeed(player.getWalkSpeed()+(speedpercent/500));}

	public void neoItemUseAgainst(ItemStack itemInHand, Player player, GameState gameState, Player damager) {
		ItemUseAgainst(itemInHand, player, gameState);
	}
	public void onEat(ItemStack item, GameState gameState) {}
	public boolean onPickupItem(Item item) {
		return false;}
	public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
		for (Bijus value : Bijus.values()) {
			value.getBiju().onAPlayerDie(player, gameState, killer);
		}
		for (Titans value : Titans.values()) {
			value.getTitan().onAPlayerDie(player, killer);
		}
		if (!player.getWorld().equals(Main.getInstance().getWorldManager().getGameWorld())) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> GameListener.RandomTp(player, Main.getInstance().getWorldManager().getGameWorld()), 20);
		}
		if (!gameState.hasRoleNull(player)){
			if (gameState.getGamePlayer().get(player.getUniqueId()).getRole() instanceof Nakime) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (p.getWorld().equals(Bukkit.getWorld("nakime"))) {
						GameListener.RandomTp(p, Main.getInstance().getWorldManager().getGameWorld());
						p.sendMessage("§7Vous avez été éjecté de la§c cage de Nakime§7 du à la mort de cette dernière");
						player.sendMessage(p.getName()+"§7 est sortie de votre cage");
					}
				}
			}
			gameState.DeadRole.add(gameState.getGamePlayer().get(player.getUniqueId()).getRole().getRoles());
		}
	}
	public void OnAPlayerKillAnotherPlayer(Player player, Player damager, GameState gameState) {}
	public void giveHeartatInt(Player target, double coeur) {
		if (!gameState.hasRoleNull(target)) {
			gameState.getGamePlayer().get(target.getUniqueId()).getRole().setMaxHealth(gameState.getGamePlayer().get(target.getUniqueId()).getRole().getMaxHealth()+coeur*2);
		}
	}
	public final void giveHealedHeartatInt(final Player target,final double coeur) {
		if (!gameState.hasRoleNull(target)) {
			GamePlayer GP = gameState.getGamePlayer().get(target.getUniqueId());
			GP.getRole().setMaxHealth(GP.getRole().getMaxHealth()+coeur*2);
			target.setMaxHealth(GP.getRole().getMaxHealth());
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
				if (gameState.hasRoleNull(target))return;
				if (target.getHealth() <= (GP.getRole().getMaxHealth())-(coeur*2)) {
					target.setHealth(target.getHealth()+(coeur*2));
				}else {
					if (gameState.hasRoleNull(target))return;
					target.setHealth(GP.getRole().getMaxHealth());
				}
			}, 20);
		}
	}
	public final void giveHealedHeartatInt(final double coeur) {giveHealedHeartatInt(owner, coeur);}
	public void giveHalfHeartatInt(Player target, double demicoeur){
		if (!gameState.hasRoleNull(target)) {
			GamePlayer GP = gameState.getGamePlayer().get(target.getUniqueId());
			GP.getRole().setMaxHealth(GP.getRole().getMaxHealth()+demicoeur);
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
				if (target.getHealth() <= (GP.getRole().getMaxHealth())-(demicoeur)) {
					target.setHealth(target.getHealth()+(demicoeur));
				}else {
					target.setHealth(GP.getRole().getMaxHealth());
				}
			}, 20);
		}
	}
	public void Heal(Player target, double demicoeur) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			if (target.getHealth() - demicoeur <= 0 && demicoeur <0) {
				Main.getInstance().getDeathManager().KillHandler(target, target);
			}
			if (target.getHealth() <= (target.getMaxHealth()-demicoeur)) {
				target.setHealth(target.getHealth()+demicoeur);
			}else {
				target.setHealth(target.getMaxHealth());
			}
		}, 5);
	}
	public void onLeftClick(PlayerInteractEvent event, GameState gameState) {}
	public void KnowRole(Player knower, Roles toknow, int delayinTick) {
		if (Main.isDebug()){
			System.out.println("Starting trying to get player who own the role "+toknow.name());
		}
		Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (gameState.getInSpecPlayers().contains(p))return;
				if (!gameState.hasRoleNull(p)) {
					if (getListPlayerFromRole(toknow).contains(p)) {
						if (knower.isOnline() && p.isOnline() && !gameState.hasRoleNull(p) && gameState.getPlayerRoles().get(p).getOriginTeam() != null) {
							knower.sendMessage("Le joueur possédant le rôle de "+toknow.getTeam().getColor()+toknow.name()+"§f est "+p.getName());
						}
					}
				}
			}
		}, delayinTick);
	}
	public void onProjectileLaunch(Projectile projectile, Player shooter) {}
	public void onProjectileHit(Projectile entity, Player shooter) {}
	public void listPotionEffects(Player target, Player receiver) {
		receiver.sendMessage("\nEffets de potion actifs pour§7 " + target.getName() + "§r:");
	    for (PotionEffect effect : target.getActivePotionEffects()) {
			PotionEffectType type = effect.getType();
			if (type != PotionEffectType.ABSORPTION) {
				String effectName = formatEffectName(type.getName());
				String message = ChatColor.GRAY + "- " + effectName;
				receiver.sendMessage(message);
			}
		}
	}
	private String formatEffectName(String effectName) {
		String[] words = effectName.split("_");
		StringBuilder formattedName = new StringBuilder();
		for (String word : words) {
			formattedName.append(ChatColor.WHITE).append(word.substring(0, 1).toUpperCase()).append(word.substring(1)).append(" ");
		}
		return formattedName.toString().trim();
	}
	public void onAllPlayerMoove(PlayerMoveEvent e, Player moover) {}

	public void onEndGame() {
		toClaim.clear();
		customName.clear();
	}
	public List<ItemStack> toClaim = new ArrayList<>();
	public int countEmptySlots(Player player) {
        int emptySlots = 0;
        ItemStack[] contents = player.getInventory().getContents();
        for (ItemStack item : contents) {
            if (item == null || item.getType() == Material.AIR) {
                emptySlots++;
            }
        }
        return emptySlots;
    }
	public void giveItem(Player target, boolean message, ItemStack... toGive) {
		for (ItemStack stack : toGive) {
			if (countEmptySlots(target) > 0) {
				target.getInventory().addItem(stack);
				if (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName() && message) {
					target.sendMessage(stack.getItemMeta().getDisplayName()+"§7 à été ajouté à votre inventaire !");
				}
				if (toClaim.contains(stack)) {
					toClaim.remove(stack);
					target.updateInventory();
				}
			}else {
				if (target.getInventory().contains(stack)) {
					for (ItemStack s : target.getInventory().getContents()) {
						if (s == null)return;
						if (s.getType() != stack.getType())return;
						if (s.isSimilar(stack)) {
							if (s.getAmount() + stack.getAmount() <= 64) {
								target.getInventory().addItem(stack);
								toClaim.remove(s);
								if (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName() && message) {
									target.sendMessage(stack.getItemMeta().getDisplayName()+"§7 à été ajouté à votre inventaire !");
								}
								if (toClaim.contains(stack)) {
									toClaim.remove(stack);
									target.updateInventory();
								}
								return;
							}
						}
					}
				}
				if (!toClaim.contains(stack)) {
					toClaim.add(stack);
				}
				if (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
					target.sendMessage("L'item§6 "+stack.getItemMeta().getDisplayName()+"§f à été placé dans votre§l /claim");
				}else {
					target.sendMessage("Un item à été ajouté dans votre§l /claim");
				}
			}
		}
	}
	public void onALLPlayerDamage(EntityDamageEvent e, Player victim) {}
	public void onProjectileLaunch(ProjectileLaunchEvent event, Player shooter) {}
	public void onProjectileHit(ProjectileHitEvent event, Player shooter) {}
	public Player getTargetPlayer(Player player, double distanceMax) {
        RayTrace rayTrace = new RayTrace(player.getEyeLocation().toVector(), player.getEyeLocation().getDirection());
        List<Vector> positions = rayTrace.traverse(distanceMax, 0.1D);
        for (Vector vector : positions) {
            Location position = vector.toLocation(player.getWorld());
            Collection<Entity> entities = player.getWorld().getNearbyEntities(position, 1.0D, 1.0D, 1.0D);
            for (Entity entity : entities) {
                if (entity instanceof Player && entity != player) {
                	if (((Player)entity).getGameMode() != GameMode.SPECTATOR) {
                		if (player.canSee((Player)entity) && rayTrace.intersects(new BoundingBox(entity), distanceMax, 0.1D)) {
                			return (Player) entity;
                		}
                	}
                }
            }
        }
        return null;
    }
	public static Location getTargetLocation(Player player, int maxDistance) {
        BlockIterator blockIterator = new BlockIterator(player, maxDistance);
        
        while (blockIterator.hasNext()) {
            Block block = blockIterator.next();
            // Vous pouvez ajouter des vérifications supplémentaires ici si nécessaire
            if (!block.getType().equals(Material.AIR)) {
                return block.getLocation();
            }
        }

        // Si aucune cible n'est trouvée, retournez la position du joueur à la distance maximale
        Vector targetDirection = player.getLocation().getDirection().normalize().multiply(maxDistance);
        return player.getLocation().add(targetDirection);
    }
	public void onALLPlayerDamageByEntity(EntityDamageByEntityEvent event, Player victim, Entity entity) {}
	public void onAllPlayerInventoryClick(InventoryClickEvent event, ItemStack item, Inventory inv, Player clicker) {}
	@SuppressWarnings("deprecation")
	public void onAllPlayerChat(org.bukkit.event.player.PlayerChatEvent e, Player p) {}
	public Player getPlayerFromRole(Roles roles) {
		List<Player> toReturn = new ArrayList<>();
		for (UUID u : gameState.getInGamePlayers()) {
			Player p = Bukkit.getPlayer(u);
			if (p == null)continue;
			if (!gameState.hasRoleNull(p)) {
				GamePlayer GP = gameState.getGamePlayer().get(p.getUniqueId());
				if (GP.getRole().getRoles().equals(roles)) {
					toReturn.add(p);
				}
			}
		}
        return toReturn.get(0);
	}
	public List<Player> getListPlayerFromRole(Roles roles){
		List<Player> toReturn = new ArrayList<>();

		Bukkit.getOnlinePlayers().stream().filter(e -> !gameState.hasRoleNull(e)).filter(e -> gameState.getGamePlayer().get(e.getUniqueId()).getRole().getRoles() == roles).filter(p -> gameState.getInGamePlayers().contains(p.getUniqueId())).forEach(toReturn::add);
		return toReturn;
	}
	public List<Player> getListPlayerFromRole(Class<? extends RoleBase> role) {
		List<Player> toReturn = new ArrayList<>();
		for (UUID u : gameState.getInGamePlayers()) {
			Player p = Bukkit.getPlayer(u);
			if (p == null)continue;
			if (!gameState.hasRoleNull(p)) {
				if (gameState.getPlayerRoles().get(p).getClass().equals(role)) {
					if (gameState.getPlayerRoles().get(p).getGamePlayer().isAlive()){
						toReturn.add(p);
					}
				}
			}
		}
		return toReturn;
	}
	public void onALLPlayerInteract(PlayerInteractEvent event, Player player) {}
	public void onALLPlayerEat(PlayerItemConsumeEvent e, ItemStack item, Player eater) {}
	public void damage(Player target, double damage, int delay) {
		if (target != null && target.isOnline()) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
				if (!target.isOnline()) {
					return;
				}
				if (target.getHealth() < 0) {
					return;
				}
				if (target.getHealth() - damage < 0) {
					target.setHealth(1.0);
				}else {
					target.setHealth(target.getHealth()-damage);
				}
				target.damage(0.0);
			}, delay);
		}
	}
	public void damage(Player target, double damage, int delay, Player damager, boolean kill) {
		if (target != null) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
				if (!target.isOnline()) {
					return;
				}
				if (target.getHealth() < 0) {
					return;
				}
				if ((target.getHealth() - damage) < 0) {
					if (!kill) {
						target.setHealth(1.0);
					} else {
						target.damage(9999.0, damager);
					}
				} else {
					target.setHealth(target.getHealth()-damage);
					System.out.println(target.getHealth());
				}
				target.damage(0.0);
			}, delay);
		}
	}
	public boolean hasPermanentEffect(Player player, PotionEffectType type) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().equals(type) && effect.getDuration() >= 999999) {
                return true;
            }
        }
        return false;
    }
	public static List<PotionEffectType> getPermanentPotionEffects(Player player) {
        List<PotionEffectType> permanentEffects = new ArrayList<>();

        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getDuration() >= 999999) {
                permanentEffects.add(effect.getType());
            }
        }

        return permanentEffects;
    }
	public boolean onPreDie(Entity damager, GameState gameState2) {
		return false;
	}
	public void onInventoryClick(InventoryClickEvent event, ItemStack item, Inventory inv, Player clicker) {
	}
	public void neoFormChoosen(ItemStack item, Inventory inv, int slot, GameState gameState) {}
	public void playSound(Player p, String sound) {
		p.getWorld().setGameRuleValue("sendCommandFeedback", "false");
		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		Bukkit.dispatchCommand(console, "playSound "+sound+" "+p.getName()+ " "+p.getLocation().getBlockX()+" "+p.getLocation().getBlockY()+" "+p.getLocation().getBlockZ());
		p.getWorld().setGameRuleValue("sendCommandFeedback", "true");
	}
	public HashMap<UUID, String> customName = new HashMap<>();
	public void onALLPlayerDamageByEntityAfterPatch(EntityDamageByEntityEvent event, Player victim, Player damager) {}
	public boolean onEntityDeath(EntityDeathEvent e, LivingEntity entity) {
		return false;
	}
	@Override
	public TextComponent getComponent() {
		return new TextComponent("");
	}
	public void givePotionEffect(PotionEffect effect, EffectWhen when) {
		getEffects().put(effect, when);
	}
}
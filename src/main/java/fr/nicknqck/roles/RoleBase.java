package fr.nicknqck.roles;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.bijus.Bijus;
import fr.nicknqck.roles.aot.titans.Titans;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.utils.BoundingBox;
import fr.nicknqck.utils.NMSPacket;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.RayTrace;
import fr.nicknqck.utils.StringUtils;

public abstract class RoleBase {

	public boolean canShift = false;
	public Player owner;
	public Roles type;
	private Double maxHealth = 20.0;
	public Float bonusSpeedMultiplier = 0.00f;
	private boolean canRespawn = false;
	private boolean hasNoFall = false;
	private ArrayList<Player> linkWith = new ArrayList<>();
	private Roles oldRole = null;	
	float speedBase;
	private boolean powerEnabled = true;
	private boolean invincible = false;
	private boolean canuseblade = false;
	private double force = 0;
	private double resi = 0;
	private TeamList team;
	private TeamList oldteam;
	private double Bonusforce = 0;
	private double Bonusresi = 0;
	public GameState gameState;
	public ArrayList<Player> canBeCibleYahaba = new ArrayList<>();
	public abstract String[] Desc();
	
	public abstract ItemStack[] getItems();
	public ArrayList<Player> getIGPlayers() {return gameState.getInGamePlayers();}

	public int maxduralame = 40;
	boolean lameincassable = false;
	public int actualduralame = 0;
	public boolean hasblade = false;
	public int roleID = 0;
	public String StringID = "";
	public RoleBase(Player player, Roles roles, GameState gameState) {
		this.gameState = gameState;
		owner = player;
		speedBase = 0.20f;
		owner.setWalkSpeed(speedBase*(bonusSpeedMultiplier+1));
		type = roles;
		canBeCibleYahaba.clear();
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
				if (this.getTeam() != null) {
					owner.sendMessage(ChatColor.BOLD+"Camp: "+ this.getTeam().getColor() +StringUtils.replaceUnderscoreWithSpace(this.getTeam().name()));
					System.out.println(owner.getName() +" Team: "+ this.getTeam());
					oldteam = team;
				}
				if (this.type != null) {
					System.out.println(owner.getName() +" Role: "+ type.name());
				}

        }, 20);
		if (owner != null) {
			owner.sendMessage("");
			owner.setAllowFlight(false);
			owner.setFlying(false);
			owner.setGameMode(GameMode.SURVIVAL);
			gazAmount= 100.0;
			actualTridiCooldown = -1;
			roleID = RandomUtils.getRandomDeviationValue(1, -500000, 500000);
			System.out.println(owner.getName()+", RoleID: "+roleID);
			StringID = RandomUtils.generateRandomString(24);
			System.out.println(owner.getName()+", StringID: "+StringID);
		}
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if (owner.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE) && getForce() < 20) {
					setForce(20);
				}
				if (owner.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE) && getResi() < 20) {
					setResi(20);
				}
				if (gameState.getServerState() != ServerStates.InGame) {
					cancel();
				}
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
	}
	public abstract void resetCooldown();
	public boolean HisUnbreak() {return lameincassable;}
	public void setLameIncassable(Player target, boolean a) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			if (!gameState.hasRoleNull(target)) {
				getPlayerRoles(target).lameincassable = a;
				if (a) {
					sendMessageAfterXseconde(target, "Votre lame est devenue incassable", 1);
				} else {
					sendMessageAfterXseconde(target, "Votre lame n'est plus incassable", 1);
				}	
			} else {
				target.sendMessage("On dirait qu'on à essayer de donner une lame incassable cependant au moment ou on vous l'a donné vous n'aviez pas de rôle");
			}
			
    }, 20);	
	}
	public void sendActionBarCooldown(Player player, int cooldown) {
		if (cooldown > 0) {
		NMSPacket.sendActionBar(player, "Cooldown: "+cd(cooldown));
		}else {
			NMSPacket.sendActionBar(player, getItemNameInHand(player)+" Utilisable");
		}
	}
	public void sendCustomActionBar(Player player, String msg) {NMSPacket.sendActionBar(player, msg);}
	public String cd(int cooldown) {
		if (cooldown <= 0) {
			return "Utilisable";
		}else{
			return StringUtils.secondsTowardsBeautiful(cooldown);}
		}
	public TeamList getOldTeam() {return oldteam;}
	public TeamList getOldTeam(Player p) {
		return getPlayerRoles(p).getOldTeam();
	}
	public void setOldTeamList(TeamList list) {list = oldteam;}
	public RoleBase getPlayerRoles(Player player) {return gameState.getPlayerRoles().get(player);}
	public void sendMessageAfterXseconde(Player player, String message, int seconde) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			player.sendMessage(message);
		}, 20*seconde);
	}
	public String getTeamColor(Player target) {
		if (!gameState.hasRoleNull(target)) {
			if (getPlayerRoles(target).getTeam() != null) {
				return getPlayerRoles(target).getTeam().getColor();
			}else {
				return "";
			}
		}else {
			return "";
		}
	}
	public String getTeamColor() {
		return getTeam().getColor();
	}
	public void givePotionEffet(Player player, PotionEffectType type, int time, int level, boolean force) {player.addPotionEffect(new PotionEffect(type, time, level-1, false, false), force);}	
	public void givePotionEffet(PotionEffectType type, int time, int level, boolean force) {owner.addPotionEffect(new PotionEffect(type, time, level-1, false, false), force);}	
	public String getItemNameInHand(Player player) {return player.getItemInHand().getItemMeta().getDisplayName()+"§r";}
	public void sendCooldown(Player player, int cooldown) {player.sendMessage("Cooldown: "+StringUtils.secondsTowardsBeautiful(cooldown));}
	public Player getRightClicked(double maxDistance, int radius) {
		 Player player = owner;
	        Vector lineOfSight = player.getEyeLocation().getDirection().normalize();
	        for (double i = 0; i < maxDistance; ++i) {
	            Location add = player.getEyeLocation().add(lineOfSight.clone().multiply(i));
	            Block block = add.getBlock();
	            if (!block.getType().isSolid()) {
	                Collection<Entity> nearbyEntities = add.getWorld().getNearbyEntities(add, radius, radius, radius);
	                if (nearbyEntities.isEmpty()) {
	                    continue;
	                }

	                Entity next = nearbyEntities.iterator().next();
	                if (next instanceof Player) {
	                    Player nextPlayer = (Player) next;
	                    if (nextPlayer.getUniqueId().equals(player.getUniqueId()) || nextPlayer.getGameMode() == GameMode.SPECTATOR) continue;
	                    return nextPlayer;
	                }
	                continue;
	            }

	            return null;
	        }
	        return null;
	    }
	public void setTeam(TeamList team) {
		this.team = team;
		this.team.addPlayer(this.owner);	
	}
	public boolean hasTeam(Player player) {
		if (gameState.hasRoleNull(player)) {
			return false;
		}else {
			if (getPlayerRoles(player).getTeam() != null) {
				return true;
			}else {
				return false;
			}
		}
	}
	public TeamList getTeam() {return team;}
	public TeamList getTeam(Player player) {
		TeamList team = null;
		if (!gameState.hasRoleNull(player)) {
			team = getPlayerRoles(player).getTeam();
		}
		return team;
	}
	public double getForce() {return force;}
	public void setForce(double force) {this.force = force;}
	double allforce = getForce()+getBonusForce();
	public void addforce(double force) {setForce(getForce() + force);}
	public double getBonusForce() {return Bonusforce;}
	public void setBonusForce(double Bonusforce) {this.Bonusforce = Bonusforce;}
	public double getAllForce() {return allforce;}
	public void addBonusforce(double Bonusforce) {
		setBonusForce(getBonusForce() + Bonusforce);
	}
	double allresi = getResi()+getBonusForce();
	public double getAllResi() {return allresi;}
	public double getResi() {return resi;}
	public void setResi(double resi) {this.resi = resi;}
	public void addresi(double resi) {setResi(getResi() + resi);}
	public double getBonusResi() {return Bonusresi;}
	public void setBonusResi(double Bonusresi) {this.Bonusresi = Bonusresi;}
	public void addBonusResi(double Bonusresi) {setBonusResi(getBonusResi() + Bonusresi);}
	public void GiveItems() {}
	public void RoleGiven(GameState gameState) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			owner.sendMessage(ChatColor.RED+"Discord du mode de jeu: "+ChatColor.GOLD+"https://discord.gg/RF3D4Du8VN");
        }, 20*10);//20ticks* le nombre de seconde voulue
	}
	public void Update(GameState gameState) {    //Update every 1s (20ticks)
		allforce = getForce()+getBonusForce();
		allresi = getResi()+getBonusResi();
		if (owner != null) {
		owner.setMaxHealth(getMaxHealth());
		if (actualTridiCooldown > 0) {
			actualTridiCooldown--;
			if (owner.getItemInHand().isSimilar(gameState.EquipementTridi())) {
				DecimalFormat df = new DecimalFormat("0.0");
			//	sendCustomActionBar(owner, aqua+"Gaz:§c "+df.format(gazAmount)+"%"+aqua+" Cooldown:§6 "+actualTridiCooldown+"s");
				sendCustomActionBar(owner, "Gaz restant§8»"+gameState.sendGazBar(gazAmount, 2)+"§7("+aqua+df.format(gazAmount)+"%§7), Cooldown:§b "+cd(actualTridiCooldown));
			}
		}else if (actualTridiCooldown == 0){
			owner.sendMessage("§7§l"+gameState.EquipementTridi().getItemMeta().getDisplayName()+"§7 utilisable !");
			actualTridiCooldown--;
		}
		if (actualTridiCooldown <= 0) {
			if (owner.getItemInHand().isSimilar(gameState.EquipementTridi())) {
				DecimalFormat df = new DecimalFormat("0.0");
				sendCustomActionBar(owner, aqua+"Gaz:§c "+df.format(gazAmount)+"% "+"§7§lArc Tridimentionnel§r:§6 Utilisable");
			}
		}
			for (Player p : gameState.getInGamePlayers()) {
				if (gameState.getPlayerRoles().containsKey(p)) {
					if (gameState.getPlayerRoles().get(p).getTeam() == TeamList.Slayer) {
						if (!canBeCibleYahaba.contains(p)) {
							canBeCibleYahaba.add(p);
						}
					}
				}
			}
		}
		if (owner.getWorld().getGameRuleValue("doMobSpawning") != "false") owner.getWorld().setGameRuleValue("doMobSpawning", "false");
		if (owner.getWorld().getGameRuleValue("doFireTick") != "false")owner.getWorld().setGameRuleValue("doFireTick", "false");
		if (owner.getWorld().hasStorm())owner.getWorld().setStorm(false);
		if (owner.getWorld().isThundering())owner.getWorld().setThundering(false);
		}	
	public int getActualCooldownArc() {return actualTridiCooldown;}
	private boolean haslamecoeur = false;
	private boolean haslamefr = false;
	private boolean haslamespeed = false;
	private boolean haslameresi = false;
	private boolean haslameforce = false;	
	public boolean ItemUse(ItemStack item, GameState gameState) {return false;}
	public void ItemUseAgainst(ItemStack item, Player victim, GameState gameState) {}
	public void OpenFormInventory(GameState gameState) {}
	public void FormChoosen(ItemStack item, GameState gameState) {}
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {OnAPlayerDie(victim, gameState, killer);}
	public int UpdateScoreboard(Objective objective, int i) {return (i);}
	// Fonction appelée a la fin d'une partie, utiliser pour supprimer des variables ou données spécifiques.
	public void endRole() {}
	
	public void setCanUseBlade(boolean canuseblade) {this.canuseblade = canuseblade;}
	public boolean isCanUseBlade() {return canuseblade;}
	public boolean hasLameForce() {return haslameforce;}
	public void setLameForce(boolean haslameforce) {this.haslameforce = haslameforce;}
	public boolean isHasNoFall() {return hasNoFall;}
	public void setNoFall(boolean hasNoFall) {this.hasNoFall = hasNoFall;}
	
	public boolean hasLameresi() {return haslameresi;}
	public void setLameresi(boolean haslameresi) {this.haslameresi = haslameresi;}
	
	public boolean hasLameFr() {return haslamefr;}
	public void setLameFr(boolean haslamefr) {this.haslamefr = haslamefr;}
	public boolean hasLamecoeur() {return haslamecoeur;}
	public void setLamecoeur(boolean haslamecoeur) {		this.haslamecoeur = haslamecoeur;	}
	public boolean hasLameSpeed() {		return haslamespeed;	}
	public void setLameSpeed(boolean haslamespeed) {		this.haslamespeed = haslamespeed;	}
	
	public boolean isCanRespawn() {return canRespawn;}
	public void setCanRespawn(boolean canRespawn) {this.canRespawn = canRespawn;}

	public ArrayList<Player> getLinkWith() {return linkWith;}
	public void setLinkWith(ArrayList<Player> linkWith) {this.linkWith = linkWith;}
	public void addLinkWith(Player player) {
		if (!linkWith.contains(player)) {
			linkWith.add(player);
		}
	}
	
	public boolean AttackedByPlayer(Player attacker, GameState gameState) {return false;}

	public Roles getOldRole() {return oldRole;}

	public void setOldRole(Roles oldRole) {this.oldRole = oldRole;}

	public Double getMaxHealth() {return maxHealth;}

	public void setMaxHealth(Double maxHealth) {this.maxHealth = maxHealth; owner.setMaxHealth(maxHealth);}

	public void setPower(boolean powerEnabled) {this.powerEnabled = powerEnabled;}
	
	public boolean isPowerEnabled() {return this.powerEnabled;}

	public void setInvincible(boolean b) {invincible = b;}
	public boolean isInvincible() {return this.invincible;}

	public void neoAttackedByPlayer(Player attacker, GameState gameState) {}
	public boolean CancelAttack = false;

	public void onDay(GameState gameState) {}
	public void onNight(GameState gameState) {}

	public void addSpeedAtInt(Player player, float speedpercent) {player.setWalkSpeed(player.getWalkSpeed()+(speedpercent/500));}

	public void neoItemUseAgainst(ItemStack itemInHand, Player player, GameState gameState, Player damager) {
		Player p = damager;
		ItemUseAgainst(itemInHand, player, gameState);
		if (gameState.getInGamePlayers().contains(p)) {
			if (gameState.getPlayerRoles().containsKey(p)) {
				if (getPlayerRoles(p).hasblade) {
					if (getPlayerRoles(p).lameincassable)return;
					if (getPlayerRoles(p).actualduralame <= 0)return;
					int r = RandomUtils.getRandomInt(0, 5);
					System.out.println("Dura -1 "+getPlayerRoles(p).owner.getName()+" + "+getPlayerRoles(p).actualduralame+" / "+getPlayerRoles(p).maxduralame);
					if (r < 1) {
						getPlayerRoles(p).actualduralame-=1;
						if (getPlayerRoles(p).actualduralame == 0) {
							getPlayerRoles(p).owner.sendMessage("Votre lame c'est cassé vous perdez donc les effets dû à votre lame");
							if (getPlayerRoles(p).hasLamecoeur()) {
								getPlayerRoles(p).setMaxHealth(getPlayerRoles(p).getMaxHealth()-4.0);
								getPlayerRoles(p).setLamecoeur(false);
								getPlayerRoles(p).owner.sendMessage("Vous avez perdu votre Lame de "+ChatColor.LIGHT_PURPLE+"Coeur");
							}
							if (getPlayerRoles(p).hasLameForce()) {
								getPlayerRoles(p).addBonusforce(-10);
								getPlayerRoles(p).setLameForce(false);
								getPlayerRoles(p).owner.sendMessage("Vous avez perdu votre Lame de "+ChatColor.RED+"Force");
							}
							if (getPlayerRoles(p).hasLameFr()) {
								if (getPlayerRoles(p).type != Roles.Tanjiro) {
									getPlayerRoles(p).owner.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
									getPlayerRoles(p).setLameFr(false);
									getPlayerRoles(p).owner.sendMessage("Vous avez perdu votre Lame de "+ChatColor.GOLD+"Fire Résistance");
								}
							}
							if (getPlayerRoles(p).hasLameresi()) {
								getPlayerRoles(p).addBonusResi(-10);
								getPlayerRoles(p).setLameresi(false);
								getPlayerRoles(p).owner.sendMessage("Vous avez perdu votre Lame de "+ChatColor.GRAY+"Résistance");
							}
							if (getPlayerRoles(p).hasLameSpeed()) {
								getPlayerRoles(p).addSpeedAtInt(p, -10);
								getPlayerRoles(p).setLameSpeed(false);
								getPlayerRoles(p).owner.sendMessage("Vous avez perdu votre Lame de "+ChatColor.AQUA+"Speed");
							}
							if (getPlayerRoles(p).isHasNoFall()) {
								if (getPlayerRoles(p).type != Roles.Kanao) {
									getPlayerRoles(p).setNoFall(false);
									getPlayerRoles(p).owner.sendMessage("Vous avez perdu votre Lame de "+ChatColor.GREEN+"NoFall");
								}
							}
						}
					}
				}
			}
		}	
	}
	public void onEat(ItemStack item, GameState gameState) {}
	public void onDSCommandSend(String[] args, GameState gameState) {}
	public boolean hasItemInHotbar(Player player, Material mat) {
        for (int slot = 0; slot < 9; slot++) {
            ItemStack item = player.getInventory().getItem(slot);
            if (item != null && item.getType() == mat) {
                return true;
            }
        }
        return false;
    }
	public boolean isTransformedinTitan = false;
	public boolean onPickupItem(Item item) {
		for (ItemStack stack : getItems()) {
			if (item.getItemStack().isSimilar(stack)) {
				return true;
			}else {
				return false;
			}
		}
		return false;}
	public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
		for (Bijus value : Bijus.values()) {
			value.getBiju().onAPlayerDie(player, gameState, killer);
		}
		for (Titans value : Titans.values()) {
			value.getTitan().onAPlayerDie(player, killer);
		}
		if (!player.getWorld().equals(Main.getInstance().gameWorld)) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
				GameListener.RandomTp(player, gameState, Main.getInstance().gameWorld);
			}, 20);
		}
		if (!gameState.hasRoleNull(player)){
			if (getPlayerRoles(player).type == Roles.Nakime) {
				for (Player p : gameState.getOnlinePlayers()) {
					if (p.getWorld().equals(Bukkit.getWorld("nakime"))) {
						GameListener.RandomTp(p, gameState, Main.getInstance().gameWorld);
						p.sendMessage("§7Vous avez été éjecté de la§c cage de Nakime§7 du à la mort de cette dernière");
						player.sendMessage(p.getName()+"§7 est sortie de votre cage");
					}
				}
			}
			if (killer instanceof Player) {
				if (!gameState.hasRoleNull((Player) killer)) {
					Player k = (Player) killer;
					if (getPlayerRoles(k).gazAmount < 100.0) {
						double victimgaz = getPlayerRoles(player).gazAmount;
						if (victimgaz > 1.0) {
							double killergaz = getPlayerRoles(k).gazAmount;
							DecimalFormat df = new DecimalFormat("0.0");
							if (killergaz + victimgaz > 100.0) {
								k.sendMessage("§7Vous venez de récupérer§c "+df.format(victimgaz/3)+"%§7 de gaz");
								getPlayerRoles(k).gazAmount = 100;
							}else {
								k.sendMessage("§7Vous venez de récupérer§c "+df.format(victimgaz/3)+"%§7 de gaz");
								getPlayerRoles(k).gazAmount += victimgaz/3;
							}
						}
					}
				}
			}
			gameState.DeadRole.add(getPlayerRoles(player).type);
		}
	}
	public boolean onBucketEmpty(Material bucket, Block block, GameState gameState, Player player) {return false;}
	public boolean onBlockPlaced(Block block, Player player, GameState gameState) {return false;}
	public boolean onBlockBreak(Player player, Block block, GameState gameState) {return false;}
	public void OnAPlayerKillAnotherPlayer(Player player, Player damager, GameState gameState) {}
	public void onAotCommands(String arg, String[] args, GameState gameState) {}
	public void onArcTridi(Player player, GameState gameState) {}
	public int actualTridiCooldown = -1;
	public int ArcTridiCooldown() {return actualTridiCooldown;}
	public void TransfoEclairxMessage(Player player) {
		gameState.spawnLightningBolt(player.getWorld(), player.getLocation());
		for (Player p : getIGPlayers()) {p.sendMessage("\n§6§lUn Titan c'est transformé !");p.sendMessage("");}
	}
	public void TransfoMessage() {	for (Player p : getIGPlayers()) {p.sendMessage("\n§6§lUn Titan c'est transformé !");p.sendMessage("");p.playSound(p.getLocation(), "aotmtp.transfo", 8, 1);}	}
	public boolean hasRoleInfo() {return false;}
	public void giveHeartatInt(Player target, double coeur) {
		if (!gameState.hasRoleNull(target)) {
			getPlayerRoles(target).setMaxHealth(getPlayerRoles(target).getMaxHealth()+coeur*2);
		}
	}
	public final void giveHealedHeartatInt(final Player target,final double coeur) {
		if (!gameState.hasRoleNull(target)) {
			getPlayerRoles(target).setMaxHealth(getPlayerRoles(target).getMaxHealth()+coeur*2);
			target.setMaxHealth(getPlayerRoles(target).getMaxHealth());
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
				if (gameState.hasRoleNull(target))return;
				if (target.getHealth() <= (getPlayerRoles(target).getMaxHealth())-(coeur*2)) {
					target.setHealth(target.getHealth()+(coeur*2));
				}else {
					if (gameState.hasRoleNull(target))return;
					target.setHealth(getPlayerRoles(target).getMaxHealth());
				}
			}, 20);
		}
	}
	public final void giveHealedHeartatInt(final double coeur) {giveHealedHeartatInt(owner, coeur);}
	public void giveHalfHeartatInt(Player target, double demicoeur){
		if (!gameState.hasRoleNull(target)) {
			getPlayerRoles(target).setMaxHealth(getPlayerRoles(target).getMaxHealth()+demicoeur);
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
				if (target.getHealth() <= (getPlayerRoles(target).getMaxHealth())-(demicoeur)) {
					target.setHealth(target.getHealth()+(demicoeur));
				}else {
					target.setHealth(getPlayerRoles(target).getMaxHealth());
				}
			}, 20);
		}
	}
//TODO Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {}, 20);
	public void Heal(Player target, double demicoeur) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			if (target.getHealth() - demicoeur <= 0 && demicoeur <0) {
				GameListener.getInstance().DeathHandler(target, target, demicoeur, gameState);
			}
			if (target.getHealth() <= (target.getMaxHealth()-demicoeur)) {
				target.setHealth(target.getHealth()+demicoeur);
			}else {
				target.setHealth(target.getMaxHealth());
			}
		}, 5);
	}
	public static final String aqua = ChatColor.AQUA+"";
	public double RodSpeedMultipliyer = 0;
	public double gazAmount = 0;
	public void onAsyncChat(Player p, AsyncPlayerChatEvent e) {}
	public void onPlayerInteract(Block clickedBlock, Player player) {}
	public void onLeftClick(PlayerInteractEvent event, GameState gameState) {}
	public void KnowRole(Player knower, Roles toknow, int delayinTick) {
		if (!gameState.attributedRole.contains(toknow))return;
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (gameState.getInSpecPlayers().contains(p))return;
			if (!gameState.hasRoleNull(p)) {
				if (getListPlayerFromRole(toknow).contains(p)) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
						if (knower.isOnline() && p.isOnline() && knower != null && p != null && !gameState.hasRoleNull(p) && getOldTeam(p) != null) {
							knower.sendMessage("Le joueur possédant le rôle de "+getPlayerRoles(p).getOldTeam().getColor()+toknow.name()+"§f est "+p.getName());
						}
						
					}, delayinTick);
				}
			}
		}
	}
	public void onProjectileLaunch(Projectile projectile, Player shooter) {}

	public void onProjectileHit(Projectile entity, Player shooter) {}

	public void onNsCommand(String[] args) {}

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
	 public String formatEffectName(String effectName) {
	        // Formater le nom de l'effet de potion en ajoutant des espaces entre les mots
	        String[] words = effectName.split("_");
	        StringBuilder formattedName = new StringBuilder();
	        for (String word : words) {
	            formattedName.append(ChatColor.WHITE).append(word.substring(0, 1).toUpperCase()).append(word.substring(1)).append(" ");
	        }
	        return formattedName.toString().trim();
	    }

	public void onTick() {}

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
	public Location getTargetLocation(Player player, double distanceMax) {
        RayTrace rayTrace = new RayTrace(player.getEyeLocation().toVector(), player.getEyeLocation().getDirection());
        List<Vector> positions = rayTrace.traverse(distanceMax, 0.05D);
        for (Vector vector : positions) {
            Location position = vector.toLocation(player.getWorld());
            if (position != null) {
            	return position;
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
        Location targetLocation = player.getLocation().add(targetDirection);
        return targetLocation;
    }
	private boolean canVoleTitan = false;
	public boolean isCanVoleTitan() {return canVoleTitan;}
	public void setCanVoleTitan(boolean c) {canVoleTitan = c;}
	public void onALLPlayerDamageByEntity(EntityDamageByEntityEvent event, Player victim, Entity entity) {}
	private boolean ackerman = false;
	public boolean isAckerMan() {return ackerman;}
	public void setAckerMan(boolean b) {ackerman = b;}
	public void setChakraType(Chakras chakras) {
		if (chakras == null) {
			this.chakras = null;
			return;
		}
		chakras.getChakra().getList().add(owner.getUniqueId());
		this.chakras = chakras;
	}
	private Chakras chakras = null;
	public Chakras getChakras() {
		return chakras;
	}
	public boolean hasChakras() {
		if (chakras != null) {
			return true;
		}
		return false;
	}
	public void onAllPlayerInventoryClick(InventoryClickEvent event, ItemStack item, Inventory inv, Player clicker) {}
	@SuppressWarnings("deprecation")
	public void onAllPlayerChat(org.bukkit.event.player.PlayerChatEvent e, Player p) {}
	public Player getPlayerFromRole(Roles roles) {
		List<Player> toReturn = new ArrayList<>();
		getIGPlayers().stream().filter(e -> !gameState.hasRoleNull(e)).filter(e -> getPlayerRoles(e).type == roles).forEach(e -> toReturn.add(e));
		if (toReturn.size() == 0) {
			return null;
		}
		Player PlayerRoles = toReturn.get(0);
		return PlayerRoles;
	}
	public List<Player> getListPlayerFromRole(Roles roles){
		List<Player> toReturn = new ArrayList<>();
		Bukkit.getOnlinePlayers().stream().filter(e -> !gameState.hasRoleNull(e)).filter(e -> getPlayerRoles(e).type == roles).forEach(e -> toReturn.add(e));
		return toReturn;
	}
	public void onALLPlayerInteract(PlayerInteractEvent event, Player player) {}
	public void onBucketFill(PlayerBucketFillEvent e, Material bucket) {}
	public void onALLPlayerDropItem(PlayerDropItemEvent e, Player dropper, ItemStack item) {}
	public void onALLPlayerRecupItem(PlayerPickupItemEvent e, ItemStack s) {}
	public void onALLPlayerEat(PlayerItemConsumeEvent e, ItemStack item, Player eater) {}
	public Chakras getRandomChakras() {
        Chakras toReturn = null;
        int rdm = RandomUtils.getRandomInt(1, 5);
        if (rdm == 1) {
            toReturn = Chakras.DOTON;
        }
        if (rdm == 2) {
            toReturn = Chakras.FUTON;
        }
        if (rdm == 3) {
            toReturn = Chakras.KATON;
        }
        if (rdm == 4) {
            toReturn = Chakras.RAITON;
        }
        if (rdm == 5) {
            toReturn = Chakras.SUITON;
        }

        return toReturn;
    }
	public void damage(Player target, double damage, int delay) {
		if (target != null && target.isOnline()) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
				if (target == null || !target.isOnline()) {
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
				if (target == null || !target.isOnline()) {
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
	public boolean onAllPlayerBlockBreak(BlockBreakEvent e, Player player, Block block) {
		return false;
	}
	public void neoFormChoosen(ItemStack item, Inventory inv, int slot, GameState gameState) {}
	public Chakras getRandomChakrasBetween(Chakras... c) {
        Chakras tr = null;
        HashMap<Integer, Chakras> canReturn = new HashMap<>();
        int i = 0;
        for (Chakras ch : c) {
            i++;
            canReturn.put(i, ch);
        }
        int max = canReturn.size()+1;
        int rdm = RandomUtils.getRandomInt(1, max);
        for (Chakras r : canReturn.values()) {
            if (canReturn.get(rdm).equals(r)) {
                tr = r;
                break;
            }
        }
        return tr;
    }
	public void onJubiInvoque(Player invoquer) {}
	public boolean onReceveExplosionDamage() {return false;}
	public void onAllPlayerDamageByExplosion(EntityDamageEvent event, DamageCause cause, Player p) {}
	public void playSound(Player p, String sound) {
		p.getWorld().setGameRuleValue("sendCommandFeedback", "false");
		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		Bukkit.dispatchCommand(console, "playSound "+sound+" "+p.getName()+ " "+p.getLocation().getBlockX()+" "+p.getLocation().getBlockY()+" "+p.getLocation().getBlockZ());
		p.getWorld().setGameRuleValue("sendCommandFeedback", "true");
	}
	public HashMap<UUID, String> customName = new HashMap<>();
	public void onMcCommand(String[] args) {}

	public void onALLPlayerDamageByEntityAfterPatch(EntityDamageByEntityEvent event, Player victim, Player damager) {}
	public boolean canBeHokage = false;
	public boolean isCanTentacule() {
		return false;//Simple methode for only Killer Bee
	}

	public void onTentaculeEnd(double distanceSquared) {
		//Simple method for only Killer Bee
	}

	public boolean onEntityDeath(EntityDeathEvent e, LivingEntity entity) {
		return false;
	}
}
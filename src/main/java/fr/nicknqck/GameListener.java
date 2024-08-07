package fr.nicknqck;

import com.avaje.ebean.validation.NotNull;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.bijus.BijuListener;
import fr.nicknqck.bijus.Bijus;
import fr.nicknqck.events.EventBase;
import fr.nicknqck.events.Events;
import fr.nicknqck.events.custom.*;
import fr.nicknqck.items.InfectItem;
import fr.nicknqck.items.Items;
import fr.nicknqck.items.ItemsManager;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.titans.TitanListener;
import fr.nicknqck.roles.aot.builders.titans.Titans;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.demons.Susamaru;
import fr.nicknqck.roles.ds.demons.lune.Kaigaku;
import fr.nicknqck.roles.ds.slayers.FFA_Pourfendeur;
import fr.nicknqck.roles.ds.slayers.Nezuko;
import fr.nicknqck.roles.ds.slayers.pillier.Mitsuri;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.scenarios.impl.AntiPvP;
import fr.nicknqck.scenarios.impl.FFA;
import fr.nicknqck.scenarios.impl.Hastey_Babys;
import fr.nicknqck.scenarios.impl.Hastey_Boys;
import fr.nicknqck.utils.AntiLopsa;
import fr.nicknqck.utils.PotionUtils;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.betteritem.BetterItem;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.KamuiUtils;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class GameListener implements Listener {

	private final GameState gameState;
	public WorldBorder border;
	@Getter
	private static GameListener Instance;
//	private BukkitScheduler gameTimer = Bukkit.getServer().getScheduler(); // Seconds
	public GameListener(GameState gameState) {
		this.gameState = gameState;
		Instance = this;
		border = Main.getInstance().gameWorld.getWorldBorder();
		border.setSize(Border.getMaxBorderSize());
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () -> {
			UpdateGame();
			for (Events e : Events.values()) {
				e.getEvent().onSecond();
			}
			for (Chakras ch : Chakras.values()) {
				ch.getChakra().onSecond(gameState);
			}
			InfectItem.getInstance().onSecond();
			BijuListener.getInstance().runnableTask(gameState);
			TitanListener.getInstance().onSecond();

		}, 20, 20);
	}
	//public static GameListener getInstance() {return Instance;}
	private boolean infectedgiveforce = false;
	private void UpdateGame() {
		switch(gameState.getServerState()) {
		case InLobby:
			for (Player p : gameState.getInLobbyPlayers()) {
				World world = Bukkit.getWorld("world");
				if (p.getLocation().getY() < 100 && !p.isFlying() && p.getGameMode() != GameMode.CREATIVE) {
					p.setMaxHealth(20.0);
					p.setHealth(p.getMaxHealth());
					p.setFoodLevel(20);
					p.teleport(new Location(world, 0, 151, 0));
					p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
				}
				if (p.getGameMode() != GameMode.ADVENTURE) {
					if (!p.isOp() && !gameState.getHost().contains(p.getUniqueId())) {
						p.setGameMode(GameMode.ADVENTURE);
					}
				}
				p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, false, false));
				world.setWeatherDuration(0);
				p.getWorld().setWeatherDuration(0);
				p.getWorld().setStorm(false);
			}
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (!gameState.getInGamePlayers().isEmpty()) gameState.getInGamePlayers().clear();
				if (!gameState.getInSpecPlayers().isEmpty())gameState.getInSpecPlayers().clear(); //ils seront ajouté au lobby plus loin dans le code
				if (!gameState.getInSleepingPlayers().isEmpty()) gameState.getInSleepingPlayers().clear();
				if (!gameState.getPillier().isEmpty())gameState.getPillier().clear();
				if (!gameState.getInObiPlayers().isEmpty())gameState.getInObiPlayers().clear();
				if (!gameState.getInLobbyPlayers().contains(p))gameState.addInLobbyPlayers(p);
			}
			break;
		case InGame:
			Main.getInstance().gameWorld.setPVP(gameState.pvp);
			for (Player p : gameState.getInGamePlayers()) {
				if (gameState.inGameTime < 10) {
					if (p.getGameMode() != GameMode.SURVIVAL)p.setGameMode(GameMode.SURVIVAL);
				}
				if (gameState.infected != null && gameState.infected.getUniqueId() == p.getUniqueId()) {
					if (gameState.nightTime) {
						gameState.getPlayerRoles().get(p).givePotionEffet(gameState.infected, PotionEffectType.INCREASE_DAMAGE, 20*3, 1, true);
						if (!infectedgiveforce) {
							infectedgiveforce = true;
						}
					} else {
						if (infectedgiveforce) {
							infectedgiveforce = false;
						}
					}
				}
			}
			AntiLopsa.startWorldBorderChecker();
			if (Hastey_Boys.isHasteyBoys()) {
				for (Player p : gameState.getInGamePlayers()) {
					ItemStack is = p.getItemInHand();
					ItemMeta meta = is.getItemMeta();
					Material m = is.getType();
					if (meta != null) {
								if (!meta.hasEnchant(Enchantment.DIG_SPEED)) {
									if (!meta.hasEnchants()) {
										if (!meta.spigot().isUnbreakable()) {
                                            if (m == Material.IRON_PICKAXE || m == Material.IRON_SPADE || m == Material.IRON_AXE || m == Material.DIAMOND_PICKAXE || m == Material.DIAMOND_SPADE || m == Material.DIAMOND_AXE || m == Material.GOLD_PICKAXE || m == Material.GOLD_SPADE || m == Material.GOLD_AXE || m == Material.WOOD_PICKAXE || m == Material.WOOD_SPADE || m == Material.WOOD_AXE || m == Material.STONE_AXE || m == Material.STONE_PICKAXE || m == Material.STONE_SPADE) {
                                                meta.addEnchant(Enchantment.DIG_SPEED, 3, true);
                                                meta.addEnchant(Enchantment.DURABILITY, 3, true);
                                                is.setItemMeta(meta);
                                                p.sendMessage(Hastey_Boys.hasteyboy() + ChatColor.WHITE + "Enchantement de votre item");
                                                p.updateInventory();
                                            }
                                        }
									}
								}				
							}
						}
					}
			if (Hastey_Babys.isHasteyBabys()) {
				for (Player p : gameState.getInGamePlayers()) {
					ItemStack is = p.getItemInHand();
					ItemMeta meta = is.getItemMeta();
					Material m = is.getType();
					if (meta != null) {
						if (!meta.hasEnchant(Enchantment.DIG_SPEED)) {
							if (!meta.hasEnchants()) {
								if (!meta.spigot().isUnbreakable()) {
                                    if (m == Material.IRON_PICKAXE || m == Material.IRON_SPADE || m == Material.IRON_AXE || m == Material.DIAMOND_PICKAXE || m == Material.DIAMOND_SPADE || m == Material.DIAMOND_AXE || m == Material.GOLD_PICKAXE || m == Material.GOLD_SPADE || m == Material.GOLD_AXE || m == Material.WOOD_PICKAXE || m == Material.WOOD_SPADE || m == Material.WOOD_AXE || m == Material.STONE_AXE || m == Material.STONE_PICKAXE || m == Material.STONE_SPADE) {
                                        meta.addEnchant(Enchantment.DIG_SPEED, 1, true);
                                        meta.addEnchant(Enchantment.DURABILITY, 3, true);
                                        is.setItemMeta(meta);
                                        p.sendMessage(Hastey_Babys.HasteyBabys() + "Enchantement de votre item");
                                        p.updateInventory();
                                    }
                                }
							}
						}				
					}
				}
			}
			gameState.prevNightTime = gameState.nightTime;
			
			if (gameState.inGameTime == Border.getTempReduction()) {
				gameState.shrinking = true;
				long speed = (long) Border.getBorderSpeed();
				border.setSize(Border.getMinBorderSize()*2, speed*120);
				SendToEveryone("§7La bordure commence à bouger !");
			}
			if (gameState.inGameTime == 0) {
				gameState.nightTime = false;
				SendToEveryone(ChatColor.DARK_GRAY + "§o§m-----------------------------------");
				SendToEveryone("\n §bIl fait maintenant jour");
				SendToEveryone(ChatColor.DARK_GRAY + "\n§o§m-----------------------------------");
				Main.getInstance().gameWorld.setTime(0);
				Main.getInstance().gameWorld.setGameRuleValue("doDaylightCycle", "false");
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
					for (Player p : gameState.getOnlinePlayers()) {
						if (gameState.getPlayerRoles().containsKey(p)) {
							gameState.getPlayerRoles().get(p).onDay(gameState);
						}
					}
					Bukkit.getPluginManager().callEvent(new DayEvent(gameState));
	        }, 50);
			} else {
				gameState.t--;
				if (gameState.t <= 0) {
					if (gameState.nightTime) {
						Main.getInstance().gameWorld.setTime(0);
						gameState.nightTime = false;
						SendToEveryone(ChatColor.DARK_GRAY + "§o§m-----------------------------------");
						SendToEveryone("\n §bIl fait maintenant jour");
						SendToEveryone(ChatColor.DARK_GRAY + "\n§o§m-----------------------------------");
						gameState.t = gameState.timeday;
						for (Player p : gameState.getInGamePlayers()) {
							if (gameState.getPlayerRoles().containsKey(p)) {
								gameState.getPlayerRoles().get(p).onDay(gameState);
							}
						}
						Bukkit.getPluginManager().callEvent(new DayEvent(gameState));
						//detectWin(gameState);
					} else {
						Main.getInstance().gameWorld.setTime(16500);
						gameState.nightTime = true;
						SendToEveryone(ChatColor.DARK_GRAY + "§o§m-----------------------------------");
						SendToEveryone("\n §bIl fait maintenant nuit\n");
						SendToEveryone(ChatColor.DARK_GRAY + "\n§o§m-----------------------------------");
						gameState.t = gameState.timeday;
						for (Player p : gameState.getInGamePlayers()) {
							if (gameState.getPlayerRoles().containsKey(p)) {
								gameState.getPlayerRoles().get(p).onNight(gameState);
							}
						}
						Bukkit.getPluginManager().callEvent(new NightEvent(gameState));
					}
				}
			}

			if (gameState.inGameTime == gameState.roleTimer) {
				for (Player p : gameState.getInGamePlayers()) {
					RoleBase role = gameState.GiveRole(p);
					if (role != null){
						role.GiveItems();
					}
				}
				for (RoleBase r : gameState.getPlayerRoles().values()) {
					r.RoleGiven(gameState);
				}
			}
			for (Player p : gameState.getInGamePlayers()) {
				if (gameState.getPlayerRoles().containsKey(p)) {
					gameState.getPlayerRoles().get(p).Update(gameState);
				}
			}
			for (Events value : Events.values()) {
				if (gameState.getInGameTime() == value.getEvent().getMinTime()) {
					if (RandomUtils.getOwnRandomProbability(value.getProba())) {
						value.getEvent().PlayEvent(gameState.getInGameTime());
					}
				}
			}
			if (gameState.getActualPvPTimer() == 0){
				gameState.setPvP(true);
				SendToEveryone("(§c!§f) Le§c pvp§f est maintenant activé !");
				gameState.setActualPvPTimer(-1);
			} else {
				gameState.setActualPvPTimer(gameState.getActualPvPTimer()-1);
			}
			gameState.inGameTime+=1;
			break;
		case GameEnded:
			gameState.setServerState(ServerStates.InLobby);
			break;
		default:
			break;
		}
	}
	@SuppressWarnings("deprecation")
	public static void EndGame(final GameState gameState, final TeamList team) {
		gameState.setServerState(ServerStates.GameEnded);
		Bukkit.getPluginManager().callEvent(new EndGameEvent(gameState, team));
		gameState.setJubiCrafter(null);
		gameState.setActualPvPTimer(gameState.getPvPTimer());
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
			gameState.inGameTime = 0;
			Border.setActualBorderSize(Border.getMaxBorderSize());
			gameState.getGamePlayer().clear();
			gameState.setPvP(false);
			gameState.getInLobbyPlayers().clear();
			HubListener.spawnPlatform(gameState.world, Material.GLASS);
			gameState.getInGameEvents().clear();
			gameState.setInObiPlayers(new ArrayList<>());
			gameState.setInSleepingPlayers(new ArrayList<>());
			gameState.TitansRouge.clear();
			gameState.infectedbyadmin.clear();
			TitanListener.getInstance().resetCooldown();
			BetterItem.getRegisteredItems().clear();
			PotionUtils.getNoFalls().clear();
			fr.nicknqck.utils.AttackUtils.CantAttack.clear();
			fr.nicknqck.utils.AttackUtils.CantReceveAttack.clear();
			if (gameState.getHokage() != null) {
				gameState.getHokage().stop();
			}
			gameState.getDeadRoles().clear();
			for (Chakras ch : Chakras.values()) {
				ch.getChakra().getList().clear();
			}
			for (Player p : Bukkit.getOnlinePlayers()) {
				Main.getInstance().getScoreboardManager().onLogout(p);
				p.setPlayerListName(Bukkit.getPlayer(p.getUniqueId()).getName());
			}
			for (Events e : Events.values()) {
				e.getEvent().resetCooldown();
				e.getEvent().setActivated(false);
			}
			for (Bijus b : Bijus.values()) {
				b.getBiju().resetCooldown();
				b.getBiju().setHote(null);
				if (Main.isDebug()){
					System.out.println("reseted "+b.name());
				}
			}
			BijuListener.getInstance().resetCooldown();
			gameState.DeadRole.clear();
			gameState.attributedRole.clear();
			KamuiUtils.resetUtils();
			gameState.setHokage(null);
			for (Player p : gameState.getInGamePlayers()) {
				ItemsManager.ClearInventory(p);
				if (!gameState.hasRoleNull(p)) {
					RoleBase r = gameState.getPlayerRoles().get(p);
					r.setBonusForce(0);
					r.setBonusResi(0);
					r.setResi(0);
					r.customName.clear();
					if (r instanceof NSRoles) {
						((NSRoles) r).setCanBeHokage(false);
					}
				}
				if (!p.getWorld().getName().equalsIgnoreCase(Main.getInstance().gameWorld.getName())) {
					p.teleport(new Location(Main.getInstance().gameWorld, 0, 151, 0));
				}
				if (((CraftPlayer) p ).getHandle().isBurning()){
					((CraftPlayer) p).getHandle().fireTicks = 0;
				}
				if (p.getFireTicks() > 0) {
					p.setFireTicks(0);
				}
			}
			String title = null;
			if (team != null) {
				if (team != TeamList.Solo) {
					String Vainqueurs = "Victoire du camp: "+team.getColor()+StringUtils.replaceUnderscoreWithSpace(team.name());
					title = "Victoire des: "+team.getColor()+StringUtils.replaceUnderscoreWithSpace(team.name());
                    SendToEveryone(Vainqueurs);
				} else {
					if (gameState.getInGamePlayers().get(0) != null) {
						Player winer = gameState.getInGamePlayers().get(0);
						String Vainqueurs = "Vainqueur:§l "+team.getColor()+winer.getName();
						Vainqueurs += "\n§fQui était "+team.getColor()+gameState.getPlayerRoles().get(winer).getRoles()+"§f avec§6 "+gameState.getPlayerKills().get(winer).size()+"§f kill(s)";
						title = "Victoire de: "+team.getColor()+gameState.getPlayerRoles().get(winer).getRoles().name();
                        SendToEveryone(Vainqueurs);
					}
				}
			}
	        for (Player player : Bukkit.getOnlinePlayers()) {
	        	if (team != null && title != null) {
	        		player.sendTitle(title, "");
	        	}
			}
	        SendToEveryone(" ");
	        SendToEveryone("Résumé de la partie");
	        SendToEveryone(" ");
	        if (!gameState.getInGamePlayers().isEmpty()) {

					for (Player p : Bukkit.getOnlinePlayers()) {
						((CraftPlayer) p).getHandle().getDataWatcher().watch(9, (byte) 0); // Supprime les fleches du joueur
						gameState.addInLobbyPlayers(p);
						if (!gameState.hasRoleNull(p)) {
							RoleBase prole = gameState.getPlayerRoles().get(p);
							if (prole != null) {
								StringBuilder s = new StringBuilder();
								if (gameState.getPlayerKills().containsKey(p)) {
									if (!gameState.getPlayerKills().get(p).isEmpty()) {
										int i = 0;
										for (Player k : gameState.getPlayerKills().get(p).keySet()) {
											i++;
											if (i != gameState.getPlayerKills().get(p).size()) {
												s.append("§7 - §f").append(prole.getTeamColor(k)).append(k.getName()).append("§7 (").append(prole.getTeamColor(k)).append(prole.getPlayerRoles(k).getRoles().name()).append("§7)\n");
											} else {
												s.append("§7 - §f").append(prole.getTeamColor(k)).append(k.getName()).append("§7 (").append(prole.getTeamColor(k)).append(prole.getPlayerRoles(k).getRoles().name()).append("§7)");
											}
										}
										SendToEveryoneWithHoverMessage(prole.getTeamColor()+p.getDisplayName(), "§f ("+prole.getTeamColor()+prole.getRoles().name(), s.toString(), "§f) avec§c "+gameState.getPlayerKills().get(p).size()+"§f kill(s)");
									} else {
										SendToEveryone(prole.getTeamColor()+p.getDisplayName()+"§f ("+prole.getTeamColor()+prole.getRoles().name()+"§f) avec§c "+gameState.getPlayerKills().get(p).size()+"§f kill");
									}
								}
							}
						}
                    }
				}

			System.out.println("end");
			gameState.pregenNakime = false;
			gameState.setInGamePlayers(new ArrayList<>());
			BijuListener.getInstance().resetCooldown();
			for (Player p : gameState.getInSpecPlayers()) {
				if (!gameState.getInLobbyPlayers().contains(p)) {
					gameState.addInLobbyPlayers(p);
					p.setGameMode(GameMode.ADVENTURE);
				}
			}
			gameState.Shifter.clear();
			gameState.setInSpecPlayers(new ArrayList<>());
			gameState.setPlayerRoles(new HashMap<>());
			gameState.DeadRole = new ArrayList<>();
			if (!gameState.getInLobbyPlayers().isEmpty()) {
				for (Player p : gameState.getInLobbyPlayers()) {
					if (p != null && p.isOnline()) {
						p.setGameMode(GameMode.ADVENTURE);
						p.setMaxHealth(20.0);
						p.getInventory().clear();
						for (PotionEffect effect : p.getActivePotionEffects()) {
							p.removePotionEffect(effect.getType());
						}
						ItemsManager.GiveHubItems(p);
						p.teleport(new Location(Bukkit.getWorld("world"), 0.0, 151.0, 0.0));
					}
                    assert p != null;
                    if (!p.isOnline())gameState.getInLobbyPlayers().remove(p);
				}
			}
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
				for (Entity e : gameState.world.getEntities()) {
					if (!(e instanceof Player)) {
						e.remove();
					}
				}
				for (Player p : gameState.getOnlinePlayers()) {
					p.setGameMode(GameMode.ADVENTURE);
					p.closeInventory();
					p.getInventory().clear();
					ItemsManager.GiveHubItems(p);
					p.setFlying(false);
					p.setAllowFlight(false);
					Main.getInstance().getScoreboardManager().onLogin(p);
				}
	        }, 20);
		}, 1);
		System.out.println("game ended");
	}
	public static void SendToEveryone(String message) {for (Player p : Bukkit.getOnlinePlayers()) {p.sendMessage(message);}}
	public static void SendToEveryoneWithHoverMessage(String prefix, String HoverWord, String HoverContent, String suffix) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			GameListener.getInstance().sendHoverMessage(p, prefix, HoverWord, HoverContent, suffix);
		}
	}
	public static void SendToEveryoneWithHoverMessageExcept(String prefix, String HoverWord, String HoverContent, String suffix, Player cantSee) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getUniqueId() != cantSee.getUniqueId()) {
				GameListener.getInstance().sendHoverMessage(p, prefix, HoverWord, HoverContent, suffix);
			}
		}
	}
	public void SendToEveryoneExcept(String message, Player player) {for (Player p : Bukkit.getOnlinePlayers()) {if (p.equals(player)) continue;p.sendMessage(message);}}
	public static Location RandomTp(final Entity entity, final GameState gameState) {
		Location loc = null;
		while (loc == null || gameState.world.getBlockAt(loc).getType() == Material.WATER || gameState.world.getBlockAt(loc).getType() == Material.LAVA) {
			float x = Border.getActualBorderSize()*Main.RANDOM.nextFloat();
			float z = Border.getActualBorderSize()*Main.RANDOM.nextFloat();
			loc = gameState.world.getHighestBlockAt(new Location(gameState.world, x-Border.getActualBorderSize()/2, 0, z-Border.getActualBorderSize()/2)).getLocation();
		}
		loc.setY(loc.getY()+1);
		if (entity != null) entity.teleport(loc);
		if (entity instanceof Player) {
			((Player)entity).playSound(entity.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
		}
		return loc;
	}
	public static Location RandomTp(final Entity entity, final World world) {
		Location loc = null;
		while (loc == null || world.getBlockAt(loc).getType() == Material.WATER || world.getBlockAt(loc).getType() == Material.LAVA || world.getBlockAt(new Location(world, loc.getX(), loc.getY()-1, loc.getZ() ) ).getType() == Material.LAVA ) {
			float x = Border.getActualBorderSize()*Main.RANDOM.nextFloat();
			float z = Border.getActualBorderSize()*Main.RANDOM.nextFloat();
			loc = world.getHighestBlockAt(new Location(world, x-Border.getActualBorderSize(), 0, z-Border.getActualBorderSize())).getLocation();
		}
		loc.setY(loc.getY()+1);
		if (entity != null) entity.teleport(loc);
		if (entity instanceof Player) {
			((Player)entity).playSound(entity.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
		}
		return loc;
	}
	public static Location generateRandomLocation(final GameState gameState,final World world) {
	    Location loc;
	    do {
	        float x = Border.getActualBorderSize() * Main.RANDOM.nextFloat();
	        float z = Border.getActualBorderSize() * Main.RANDOM.nextFloat();
	        loc = world.getHighestBlockAt(new Location(world, x - Border.getActualBorderSize() / 2, 0, z - Border.getActualBorderSize() / 2)).getLocation();
	        loc.setY(loc.getY() + 1);
	    } while (loc.getX() <= -Border.getMaxBorderSize() || loc.getX() >= Border.getMaxBorderSize() || loc.getZ() <= -Border.getMaxBorderSize() || loc.getZ() >= Border.getMaxBorderSize() || loc.getBlock().getType().equals(Material.STATIONARY_LAVA));
	    return loc;
	}
	public void DeathMessage(Player victim) {
			SendToEveryone(ChatColor.DARK_GRAY+"§o§m-----------------------------------");
			SendToEveryone(victim.getDisplayName()+"§7 est mort,");
			if (!gameState.hasRoleNull(victim)) {
				World world = Bukkit.getWorld("nakime");
				RoleBase role = gameState.getPlayerRoles().get(victim);
				if (world != null && victim.getWorld().equals(world)){
					SendToEveryone("§7Son rôle était: "+(victim.getWorld().equals(Objects.requireNonNull(Bukkit.getWorld("nakime"))) ? role.getTeam().getColor()+role.getName() : "§k"+victim.getDisplayName()));
				} else {
					SendToEveryone("§7Son rôle était: "+role.getTeam().getColor()+role.getName()+role.getSuffixString());
				}
			} else {
				SendToEveryone(victim.getDisplayName()+"§c est mort, il n'avait pas de rôle");
			}
			SendToEveryone(ChatColor.DARK_GRAY+"§o§m-----------------------------------");
	}
	@NotNull
	public void DeathHandler(final Player player,final Entity damager,final Double damage,final GameState gameState) {
		UHCPlayerKillEvent playerKillEvent = new UHCPlayerKillEvent(player, damager, gameState);
		Bukkit.getPluginManager().callEvent(playerKillEvent);
		Bukkit.getPluginManager().callEvent(new UHCDeathEvent(player, gameState));
		for (EventBase event : gameState.getInGameEvents()) {
			if (damager instanceof Player) {
				event.OnPlayerKilled((Player) damager, player, gameState);
			} else if (damager instanceof Arrow) {
				Arrow arrow = (Arrow)damager;
				if (arrow.getShooter() instanceof Player) {
					event.OnPlayerKilled((Player) arrow.getShooter(), player, gameState);
				}
			} else {
				event.OnPlayerKilled(null, player, gameState);
			}
		}
		boolean cantDie = false;
		if (!gameState.hasRoleNull(player)) {
			if (gameState.getPlayerRoles().get(player).onPreDie(damager, gameState) || gameState.getPlayerRoles().get(player).getGamePlayer().isCanRevive()) {
				cantDie = true;
			}
			if (gameState.getPlayerRoles().get(player).getItems() != null) {
				for (ItemStack item : gameState.getPlayerRoles().get(player).getItems()) {
					if (player.getInventory().contains(item)) {
						player.getInventory().remove(item);
					}
				}
			}
		}
		if (cantDie || playerKillEvent.isCancel()) {
			return;
		}
		GamePlayer gamePlayer = gameState.getGamePlayer().get(player.getUniqueId());
		gamePlayer.setAlive(false);
		gamePlayer.setDeathLocation(player.getLocation());
        gameState.getDeadRoles().add(gameState.getPlayerRoles().get(player).getRoles());
        for (ItemStack item : player.getInventory().getContents()){
			if (item != null){
				if (item.getType() != Material.AIR){
					if (item.getAmount() <= 64){
						if (item.getAmount() > 0) {
							dropItem(player.getLocation().clone(), item.clone());
						} else {
							dropItem(player.getLocation().clone(), new ItemBuilder(item).setAmount(1).toItemStack());
						}
					}
				}
			}
		}
		if (gameState.getHokage() != null) {
			gameState.getHokage().onDeath(player, damager, gameState);
		}
		dropItem(player.getLocation(), new ItemStack(Material.GOLDEN_APPLE, 2));
		if (damager != null) {
			//damager = le tueur
            //player = la victim/le mort
			if (damager instanceof Player) {
				Player killer = (Player) damager;
                DeathMessage(player);
                for (Player p : gameState.getInGamePlayers()) {
					if (gameState.getPlayerRoles().containsKey(p)) {
						gameState.getPlayerRoles().get(p).OnAPlayerKillAnotherPlayer(player, killer, gameState);
					}
				}
				if (gameState.getPlayerRoles().containsKey(damager)) {
					RoleBase role = gameState.getPlayerRoles().get(damager);
					if (role.getTeam() == TeamList.Demon || role instanceof Kaigaku || role instanceof Nezuko) {
						for (Player p : gameState.getInGamePlayers()) {
							if (!gameState.hasRoleNull(p)) {
								RoleBase role2 = gameState.getPlayerRoles().get(p);
								if (role2.getTeam().equals(TeamList.Demon) || role2.getOriginTeam().equals(TeamList.Demon)) {
                                        p.sendMessage("§cLe joueur§4 "+damager.getName()+"§c à tué quelqu'un....");
                                    }
                                }
                            }
                        }
                    }
                    for (Player p : gameState.getInGamePlayers()) {
                        if (gameState.getPlayerRoles().containsKey(p)) {
                            gameState.getPlayerRoles().get(p).PlayerKilled((Player)damager, player, gameState);
                            if (!gameState.getPlayerKills().get(damager).containsKey(player)) {
                                RoleBase fakeRole = gameState.getPlayerRoles().get(player);
                                fakeRole.setOldRole(gameState.getPlayerRoles().get(player).getOldRole());
                                gameState.getPlayerKills().get(damager).put(player, fakeRole);
                            }
                        }
                    }
                    for (Player p : gameState.getInGamePlayers()) {
                        if (!gameState.hasRoleNull(player)) {
                            gameState.getPlayerRoles().get(p).OnAPlayerDie(player, gameState, damager);
                        }
                    }
                }else {
                    if (damager instanceof Arrow) {
                        Arrow arr = (Arrow) damager;
                        if (arr.getShooter() instanceof Player) {
                            Player killer = (Player) arr.getShooter();
                            DeathMessage(player);
                            for (Player p : gameState.getInGamePlayers()) {
                                if (gameState.getPlayerRoles().containsKey(p)) {
                                    gameState.getPlayerRoles().get(p).OnAPlayerKillAnotherPlayer(player, killer, gameState);
                                }
                            }
                            if (gameState.getPlayerRoles().containsKey((Player)arr.getShooter())) {
                                RoleBase role = gameState.getPlayerRoles().get((Player)arr.getShooter());
                                if (role.getTeam() == TeamList.Demon || role instanceof Kaigaku || role instanceof Nezuko) {
                                    for (Player p : gameState.getInGamePlayers()) {
                                        if (!gameState.hasRoleNull(p)) {
                                            RoleBase role2 = gameState.getPlayerRoles().get(p);
                                            if (role2.getOldTeam() == TeamList.Demon || role2 instanceof Kaigaku) {
                                                p.sendMessage("§cLe joueur§4 "+damager.getName()+"§c à tué quelqu'un....");
                                            }
                                        }
                                    }
                                }
                            }
                            for (Player p : gameState.getInGamePlayers()) {
                                if (gameState.getPlayerRoles().containsKey(p))
                                    gameState.getPlayerRoles().get(p).PlayerKilled((Player)arr.getShooter(), player, gameState);
                                if (!gameState.getPlayerKills().get((Player)arr.getShooter()).containsKey(player)) {
                                    RoleBase fakeRole = gameState.getPlayerRoles().get(player);
                                    fakeRole.setOldRole(gameState.getPlayerRoles().get(player).getOldRole());
                                    gameState.getPlayerKills().get((Player)arr.getShooter()).put(player, fakeRole);
                                }
                            }
                            for (Player p : gameState.getInGamePlayers()) {
                                if (!gameState.hasRoleNull(player)) {
                                    gameState.getPlayerRoles().get(p).OnAPlayerDie(player, gameState, damager);
                                }
                            }
                        }else {
                            DeathMessage(player);
                        }
                    }else {
                        DeathMessage(player);
                    }
                }
                for (Titans t : Titans.values()) {
                    t.getTitan().PlayerKilled(player, damager);
                }
            } else {//Fin du if damager != null
                DeathMessage(player);
            }
			for (Events event : Events.values()) {
				event.getEvent().onPlayerKilled(damager, player, gameState);
			}
        gameState.delInGamePlayers(player);
        gameState.addInSpecPlayers(player);
        if (gameState.morteclair) {
            player.getWorld().strikeLightningEffect(player.getLocation());
        }
        if (gameState.getInGamePlayers().size()-1 <= 0) {
				ItemsManager.ClearInventory(player);
			} else {
				dropItem(player.getLocation(), new ItemStack(Material.ARROW, 8));
				dropItem(player.getLocation(), new ItemStack(Material.BRICK, 16));
			}
			player.setMaxHealth(20.0);
			player.setHealth(player.getMaxHealth());
			player.setFoodLevel(20);
			player.setGameMode(GameMode.SPECTATOR);
			ItemsManager.ClearInventory(player);
			player.updateInventory();
			detectWin(gameState);
	}
	private static int trueCount(boolean... b) {
        int sum = 0;
        for (boolean b1 : b) {
            if (b1) sum++;
        }
        return sum;
    }
	@EventHandler(priority = EventPriority.HIGHEST)
	private void VanillaDeath(PlayerDeathEvent e) {
		e.setDroppedExp(5);
		e.getEntity().getInventory().clear();
		e.setDeathMessage(null);
	}
	@EventHandler
	private void onEntityDeath(EntityDeathEvent e){
		if (e.getEntity() instanceof Player){
			DeathHandler((Player) e.getEntity(), e.getEntity().getKiller(), e.getEntity().getLastDamage(), gameState);
			e.setDroppedExp(15);
			e.getDrops().clear();
		}
	}
	public static void detectWin(GameState gameState) {
        List<Player> players = new ArrayList<>(gameState.igPlayers);
		players.removeAll(gameState.getInSpecPlayers());
		boolean gameDone = false;
		TeamList winer = null;
		if (players.isEmpty()) {
			gameState.sendTitleToAll("§fVictoire de", "§7Personne", false);
            gameDone = true;
        }
		boolean Slayer = false;
		
		boolean Demon = false;
		
		boolean Solo = false;
		
		boolean Jigoro = false;
		
		boolean Mahr = false;
		
		boolean Titans = false;
		
		boolean Soldat = false;
		
		boolean Jubi = false;
		
		boolean Alliance = false;
		
		boolean Orochimaru = false;
		
		boolean Akatsuki = false;
		
		boolean Sasuke = false;
		
		boolean Brume = false;
		
		boolean Shinobi = false;
		
		boolean Kumogakure = false;
		
		for (Player player2 : gameState.getInGamePlayers()) {
			if (gameState.getPlayerRoles().get(player2) != null) {
				RoleBase role = gameState.getPlayerRoles().get(player2);
				switch (role.getTeam()) {
				case Akatsuki:
					Akatsuki = true;
					break;
				case Alliance:
					Alliance = true;
					break;
				case Demon:
					Demon = true;
					break;
				case Jigoro:
					Jigoro = true;
					break;
				case Jubi:
					Jubi = true;
					break;
				case Kumogakure:
					Kumogakure = true;
					break;
				case Mahr:
					Mahr = true;
					break;
				case Orochimaru:
					Orochimaru = true;
					break;
				case Sasuke:
					Sasuke = true;
					break;
				case Shinobi:
					Shinobi = true;
					break;
				case Slayer:
					Slayer = true;
					break;
				case Soldat:
					Soldat = true;
					break;
				case Solo:
					Solo = true;
					break;
				case Titan:
					Titans = true;
					break;
				case Zabuza_et_Haku:
					Brume = true;
					break;
				}
			}
		}
		int i = trueCount(Slayer, Demon, Solo, Jigoro, Mahr, Titans, Soldat, Jubi, Alliance, Orochimaru, Akatsuki, Sasuke, Brume, Shinobi, Kumogakure);
		if (gameDone) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (!gameState.hasRoleNull(p)) {
					gameState.getPlayerRoles().get(p).onEndGame();
				}
			}
			System.out.println("game ending");
		}
		if (gameDone) {
			EndGame(gameState, null);
			return;
		}
		if (i == 0) {
			EndGame(gameState, null);
		} else if (i == 1) {
			if (Kumogakure) {
				winer = TeamList.Kumogakure;
				gameDone = true;
			}
			if (Slayer) {
				//win des slayers
				winer = TeamList.Slayer;
				gameDone = true;
			}
			if (Demon) {
				//win des Demon
				winer = TeamList.Demon;
				gameDone = true;
			}
			if (Solo) {
				if (gameState.getInGamePlayers().size() == 1) {
					//win du solo (tah le bg)
					winer = TeamList.Solo;
					gameDone = true;
				}	
			}
			if (Mahr) {
				winer = TeamList.Mahr;
				gameDone = true;
			}
			if (Jigoro) {
				winer = TeamList.Jigoro;
				gameDone = true;
			}
			if (Titans) {
				winer = TeamList.Titan;
				gameDone = true;
			}
			if (Soldat) {
				winer = TeamList.Soldat;
				gameDone = true;
			}
			if (Jubi) {
				winer = TeamList.Jubi;
				gameDone = true;
			}
			if (Alliance) {
				winer = TeamList.Alliance;
				//win de l'alliance Shinjuro-Kyojuro
				gameDone = true;
			}
			if (Orochimaru) {
				winer = TeamList.Orochimaru;
				gameDone = true;
			}
			if (Akatsuki) {
				winer = TeamList.Akatsuki;
				gameDone = true;
			}
			if (Sasuke) {
				winer = TeamList.Sasuke;
				gameDone = true;
			}
			if (Brume) {
				winer = TeamList.Zabuza_et_Haku;
				gameDone = true;
			}
			if (Shinobi) {
				winer = TeamList.Shinobi;
				gameDone = true;
			}
		}
		if (gameDone){
			EndGame(gameState, winer);
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void OnDamagedEntityByEntity(EntityDamageByEntityEvent event) {
		if (gameState.getServerState() == ServerStates.InGame) {
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				Entity damageur = event.getDamager();
				double damage = event.getFinalDamage();
				for (Events e : Events.values()) {
					e.getEvent().onPlayerDamagedByPlayer(event, player, damageur);
				}
				if (damageur instanceof Player) {
					Player damager = (Player) event.getDamager();					
					if (gameState.getPlayerRoles().containsKey(damager)) {
						gameState.getPlayerRoles().get(damager).ItemUseAgainst(damager.getItemInHand(), player, gameState);
						gameState.getPlayerRoles().get(damager).neoItemUseAgainst(damager.getItemInHand(), player, gameState, damager);
						/*
						 * (damager).getItemInHand() = ItemStack item
						 * player = Player victim
						 * gameState = GameState gameState
						 */
						if (player != null) {
							Player attacker = (Player) damageur;
                            if (gameState.shutdown.contains(attacker)) {
								event.setCancelled(true);
							}
							if (gameState.getCharmed().contains(attacker)) {
								if (gameState.getPlayerRoles().get(player) instanceof Mitsuri) {
									attacker.sendMessage("Vous n'avez pas le pouvoir de tapée l'amour de votre vie");
									double x = player.getLocation().getX();
									double y = player.getLocation().getY();
									double z = player.getLocation().getZ();
									MathUtil.sendParticleTo(attacker, EnumParticle.HEART, x, y+2, z);
									event.setCancelled(true);
								}
							}
						if (gameState.getPlayerRoles().get(player).getRoles().equals(Roles.Slayer) && FFA.getFFA()) {
							FFA_Pourfendeur f = (FFA_Pourfendeur) gameState.getPlayerRoles().get(player);
							if (f.getPlayerRoles(f.owner).getRoles() == Roles.Slayer) {
								if (f.owner == player) {
									if (f.Serpent) {
										if (f.serpentactualtime >= 0) {
											if (RandomUtils.getOwnRandomProbability(20)) {
												f.owner.sendMessage("Vous venez d'esquiver un coup grâce à votre Soufle");
												event.setCancelled(true);	
											}							
										}
									}
								}
							}
						}
							gameState.getPlayerRoles().get(player).neoAttackedByPlayer(attacker, gameState);
							if (gameState.getPlayerRoles().get(player).CancelAttack)event.setCancelled(true);
						}
					}
				}
				if (player.getHealth()-damage <= 0) {
					if (event.getCause() != DamageCause.FALL) {
						if (gameState.getInGamePlayers().contains(player)) {
							if (!gameState.hasRoleNull(player)) {
								if (gameState.getPlayerRoles().get(player).isCanRespawn()) {
									gameState.getPlayerRoles().get(player).PlayerKilled((Player)damageur, player, gameState);
									event.setCancelled(true);
									return;
								}
							}
						}
					} else {
						if (gameState.getPlayerRoles().containsKey(player)) {
							if (gameState.getPlayerRoles().get(player).isHasNoFall()) {
								event.setDamage(0);
								event.setCancelled(true);
							}
						}
					}
				}				
			}
		} else {//else du serverstates.ingame
			if (AntiPvP.isAntipvplobby()) {
				event.setCancelled(true);
			}
		}
	}
	@EventHandler
	private void OnGuiInterract(InventoryClickEvent event) {
		Inventory inv = event.getClickedInventory();
		if (inv == null) return;
		if (inv.getTitle().contains("Inventaire de ")) {
			event.setCancelled(true);
		}
		ItemStack item = event.getCurrentItem();
		if (inv.getTitle().equals("§c/ds claim") || inv.getTitle().equals("§c/claim")) {
			if (item != null && item.getType() != Material.AIR && item.getType() != Material.STAINED_GLASS_PANE) {
				Player p = Bukkit.getPlayer(event.getWhoClicked().getName());
				if (!gameState.hasRoleNull(p)) {
					gameState.getPlayerRoles().get(p).giveItem(p, true, item);
					p.updateInventory();
				}
				p.updateInventory();
				p.closeInventory();
				event.setCancelled(true);
			}
		}
		if (event.getWhoClicked() instanceof Player) {
			Player clicker = (Player)event.getWhoClicked();
			if (!gameState.hasRoleNull(clicker)) {
				gameState.getPlayerRoles().get(clicker).onInventoryClick(event, item, inv, clicker);
			}
			for (Player p : Bukkit.getOnlinePlayers()){
				if (gameState.getInGamePlayers().contains(p)) {
					if (!gameState.hasRoleNull(p)) {
						gameState.getPlayerRoles().get(p).onAllPlayerInventoryClick(event, item, inv, clicker);
					}
				}
			}
		}
			switch(inv.getTitle()) {
			case "Choix de forme":
			case "Choix du niveau":
			case "Choix de fils":
			case "§cChoix de§l Kokushibo":
			case "Choix de la lame":
			case "Attaque":
			case "Défense":
			case "Claim":
			case "Nature de Chakra":
			case "§aCopie":
			case "§cSharingan":
			case "§aTechnique":
			case "Choix du Joueur":
				if (event.getWhoClicked() instanceof Player) {
					event.setCancelled(true);
					if (item != null && item.getType() != Material.AIR && item.getType() != Material.STAINED_GLASS_PANE) {
					    Player p = Bukkit.getPlayer(event.getWhoClicked().getName());
					    p.closeInventory();
					    gameState.getPlayerRoles().get(p).FormChoosen(item, gameState);
					    gameState.getPlayerRoles().get(p).neoFormChoosen(item, inv, event.getSlot(), gameState);
					}
				}
				break;
			default:
				break;
			}
	}
	@EventHandler
	public void OnItemInteract(PlayerInteractEvent event) {
		if (gameState.getServerState() != ServerStates.InGame) return;
		Player player = event.getPlayer();
		if (event.hasItem()) {
			ItemStack itemstack = event.getItem();
			for (Events e : Events.values()) {
				e.getEvent().onItemInteract(event, itemstack, player);
			}
			for (Player p : gameState.getInGamePlayers()) {
				if (!gameState.hasRoleNull(p)) {
					gameState.getPlayerRoles().get(p).onALLPlayerInteract(event, player);
				}
			}
			if (!gameState.hasRoleNull(player)) {
				if (event.getAction().name().contains("RIGHT")){
					event.setCancelled(gameState.getPlayerRoles().get(player).ItemUse(itemstack, gameState));
				} else {
					gameState.getPlayerRoles().get(player).onLeftClick(event, gameState);
				}
			}
				if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
					if (gameState.getInGamePlayers().contains(player)) {
						if (gameState.getPlayerRoles().containsKey(player)) {
							if (player.getItemInHand().isSimilar(Items.getSusamaruBow())) {
			        			if (itemstack.isSimilar(Items.getSusamaruBow())) {
			        				RoleBase role = gameState.getPlayerRoles().get(player);
		                			Susamaru sam = (Susamaru) role;
		                			if (role.getPlayerRoles(player).getRoles() != Roles.Susamaru)return;
		                			if (player != sam.owner)return;
		                			if (sam.Niveau1 && !sam.Niveau2 && sam.changecd <= 0) {
		                				sam.Niveau1 = false;
		                				sam.Niveau2 = true;
		                				sam.owner.sendMessage("Vous êtes passé au §6niveau 2");
		                				sam.changecd = 1;
		                			}
		                			if (sam.Niveau2 && !sam.Niveau1 && sam.changecd <= 0) {
		                				sam.Niveau1 = true;
		                				sam.Niveau2 = false;
		                				sam.changecd = 1;
		                				sam.owner.sendMessage("Vous êtes passé au §6niveau 1");
		                			}
			        			}
			        		}
						}
					}
				}
		}		
	}	
	@EventHandler
	public void OnPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		if (gameState.getInGamePlayers().contains(player)) {
			if (!gameState.hasRoleNull(player)) {
				if (gameState.getPlayerRoles().get(player).getRoles() == null) {
					event.setCancelled(true);
				}
			}
		}
	}
	public static void dropItem(final Location loc, final ItemStack item) {loc.getWorld().dropItem(loc.clone().add(0.5D, 0.3D, 0.5D), item);
		if (Main.isDebug()){
			System.out.println("droped item at x"+loc.getX()+" z"+loc.getZ()+" the item "+item.getType().name()+" x"+item.getAmount());
		}
	}
	@EventHandler
	public void OnMoove(PlayerMoveEvent e) {
		for (Player p : gameState.getInGamePlayers()) {
			if (!gameState.hasRoleNull(p)) {
				gameState.getPlayerRoles().get(p).onAllPlayerMoove(e, e.getPlayer());
			}
		}
		Player p = e.getPlayer();
		Location from = e.getFrom();
		Location to = e.getTo();
		if(to == null) return;
		if (to.getY() < 0) {
			p.teleport(new Location(to.getWorld(), to.getX(), to.getWorld().getHighestBlockYAt(to), to.getZ()));
		}
        if(to.getX() == from.getX() && to.getY() == from.getY() && from.getZ() == to.getZ()) return;//autrement dit si le joueur fait rien il ce passe rien
        for (Chakras ch : Chakras.values()) {
        	ch.getChakra().onPlayerMoove(e, p, from, to);
        }
        if (gameState.getInSleepingPlayers().contains(p)) {//si le joueur est endormie par enmu
        	
        	if (gameState.getServerState() == ServerStates.InLobby)gameState.delInSleepingPlayers(p);//si on est dans le lobby et qu'on est endormie sa nous réveille
        	if (gameState.getInSpecPlayers().contains(p))gameState.delInSleepingPlayers(p);//si on est en spec et qu'on est endormie sa nous réveille
        	
        	p.teleport(from);//teleporte le joueur à son endroit initial
        	p.setAllowFlight(true);//au cas ou il est en l'air on autorise le fly
        }        
        if (gameState.getInObiPlayers().contains(p)) {//si le joueur est touchée par les Obis de Daki
        	if (gameState.getServerState() == ServerStates.InLobby)gameState.delInObiPlayers(p);
        	if (gameState.getInSpecPlayers().contains(p))gameState.delInObiPlayers(p);
        	p.teleport(from);
        	p.setAllowFlight(true);
        }
    	if (gameState.shutdown.contains(e.getPlayer())) {
    		p.teleport(from);
    		p.setAllowFlight(false);
			if (Main.isDebug()){
				System.out.println(e.getPlayer().getName()+" peux pas bouger");
			}
    	}
	}
	@EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        ItemStack itemStack = event.getInventory().getResult();
        ItemStack zero = event.getInventory().getItem(0).clone();
        if (zero.getType() == Material.APPLE) {
        	event.getInventory().setResult(new ItemStack(Material.OBSIDIAN, 1));
        }
        if (itemStack == null || itemStack.getType() == Material.AIR) {
        	return;
        }        
        if (itemStack.getType().name().contains("AXE") || itemStack.getType().name().contains("SPADE") || itemStack.getType().name().contains("PICKAXE") && Hastey_Boys.isHasteyBoys()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.addEnchant(Enchantment.DIG_SPEED, 3, true);
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);
            itemStack.setItemMeta(itemMeta);
        }
    }
	@EventHandler
    public void onCraftItem(CraftItemEvent event) {
        CraftingInventory inventory = event.getInventory();
        Recipe recipe = event.getRecipe();

        // Vérifier si le craft correspond au craft personnalisé
        if (recipe instanceof ShapedRecipe && ((ShapedRecipe) recipe).getResult() == Items.getLamedenichirin()) {
            // Vérifier si tous les ingrédients sont présents
            ItemStack[] matrix = inventory.getMatrix();
            if (matrix[0] != null && matrix[1] != null && matrix[2] != null &&
                matrix[3] != null && matrix[5] != null && matrix[6] != null &&
                matrix[7] != null && matrix[8] != null) {

                // Vérifier si les ingrédients correspondent au schéma
                if (matrix[0].getType() == Material.OBSIDIAN &&
                    matrix[1].getType() == Material.IRON_INGOT &&
                    matrix[2].getType() == Material.DIAMOND &&
                    matrix[3].getType() == Material.GOLD_BLOCK &&
                    matrix[5].getType() == Material.IRON_INGOT &&
                    matrix[6].getType() == Material.OBSIDIAN &&
                    matrix[7].getType() == Material.GOLD_BLOCK &&
                    matrix[8].getType() == Material.IRON_BLOCK) {

                    inventory.setResult(Items.getLamedenichirin());
                    inventory.setResult(inventory.getResult().clone()); // Nécessaire pour mettre à jour le résultat
                }
            }
        }
    }
    @EventHandler
    public void setMOTD(ServerListPingEvent e) {
        String motd;
        if (gameState.getServerState() == ServerStates.InLobby) {
            motd = "        §e» §cStatus §f: §aEn Attente §f┃ "+ gameState.getInLobbyPlayers().size() +" §9Joueurs §f┃ "+gameState.getroleNMB()+" §9Rôles §e«\n                      §f§l▶ §r§b" +gameState.getAvailableRoles().size()+ " §aRôles Disponibles §f§l◀";
        } else {
            motd = "        §e» §cStatus §f: §6En Cours §f┃ " + gameState.getInGamePlayers().size() +" §9Joueurs §f┃ "+gameState.getroleNMB()+" §9Rôles §e«\n                      §f§l▶ §r§b" +gameState.getAvailableRoles().size()+ " §aRôles Disponibles §f§l◀";
        }
        e.setMotd(motd);
    }
    @EventHandler
    public void onRegainHeal(EntityRegainHealthEvent event) {
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED || event.getRegainReason() == EntityRegainHealthEvent.RegainReason.EATING) {
        	 event.setCancelled(true);
        }
	}
    public void sendHoverMessage(Player player, String prefix, String toHover, String inHover, String suffix) {
        TextComponent pref = new TextComponent(prefix);
        TextComponent hver = new TextComponent(toHover);
        hver.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent(inHover)}));
        pref.addExtra(hver);
        pref.addExtra(suffix);

        player.spigot().sendMessage(pref);
    }
}
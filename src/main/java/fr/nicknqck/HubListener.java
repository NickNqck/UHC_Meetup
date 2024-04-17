package fr.nicknqck;

import fr.nicknqck.GameState.MDJ;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.bijus.Bijus;
import fr.nicknqck.chat.Chat;
import fr.nicknqck.events.Events;
import fr.nicknqck.events.custom.StartGameEvent;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.items.Items;
import fr.nicknqck.items.ItemsManager;
import fr.nicknqck.pregen.PregenerationTask;
import fr.nicknqck.roles.TeamList;
import fr.nicknqck.roles.aot.titans.TitanListener;
import fr.nicknqck.roles.ns.Hokage;
import fr.nicknqck.scenarios.*;
import fr.nicknqck.scenarios.impl.AntiPvP;
import fr.nicknqck.scenarios.impl.CutClean;
import fr.nicknqck.scenarios.impl.DiamondLimit;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.StringUtils;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class HubListener implements Listener {
	private final GameState gameState;
	@Getter
	private static HubListener instance;
	public HubListener(GameState gameState) {this.gameState = gameState; instance = this;}
	public final void StartGame() {
		gameState.updateGameCanLaunch();
		if (!gameState.gameCanLaunch) {
			System.out.println("Impossible de start la partie");
			return;
		}
		gameState.world = Main.getInstance().gameWorld;
		gameState.setInGamePlayers(gameState.getInLobbyPlayers());
		gameState.setInLobbyPlayers(new ArrayList<>());
		gameState.igPlayers.addAll(gameState.getInGamePlayers());
		gameState.lunesup.clear();
		spawnPlatform(Bukkit.getWorld("world"), Material.AIR);
		gameState.infected = null;
		gameState.infecteur = null;
		gameState.Assassin = null;
		gameState.demonKingTanjiro = false;
		gameState.canBeAssassin.clear();
		if (gameState.getMdj() != null && gameState.getMdj().equals(MDJ.NS)){
			gameState.hokage = new Hokage(90, gameState);
			gameState.hokage.run();
		}
		for (Events e : Events.values()) {
			e.setProba(e.getEvent().getProba());
			e.getEvent().resetCooldown();
		}
		ItemsManager.instance.clearJspList();
		gameState.t = gameState.timeday;
		Events.initEvents();
		gameState.setPlayerRoles(new HashMap<>());
		gameState.setPlayerKills(new HashMap<>());
		gameState.borderSize = gameState.maxBorderSize;
		gameState.shrinking = false;
		gameState.world.getWorldBorder().setSize(gameState.maxBorderSize);
		if (gameState.JigoroV2Pacte2)gameState.JigoroV2Pacte2 = false;
		if (gameState.JigoroV2Pacte3)gameState.JigoroV2Pacte3 = false;
		TitanListener.getInstance().onStartGame();
		for (Entity e : gameState.world.getEntities()) {
			if (e instanceof Player) continue;
			e.remove();
		}
		// Debug Affichages des roles InGame
		int roleNmb = 0;
		
		for (Roles r : gameState.getAvailableRoles().keySet()) {
			System.out.println("role: "+r+", nmb: "+gameState.getAvailableRoles().get(r));
			roleNmb += gameState.getAvailableRoles().get(r);
			if (gameState.getAvailableRoles().get(r) == 0){
				gameState.getDeadRoles().add(r);
			}
		}
		System.out.println("lobby: "+gameState.getInLobbyPlayers().size()+", roles: "+roleNmb+", equal: "+(gameState.getInLobbyPlayers().size() == roleNmb));
		
		for (Player p : gameState.getInGamePlayers()) {
			p.setMaxHealth(20.0);
			p.setHealth(20.0);
			p.setFoodLevel(20);
			for (PotionEffect effect : p.getActivePotionEffects()) {
				p.removePotionEffect(effect.getType());
			}
			((CraftPlayer) p).getHandle().getDataWatcher().watch(9, (byte) 0); // Supprime les fleches du joueur
			p.setAllowFlight(false);
			p.setExp(0);
			p.setLevel(0);
			p.setFallDistance(0);
			GameListener.RandomTp(p, gameState);
			gameState.addPlayerKills(p);
			p.setGameMode(GameMode.SURVIVAL);
			giveStartInventory(p);
		}
		gameState.nightTime = false;
		// Supression de la plateforme
		gameState.initEvents(gameState);
		for (Bijus b : Bijus.values()) {
			b.getBiju().setHote(null);
			b.getBiju().resetCooldown();
			b.getBiju().getListener().resetCooldown();
		}
		Bijus.initBiju(gameState);
		Bukkit.getPluginManager().callEvent(new StartGameEvent(gameState));
		gameState.setServerState(ServerStates.InGame);
	}
	public void giveStartInventory(Player p) {
		Main.getInstance().getScoreboardManager().update(p);
		ItemsManager.ClearInventory(p);
			p.getInventory().setItem(0, Items.getdiamondsword());
			p.getInventory().setItem(2, Items.getbow());
			if (GameState.pearl == 1) {
				p.getInventory().setItem(4, new ItemStack(Material.ENDER_PEARL, GameState.pearl));
			}	
			p.getInventory().setItem(5, new ItemStack(Material.GOLDEN_CARROT, 64));
					
			p.getInventory().setItem(9, new ItemStack(Material.ARROW, gameState.nmbArrow));
			p.getInventory().setItem(20, new ItemStack(Material.ANVIL, 1));
			p.getInventory().setItem(11, Items.getironshovel());
			p.getInventory().setItem(12, Items.getironpickaxe());
		if (GameState.nmbblock == 1) {
			p.getInventory().setItem(1, new ItemStack(Material.BRICK, 64));
		} else if (GameState.nmbblock == 2) {
			p.getInventory().setItem(1, new ItemStack(Material.BRICK, 64));
			p.getInventory().setItem(28, new ItemStack(Material.BRICK, 64));
		} else if (GameState.nmbblock == 3) {
			p.getInventory().setItem(1, new ItemStack(Material.BRICK, 64));
			p.getInventory().setItem(28, new ItemStack(Material.BRICK, 64));
			p.getInventory().setItem(19, new ItemStack(Material.BRICK, 64));
		} else if (GameState.nmbblock == 4) {
			p.getInventory().setItem(1, new ItemStack(Material.BRICK, 64));
			p.getInventory().setItem(28, new ItemStack(Material.BRICK, 64));
			p.getInventory().setItem(19, new ItemStack(Material.BRICK, 64));
			p.getInventory().setItem(10, new ItemStack(Material.BRICK, 64));
		}
		if (GameState.eau == 1) {
			p.getInventory().setItem(7, new ItemStack(Material.WATER_BUCKET, 1));
		} else if (GameState.eau == 2) {
			p.getInventory().setItem(7, new ItemStack(Material.WATER_BUCKET, 1));
			p.getInventory().setItem(34, new ItemStack(Material.WATER_BUCKET, 1));
		} else if (GameState.eau == 3) {
			p.getInventory().setItem(7, new ItemStack(Material.WATER_BUCKET, 1));
			p.getInventory().setItem(25, new ItemStack(Material.WATER_BUCKET, 1));
			p.getInventory().setItem(34, new ItemStack(Material.WATER_BUCKET, 1));
		} else if (GameState.eau == 4) {
			p.getInventory().setItem(7, new ItemStack(Material.WATER_BUCKET, 1));
			p.getInventory().setItem(16, new ItemStack(Material.WATER_BUCKET, 1));
			p.getInventory().setItem(25, new ItemStack(Material.WATER_BUCKET, 1));
			p.getInventory().setItem(34, new ItemStack(Material.WATER_BUCKET, 1));
		}
		if (GameState.lave == 1) {
			p.getInventory().setItem(6, new ItemStack(Material.LAVA_BUCKET, 1));
		} else if (GameState.lave == 2) {
			p.getInventory().setItem(6, new ItemStack(Material.LAVA_BUCKET, 1));
			p.getInventory().setItem(33, new ItemStack(Material.LAVA_BUCKET, 1));
		} else if (GameState.lave == 3) {
			p.getInventory().setItem(6, new ItemStack(Material.LAVA_BUCKET, 1));
			p.getInventory().setItem(24, new ItemStack(Material.LAVA_BUCKET, 1));
			p.getInventory().setItem(33, new ItemStack(Material.LAVA_BUCKET, 1));
		} else if (GameState.lave == 4) {
			p.getInventory().setItem(6, new ItemStack(Material.LAVA_BUCKET, 1));
			p.getInventory().setItem(15, new ItemStack(Material.LAVA_BUCKET, 1));
			p.getInventory().setItem(24, new ItemStack(Material.LAVA_BUCKET, 1));
			p.getInventory().setItem(33, new ItemStack(Material.LAVA_BUCKET, 1));
		}
		p.getInventory().setHelmet(Items.getdiamondhelmet());
		p.getInventory().setChestplate(Items.getdiamondchestplate());
		p.getInventory().setLeggings(Items.getironleggings());
		p.getInventory().setBoots(Items.getdiamondboots());
		p.getInventory().setItem(3, new ItemStack(Material.GOLDEN_APPLE, gameState.nmbGap));
		p.updateInventory();
		p.setWalkSpeed(0.2f);
		((CraftPlayer) p).getHandle().setAbsorptionHearts(0);
	}
	// Creer une plateforme pour le spawn des joueurs (Suppression possible grace a Material.AIR)
	public static void spawnPlatform(final World world, final Material material) {
		for (int x = -16; x <= 16; x++) {
			for (int z = -16; z <= 16; z++) {
				world.getBlockAt(new Location(world, x, 150, z)).setType(material);
			}
		}
	}
	// Gerer l'interaction avec un item
	@EventHandler
	public void OnItemInteract(PlayerInteractEvent event) {
		if (gameState.getServerState() != ServerStates.InLobby) return; // Uniquement dans le lobby
		Player player = event.getPlayer();
		if (event.hasItem()) {
			ItemStack itemstack = event.getItem();
			// Si click droit alors afficher un menu (AdminWatch)
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (itemstack.isSimilar(ItemsManager.adminWatch)) {
					if (player.isOp() || gameState.getHost().contains(player)) {
						player.openInventory(GUIItems.getAdminWatchGUI());
		    			updateAdminInventory(player);
					} else {
						player.openInventory(GUIItems.getRoleSelectGUI());
						updateRoleInventory(player);
					}
				}
			}
		}
	}	
	@EventHandler
	public void OnInventoryClicked(InventoryClickEvent event) {		
		if (gameState.getServerState() != ServerStates.InLobby) return;
		if (event.getWhoClicked() instanceof Player) {
		    Player player = Bukkit.getPlayer(event.getWhoClicked().getName());
		    Inventory inv = event.getClickedInventory();
		    InventoryAction action = event.getAction();
		    if (inv != null && event.getCurrentItem() != null) {
		    	if (event.getCurrentItem().isSimilar(GUIItems.getStartGameButton()) && gameState.gameCanLaunch) StartGame(player);
		    	ItemStack item = event.getCurrentItem();
		    	boolean mahr = item.isSimilar(GUIItems.getSelectMahrButton());
		    	boolean sl = item.isSimilar(GUIItems.getSelectSlayersButton());
				boolean d = item.isSimilar(GUIItems.getSelectDemonButton());
				boolean solo = item.isSimilar(GUIItems.getSelectSoloButton());
				boolean titans = item.isSimilar(GUIItems.getSelectTitanButton());
				boolean soldat = item.isSimilar(GUIItems.getSelectSoldatButton());
				boolean aotconfig = item.isSimilar(GUIItems.getSelectConfigAotButton());
				boolean demon = item.isSimilar(GUIItems.getSelectDSButton());
				boolean aot = item.isSimilar(GUIItems.getSelectAOTButton());
				boolean ns = item.isSimilar(GUIItems.getSelectNSButton());
				boolean akatsuki = item.isSimilar(GUIItems.getSelectAkatsukiButton());
				boolean orochimaru = item.isSimilar(GUIItems.getSelectOrochimaruButton());
				boolean brume = item.isSimilar(GUIItems.getSelectBrumeButton());
				boolean shinobi = item.isSimilar(GUIItems.getSelectShinobiButton());
				boolean kumo = item.isSimilar(GUIItems.getSelectKumogakureButton());
				if (!item.hasItemMeta())return;
				switch(inv.getTitle()) {
				case "§fConfiguration":
		    		if (item.isSimilar(GUIItems.getStartGameButton()) && (player.isOp() || gameState.getHost().contains(player) )) {
		    			StartGame(player);
		    		} else if (item.isSimilar(GUIItems.getSelectRoleButton())  && (player.isOp() || gameState.getHost().contains(player) )) {
		    			player.openInventory(GUIItems.getRoleSelectGUI());
		    			updateRoleInventory(player); //Ouvre le menu role
		    		}  else if (item.isSimilar(GUIItems.getSelectScenarioButton())  && (player.isOp() || gameState.getHost().contains(player) ) ) {
		    			player.openInventory(GUIItems.getScenarioGUI());
		    			updateScenarioInventory(player); //Ouvre le menu des scenarios
		    		} else if (item.isSimilar(GUIItems.getSelectConfigButton())  && (player.isOp() || gameState.getHost().contains(player) )) {
		    			player.openInventory(GUIItems.getConfigSelectGUI());
		    			updateConfigInventory(player); //Ouvre le menu permettant de configurer l'essentiel de la partie
		    		} else if (item.isSimilar(GUIItems.getSelectInvsButton())  && (player.isOp() || gameState.getHost().contains(player) )) {	    		
		    		player.openInventory(GUIItems.getSelectInventoryGUI());
		    		updateSelectInventory(player); //Ouvre le menu pour config l'inventaire
		    		} else if (item.isSimilar(AntiPvP.getlobbypvp())||item.isSimilar(AntiPvP.getnotlobbypvp())  && (player.isOp() || gameState.getHost().contains(player) )) {
		    			if (AntiPvP.isAntipvplobby()) {
		    				AntiPvP.setAntipvplobby(false);
		    				player.sendMessage("Vous venez d'activer le PvP dans le Lobby");
		    				Bukkit.broadcastMessage("Un administrateur à activer le PvP dans le Lobby");
		    			} else {
		    				AntiPvP.setAntipvplobby(true);
		    				player.sendMessage("Vous venez de désactiver le PvP dans le lobby");
		    				Bukkit.broadcastMessage("Un administrateur à desactiver le PvP dans le Lobby");
		    			}
		    		}  else if (item.isSimilar(GUIItems.getCrit(gameState))  && (player.isOp() || gameState.getHost().contains(player) )) {
		    			if (action.equals(InventoryAction.PICKUP_ALL)) {
		    				if (gameState.critP < 50) {
		    					gameState.critP+=1;
		    				}
		    			} else if (action.equals(InventoryAction.PICKUP_HALF)) {
		    				if (gameState.getCritP() > 0) {
		    					gameState.critP-=1;
		    				}
		    			}
		    		} else if (item.isSimilar(Chat.getColoritem())) {
		    			if (action.equals(InventoryAction.PICKUP_ALL)) {
		    				ChatColor c = Chat.getopColor();
		    				if (c == ChatColor.RED) Chat.setopColor(ChatColor.GOLD);
		    				if (c == ChatColor.GOLD) Chat.setopColor(ChatColor.YELLOW);
		    				if (c == ChatColor.YELLOW) Chat.setopColor(ChatColor.DARK_GREEN);
		    				if (c == ChatColor.DARK_GREEN) Chat.setopColor(ChatColor.GREEN);
		    				if (c == ChatColor.GREEN) Chat.setopColor(ChatColor.AQUA);
		    				if (c == ChatColor.AQUA) Chat.setopColor(ChatColor.DARK_AQUA);
		    				if (c == ChatColor.DARK_AQUA) Chat.setopColor(ChatColor.DARK_BLUE);
		    				if (c == ChatColor.DARK_BLUE) Chat.setopColor(ChatColor.BLUE);
		    				if (c == ChatColor.BLUE) Chat.setopColor(ChatColor.LIGHT_PURPLE);
		    				if (c == ChatColor.LIGHT_PURPLE) Chat.setopColor(ChatColor.DARK_PURPLE);
		    				if (c == ChatColor.DARK_PURPLE) Chat.setopColor(ChatColor.WHITE);
		    				if (c == ChatColor.WHITE) Chat.setopColor(ChatColor.GRAY);
		    				if (c == ChatColor.GRAY) Chat.setopColor(ChatColor.DARK_GRAY);
		    				if (c == ChatColor.DARK_GRAY) Chat.setopColor(ChatColor.BLACK);
		    				if (c == ChatColor.BLACK) Chat.setopColor(ChatColor.DARK_RED);
		    				if (c == ChatColor.DARK_RED) Chat.setopColor(ChatColor.RED);
		    			} else if (action.equals(InventoryAction.PICKUP_HALF)) {
		    				ChatColor c = Chat.getopColor();
		    				if (c == ChatColor.RED) Chat.setopColor(ChatColor.DARK_RED);
		    				if (c == ChatColor.DARK_RED) Chat.setopColor(ChatColor.BLACK);
		    				if (c == ChatColor.BLACK) Chat.setopColor(ChatColor.DARK_GRAY);
		    				if (c == ChatColor.DARK_GRAY) Chat.setopColor(ChatColor.GRAY);
		    				if (c == ChatColor.GRAY) Chat.setopColor(ChatColor.WHITE);
		    				if (c == ChatColor.WHITE)Chat.setopColor(ChatColor.DARK_PURPLE);
		    				if (c == ChatColor.DARK_PURPLE) Chat.setopColor(ChatColor.LIGHT_PURPLE);
		    				if (c == ChatColor.LIGHT_PURPLE) Chat.setopColor(ChatColor.BLUE);
		    				if (c == ChatColor.BLUE) Chat.setopColor(ChatColor.DARK_BLUE);
		    				if (c == ChatColor.DARK_BLUE) Chat.setopColor(ChatColor.DARK_AQUA);
		    				if (c == ChatColor.DARK_AQUA) Chat.setopColor(ChatColor.AQUA);
		    				if (c == ChatColor.AQUA) Chat.setopColor(ChatColor.GREEN);
		    				if (c == ChatColor.GREEN) Chat.setopColor(ChatColor.DARK_GREEN);
		    				if (c == ChatColor.DARK_GREEN) Chat.setopColor(ChatColor.YELLOW);
		    				if (c == ChatColor.YELLOW) Chat.setopColor(ChatColor.GOLD);
		    				if (c == ChatColor.GOLD) Chat.setopColor(ChatColor.RED);
		    			}
		    		} else if (item.isSimilar(GUIItems.getPregen(gameState))) {
		    			if (!gameState.hasPregen){
		    				new PregenerationTask(Main.getInstance().gameWorld, gameState.getMaxBorderSize());
		    				gameState.hasPregen = true;
		    			}
		    		}else if (item.isSimilar(GUIItems.getSelectEventButton())) {
		    			player.openInventory(GUIItems.getEventSelectGUI());	
		    			updateEventInventory(player);
		    		}
		    		if (item.isSimilar(GUIItems.getx())) player.closeInventory();
		    		event.setCancelled(true);
		    		break;
				case "§fConfiguration§7 ->§6 Roles":
					if (item.getType() != Material.AIR) {
						if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
							if (demon) {
								player.openInventory(GUIItems.getDemonSlayerInventory());
								updateDSInventory(player);
							}
							if (aot) {
								player.openInventory(GUIItems.getSelectAOTInventory());
								updateAOTInventory(player);
							}
							if (ns) {
								player.openInventory(GUIItems.getSelectNSInventory());
								updateNSInventory(player);
							}
							if (item.getType() == Material.BOOKSHELF) {
								Inventory inventaire = Bukkit.createInventory(player, 9, "Séléction du mode de jeu");
								player.openInventory(inventaire);
								updateSelectMDJ(player);
							}
						} else {
							if (item.isSimilar(GUIItems.getSelectBackMenu())) {
									if ((player.isOp() || gameState.getHost().contains(player) ))player.openInventory(GUIItems.getAdminWatchGUI());
									if (!player.isOp() && player.getOpenInventory() != null && player.getInventory() != null) player.closeInventory();
							  }
						}
					}					
					for (Player p : gameState.getInLobbyPlayers()) {
						updateRoleInventory(p);
					}
		    		event.setCancelled(true);		    		
					break;
				case "Séléction du mode de jeu":
					if (item.getType() != Material.AIR) {
						if (item.isSimilar(GUIItems.getSelectBackMenu())) {
							player.openInventory(GUIItems.getRoleSelectGUI());
							updateRoleInventory(player);
						}
						for (MDJ mdj : MDJ.values()) {
							if (item.isSimilar(mdj.getItem())) {
								if (mdj.isEnable()) {
									gameState.setAllMDJDesac();
								}else {
									gameState.setAllMDJDesac();
									mdj.setEnable(true);
									gameState.setMdj(mdj);
								}
							}
						}
					}
					for (Player p : gameState.getInLobbyPlayers()) {
						updateSelectMDJ(p);
					}
					event.setCancelled(true);
					break;
				case "§fConfiguration§7 -> §6Événements":
                    if (item.getType() != Material.AIR) {
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            player.openInventory(GUIItems.getAdminWatchGUI());
                            updateAdminInventory(player);
                        } else {
                            for (Events e : Events.values()) {
                                if (item.getItemMeta().getDisplayName().equals(e.getName())) {
                                    if (e.equals(Events.DemonKingTanjiro)) {
                                        if (action == InventoryAction.PICKUP_ALL) {
                                            gameState.DKminTime += 60;
                                        }
                                        if (action == InventoryAction.PICKUP_HALF) {
                                            if (gameState.DKminTime > 60) {
                                                gameState.DKminTime -= 60;
                                            }
                                        }
                                        if (action == InventoryAction.DROP_ONE_SLOT) {
                                            if (gameState.DKTProba == 0) {
                                                gameState.DKTProba = 101;
                                            }
                                            if (gameState.DKTProba > 0) {
                                                gameState.DKTProba--;
                                            }
                                        }
                                        if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                                            if (gameState.DKTProba == 100) {
                                                gameState.DKTProba = -1;
                                            }
                                            if (gameState.DKTProba < 100) {
                                                gameState.DKTProba++;
                                            }
                                        }
                                    }
                                    if (e.equals(Events.AkazaVSKyojuro)) {
                                        if (action == InventoryAction.PICKUP_ALL) {
                                            gameState.AkazaVsKyojuroTime += 60;
                                        }
                                        if (action == InventoryAction.PICKUP_HALF) {
                                            if (gameState.AkazaVsKyojuroTime > 60) {
                                                gameState.AkazaVsKyojuroTime -= 60;
                                            }
                                        }
                                        if (action == InventoryAction.DROP_ONE_SLOT) {
                                            if (gameState.AkazaVSKyojuroProba == 0) {
                                                gameState.AkazaVSKyojuroProba = 101;
                                            }
                                            if (gameState.AkazaVSKyojuroProba > 0) {
                                                gameState.AkazaVSKyojuroProba--;
                                            }
                                        }
                                        if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                                            if (gameState.AkazaVSKyojuroProba == 100) {
                                                gameState.AkazaVSKyojuroProba = -1;
                                            }
                                            if (gameState.AkazaVSKyojuroProba < 100) {
                                                gameState.AkazaVSKyojuroProba++;
                                            }
                                        }
                                    }
                                    if (e.equals(Events.Alliance)) {
                                        if (action == InventoryAction.PICKUP_ALL) {
                                            gameState.AllianceTime += 60;
                                        }
                                        if (action == InventoryAction.PICKUP_HALF) {
                                            if (gameState.AllianceTime > 60) {
                                                gameState.AllianceTime -= 60;
                                            }
                                        }
                                        if (action == InventoryAction.DROP_ONE_SLOT) {
                                            if (gameState.AllianceProba == 0) {
                                                gameState.AllianceProba = 101;
                                            }
                                            if (gameState.AllianceProba > 0) {
                                                gameState.AllianceProba--;
                                            }
                                        }
                                        if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                                            if (gameState.AllianceProba == 100) {
                                                gameState.AllianceProba = -1;
                                            }
                                            if (gameState.AllianceProba < 100) {
                                                gameState.AllianceProba++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    for (Player p : gameState.getInLobbyPlayers()) {
		    			updateEventInventory(p);
					}
		    		event.setCancelled(true);
					break;
				case "§fConfiguration§7 -> §6scenarios":
					for (Scenarios scenario : Scenarios.values()){
						if (item.isSimilar(scenario.getScenarios().getAffichedItem())){
							scenario.getScenarios().setAction(action);
							scenario.getScenarios().onClick(player);
						}
					}
					player.updateInventory();
					    if (item.isSimilar(GUIItems.getSelectBackMenu())) {
			    			for (Player p : gameState.getInLobbyPlayers()) {
								  if (p == event.getWhoClicked()) {
									  if ((player.isOp() || gameState.getHost().contains(player) ))p.openInventory(GUIItems.getAdminWatchGUI());
									  if (!p.isOp() && p.getOpenInventory() != null && p.getInventory() != null) p.closeInventory();
								  }									 
							  }
						  }
						if (item.isSimilar(DiamondLimit.ChangeDiamond())) {
								if (action.equals(InventoryAction.PICKUP_ALL)) {
									if (DiamondLimit.getmaxdiams() != 32) {
										DiamondLimit.setMaxDiams(DiamondLimit.getmaxdiams()+1);
									} else {
									player.sendMessage(DiamondLimit.DM()+"Vous avez déjà atteint le nombre maximum de la "+ChatColor.GOLD+"DiamondLimit");	
									}								
								} else if (action.equals(InventoryAction.PICKUP_HALF)) {
									if (DiamondLimit.getmaxdiams() != 1) {
										DiamondLimit.setMaxDiams(DiamondLimit.getmaxdiams() - 1);
									} else {
										player.sendMessage(DiamondLimit.DM()+"Vous avez déjà atteint le nombre minimum de la "+ChatColor.GOLD+"DiamondLimit");
									}								
								}
							
			    		}
					  event.setCancelled(true);
					break;
				case "§bCutClean":
					if (action.equals(InventoryAction.PICKUP_ALL)||action.equals(InventoryAction.PICKUP_HALF)) {
						InventoryAction g = InventoryAction.PICKUP_ALL;
						String name = item.getItemMeta().getDisplayName();
						if (name.startsWith("Point")) {
							if (item.isSimilar(CutClean.getXpCharbon(gameState))) {
								if (action == g) {
									gameState.xpcharbon++;
								} else {
									gameState.xpcharbon--;
								}
							}
							if (item.isSimilar(CutClean.getXpFer(gameState))) {
								if (action == g) {
									gameState.xpfer++;
								} else {
									gameState.xpfer--;
								}
							}
							if (item.isSimilar(CutClean.getXpOr(gameState))) {
								if (action == g) {
									gameState.xpor++;
								} else {
									gameState.xpor--;
								}
							}
							if (item.isSimilar(CutClean.getXpDiams(gameState))) {
								if (action == g) {
									gameState.xpdiams++;
								} else {
									gameState.xpdiams--;
								}
							}
							if (gameState.xpcharbon<1)gameState.xpcharbon++;
							if (gameState.xpfer<1)gameState.xpfer++;
							if (gameState.xpor<1)gameState.xpor++;
							if (gameState.xpdiams<1)gameState.xpdiams++;
						} else {
							if (item.isSimilar(GUIItems.getSelectBackMenu())) {
								for (Player p : gameState.getInLobbyPlayers()) {
									  if (p == event.getWhoClicked()) {
										  if ((player.isOp() || gameState.getHost().contains(player) ))p.openInventory(GUIItems.getScenarioGUI()); updateScenarioInventory(player);
										  if (!p.isOp() && p.getOpenInventory() != null && p.getInventory() != null) p.closeInventory();
									  }									 
								  }
							  }
						}
					}
					player.updateInventory();
					updateCutCleanInventory(player);
					event.setCancelled(true);
					break;
				case "§fConfiguration§7 ->§6 Inventaire":
					if (item.getType() == Material.AIR) return;
					if (item.getType() == Material.DIAMOND_HELMET) {
						if (action.equals(InventoryAction.PICKUP_ALL)) {
							if (GameState.pc < 4) {
								GameState.pc++;
							}
		    			}else {
		    				if (GameState.pc > 0) {
		    					GameState.pc--;
		    				}
		    			}
					}
					if (item.getType() == Material.DIAMOND_CHESTPLATE) {
						if (action.equals(InventoryAction.PICKUP_ALL)) {
							if (GameState.pch < 4) {
								GameState.pch++;
							}
		    			}else {
		    				if (GameState.pch > 0) {
		    					GameState.pch--;
		    				}
		    			}
					}
					if (item.getType() == Material.IRON_LEGGINGS) {
						if (action.equals(InventoryAction.PICKUP_ALL)) {
							if (GameState.pl < 4) {
								GameState.pl++;
							}
		    			}else {
		    				if (GameState.pl > 0) {
		    					GameState.pl--;
		    				}
		    			}
					}
					if (item.getType() == Material.DIAMOND_BOOTS) {
						if (action.equals(InventoryAction.PICKUP_ALL)) {
							if (GameState.pb < 4) {
								GameState.pb++;
							}
		    			} else {
		    				if (GameState.pb > 0) {
		    					GameState.pb--;
		    				}
		    			}
					}
					if (item.hasItemMeta()) {
						if (item.getItemMeta().hasDisplayName()) {
							if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§r§fNombre de pomme d'§eor")) {
								if (action.equals(InventoryAction.PICKUP_ALL)) {
									if (gameState.nmbGap != 64) {
										gameState.nmbGap++;
									} else {
									player.sendMessage("Vous avez déjà atteint le nombre maximum de Pomme en Or ("+ChatColor.GOLD+"64"+ChatColor.RESET+")");	
									}								
								} else if (action.equals(InventoryAction.PICKUP_HALF)) {
									if (gameState.nmbGap != gameState.minnmbGap) {
										gameState.nmbGap--;
									} else {
										player.sendMessage("Vous avez déjà atteint le nombre minimum de Pomme en Or ("+ChatColor.GOLD+"12"+ChatColor.RESET+")");
									}								
								}
							}
						}
					}
					if (item.isSimilar(GUIItems.getdiamondsword())) {						
						if (action.equals(InventoryAction.PICKUP_ALL)) {
							if (GameState.sharpness != 5) {
								GameState.sharpness++;
							} else {
								player.sendMessage("Vous avez déjà atteint la limite de sharpness maximal");
							}
						} else if (action.equals(InventoryAction.PICKUP_HALF)) {
							if (GameState.sharpness != 1) {
								GameState.sharpness--;
							} else {
								player.sendMessage("Vous avez déjà atteint la limite de sharpness minimal");
							}
						}
					}
					if (item.isSimilar(GUIItems.getblock())) {
						if (action.equals(InventoryAction.PICKUP_ALL)) {
							if (GameState.nmbblock != 4) {
								GameState.nmbblock++;
							} else {
								player.sendMessage("Vous avez déjà atteint la limite de block");
							}
						} else if (action.equals(InventoryAction.PICKUP_HALF)) {
							if (GameState.nmbblock != 1) {
								GameState.nmbblock--;
							} else {
								player.sendMessage("Vous avez déjà atteint la limite de block");
							}
						}
					}
					if (item.isSimilar(GUIItems.getbow())) {
						if (action.equals(InventoryAction.PICKUP_ALL)) {
							if (GameState.power != 5) {
								GameState.power++;
							} else {
								player.sendMessage("Vous avez déjà atteint la limite de power");
							}
						} else if (action.equals(InventoryAction.PICKUP_HALF)) {
							if (GameState.power != 1) {
								GameState.power--;
							} else {
								player.sendMessage("Vous avez déjà atteint la limite minimal de power");
							}
						}
						
					}
					if (item.isSimilar(GUIItems.getEnderPearl())) {
						if (action.equals(InventoryAction.PICKUP_ALL)) {
							if (GameState.pearl == 0) {
								GameState.pearl++;
							} else {
								player.sendMessage("Vous avez déjà atteint la limite maximum d'ender pearl");
							}
						} else if (action.equals(InventoryAction.PICKUP_HALF)) {
							if (GameState.pearl == 1) {
								GameState.pearl--;
							} else {
								player.sendMessage("Vous avez déjà atteint la limite minimum d'ender pearl");
							}
						}
					}
					if (item.isSimilar(GUIItems.geteau())) {
						if (action.equals(InventoryAction.PICKUP_ALL)) {
							if (GameState.eau != 4) {
								GameState.eau++;
							} else {
								player.sendMessage("Vous avez déjà atteint la limite de sceau d'eau");
							}
						} else if (action.equals(InventoryAction.PICKUP_HALF)) {
							if (GameState.eau != 1) {
								GameState.eau--;
							} else {
								player.sendMessage("Vous avez déjà atteint la limite minimal de sceau d'eau");
							}
						}
						
					}
					if (item.isSimilar(GUIItems.getlave())) {
						if (action.equals(InventoryAction.PICKUP_ALL)) {
							if (GameState.lave != 4) {
								GameState.lave++;
							} else {
								player.sendMessage("Vous avez déjà atteint la limite de sceau de lave");
							}
						} else if (action.equals(InventoryAction.PICKUP_HALF)) {
							if (GameState.lave != 1) {
								GameState.lave--;
							} else {
								player.sendMessage("Vous avez déjà atteint la limite minimal de sceau de lave");
							}
						}
					}
					if (item.isSimilar(GUIItems.getSelectBackMenu())) {
						for (Player p : gameState.getInLobbyPlayers()) {
							  if (p == event.getWhoClicked()) {
								  if ((player.isOp() || gameState.getHost().contains(player) ))p.openInventory(GUIItems.getAdminWatchGUI());
								  if (!p.isOp() && p.getOpenInventory() != null && p.getInventory() != null) p.closeInventory();
							  }
						  }
					  } else {
						  if (item.getType() == Material.ARROW) {
								if (action.equals(InventoryAction.PICKUP_ALL)) {
									if (gameState.nmbArrow < 64) {
										gameState.nmbArrow++;
									}
								} else {
									if (gameState.nmbArrow > 0) {
										gameState.nmbArrow--;
									}
								}
							}
					}
					event.setCancelled(true);
					break;
				case "DemonSlayer ->§a Slayers":
					if (item.getItemMeta() == null)return;
					if (item.getItemMeta().getDisplayName() == null)return;
					if (item.getType() == Material.AIR)return;
					if (item.isSimilar(GUIItems.getx())) {
						event.setCancelled(true);
						return; 
					}
					if (!event.getWhoClicked().isOp() && !gameState.getHost().contains(player)) {
						event.setCancelled(true);
						return;
					}
					if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
						if (!item.isSimilar(GUIItems.getGreenStainedGlassPane()) && !item.isSimilar(GUIItems.getSelectSoloButton()) && !item.isSimilar(GUIItems.getSelectDemonButton()) && !item.isSimilar(GUIItems.getCantStartGameButton()) && !item.isSimilar(GUIItems.getStartGameButton())) {
							String name = item.getItemMeta().getDisplayName();
							Roles role = Roles.valueOf(name);
							if (action.equals(InventoryAction.PICKUP_ALL)) {
								gameState.addInAvailableRoles(role, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(role)+1));
							} else if (action.equals(InventoryAction.PICKUP_HALF)) {
								gameState.addInAvailableRoles(role, Math.max(0, gameState.getAvailableRoles().get(role)-1));
							}
						}
						if (item.isSimilar(GUIItems.getStartGameButton()) && gameState.gameCanLaunch) StartGame(player);
						if (sl) {
							player.openInventory(GUIItems.getSlayersSelectGUI());
							updateSlayerInventory(player);
						}
						if (d) {
							player.openInventory(GUIItems.getDemonSelectGUI());
							updateDemonInventory(player);
						}
						if (solo) {
							player.openInventory(GUIItems.getDSSoloSelectGUI());
							updateDSSoloInventory(player);
						}
						
					} else {
						for (Player p : gameState.getInLobbyPlayers()) {
							  if (p == event.getWhoClicked()) {
								  if ((player.isOp() || gameState.getHost().contains(player) ))p.openInventory(GUIItems.getRoleSelectGUI());updateRoleInventory(player);
								  if (!p.isOp() && p.getOpenInventory() != null && p.getInventory() != null) p.closeInventory();
							  }									 
						  }						
					}
					for (Player p : gameState.getInLobbyPlayers()) {
						updateSlayerInventory(p);
					}
					event.setCancelled(true);
					break;
				case "DemonSlayer -> §cDémons":
					if (item.getItemMeta() == null)return;
					if (item.getItemMeta().getDisplayName() == null)return;
					if (item.getType() == Material.AIR)return;
					if (item.isSimilar(GUIItems.getx())) {
						event.setCancelled(true);
						return; 
					}
					if (!event.getWhoClicked().isOp() && !gameState.getHost().contains(player)) {
						event.setCancelled(true);
						return;
					}
					if (!player.isOp() && !gameState.getHost().contains(player))return;
					if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
						if (!item.isSimilar(GUIItems.getRedStainedGlassPane()) && !item.isSimilar(GUIItems.getSelectSoloButton()) && !item.isSimilar(GUIItems.getSelectDemonButton()) && !item.isSimilar(GUIItems.getSelectSlayersButton())&& !item.isSimilar(GUIItems.getCantStartGameButton()) && !item.isSimilar(GUIItems.getStartGameButton())) {
							String name = item.getItemMeta().getDisplayName();
							Roles role = Roles.valueOf(name);
							if (action.equals(InventoryAction.PICKUP_ALL)) {
								gameState.addInAvailableRoles(role, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(role)+1));
							} else if (action.equals(InventoryAction.PICKUP_HALF)) {
								gameState.addInAvailableRoles(role, Math.max(0, gameState.getAvailableRoles().get(role)-1));
							}
						}
						if (item.isSimilar(GUIItems.getStartGameButton()) && gameState.gameCanLaunch) StartGame(player);
					} else {
						for (Player p : gameState.getInLobbyPlayers()) {
							  if (p == event.getWhoClicked()) {
								  if ((player.isOp() || gameState.getHost().contains(player) ))p.openInventory(GUIItems.getRoleSelectGUI());updateRoleInventory(player);
								  if (!p.isOp() && p.getOpenInventory() != null && p.getInventory() != null) p.closeInventory();
							  }									 
						  }
					}
					for (Player p : gameState.getInLobbyPlayers()) {
						updateDemonInventory(p);
					}
						if (sl) {
							player.openInventory(GUIItems.getSlayersSelectGUI());
							updateSlayerInventory(player);
						}
						if (d) {
							player.openInventory(GUIItems.getDemonSelectGUI());
							updateDemonInventory(player);
						}
						if (solo) {
							player.openInventory(GUIItems.getDSSoloSelectGUI());
							updateDSSoloInventory(player);
						}
					
					event.setCancelled(true);
					break;
				case "DemonSlayer -> §eSolo":
					if (item.getItemMeta() == null)return;
					if (item.getItemMeta().getDisplayName() == null)return;
					if (item.getType() == Material.AIR)return;
					if (item.isSimilar(GUIItems.getx())) {
						event.setCancelled(true);
						return;
					}
					if (!event.getWhoClicked().isOp() && !gameState.getHost().contains(player)) {
						event.setCancelled(true);
						return;
					}
					if (!player.isOp() && !gameState.getHost().contains(player))return;
					if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
						if (!item.isSimilar(GUIItems.getOrangeStainedGlassPane()) && !solo && !d && !sl && !mahr && !titans && !soldat && !item.isSimilar(GUIItems.getCantStartGameButton()) && !item.isSimilar(GUIItems.getStartGameButton())) {
							String name = item.getItemMeta().getDisplayName();
							Roles role = Roles.valueOf(name);
							if (action.equals(InventoryAction.PICKUP_ALL)) {
								gameState.addInAvailableRoles(role, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(role)+1));
							} else if (action.equals(InventoryAction.PICKUP_HALF)) {
								gameState.addInAvailableRoles(role, Math.max(0, gameState.getAvailableRoles().get(role)-1));
							}
						}
						if (item.isSimilar(GUIItems.getStartGameButton()) && gameState.gameCanLaunch) StartGame(player);
						
					} else {
						for (Player p : gameState.getInLobbyPlayers()) {
							  if (p == event.getWhoClicked()) {
								  if ((player.isOp() || gameState.getHost().contains(player) ))p.openInventory(GUIItems.getRoleSelectGUI());updateRoleInventory(player);
								  if (!p.isOp() && p.getOpenInventory() != null && p.getInventory() != null) p.closeInventory();
							  }									 
						  }
					}
					for (Player p : gameState.getInLobbyPlayers()) {
						updateDSSoloInventory(p);
					}
					if (sl) {
						player.openInventory(GUIItems.getSlayersSelectGUI());
						updateSlayerInventory(player);
					}
					if (d) {
						player.openInventory(GUIItems.getDemonSelectGUI());
						updateDemonInventory(player);
					}
					if (solo) {
						player.openInventory(GUIItems.getDSSoloSelectGUI());
						updateDSSoloInventory(player);
					}
					if (mahr) {
						player.openInventory(GUIItems.getMahrGui());
						updateMahrInventory(player);
					}
					if (titans) {
						player.openInventory(GUIItems.getSecretTitansGui());
						updateSecretTitansInventory(player);
					}
					if (soldat) {
						player.openInventory(GUIItems.getSecretSoldatGui());
						updateSoldatInventory(player);
					}
					event.setCancelled(true);
					break;
				case "Configuration de la partie":
					if (item.getType() != Material.AIR) {
						String name = item.getItemMeta().getDisplayName();
						if (item.getType().equals(Material.WATER_BUCKET)) {
							if (action.equals(InventoryAction.PICKUP_ALL)) {
								if (gameState.WaterEmptyTiming != 60) {
									gameState.WaterEmptyTiming+=1;
								}else {
									player.sendMessage("Timing maximal atteint !");
								}
							}else {
								if (gameState.WaterEmptyTiming != 10) {
									gameState.WaterEmptyTiming-=1;
								}else {
									player.sendMessage("Timing minimal atteint !");
								}
							}
						}
						if (item.getType().equals(Material.LAVA_BUCKET)) {
							if (action.equals(InventoryAction.PICKUP_ALL)) {
								if (gameState.LavaEmptyTiming != 60) {
									gameState.LavaEmptyTiming+=1;
								}else {
									player.sendMessage("Timing maximal atteint !");
								}
							}else {
								if (gameState.LavaEmptyTiming != 10) {
									gameState.LavaEmptyTiming-=1;
								}else {
									player.sendMessage("Timing minimal atteint !");
								}
							}
						}
						if (name.equals("Taille de la Bordure Max")) {
							if (action.equals(InventoryAction.PICKUP_ALL)) {
								gameState.maxBorderSize += 50;
							} else if (action.equals(InventoryAction.PICKUP_HALF)) {
								gameState.maxBorderSize -= 50;
							}
						}
						if (name.equals("Taille de la Bordure Min")) {
							if (action.equals(InventoryAction.PICKUP_ALL)) {
								gameState.minBorderSize += 50;
							} else if (action.equals(InventoryAction.PICKUP_HALF)) {
								gameState.minBorderSize -= 50;
							}
						}
						if (name.equals("Vitesse de la Bordure")) {
							if (action.equals(InventoryAction.PICKUP_ALL)) {
								gameState.borderSpeed += 0.1;
							} else if (action.equals(InventoryAction.PICKUP_HALF)) {
								gameState.borderSpeed -= 0.1;
							}
						}
						if (name.equals("Temps avant activation du PVP")) {
							if (action.equals(InventoryAction.PICKUP_ALL)) {
								gameState.pvpTimer += 60;
							} else if (action.equals(InventoryAction.PICKUP_HALF)) {
								gameState.pvpTimer -= 60;
							}
						}
						if (name.equals("Temps avant annonce des Roles")) {
							if (action.equals(InventoryAction.PICKUP_ALL)) {
								gameState.roleTimer+=60;
							} else if (action.equals(InventoryAction.PICKUP_HALF)) {
								gameState.roleTimer-=60;
							}
						}
						if (name.equals("Temps avant réduction de Bordure")) {
							if (action.equals(InventoryAction.PICKUP_ALL)) {
								gameState.shrinkTimer += 60;
							} else if (action.equals(InventoryAction.PICKUP_HALF)) {
								gameState.shrinkTimer -= 60;
							}
						}
						if (item.getType() == Material.REDSTONE) {
							if (action.equals(InventoryAction.PICKUP_ALL)) {
								if (gameState.TimingAssassin < 60*5) {
									gameState.TimingAssassin+=10;
								}else {
									player.sendMessage("Le timing de l'§cAssassin§r est au maximum");
								}
							} else if (action.equals(InventoryAction.PICKUP_HALF)) {
								if (gameState.TimingAssassin > 10) {
									gameState.TimingAssassin-=10;
								}else {
									player.sendMessage("Le timing de l'§cAssassin§r est au minimum");
								}
							}
						}
						if (item.getType().equals(Material.TNT)) {
							if (gameState.doTNTGrief) {
								gameState.doTNTGrief = false;
							} else {
								gameState.doTNTGrief = true;
							}
						}
						gameState.borderSpeed = Math.max(0.1f, Math.min(gameState.borderSpeed, 5));
						gameState.maxBorderSize = Math.max(50, Math.min(gameState.maxBorderSize, 2400));
						gameState.minBorderSize = Math.max(50, Math.min(gameState.minBorderSize, gameState.maxBorderSize));
						gameState.pvpTimer = Math.max(0, Math.min(gameState.pvpTimer, 40*60));
						gameState.roleTimer = Math.max(0, Math.min(gameState.roleTimer, 40*60));
						gameState.shrinkTimer = Math.max(0, Math.min(gameState.shrinkTimer, 60*60));
						if (item.isSimilar(GUIItems.getRoleInfo()) || item.isSimilar(GUIItems.getdisRoleInfo())) {
			    			if (player.isOp() || gameState.getHost().contains(player)) {
			    				if (gameState.roleinfo) {
			    					gameState.roleinfo = false;
			    					player.sendMessage("Désactivation de l'affichage du Role Info");
			    				} else {
			    					gameState.roleinfo = true;
			    					player.sendMessage("Activation de l'affichage du Role Info");
			    				}
			    			}
			    		}
						if (name.equals("Durée du jour (et de la nuit)")) {
							if (player.isOp() || gameState.getHost().contains(player)) {
								if (action.equals(InventoryAction.PICKUP_ALL)) {
									gameState.timeday+=10;
									player.updateInventory();
									updateConfigInventory(player);
								} else {
									if (action.equals(InventoryAction.PICKUP_HALF)) {
										gameState.timeday-=10;
										player.updateInventory();
										updateConfigInventory(player);
									}
								}
							}
						}
						
						if (item.isSimilar(GUIItems.getTabRoleInfo(gameState))) {
							if (player.isOp() || gameState.getHost().contains(player)) {
								if (action.equals(InventoryAction.PICKUP_ALL)) {
									if (!gameState.roletab) {
										player.sendMessage("Role dans le tab est désormais§6 activé");
										gameState.roletab = true;
									}
								} else {
									if (gameState.roletab) {
										player.sendMessage("Role dans le tab est désormais§6 désactivé");
										gameState.roletab = false;
									}
								}								
								player.updateInventory();
								updateConfigInventory(player);
							}
						}
						if (item.isSimilar(Items.geteclairmort())) {
							if (player.isOp() || gameState.getHost().contains(player)) {
								if (action.equals(InventoryAction.PICKUP_ALL)) {
									if (!gameState.morteclair) {
										player.sendMessage("Éclair à la mort est désormais§6 activé");
										gameState.morteclair = true;
									}
								} else {
									if (gameState.morteclair) {
										player.sendMessage("Éclair à la mort est désormais§6 désactivé");
										gameState.morteclair = false;
									}
								}
							}
						}
						if (item.getItemMeta().getDisplayName().equals("§fBijus")) {
							if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
								player.closeInventory();
								player.openInventory(Bukkit.createInventory(player, 9, "Configuration ->§6 Bijus"));
								openConfigBijusInventory(player);
							} else {
								if (gameState.BijusEnable) {
									gameState.BijusEnable = false;
								}else {
									gameState.BijusEnable = true;
								}
							}
						}
						if (item.getItemMeta().getDisplayName().equals("§cInfection")) {
							if (action.equals(InventoryAction.PICKUP_ALL)) {
								if (gameState.timewaitingbeinfected < 60*20) {
									gameState.timewaitingbeinfected+=5;
								}
							}else if (action.equals(InventoryAction.PICKUP_HALF)) {
								if (gameState.timewaitingbeinfected > 5) {
									gameState.timewaitingbeinfected-=5;
								}
							}
						}
					}
					for (Player p : gameState.getInLobbyPlayers()) {
		    			updateConfigInventory(p);
					}
					if (item.isSimilar(GUIItems.getSelectBackMenu())) {
								  if ((player.isOp() || gameState.getHost().contains(player)))player.openInventory(GUIItems.getAdminWatchGUI());
					 }
			    	event.setCancelled(true);
					break;
				case "§fAOT§7 ->§9 Mahr":
					if (item.getItemMeta() == null)return;
					if (item.getItemMeta().getDisplayName() == null)return;
					if (item.getType() == Material.AIR)return;
					if (item.isSimilar(GUIItems.getx())) {
						event.setCancelled(true);
						return;
					}
					if (!event.getWhoClicked().isOp() && !gameState.getHost().contains(player)) {
						event.setCancelled(true);
						return;
					}
					if (!player.isOp() && !gameState.getHost().contains(player))return;
					if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
						if (!item.isSimilar(GUIItems.getSBluetainedGlassPane()) && !solo && !d && !sl && !mahr && !titans && !soldat && !aotconfig &&!item.isSimilar(GUIItems.getCantStartGameButton()) && !item.isSimilar(GUIItems.getStartGameButton())) {
							String name = item.getItemMeta().getDisplayName();
							Roles role = Roles.valueOf(name);
							if (action.equals(InventoryAction.PICKUP_ALL)) {
								gameState.addInAvailableRoles(role, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(role)+1));
							} else if (action.equals(InventoryAction.PICKUP_HALF)) {
								gameState.addInAvailableRoles(role, Math.max(0, gameState.getAvailableRoles().get(role)-1));
							}else {
								event.setCancelled(true);
							}
						}
						if (item.isSimilar(GUIItems.getStartGameButton()) && gameState.gameCanLaunch) StartGame(player);
						
					} else {
						if ((player.isOp() || gameState.getHost().contains(player) ))player.openInventory(GUIItems.getSelectAOTInventory());updateAOTInventory(player);
					}
					for (Player p : gameState.getInLobbyPlayers()) {
						updateAOTSoloInventory(p);
					}
					if (mahr) {
						player.openInventory(GUIItems.getMahrGui());
						updateMahrInventory(player);
					}
					if (titans) {
						player.openInventory(GUIItems.getSecretTitansGui());
						updateSecretTitansInventory(player);
					}
					if (solo) {
						player.openInventory(GUIItems.getAOTSoloSelectGUI());
						updateAOTSoloInventory(player);
					}
					if (soldat) {
						player.openInventory(GUIItems.getSecretSoldatGui());
						updateSoldatInventory(player);
					}
					if (aotconfig) {
						player.openInventory(GUIItems.getConfigurationAOT());
						updateAOTConfiguration(player);
					}
					event.setCancelled(true);
					break;
				case "§fAOT§7 ->§c Titans":
					if (item.getItemMeta() == null)return;
					if (item.getItemMeta().getDisplayName() == null)return;
					if (item.getType() == Material.AIR)return;
					if (item.isSimilar(GUIItems.getx())) {
						event.setCancelled(true);
						return;
					}
					if (!event.getWhoClicked().isOp() && !gameState.getHost().contains(player)) {
						event.setCancelled(true);
						return;
					}
					if (!player.isOp() && !gameState.getHost().contains(player))return;
					if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
						if (!item.isSimilar(GUIItems.getRedStainedGlassPane()) && !solo && !d && !sl && !mahr && !titans && !soldat && !aotconfig &&!item.isSimilar(GUIItems.getCantStartGameButton()) && !item.isSimilar(GUIItems.getStartGameButton())) {
							for (Roles roles : Roles.values()) {
								if (item.getItemMeta().getDisplayName().contains(roles.getItem().getItemMeta().getDisplayName())) {
									if (action.equals(InventoryAction.PICKUP_ALL)) {
										gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
										break;
									} else if (action.equals(InventoryAction.PICKUP_HALF)) {
										gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
										break;
									}
								}
							}
						}
						if (item.isSimilar(GUIItems.getStartGameButton()) && gameState.gameCanLaunch) StartGame(player);
						
					} else {
						if ((player.isOp() || gameState.getHost().contains(player) ))player.openInventory(GUIItems.getSelectAOTInventory());updateAOTInventory(player);
					}
					for (Player p : gameState.getInLobbyPlayers()) {
						updateDSSoloInventory(p);
					}
					if (mahr) {
						player.openInventory(GUIItems.getMahrGui());
						updateMahrInventory(player);
					}
					if (titans) {
						player.openInventory(GUIItems.getSecretTitansGui());
						updateSecretTitansInventory(player);
					}
					if (solo) {
						player.openInventory(GUIItems.getAOTSoloSelectGUI());
						updateAOTSoloInventory(player);
					}
					if (soldat) {
						player.openInventory(GUIItems.getSecretSoldatGui());
						updateSoldatInventory(player);
					}
					if (aotconfig) {
						player.openInventory(GUIItems.getConfigurationAOT());
						updateAOTConfiguration(player);
					}
					event.setCancelled(true);
					break;
				case "§fAOT§7 ->§a Soldats":
					if (item.getItemMeta() == null)return;
					if (item.getItemMeta().getDisplayName() == null)return;
					if (item.getType() == Material.AIR)return;
					if (item.isSimilar(GUIItems.getx())) {
						event.setCancelled(true);
						return;
					}
					if (!event.getWhoClicked().isOp() && !gameState.getHost().contains(player)) {
						event.setCancelled(true);
						return;
					}
					if (!player.isOp() && !gameState.getHost().contains(player))return;
					if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
						if (!item.isSimilar(GUIItems.getGreenStainedGlassPane()) && !solo && !d && !sl && !mahr && !titans && !soldat && !aotconfig &&!item.isSimilar(GUIItems.getCantStartGameButton()) && !item.isSimilar(GUIItems.getStartGameButton())) {
							String name = item.getItemMeta().getDisplayName();
							Roles role = Roles.valueOf(name);
							if (action.equals(InventoryAction.PICKUP_ALL)) {
								gameState.addInAvailableRoles(role, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(role)+1));
							} else if (action.equals(InventoryAction.PICKUP_HALF)) {
								gameState.addInAvailableRoles(role, Math.max(0, gameState.getAvailableRoles().get(role)-1));
							}else {
								event.setCancelled(true);
							}
						}
						if (item.isSimilar(GUIItems.getStartGameButton()) && gameState.gameCanLaunch) StartGame(player);
						
					} else {
						if ((player.isOp() || gameState.getHost().contains(player) ))player.openInventory(GUIItems.getSelectAOTInventory());updateAOTInventory(player);
					}
					if (mahr) {
						player.openInventory(GUIItems.getMahrGui());
						updateMahrInventory(player);
					}
					if (titans) {
						player.openInventory(GUIItems.getSecretTitansGui());
						updateSecretTitansInventory(player);
					}
					if (solo) {
						player.openInventory(GUIItems.getAOTSoloSelectGUI());
						updateAOTSoloInventory(player);
					}
					if (soldat) {
						player.openInventory(GUIItems.getSecretSoldatGui());
						updateSoldatInventory(player);
					}
					if (aotconfig) {
						player.openInventory(GUIItems.getConfigurationAOT());
						updateAOTConfiguration(player);
					}
					event.setCancelled(true);
					break;
				case "Configuration -> AOT":
					if (!item.hasItemMeta())return;
					if (!item.getItemMeta().hasDisplayName())return;
					if (item.getType() == Material.AIR)return;
					String name = item.getItemMeta().getDisplayName();
					if (name.equals("§rCooldown Equipement Tridimentionnel")) {
						if (action.equals(InventoryAction.PICKUP_ALL)) {
							gameState.TridiCooldown+=1;
						}else {
							if (gameState.TridiCooldown > 0) {
								gameState.TridiCooldown-=1;
							}
						}
					}
					if (name.equals("§rEquipement Tridimentionnel")){
						if (gameState.rod) {
							gameState.rod = false;
						}else {
							gameState.rod = true;
						}
					}
					if (name.equals("§r§6Lave§f pour les titans (transformé)")) {
                        gameState.LaveTitans = !gameState.LaveTitans;
					}
					if (item.isSimilar(GUIItems.getSelectBackMenu())) {
						player.openInventory(GUIItems.getMahrGui());
						updateMahrInventory(player);
					}
					updateAOTConfiguration(player);
					event.setCancelled(true);
					break;
				case "§fRoles§7 ->§6 DemonSlayer":
					if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
						if (sl || d || solo) {
							if (sl) {
								player.openInventory(GUIItems.getSlayersSelectGUI());
								updateSlayerInventory(player);
							}
							if (d) {
								player.openInventory(GUIItems.getDemonSelectGUI());
								updateDemonInventory(player);
							}
							if (solo) {
								player.openInventory(GUIItems.getDSSoloSelectGUI());
								updateDSSoloInventory(player);
							}
						}
					}else {
						player.openInventory(GUIItems.getRoleSelectGUI());
						updateRoleInventory(player);
					}
					event.setCancelled(true);
					break;
				case "§fAOT§7 -> §eSolo":
					if (item.getItemMeta() == null)return;
					if (item.getItemMeta().getDisplayName() == null)return;
					if (item.getType() == Material.AIR)return;
					if (item.isSimilar(GUIItems.getx())) {
						event.setCancelled(true);
						return;
					}
					if (!event.getWhoClicked().isOp() && !gameState.getHost().contains(player)) {
						event.setCancelled(true);
						return;
					}
					if (!player.isOp() && !gameState.getHost().contains(player))return;
					if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
						if (!item.isSimilar(GUIItems.getOrangeStainedGlassPane()) && !solo && !d && !sl && !mahr && !titans && !soldat && !item.isSimilar(GUIItems.getCantStartGameButton()) && !item.isSimilar(GUIItems.getStartGameButton())) {
							for (Roles roles : Roles.values()) {
								if (item.getItemMeta().getDisplayName().equalsIgnoreCase(roles.getItem().getItemMeta().getDisplayName())) {
									if (action.equals(InventoryAction.PICKUP_ALL)) {
										gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
									} else if (action.equals(InventoryAction.PICKUP_HALF)) {
										gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
									}
								}
							}
						}
						if (item.isSimilar(GUIItems.getStartGameButton()) && gameState.gameCanLaunch) StartGame(player);
						
					} else {
						for (Player p : gameState.getInLobbyPlayers()) {
							  if (p == event.getWhoClicked()) {
								  if ((player.isOp() || gameState.getHost().contains(player) ))p.openInventory(GUIItems.getRoleSelectGUI());updateRoleInventory(player);
								  if (!p.isOp() && p.getOpenInventory() != null && p.getInventory() != null) p.closeInventory();
							  }									 
						  }
					}
					for (Player p : gameState.getInLobbyPlayers()) {
						updateDSSoloInventory(p);
					}
					if (solo) {
						player.openInventory(GUIItems.getDSSoloSelectGUI());
						updateDSSoloInventory(player);
					}
					if (mahr) {
						player.openInventory(GUIItems.getMahrGui());
						updateMahrInventory(player);
					}
					if (titans) {
						player.openInventory(GUIItems.getSecretTitansGui());
						updateSecretTitansInventory(player);
					}
					if (soldat) {
						player.openInventory(GUIItems.getSecretSoldatGui());
						updateSoldatInventory(player);
					}
					event.setCancelled(true);
					break;
				case "§fRoles§7 ->§6 AOT":
					if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
						if (solo || mahr || titans || soldat) {
							if (solo) {
								player.openInventory(GUIItems.getAOTSoloSelectGUI());
								updateAOTSoloInventory(player);
							}
							if (mahr) {
								player.openInventory(GUIItems.getMahrGui());
								updateMahrInventory(player);
							}
							if (titans) {
								player.openInventory(GUIItems.getSecretTitansGui());
								updateSecretTitansInventory(player);
							}
							if (soldat) {
								player.openInventory(GUIItems.getSecretSoldatGui());
								updateSoldatInventory(player);
							}
						}
					}else {
						player.openInventory(GUIItems.getRoleSelectGUI());
						updateRoleInventory(player);
					}
					event.setCancelled(true);
					break;
				case "Configuration ->§6 Bijus":
					if (!item.hasItemMeta())return;
					if (item.isSimilar(GUIItems.getSelectBackMenu())) {
						player.openInventory(GUIItems.getConfigSelectGUI());
						updateConfigInventory(player);
					} else {
						for (Bijus bijus : Bijus.values()) {
							if (item.isSimilar(bijus.getBiju().getItemInMenu())) {
								if (bijus.isEnable()) {
									bijus.setEnable(false);
								} else {
									bijus.setEnable(true);
								}
							}
						}
					}
					openConfigBijusInventory(player);
					event.setCancelled(true);
					break;
				case "§aNaruto§7 ->§c Akatsuki":
					if (!item.hasItemMeta())return;
					if (!item.getItemMeta().hasDisplayName())return;
					if (item.isSimilar(GUIItems.getSelectBackMenu())) {
						player.openInventory(GUIItems.getRoleSelectGUI());
						updateRoleInventory(player);
					} else {
						if (gameState.getHost().contains(player) || player.isOp()) {
							for (Roles roles : Roles.values()) {
								if (item.getItemMeta().getDisplayName().contains(roles.getItem().getItemMeta().getDisplayName())) {
									if (action.equals(InventoryAction.PICKUP_ALL)) {
										gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
									} else if (action.equals(InventoryAction.PICKUP_HALF)) {
										gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
									}
								}
							}
						}
					}
					event.setCancelled(true);
					break;
				case "§fRoles§7 ->§6 NS":
					if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
						if (item.isSimilar(GUIItems.getStartGameButton())) {
							if (gameState.gameCanLaunch) {
								HubListener.getInstance().StartGame(player);
							}
						} else {
							if (solo) {
								player.openInventory(GUIItems.getSelectNSSoloInventory());
								updateNSSoloInventory(player);
							}
							if (akatsuki) {
								player.openInventory(GUIItems.getSelectAkatsukiInventory());
								updateNSAkatsukiInventory(player);
							}
							if (orochimaru) {
								player.openInventory(GUIItems.getSelectOrochimaruInventory());
								updateNSOrochimaruInventory(player);
							}
							if (shinobi) {
								player.openInventory(GUIItems.getSelectNSShinobiInventory());
								updateNSShinobiInventory(player);
							}
						}
					}else {
						player.openInventory(GUIItems.getRoleSelectGUI());
						updateRoleInventory(player);
					}
					event.setCancelled(true);
					break;
				case "§aNaruto§7 ->§5 Orochimaru":
					if (!item.getItemMeta().hasDisplayName())return;
					if (item.isSimilar(GUIItems.getSelectBackMenu())) {
						player.openInventory(GUIItems.getRoleSelectGUI());
						updateRoleInventory(player);
					} else {
						if (gameState.getHost().contains(player) || player.isOp()) {
							for (Roles roles : Roles.values()) {
								if (item.getItemMeta().getDisplayName().contains(roles.getItem().getItemMeta().getDisplayName())) {
									if (action.equals(InventoryAction.PICKUP_ALL)) {
										gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
									} else if (action.equals(InventoryAction.PICKUP_HALF)) {
										gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
									}
								}
							}
						}
					}
					event.setCancelled(true);
					break;
				case "§aNaruto§7 ->§e Solo":
					if (!item.hasItemMeta())return;
					if (!item.getItemMeta().hasDisplayName())return;
					if (item.isSimilar(GUIItems.getSelectBackMenu())) {
						player.openInventory(GUIItems.getRoleSelectGUI());
						updateRoleInventory(player);
					} else {
						if (item.isSimilar(GUIItems.getSelectJubiButton())) {
							player.openInventory(GUIItems.getSelectNSJubiInventory());
							updateNSJubiInventory(player);
							event.setCancelled(true);
							return;
						}
						if (brume) {
							player.openInventory(GUIItems.getSelectNSBrumeInventory());
							updateNSBrumeInventory(player);
							event.setCancelled(true);
							return;
						}
						if (kumo){
							player.openInventory(Bukkit.createInventory(player, 54, "§eSolo§7 ->§6 Kumogakure"));
							updateNSKumogakure(player);
							event.setCancelled(true);
							return;
						}
						if (gameState.getHost().contains(player) || player.isOp()) {
							for (Roles roles : Roles.values()) {
								if (item.getItemMeta().getDisplayName().contains(roles.getItem().getItemMeta().getDisplayName())) {
									if (action.equals(InventoryAction.PICKUP_ALL)) {
										gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
									} else if (action.equals(InventoryAction.PICKUP_HALF)) {
										gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
									}
								}
							}
						}
					}
					event.setCancelled(true);
					break;
				case "§eSolo§7 ->§d Jubi":
					if (!item.hasItemMeta())return;
					if (!item.getItemMeta().hasDisplayName())return;
					if (item.isSimilar(GUIItems.getSelectBackMenu())) {
						player.openInventory(GUIItems.getSelectNSSoloInventory());
						updateNSSoloInventory(player);
					} else {
						if (gameState.getHost().contains(player) || player.isOp()) {
							for (Roles roles : Roles.values()) {
								if (item.getItemMeta().getDisplayName().contains(roles.getItem().getItemMeta().getDisplayName())) {
									if (action.equals(InventoryAction.PICKUP_ALL)) {
										gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
									} else if (action.equals(InventoryAction.PICKUP_HALF)) {
										gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
									}
								}
							}
						}
					}
					event.setCancelled(true);
					break;
				case "§eSolo§7 ->§b Zabuza et Haku":
					if (!item.hasItemMeta())return;
					if (!item.getItemMeta().hasDisplayName())return;
					if (item.isSimilar(GUIItems.getSelectBackMenu())) {
						player.openInventory(GUIItems.getSelectNSSoloInventory());
						updateNSSoloInventory(player);
					} else {
						if (gameState.getHost().contains(player) || player.isOp()) {
							for (Roles roles : Roles.values()) {
								if (item.getItemMeta().getDisplayName().contains(roles.getItem().getItemMeta().getDisplayName())) {
									if (action.equals(InventoryAction.PICKUP_ALL)) {
										gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
									} else if (action.equals(InventoryAction.PICKUP_HALF)) {
										gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
									}
								}
							}
						}
					}
					event.setCancelled(true);
					break;
				case "§aNaruto§7 ->§a Shinobi":
					if (!item.hasItemMeta())return;
					if (!item.getItemMeta().hasDisplayName())return;
					if (item.isSimilar(GUIItems.getSelectBackMenu())) {
						player.openInventory(GUIItems.getRoleSelectGUI());
						updateRoleInventory(player);
					} else {
						if (gameState.getHost().contains(player) || player.isOp()) {
							for (Roles roles : Roles.values()) {
								if (item.getItemMeta().getDisplayName().contains(roles.getItem().getItemMeta().getDisplayName())) {
									if (action.equals(InventoryAction.PICKUP_ALL)) {
										gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
									} else if (action.equals(InventoryAction.PICKUP_HALF)) {
										gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
									}
								}
							}
						}
					}
					event.setCancelled(true);
					break;
				case "§eSolo§7 ->§6 Kumogakure":
					if (!item.hasItemMeta())return;
					if (!item.getItemMeta().hasDisplayName())return;
					if (item.isSimilar(GUIItems.getSelectBackMenu())) {
						player.openInventory(GUIItems.getSelectNSSoloInventory());
						updateNSSoloInventory(player);
					} else {
						if (gameState.getHost().contains(player) || player.isOp()) {
							for (Roles roles : Roles.values()) {
								if (item.getItemMeta().getDisplayName().contains(roles.getItem().getItemMeta().getDisplayName())) {
									if (action.equals(InventoryAction.PICKUP_ALL)) {
										gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
									} else if (action.equals(InventoryAction.PICKUP_HALF)) {
										gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
									}
								}
							}
						}
					}
					event.setCancelled(true);
					break;
		    	default:
		    		break;
				}
				for (Player p : gameState.getInLobbyPlayers()) {
	    			updateAdminInventory(p);
	    			updateScenarioInventory(p);
	    			updateSelectInventory(p);
	    			updateSlayerInventory(p);
	    			updateDSSoloInventory(p);
	    			updateDemonInventory(p);
	    			updateMahrInventory(p);
	    			updateSecretTitansInventory(p);
	    			updateSoldatInventory(p);
	    			updateEventInventory(p);
	    			updateAOTConfiguration(p);
	    			updateDSInventory(p);
	    			updateAOTConfiguration(p);
	    			updateConfigInventory(p);
	    			updateAOTSoloInventory(p);
	    			updateNSAkatsukiInventory(p);
	    			updateNSInventory(p);
	    			updateNSOrochimaruInventory(p);
	    			updateNSSoloInventory(p);
	    			updateNSJubiInventory(p);
	    			updateNSBrumeInventory(p);
	    			updateNSShinobiInventory(p);
					updateNSKumogakure(p);
				}
		    }
		}
	}
	private void updateNSKumogakure(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equals("§eSolo§7 ->§6 Kumogakure")) {
					inv.clear();
					ItemStack glass = GUIItems.getOrangeStainedGlassPane();
					inv.setItem(0, glass);
					inv.setItem(1, glass);
					inv.setItem(9, glass);//haut gauche

					inv.setItem(4, GUIItems.getSelectBackMenu());
					if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
					if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

					inv.setItem(7, glass);//haut droite
					inv.setItem(8, glass);
					inv.setItem(17, glass);

					inv.setItem(45, glass);
					inv.setItem(46, glass);
					inv.setItem(36, glass);//bas gauche

					inv.setItem(44, glass);
					inv.setItem(52, glass);
					inv.setItem(53, glass);//bas droite

					inv.setItem(2, new ItemBuilder(Material.ANVIL).toItemStack());
					inv.setItem(3, new ItemBuilder(Material.ANVIL).toItemStack());
					inv.setItem(5, new ItemBuilder(Material.ANVIL).toItemStack());
					for (Roles roles : Roles.values()) {
						if (roles.getTeam() == TeamList.Kumogakure) {
							String l1 = "";
							if (gameState.getAvailableRoles().get(roles) > 0) {
								l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
							} else {
								l1 = "§c(0)";
							}
							inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
						}
					}
					inv.setItem(2, new ItemBuilder(Material.AIR).toItemStack());
					inv.setItem(3, new ItemBuilder(Material.AIR).toItemStack());
					inv.setItem(5, new ItemBuilder(Material.AIR).toItemStack());
				}
			}
		}
		player.updateInventory();
		gameState.updateGameCanLaunch();
	}
	private void updateNSShinobiInventory(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equals("§aNaruto§7 ->§a Shinobi")) {
					inv.clear();
					ItemStack glass = GUIItems.getGreenStainedGlassPane();
					inv.setItem(0, glass);
					inv.setItem(1, glass);
					inv.setItem(9, glass);//haut gauche
					
					inv.setItem(4, GUIItems.getSelectBackMenu());
				if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
				if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());
					
					inv.setItem(7, glass);//haut droite
					inv.setItem(8, glass);
					inv.setItem(17, glass);					
					
					inv.setItem(45, glass);
					inv.setItem(46, glass);
					inv.setItem(36, glass);//bas gauche
					
					inv.setItem(44, glass);
					inv.setItem(52, glass);
					inv.setItem(53, glass);//bas droite
					
					inv.setItem(2, new ItemBuilder(Material.ANVIL).toItemStack());
					inv.setItem(3, new ItemBuilder(Material.ANVIL).toItemStack());
					inv.setItem(5, new ItemBuilder(Material.ANVIL).toItemStack());
					inv.setItem(18, new ItemBuilder(Material.ANVIL).toItemStack());
					for (Roles roles : Roles.values()) {
						if (roles.getTeam() == TeamList.Shinobi) {
							String l1 = "";
							if (gameState.getAvailableRoles().get(roles) > 0) {
								l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
							} else {
								l1 = "§c(0)";
							}
							inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
						}
					}
					inv.setItem(2, new ItemBuilder(Material.AIR).toItemStack());
					inv.setItem(3, new ItemBuilder(Material.AIR).toItemStack());
					inv.setItem(5, new ItemBuilder(Material.AIR).toItemStack());
					inv.setItem(18, new ItemBuilder(Material.AIR).toItemStack());
				}
			}
		}
		player.updateInventory();
		gameState.updateGameCanLaunch();
	}
	private void updateNSBrumeInventory(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equals("§eSolo§7 ->§b Zabuza et Haku")) {
					inv.clear();
					ItemStack glass = GUIItems.getPinkStainedGlassPane();
					inv.setItem(0, glass);
					inv.setItem(1, glass);
					inv.setItem(9, glass);//haut gauche
					
					inv.setItem(4, GUIItems.getSelectBackMenu());
				if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
				if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());
					
					inv.setItem(7, glass);//haut droite
					inv.setItem(8, glass);
					inv.setItem(17, glass);					
					
					inv.setItem(45, glass);
					inv.setItem(46, glass);
					inv.setItem(36, glass);//bas gauche
					
					inv.setItem(44, glass);
					inv.setItem(52, glass);
					inv.setItem(53, glass);//bas droite
					
					inv.setItem(2, new ItemBuilder(Material.ANVIL).toItemStack());
					inv.setItem(3, new ItemBuilder(Material.ANVIL).toItemStack());
					inv.setItem(5, new ItemBuilder(Material.ANVIL).toItemStack());
					for (Roles roles : Roles.values()) {
						if (roles.getTeam() == TeamList.Zabuza_et_Haku) {
							String l1 = "";
							if (gameState.getAvailableRoles().get(roles) > 0) {
								l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
							} else {
								l1 = "§c(0)";
							}
							inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
						}
					}
					inv.setItem(2, new ItemBuilder(Material.AIR).toItemStack());
					inv.setItem(3, new ItemBuilder(Material.AIR).toItemStack());
					inv.setItem(5, new ItemBuilder(Material.AIR).toItemStack());
				}
			}
		}
		player.updateInventory();
		gameState.updateGameCanLaunch();
	}
	private void openConfigBijusInventory(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equalsIgnoreCase("Configuration ->§6 Bijus")) {
					inv.setItem(8, GUIItems.getSelectBackMenu());
					for (Bijus bijus : Bijus.values()) {
						inv.addItem( bijus.getBiju().getItemInMenu());
					}
				}
			}
		}
	}
	private void updateEventInventory(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equals("§fConfiguration§7 -> §6Événements")) {
					inv.clear();
					inv.setItem(8, GUIItems.getSelectBackMenu());
					for (Events e : Events.values()) {
						ItemStack item = null;
						if (e == Events.DemonKingTanjiro) {
							if (gameState.getAvailableEvents().contains(e)) {
								item = new ItemBuilder(Material.BLAZE_ROD)
										.addEnchant(Enchantment.ARROW_FIRE, 1)
										.hideEnchantAttributes()
										.setName(e.getName())
										.setLore("§fTiming d'apparition:§6 "+StringUtils.secondsTowardsBeautifulinScoreboard(gameState.DKminTime),
												"§a+1m§f (Clique gauche)",
												"§c-1m§f (Clique droit)",
												"§a+1%§f (Shift + Clique)",
												"§c-1%§f (Drop)",
												"§fPourcentage actuelle:§b "+gameState.DKTProba+"%")
										.toItemStack();
								inv.addItem(item);
							}
						}
						if (e == Events.AkazaVSKyojuro) {
							if (gameState.getAvailableEvents().contains(e)) {
								item = new ItemBuilder(Material.IRON_SWORD)
										.addEnchant(Enchantment.ARROW_DAMAGE, 1)
										.hideAllAttributes()
										.setName(e.getName())
										.setLore("§fTiming d'apparition:§6 "+StringUtils.secondsTowardsBeautifulinScoreboard(gameState.AkazaVsKyojuroTime),
												"§a+1m§f (Clique gauche)",
												"§c-1m§f (Clique droit)",
												"§a+1%§f (Shift + Clique)",
												"§c-1%§f (Drop)",
												"§fPourcentage actuelle:§b "+gameState.AkazaVSKyojuroProba+"%")
										.toItemStack();
								inv.addItem(item);
							}
						}
						if (e == Events.Alliance) {
							if (gameState.getAvailableEvents().contains(e)) {
								item = new ItemBuilder(Material.LAVA_BUCKET)
										.addEnchant(Enchantment.ARROW_DAMAGE, 1)
										.hideAllAttributes()
										.setName(e.getName())
										.setLore("§fTiming d'apparition:§6 "+StringUtils.secondsTowardsBeautifulinScoreboard(gameState.AllianceTime),
												"§a+1m§f (Clique gauche)",
												"§c-1m§f (Clique droit)",
												"§a+1%§f (Shift + Clique)",
												"§c-1%§f (Drop)",
												"§fPourcentage actuelle:§b "+gameState.AllianceProba+"%")
										.toItemStack();
								inv.addItem(item);
							}
						}
					}
				}
			}
		}
	}
	private HashMap<Roles, Integer> availableRoles = new HashMap<>();
	public HashMap<Roles, Integer> getAvailableRoles() {return availableRoles;}
	public void setAvailableRoles(HashMap<Roles, Integer> availableRoles) {this.availableRoles = availableRoles;}
	public void addInAvailableRoles(Roles role, Integer nmb) {availableRoles.put(role, nmb);}
	public void delInAvailableRoles(Roles role) {availableRoles.remove(role);}
	public void updateDemonInventory(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equals("DemonSlayer -> §cDémons")) {
					inv.clear();	
					inv.setItem(0, GUIItems.getRedStainedGlassPane());
					inv.setItem(1, GUIItems.getRedStainedGlassPane());
					inv.setItem(9, GUIItems.getRedStainedGlassPane());//haut gauche
					
				//	inv.setItem(2, GUIItems.getx());
					inv.setItem(3, GUIItems.getSelectSlayersButton());//haut milleu
					inv.setItem(4, GUIItems.getSelectBackMenu());
					inv.setItem(5, GUIItems.getSelectSoloButton());
					if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
					if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());
					inv.setItem(7, GUIItems.getRedStainedGlassPane());//haut droite
					inv.setItem(8, GUIItems.getRedStainedGlassPane());
					inv.setItem(17, GUIItems.getRedStainedGlassPane());					
					
					inv.setItem(45, GUIItems.getRedStainedGlassPane());
					inv.setItem(46, GUIItems.getRedStainedGlassPane());
					inv.setItem(36, GUIItems.getRedStainedGlassPane());//bas gauche
					
					inv.setItem(44, GUIItems.getRedStainedGlassPane());
					inv.setItem(52, GUIItems.getRedStainedGlassPane());
					inv.setItem(53, GUIItems.getRedStainedGlassPane());//bas droite
					
					inv.setItem(2, new ItemBuilder(Material.ANVIL).toItemStack());
					inv.setItem(18, new ItemBuilder(Material.ANVIL).toItemStack());
					inv.setItem(27, new ItemBuilder(Material.ANVIL).toItemStack());
					inv.setItem(26, new ItemBuilder(Material.ANVIL).toItemStack());
					inv.setItem(35, new ItemBuilder(Material.ANVIL).toItemStack());
					for (Roles roles : Roles.values()) {
						if (roles.getTeam() == TeamList.Demon) {
							String l1 = "";
							if (gameState.getAvailableRoles().get(roles) > 0) {
								l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
							} else {
								l1 = "§c(0)";
							}
							inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
						}
					}
					inv.setItem(2, new ItemBuilder(Material.AIR).toItemStack());
					inv.setItem(18, new ItemBuilder(Material.AIR).toItemStack());
					inv.setItem(27, new ItemBuilder(Material.AIR).toItemStack());
					inv.setItem(26, new ItemBuilder(Material.AIR).toItemStack());
					inv.setItem(35, new ItemBuilder(Material.AIR).toItemStack());
				}
			}
		}
		player.updateInventory();
		gameState.updateGameCanLaunch();
	}
	private void updateAOTSoloInventory(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equals("§fAOT§7 -> §eSolo")) {
					inv.clear();	
					inv.setItem(0, GUIItems.getOrangeStainedGlassPane());
					inv.setItem(1, GUIItems.getOrangeStainedGlassPane());
					inv.setItem(9, GUIItems.getOrangeStainedGlassPane());//haut gauche
					
					inv.setItem(2, GUIItems.getSelectMahrButton());
					inv.setItem(3, GUIItems.getSelectTitanButton());//haut milleu
					inv.setItem(4, GUIItems.getSelectBackMenu());
					inv.setItem(5, GUIItems.getSelectSoldatButton());
				if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
				if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());
					
					inv.setItem(7, GUIItems.getOrangeStainedGlassPane());//haut droite
					inv.setItem(8, GUIItems.getOrangeStainedGlassPane());
					inv.setItem(17, GUIItems.getOrangeStainedGlassPane());					
					
					inv.setItem(45, GUIItems.getOrangeStainedGlassPane());
					inv.setItem(46, GUIItems.getOrangeStainedGlassPane());
					inv.setItem(36, GUIItems.getOrangeStainedGlassPane());//bas gauche
					
					inv.setItem(44, GUIItems.getOrangeStainedGlassPane());
					inv.setItem(52, GUIItems.getOrangeStainedGlassPane());
					inv.setItem(53, GUIItems.getOrangeStainedGlassPane());//bas droite
					for (Roles roles : Roles.values()) {
						if (roles.getTeam() == TeamList.Solo && roles.getMdj().equals("aot")) {
							String l1 = "";
							if (gameState.getAvailableRoles().get(roles) > 0) {
								l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
							} else {
								l1 = "§c(0)";
							}
							inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
						}
					}
				}
			}
		}
		player.updateInventory();
		gameState.updateGameCanLaunch();
	}
	private void updateDSSoloInventory(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equals("DemonSlayer -> §eSolo")) {
					inv.clear();	
					inv.setItem(0, GUIItems.getOrangeStainedGlassPane());
					inv.setItem(1, GUIItems.getOrangeStainedGlassPane());
					inv.setItem(9, GUIItems.getOrangeStainedGlassPane());//haut gauche

					inv.setItem(3, GUIItems.getSelectDemonButton());//haut milleu
					inv.setItem(4, GUIItems.getSelectBackMenu());
					inv.setItem(5, GUIItems.getSelectSlayersButton());
				if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
				if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());
					
					inv.setItem(7, GUIItems.getOrangeStainedGlassPane());//haut droite
					inv.setItem(8, GUIItems.getOrangeStainedGlassPane());
					inv.setItem(17, GUIItems.getOrangeStainedGlassPane());					
					
					inv.setItem(45, GUIItems.getOrangeStainedGlassPane());
					inv.setItem(46, GUIItems.getOrangeStainedGlassPane());
					inv.setItem(36, GUIItems.getOrangeStainedGlassPane());//bas gauche
					
					inv.setItem(44, GUIItems.getOrangeStainedGlassPane());
					inv.setItem(52, GUIItems.getOrangeStainedGlassPane());
					inv.setItem(53, GUIItems.getOrangeStainedGlassPane());//bas droite
					
					inv.setItem(2, new ItemBuilder(Material.ANVIL).toItemStack());
					for (Roles roles : Roles.values()) {
						if (roles.getTeam() == TeamList.Solo && roles.getMdj().equals("ds")) {
							String l1 = "";
							if (gameState.getAvailableRoles().get(roles) > 0) {
								l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
							} else {
								l1 = "§c(0)";
							}
							inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
						}
					}
					inv.setItem(2, new ItemBuilder(Material.AIR).toItemStack());
				}
			}
		}
		player.updateInventory();
		gameState.updateGameCanLaunch();
	}
	public void updateSlayerInventory(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equals("DemonSlayer ->§a Slayers")) {
					inv.clear();	
					inv.setItem(0, GUIItems.getGreenStainedGlassPane());
					inv.setItem(1, GUIItems.getGreenStainedGlassPane());
					inv.setItem(9, GUIItems.getGreenStainedGlassPane());//haut gauche
					
				//	inv.setItem(2, GUIItems.getx());
					inv.setItem(3, GUIItems.getSelectDemonButton());//haut milleu
					inv.setItem(4, GUIItems.getSelectBackMenu());
					inv.setItem(5, GUIItems.getSelectSoloButton());
					if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
					if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());
					
					inv.setItem(7, GUIItems.getGreenStainedGlassPane());//haut droite
					inv.setItem(8, GUIItems.getGreenStainedGlassPane());
					inv.setItem(17, GUIItems.getGreenStainedGlassPane());
					
					inv.setItem(45, GUIItems.getGreenStainedGlassPane());
					inv.setItem(46, GUIItems.getGreenStainedGlassPane());
					inv.setItem(36, GUIItems.getGreenStainedGlassPane());//bas gauche
					
					inv.setItem(44, GUIItems.getGreenStainedGlassPane());
					inv.setItem(52, GUIItems.getGreenStainedGlassPane());
					inv.setItem(53, GUIItems.getGreenStainedGlassPane());//bas droite
					
					inv.setItem(2, new ItemBuilder(Material.ANVIL).toItemStack());
					inv.setItem(18, new ItemBuilder(Material.ANVIL).toItemStack());
					inv.setItem(27, new ItemBuilder(Material.ANVIL).toItemStack());
					inv.setItem(26, new ItemBuilder(Material.ANVIL).toItemStack());
					inv.setItem(35, new ItemBuilder(Material.ANVIL).toItemStack());
					for (Roles roles : Roles.values()) {
						if (roles.getTeam() == TeamList.Slayer) {
							String l1 = "";
							if (gameState.getAvailableRoles().get(roles) > 0) {
								l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
							} else {
								l1 = "§c(0)";
							}
							inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
						}
					}
					inv.setItem(2, new ItemBuilder(Material.AIR).toItemStack());
					inv.setItem(18, new ItemBuilder(Material.AIR).toItemStack());
					inv.setItem(27, new ItemBuilder(Material.AIR).toItemStack());
					inv.setItem(26, new ItemBuilder(Material.AIR).toItemStack());
					inv.setItem(35, new ItemBuilder(Material.AIR).toItemStack());
				}
			}
			
		}
		player.updateInventory();
		gameState.updateGameCanLaunch();
	}
	public void updateAdminInventory(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equals("§fConfiguration")) {
					if (gameState.gameCanLaunch) {
						inv.setItem(0, GUIItems.getGreenStainedGlassPane());
						inv.setItem(1, GUIItems.getGreenStainedGlassPane());
						inv.setItem(2, GUIItems.getGreenStainedGlassPane());
						inv.setItem(3, GUIItems.getGreenStainedGlassPane());
						inv.setItem(4, GUIItems.getGreenStainedGlassPane());
						inv.setItem(5, GUIItems.getGreenStainedGlassPane());
						inv.setItem(6, GUIItems.getGreenStainedGlassPane());
						inv.setItem(7, GUIItems.getGreenStainedGlassPane());
						inv.setItem(8, GUIItems.getGreenStainedGlassPane());
						inv.setItem(9, GUIItems.getGreenStainedGlassPane());
						inv.setItem(17, GUIItems.getGreenStainedGlassPane());
						inv.setItem(18, GUIItems.getGreenStainedGlassPane());
						inv.setItem(26, GUIItems.getGreenStainedGlassPane());
						inv.setItem(27, GUIItems.getGreenStainedGlassPane());
						inv.setItem(35, GUIItems.getGreenStainedGlassPane());
						inv.setItem(36, GUIItems.getGreenStainedGlassPane());
						inv.setItem(44, GUIItems.getGreenStainedGlassPane());
						inv.setItem(45, GUIItems.getGreenStainedGlassPane());
						inv.setItem(46, GUIItems.getGreenStainedGlassPane());
						inv.setItem(47, GUIItems.getGreenStainedGlassPane());
						inv.setItem(48, GUIItems.getGreenStainedGlassPane());
						inv.setItem(49, GUIItems.getGreenStainedGlassPane());
						inv.setItem(50, GUIItems.getGreenStainedGlassPane());
						inv.setItem(51, GUIItems.getGreenStainedGlassPane());
						inv.setItem(52, GUIItems.getGreenStainedGlassPane());
						inv.setItem(22, GUIItems.getStartGameButton());
					} else {
						inv.setItem(22, GUIItems.getCantStartGameButton());
						inv.setItem(0, GUIItems.getRedStainedGlassPane());
						inv.setItem(1, GUIItems.getRedStainedGlassPane());
						inv.setItem(2, GUIItems.getRedStainedGlassPane());
						inv.setItem(3, GUIItems.getRedStainedGlassPane());
						inv.setItem(4, GUIItems.getRedStainedGlassPane());
						inv.setItem(5, GUIItems.getRedStainedGlassPane());
						inv.setItem(6, GUIItems.getRedStainedGlassPane());
						inv.setItem(7, GUIItems.getRedStainedGlassPane());
						inv.setItem(8, GUIItems.getRedStainedGlassPane());
						inv.setItem(9, GUIItems.getRedStainedGlassPane());
						inv.setItem(17, GUIItems.getRedStainedGlassPane());
						inv.setItem(18, GUIItems.getRedStainedGlassPane());
						inv.setItem(26, GUIItems.getRedStainedGlassPane());
						inv.setItem(27, GUIItems.getRedStainedGlassPane());
						inv.setItem(35, GUIItems.getRedStainedGlassPane());
						inv.setItem(36, GUIItems.getRedStainedGlassPane());
						inv.setItem(44, GUIItems.getRedStainedGlassPane());
						inv.setItem(45, GUIItems.getRedStainedGlassPane());
						inv.setItem(46, GUIItems.getRedStainedGlassPane());
						inv.setItem(47, GUIItems.getRedStainedGlassPane());
						inv.setItem(48, GUIItems.getRedStainedGlassPane());
						inv.setItem(49, GUIItems.getRedStainedGlassPane());
						inv.setItem(50, GUIItems.getRedStainedGlassPane());
						inv.setItem(51, GUIItems.getRedStainedGlassPane());
						inv.setItem(52, GUIItems.getRedStainedGlassPane());
					}
					inv.setItem(53, GUIItems.getx());
					inv.setItem(10, GUIItems.getSelectRoleButton());
					inv.setItem(13, GUIItems.getPregen(gameState));
					inv.setItem(19, GUIItems.getSelectConfigButton());
					inv.setItem(31, GUIItems.getSelectScenarioButton());
					inv.setItem(28, GUIItems.getSelectInvsButton());
					inv.setItem(37, GUIItems.getSelectEventButton());
					if (AntiPvP.isAntipvplobby()) {
						inv.setItem(40, AntiPvP.getlobbypvp());
					} else {
						inv.setItem(40, AntiPvP.getnotlobbypvp());
					}
					inv.setItem(34, Chat.getColoritem());
					inv.setItem(43, GUIItems.getCrit(gameState));
				}
			}
		}
		player.updateInventory();
	}
	private void updateScenarioInventory(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equals("§fConfiguration§7 -> §6scenarios")) {
					int i = 0;
					for (Scenarios sc : Scenarios.values()){
						inv.setItem(i, sc.getScenarios().getAffichedItem());
						i++;
					}
					inv.setItem(26, GUIItems.getSelectBackMenu());
				}
			}
		}
		player.updateInventory();
	}
	private void updateSelectInventory(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equals("§fConfiguration§7 ->§6 Inventaire")) {
					inv.setItem(48, new ItemBuilder(Material.GOLDEN_APPLE, gameState.nmbGap).setName("§r§fNombre de pomme d'§eor").setLore(
							"§a+1§f (Clique gauche)",
							"§c-1§f (Clique droit)",
							"§r§fNombre actuelle:§e "+gameState.nmbGap)
							.toItemStack());
					inv.setItem(0, new ItemBuilder(Material.DIAMOND_HELMET).setLore(
							"§a+1§f (Clique gauche)",
							"§c-1§f (Clique droit)",
							"§r§fNiveau de protection:§b "+GameState.pc
							).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, GameState.pc)
							
							.toItemStack());
					inv.setItem(9, new ItemBuilder(Material.DIAMOND_CHESTPLATE).setLore(
							"§a+1§f (Clique gauche)",
							"§c-1§f (Clique droit)",
							"§r§fNiveau de protection:§b "+GameState.pch
							).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, GameState.pch)
							.toItemStack());
					inv.setItem(18, new ItemBuilder(Material.IRON_LEGGINGS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, GameState.pl)
							.setLore("§a+1§f (Clique gauche)",
									"§c-1§f (Clique droit)",
									"§r§fNiveau de protection:§b "+GameState.pl)
							.toItemStack());
					inv.setItem(27, new ItemBuilder(Material.DIAMOND_BOOTS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, GameState.pb)
							.setLore("§a+1§f (Clique gauche)",
									"§c-1§f (Clique droit)",
									"§r§fNiveau de protection:§b "+GameState.pb)
							.toItemStack());
					inv.setItem(45, GUIItems.getdiamondsword());
					inv.setItem(46, GUIItems.getblock());
					inv.setItem(47, GUIItems.getbow());
					inv.setItem(49, GUIItems.getEnderPearl());
					inv.setItem(50, GUIItems.getGoldenCarrot());
					inv.setItem(51, GUIItems.getlave());
					inv.setItem(52, GUIItems.geteau());
					inv.setItem(38, new ItemBuilder(Material.ARROW, gameState.nmbArrow).setName("§fFlèches").setLore("","§7Max:§c 64","§7Minimum:§c 1","§7Actuelle:§c "+gameState.nmbArrow).toItemStack());
					//inv.setItem(9, GUIItems.getx());
					
					inv.setItem(8, GUIItems.getSelectBackMenu());
				}
			}
		}
		player.updateInventory();
	}
	public void updateRoleInventory(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equals("§fConfiguration§7 ->§6 Roles")) {
					inv.clear();
						if (gameState.isAllMdjNull()) {
							inv.setItem(13, new ItemBuilder(Material.SIGN).setName("§7Aucun mode de jeux activé !").toItemStack());
						} else {
							if (gameState.getMdj() == MDJ.DS) {
								inv.setItem(13, GUIItems.getSelectDSButton());
							}
							if (gameState.getMdj() == MDJ.AOT) {
								inv.setItem(13, GUIItems.getSelectAOTButton());
							}
							if (gameState.getMdj() == MDJ.NS) {
								inv.setItem(13, GUIItems.getSelectNSButton());
							}
						}
						if (player.isOp() || gameState.getHost().contains(player)) {
							inv.setItem(25, new ItemBuilder(Material.BOOKSHELF).setName("Configuration du mode de jeu").toItemStack());
						}
					inv.setItem(26, GUIItems.getSelectBackMenu());
				}
			}
		}
		player.updateInventory();
		gameState.updateGameCanLaunch();
	}
	public void updateSelectMDJ(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equalsIgnoreCase("Séléction du mode de jeu")) {
					inv.clear();
					for (MDJ mdj : MDJ.values()) {
						inv.addItem(mdj.getItem());
					}
					inv.setItem(8, GUIItems.getSelectBackMenu());
				}
			}
		}
		player.updateInventory();
		gameState.updateGameCanLaunch();
	}
	public void updateDSInventory(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equals("§fRoles§7 ->§6 DemonSlayer")) {
					inv.clear();
					inv.setItem(11, GUIItems.getSelectSlayersButton());
					inv.setItem(13, GUIItems.getSelectDemonButton());
					inv.setItem(15, GUIItems.getSelectSoloButton());
					inv.setItem(26, GUIItems.getSelectBackMenu());
				}
			}
		}
		player.updateInventory();
		gameState.updateGameCanLaunch();
	}
	public void updateAOTInventory(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equals("§fRoles§7 ->§6 AOT")) {
					inv.clear();
					inv.setItem(10, GUIItems.getSelectMahrButton());
					inv.setItem(12, GUIItems.getSelectTitanButton());
					inv.setItem(14, GUIItems.getSelectSoldatButton());
					inv.setItem(16, GUIItems.getSelectSoloButton());
					inv.setItem(26, GUIItems.getSelectBackMenu());
				}
			}
		}
		player.updateInventory();
		gameState.updateGameCanLaunch();
	}
	public void updateNSInventory(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equals("§fRoles§7 ->§6 NS")) {
					inv.clear();
					inv.setItem(10, GUIItems.getSelectShinobiButton());
					inv.setItem(12, GUIItems.getSelectAkatsukiButton());
					inv.setItem(14, GUIItems.getSelectOrochimaruButton());
					inv.setItem(16, GUIItems.getSelectSoloButton());
					inv.setItem(26, GUIItems.getSelectBackMenu());
				}
			}
		}
		player.updateInventory();
		gameState.updateGameCanLaunch();
	}
	public void updateCutCleanInventory(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equals(GUIItems.getCutCleanConfigGUI().getTitle())) {
					inv.setItem(0, CutClean.getXpCharbon(gameState));
					inv.setItem(2, CutClean.getXpFer(gameState));
					inv.setItem(4, CutClean.getXpOr(gameState));
					inv.setItem(6, CutClean.getXpDiams(gameState));
					inv.setItem(8, GUIItems.getSelectBackMenu());
				}
			}
		}
		player.updateInventory();
	}
	private void updateConfigInventory(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equals("Configuration de la partie")) {
					inv.clear();
					ItemStack maxBorderSize = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
					ItemStack minBorderSize = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
					ItemStack borderSpeed = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7);
					ItemStack pvpTimer = new ItemStack(Material.IRON_SWORD);
					ItemStack roleTimer = new ItemStack(Material.SKULL_ITEM);
					ItemStack shrinkTimer = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 0);
					ItemStack daytime = new ItemStack(Material.WATCH);
					ItemStack infectTime = new ItemStack(Material.REDSTONE, 1);
					ItemMeta infectMeta = infectTime.getItemMeta();
					infectMeta.setDisplayName("Temp avant l'§cAssassin§r:");
					infectMeta.setLore(Arrays.asList(
							ChatColor.DARK_PURPLE+"[10s < "+StringUtils.secondsTowardsBeautiful(gameState.TimingAssassin)+" > 5m]",
							ChatColor.DARK_PURPLE+"Click Gauche: +10s",
							ChatColor.DARK_PURPLE+"Click Droit: -10s"));
					ItemStack waterTime = new ItemStack(Material.WATER_BUCKET);
					ItemMeta waterMeta = waterTime.getItemMeta();
					waterMeta.setDisplayName("Temp avant despawn de l'§bEau");
					waterMeta.setLore(Arrays.asList(
							"[10s < "+StringUtils.secondsTowardsBeautiful(gameState.WaterEmptyTiming)+" > 1m}",
							"Click Gauche: +1s",
							"Click Droit: -1s"));
					ItemStack lavaTime = new ItemStack(Material.LAVA_BUCKET);
					ItemMeta lavaMeta = lavaTime.getItemMeta();
					lavaMeta.setDisplayName("Temp avant despawn de la§6 Lave");
					lavaMeta.setLore(Arrays.asList(
							"[10s < "+StringUtils.secondsTowardsBeautiful(gameState.LavaEmptyTiming)+" > 1m}",
							"Click Gauche: +1s",
							"Click Droit: -1s"));
					
					ItemMeta maxBSMeta = maxBorderSize.getItemMeta();
						maxBSMeta.setDisplayName("Taille de la Bordure Max");
						maxBSMeta.setLore(Arrays.asList(
						ChatColor.DARK_PURPLE+"[50b < "+gameState.maxBorderSize+" > 2400b]",
						ChatColor.DARK_PURPLE+"Click Gauche : +50b",
						ChatColor.DARK_PURPLE+"Click Droit    : -50b"));

					ItemMeta minBSMeta = minBorderSize.getItemMeta();
						minBSMeta.setDisplayName("Taille de la Bordure Min");
						minBSMeta.setLore(Arrays.asList(
						ChatColor.DARK_PURPLE+"[50b < "+gameState.minBorderSize+"b > "+gameState.maxBorderSize+"b]",
						ChatColor.DARK_PURPLE+"Click Gauche : +50b",
						ChatColor.DARK_PURPLE+"Click Droit    : -50b"));
						
					ItemMeta BSMeta = borderSpeed.getItemMeta();
						BSMeta.setDisplayName("Vitesse de la Bordure");
						BSMeta.setLore(Arrays.asList(
						ChatColor.DARK_PURPLE+"[0.1b/s < "+gameState.borderSpeed+"b/s > 5.0b/s]",
						ChatColor.DARK_PURPLE+"Click Gauche : +0.1b/s",
						ChatColor.DARK_PURPLE+"Click Droit    : -0.1b/s"));
						
					ItemMeta PTMeta = pvpTimer.getItemMeta();
						PTMeta.setDisplayName("Temps avant activation du PVP");
						PTMeta.setLore(Arrays.asList(
						ChatColor.DARK_PURPLE+"[0m < "+gameState.pvpTimer/60+"m > 40m]",
						ChatColor.DARK_PURPLE+"Click Gauche : +1m",
						ChatColor.DARK_PURPLE+"Click Droit    : -1m"));
						
					ItemMeta RTMeta = roleTimer.getItemMeta();
						RTMeta.setDisplayName("Temps avant annonce des Roles");
						RTMeta.setLore(Arrays.asList(
						ChatColor.DARK_PURPLE+"[0m < "+gameState.roleTimer/60+"m > 40m]",
						ChatColor.DARK_PURPLE+"Click Gauche : +1m",
						ChatColor.DARK_PURPLE+"Click Droit    : -1m"));
						
					ItemMeta STMeta = shrinkTimer.getItemMeta();
						STMeta.setDisplayName("Temps avant réduction de Bordure");
						STMeta.setLore(Arrays.asList(
						ChatColor.DARK_PURPLE+"[0m < "+gameState.shrinkTimer/60+"m > 60m]",
						ChatColor.DARK_PURPLE+"Click Gauche : +1m",
						ChatColor.DARK_PURPLE+"Click Droit    : -1m"));
						
					ItemMeta DTMeta = daytime.getItemMeta();
					DTMeta.setDisplayName("Durée du jour (et de la nuit)");
					DTMeta.setLore(Collections.singletonList("§r§fTemp actuelle: " + ChatColor.GOLD + StringUtils.secondsTowardsBeautiful(gameState.timeday)));
					
					daytime.setItemMeta(DTMeta);
					maxBorderSize.setItemMeta(maxBSMeta);
					minBorderSize.setItemMeta(minBSMeta);
					borderSpeed.setItemMeta(BSMeta);
					pvpTimer.setItemMeta(PTMeta);
					roleTimer.setItemMeta(RTMeta);
					shrinkTimer.setItemMeta(STMeta);
					infectTime.setItemMeta(infectMeta);
					waterTime.setItemMeta(waterMeta);
					lavaTime.setItemMeta(lavaMeta);
					inv.addItem(maxBorderSize);
					inv.addItem(minBorderSize);
					inv.addItem(borderSpeed);
					inv.addItem(pvpTimer);
					inv.addItem(roleTimer);
					inv.addItem(shrinkTimer);
					if (gameState.roleinfo) {inv.addItem(GUIItems.getRoleInfo());} else {
						inv.addItem(GUIItems.getdisRoleInfo());
					}
					inv.addItem(daytime);
					inv.addItem(GUIItems.getTabRoleInfo(gameState));
					inv.addItem(Items.geteclairmort());
					inv.addItem(infectTime);
					inv.addItem(waterTime);
					inv.addItem(lavaTime);
					inv.addItem(new ItemBuilder(Material.NETHER_STAR).setName("§fBijus").setLore(gameState.BijusEnable ? "§aActivé" : "§cDésactivé").toItemStack());
					inv.addItem(new ItemBuilder(Material.GHAST_TEAR).setName("§cInfection").setLore(new String[] {
							"§fTemp avant infection: ",
							"§a+5s§f (Clique gauche)",
							"§c-5s§f (Clique droit)",
							"§fTemp actuelle:§b "+StringUtils.secondsTowardsBeautiful(GameState.getInstance().timewaitingbeinfected)
					}).toItemStack());
					inv.addItem(new ItemBuilder(Material.TNT).setName("§fGrief du terrain par les§c TNT").setLore(gameState.doTNTGrief ? "§aActivé" : "§cDésactivé").toItemStack());
					inv.setItem(26, GUIItems.getSelectBackMenu());
				}
			}
		}
		player.updateInventory();
	}
	public void updateAOTConfiguration(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equalsIgnoreCase("Configuration -> AOT")) {
					inv.clear();
					inv.setItem(0, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());
					inv.setItem(1, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());
					inv.setItem(9, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());
					
					inv.setItem(7, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());
					inv.setItem(8, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());
					inv.setItem(17, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());
					
					inv.setItem(36, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());
					inv.setItem(45, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());
					inv.setItem(46, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());
					
					inv.setItem(44, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());
					inv.setItem(52, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());
					inv.setItem(53, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());
					
					inv.setItem(4, GUIItems.getSelectBackMenu());
					
					inv.setItem(10, new ItemBuilder(Material.BOW).setName("§rCooldown Equipement Tridimentionnel").setLore("§fCooldownActuel: "+gameState.TridiCooldown).toItemStack());
					if (gameState.rod) {
						inv.setItem(11, new ItemBuilder(Material.FISHING_ROD).setName("§rEquipement Tridimentionnel").setLore("§fEquipement actuel:§l Rod Tridimentionnelle").toItemStack());
					}else {
						inv.setItem(11, new ItemBuilder(Material.BOW).setName("§rEquipement Tridimentionnel").setLore("§fÉquipement actuel:§l Arc Tridimentionnelle").toItemStack());
					}
					inv.setItem(12, new ItemBuilder(Material.LAVA_BUCKET).setName("§r§6Lave§f pour les titans (transformé)").setLore(gameState.LaveTitans ? "§aActivé" : "§cDésactivé").toItemStack());
				}
			}
		}
		player.updateInventory();
		gameState.updateGameCanLaunch();
	}
 	public void updateMahrInventory(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equalsIgnoreCase("§fAOT§7 ->§9 Mahr")) {
					inv.clear();
					inv.setItem(0, GUIItems.getSBluetainedGlassPane());
					inv.setItem(1, GUIItems.getSBluetainedGlassPane());
					inv.setItem(9, GUIItems.getSBluetainedGlassPane());//haut gauche

					inv.setItem(2, GUIItems.getSelectSoloButton());
					inv.setItem(3, GUIItems.getSelectTitanButton());//haut milleu
					inv.setItem(4, GUIItems.getSelectBackMenu());
					inv.setItem(5, GUIItems.getSelectSoldatButton());
					if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
					if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

					inv.setItem(7, GUIItems.getSBluetainedGlassPane());//haut droite
					inv.setItem(8, GUIItems.getSBluetainedGlassPane());
					inv.setItem(17, GUIItems.getSBluetainedGlassPane());					
					
					inv.setItem(45, GUIItems.getSBluetainedGlassPane());
					inv.setItem(46, GUIItems.getSBluetainedGlassPane());
					inv.setItem(36, GUIItems.getSBluetainedGlassPane());//bas gauche
					
					inv.setItem(44, GUIItems.getSBluetainedGlassPane());
					inv.setItem(52, GUIItems.getSBluetainedGlassPane());
					inv.setItem(53, GUIItems.getSBluetainedGlassPane());//bas droite
					
					inv.setItem(49, GUIItems.getSelectConfigAotButton());
					
					for (Roles roles : Roles.values()) {
						if (roles.getTeam() == TeamList.Mahr) {
							String l1 = "";
							if (gameState.getAvailableRoles().get(roles) > 0) {
								l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
							} else {
								l1 = "§c(0)";
							}
							inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
						}
					}
				}
			}
		}
		player.updateInventory();
		gameState.updateGameCanLaunch();
	}
 	public void updateSecretTitansInventory(Player player) {
 		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equalsIgnoreCase("§fAOT§7 ->§c Titans")) {
					inv.clear();
					inv.setItem(0, GUIItems.getRedStainedGlassPane());
					inv.setItem(1, GUIItems.getRedStainedGlassPane());
					inv.setItem(9, GUIItems.getRedStainedGlassPane());//haut gauche
					
					inv.setItem(2, GUIItems.getSelectSoloButton());
					inv.setItem(3, GUIItems.getSelectMahrButton());//haut milleu
					inv.setItem(4, GUIItems.getSelectBackMenu());
					inv.setItem(5, GUIItems.getSelectSoldatButton());
					if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
					if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());
					inv.setItem(49, GUIItems.getSelectConfigAotButton());
					inv.setItem(7, GUIItems.getRedStainedGlassPane());//haut droite
					inv.setItem(8, GUIItems.getRedStainedGlassPane());
					inv.setItem(17, GUIItems.getRedStainedGlassPane());					
					
					inv.setItem(45, GUIItems.getRedStainedGlassPane());
					inv.setItem(46, GUIItems.getRedStainedGlassPane());
					inv.setItem(36, GUIItems.getRedStainedGlassPane());//bas gauche
					
					inv.setItem(44, GUIItems.getRedStainedGlassPane());
					inv.setItem(52, GUIItems.getRedStainedGlassPane());
					inv.setItem(53, GUIItems.getRedStainedGlassPane());//bas droite
					for (Roles roles : Roles.values()) {
						if (roles.getTeam() == TeamList.Titan) {
							String l1 = "";
							if (gameState.getAvailableRoles().get(roles) > 0) {
								l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
							} else {
								l1 = "§c(0)";
							}
							inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
						}
					}
				}
			}
		}
 	}
 	private void updateSoldatInventory(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equals("§fAOT§7 ->§a Soldats")) {
					inv.clear();	
					inv.setItem(0, GUIItems.getGreenStainedGlassPane());
					inv.setItem(1, GUIItems.getGreenStainedGlassPane());
					inv.setItem(9, GUIItems.getGreenStainedGlassPane());//haut gauche
					
					inv.setItem(2, GUIItems.getSelectSoloButton());
					inv.setItem(3, GUIItems.getSelectTitanButton());//haut milleu
					inv.setItem(4, GUIItems.getSelectBackMenu());
					inv.setItem(5, GUIItems.getSelectMahrButton());
					if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
					if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());
					inv.setItem(49, GUIItems.getSelectConfigAotButton());
					inv.setItem(7, GUIItems.getGreenStainedGlassPane());//haut droite
					inv.setItem(8, GUIItems.getGreenStainedGlassPane());
					inv.setItem(17, GUIItems.getGreenStainedGlassPane());
					
					inv.setItem(45, GUIItems.getGreenStainedGlassPane());
					inv.setItem(46, GUIItems.getGreenStainedGlassPane());
					inv.setItem(36, GUIItems.getGreenStainedGlassPane());//bas gauche
					
					inv.setItem(44, GUIItems.getGreenStainedGlassPane());
					inv.setItem(52, GUIItems.getGreenStainedGlassPane());
					inv.setItem(53, GUIItems.getGreenStainedGlassPane());//bas droite
					
					
					inv.setItem(18, new ItemBuilder(Material.ANVIL).toItemStack());
					for (Roles roles : Roles.values()) {
						if (roles.getTeam() == TeamList.Soldat) {
							String l1 = "";
							if (gameState.getAvailableRoles().get(roles) > 0) {
								l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
							} else {
								l1 = "§c(0)";
							}
							inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
						}
					}
					inv.setItem(18, new ItemBuilder(Material.AIR).toItemStack());
				}
			}
		}
		player.updateInventory();
		gameState.updateGameCanLaunch();
	}
 	private void updateNSAkatsukiInventory(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equals("§aNaruto§7 ->§c Akatsuki")) {
					inv.clear();	
					inv.setItem(0, GUIItems.getRedStainedGlassPane());
					inv.setItem(1, GUIItems.getRedStainedGlassPane());
					inv.setItem(9, GUIItems.getRedStainedGlassPane());//haut gauche
					
					inv.setItem(4, GUIItems.getSelectBackMenu());
				if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
				if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());
					
					inv.setItem(7, GUIItems.getRedStainedGlassPane());//haut droite
					inv.setItem(8, GUIItems.getRedStainedGlassPane());
					inv.setItem(17, GUIItems.getRedStainedGlassPane());					
					
					inv.setItem(45, GUIItems.getRedStainedGlassPane());
					inv.setItem(46, GUIItems.getRedStainedGlassPane());
					inv.setItem(36, GUIItems.getRedStainedGlassPane());//bas gauche
					
					inv.setItem(44, GUIItems.getRedStainedGlassPane());
					inv.setItem(52, GUIItems.getRedStainedGlassPane());
					inv.setItem(53, GUIItems.getRedStainedGlassPane());//bas droite
					
					inv.setItem(2, new ItemBuilder(Material.ANVIL).toItemStack());
					inv.setItem(3, new ItemBuilder(Material.ANVIL).toItemStack());
					inv.setItem(5, new ItemBuilder(Material.ANVIL).toItemStack());
					inv.setItem(18, new ItemBuilder(Material.ANVIL).toItemStack());
					for (Roles roles : Roles.values()) {
						if (roles.getTeam() == TeamList.Akatsuki) {
							String l1 = "";
							if (gameState.getAvailableRoles().get(roles) > 0) {
								l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
							} else {
								l1 = "§c(0)";
							}
							inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
						}
					}
					inv.setItem(2, new ItemBuilder(Material.AIR).toItemStack());
					inv.setItem(3, new ItemBuilder(Material.AIR).toItemStack());
					inv.setItem(5, new ItemBuilder(Material.AIR).toItemStack());
					inv.setItem(18, new ItemBuilder(Material.AIR).toItemStack());
				}
			}
		}
		player.updateInventory();
		gameState.updateGameCanLaunch();
	}
 	private void updateNSOrochimaruInventory(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equals("§aNaruto§7 ->§5 Orochimaru")) {
					inv.clear();	
					inv.setItem(0, GUIItems.getPurpleStainedGlassPane());
					inv.setItem(1, GUIItems.getPurpleStainedGlassPane());
					inv.setItem(9, GUIItems.getPurpleStainedGlassPane());//haut gauche
					
					inv.setItem(4, GUIItems.getSelectBackMenu());
				if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
				if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());
					
					inv.setItem(7, GUIItems.getPurpleStainedGlassPane());//haut droite
					inv.setItem(8, GUIItems.getPurpleStainedGlassPane());
					inv.setItem(17, GUIItems.getPurpleStainedGlassPane());					
					
					inv.setItem(45, GUIItems.getPurpleStainedGlassPane());
					inv.setItem(46, GUIItems.getPurpleStainedGlassPane());
					inv.setItem(36, GUIItems.getPurpleStainedGlassPane());//bas gauche
					
					inv.setItem(44, GUIItems.getPurpleStainedGlassPane());
					inv.setItem(52, GUIItems.getPurpleStainedGlassPane());
					inv.setItem(53, GUIItems.getPurpleStainedGlassPane());//bas droite
					
					inv.setItem(2, new ItemBuilder(Material.ANVIL).toItemStack());
					inv.setItem(3, new ItemBuilder(Material.ANVIL).toItemStack());
					inv.setItem(5, new ItemBuilder(Material.ANVIL).toItemStack());
					for (Roles roles : Roles.values()) {
						if (roles.getTeam() == TeamList.Orochimaru) {
							String l1 = "";
							if (gameState.getAvailableRoles().get(roles) > 0) {
								l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
							} else {
								l1 = "§c(0)";
							}
							inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
						}
					}
					inv.setItem(2, new ItemBuilder(Material.AIR).toItemStack());
					inv.setItem(3, new ItemBuilder(Material.AIR).toItemStack());
					inv.setItem(5, new ItemBuilder(Material.AIR).toItemStack());
				}
			}
		}
		player.updateInventory();
		gameState.updateGameCanLaunch();
	}
 	private void updateNSSoloInventory(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equals("§aNaruto§7 ->§e Solo")) {
					inv.clear();
					inv.setItem(0, GUIItems.getOrangeStainedGlassPane());
					inv.setItem(1, GUIItems.getOrangeStainedGlassPane());
					inv.setItem(9, GUIItems.getOrangeStainedGlassPane());//haut gauche
					
					inv.setItem(2, GUIItems.getSelectJubiButton());
					inv.setItem(3, GUIItems.getSelectBrumeButton());
					inv.setItem(4, GUIItems.getSelectBackMenu());
					inv.setItem(5, GUIItems.getSelectKumogakureButton());
				if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
				if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());
					
					inv.setItem(7, GUIItems.getOrangeStainedGlassPane());//haut droite
					inv.setItem(8, GUIItems.getOrangeStainedGlassPane());
					inv.setItem(17, GUIItems.getOrangeStainedGlassPane());					
					
					inv.setItem(45, GUIItems.getOrangeStainedGlassPane());
					inv.setItem(46, GUIItems.getOrangeStainedGlassPane());
					inv.setItem(36, GUIItems.getOrangeStainedGlassPane());//bas gauche
					
					inv.setItem(44, GUIItems.getOrangeStainedGlassPane());
					inv.setItem(52, GUIItems.getOrangeStainedGlassPane());
					inv.setItem(53, GUIItems.getOrangeStainedGlassPane());//bas droite

					for (Roles roles : Roles.values()) {
						if (roles.getTeam() == TeamList.Solo && roles.getMdj().equals("ns")) {
							String l1 = "";
							if (gameState.getAvailableRoles().get(roles) > 0) {
								l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
							} else {
								l1 = "§c(0)";
							}
							inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
						}
					}
				}
			}
		}
		player.updateInventory();
		gameState.updateGameCanLaunch();
	}
	private void updateNSJubiInventory(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory inv = invView.getTopInventory();
			if (inv != null) {
				if (inv.getTitle().equals("§eSolo§7 ->§d Jubi")) {
					inv.clear();
					ItemStack glass = GUIItems.getPinkStainedGlassPane();
					inv.setItem(0, glass);
					inv.setItem(1, glass);
					inv.setItem(9, glass);//haut gauche
					
					inv.setItem(4, GUIItems.getSelectBackMenu());
				if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
				if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());
					
					inv.setItem(7, glass);//haut droite
					inv.setItem(8, glass);
					inv.setItem(17, glass);					
					
					inv.setItem(45, glass);
					inv.setItem(46, glass);
					inv.setItem(36, glass);//bas gauche
					
					inv.setItem(44, glass);
					inv.setItem(52, glass);
					inv.setItem(53, glass);//bas droite
					
					inv.setItem(2, new ItemBuilder(Material.ANVIL).toItemStack());
					inv.setItem(3, new ItemBuilder(Material.ANVIL).toItemStack());
					inv.setItem(5, new ItemBuilder(Material.ANVIL).toItemStack());
					for (Roles roles : Roles.values()) {
						if (roles.getTeam() == TeamList.Jubi) {
							String l1 = "";
							if (gameState.getAvailableRoles().get(roles) > 0) {
								l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
							} else {
								l1 = "§c(0)";
							}
							inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
						}
					}
					inv.setItem(2, new ItemBuilder(Material.AIR).toItemStack());
					inv.setItem(3, new ItemBuilder(Material.AIR).toItemStack());
					inv.setItem(5, new ItemBuilder(Material.AIR).toItemStack());
				}
			}
		}
		player.updateInventory();
		gameState.updateGameCanLaunch();
	}
	public final void StartGame(final Player player) {
		gameState.updateGameCanLaunch();
		if (player.isOp() || gameState.getHost().contains(player) && gameState.gameCanLaunch) {
			StartGame();
			player.closeInventory();
		} else {
			player.closeInventory();
		}
	}
}
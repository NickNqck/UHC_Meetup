package fr.nicknqck.commands;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.HubListener;
import fr.nicknqck.Main;
import fr.nicknqck.bijus.Bijus;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.TeamList;
import fr.nicknqck.roles.aot.titans.Titans;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.slayers.FFA_Pourfendeur;
import fr.nicknqck.roles.ds.slayers.Pourfendeur;
import fr.nicknqck.scenarios.FFA;
import fr.nicknqck.utils.CC;
import fr.nicknqck.utils.NMSPacket;

public class AdminCommands implements CommandExecutor{

	GameState gameState;
	
	public AdminCommands(GameState gameState) {this.gameState = gameState;}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (gameState == null) {
			this.gameState = GameState.getInstance();
		}
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("list")) {
				if (sender.hasPermission("Host")) {
					gameState.getPlayerRoles().forEach((key, value) -> {
						sender.sendMessage("§7 -§f "+key.getName()+"§7 -> "+value.getTeamColor()+value.type.name());
					});
					return true;
				}
			}
			if (args[0].equalsIgnoreCase("name")) {
				if (sender instanceof Player) {
					if (sender.isOp() || gameState.getHost().contains(sender)) {
						StringBuilder sb = new StringBuilder();
						for (int i = 1;i<args.length;i++) {
							sb.append(" ");
							if (args[i].contains("#MDJ")||args[i].contains("#MDj")||args[i].contains("#Mdj")||args[i].contains("#MdJ")||args[i].contains("#mdj")||args[i].contains("#mDj")){
								String replaced;
								if (gameState.getMdj() != null) {
									replaced = args[i].replace("#MDJ", gameState.getMdj().name());
								} else {
									replaced = args[i].replace("#MDJ", "Aucun");
								}
								sb.append(CC.translate(replaced));
							} else {
								sb.append(CC.translate(args[i]));
							}
						}
						String name2 = sb.toString();
						if (name2.length() > 32) {
							sender.sendMessage("Il y a beaucoup trop de charactères (32 max)");
							return true;
						}
						gameState.msgBoard = name2;
						return true;
					}
				}
			}
			if (args[0].equalsIgnoreCase("resetCooldown")) {
				if (args.length == 2) {
					Player player = Bukkit.getPlayer(args[1]);
					if (player == null) {
						sender.sendMessage("§7Veuiller cibler un joueuer éxistant");
						return true;
					}
					if (!gameState.hasRoleNull(player)) {
						gameState.getPlayerRoles().get(player).resetCooldown();
						player.sendMessage("§fVos cooldown on été réinitialisé !");
						if (gameState.BijusEnable) {
							for (Bijus bijus : Bijus.values()) {
								if (bijus.getBiju().getMaster() != null &&bijus.getBiju().getMaster() == player.getUniqueId()) {
									bijus.getBiju().resetCooldown();
								}
							}
						}
						for (Titans titans : Titans.values()) {
							if (titans.getTitan().getOwner() != null&&titans.getTitan().getOwner() == player.getUniqueId()) {
								titans.getTitan().resetCooldown();
							}
						}
						return true;
					}
				}
				if (sender instanceof Player) {
					Player player = (Player)sender;
					if (!gameState.hasRoleNull(player)) {
						gameState.getPlayerRoles().get(player).resetCooldown();
						player.sendMessage("§fVos cooldown on été réinitialisé !");
						if (gameState.BijusEnable) {
							for (Bijus bijus : Bijus.values()) {
								if (bijus.getBiju().getHote() != null){
									if (bijus.getBiju().getHote().equals(player.getUniqueId()))
									bijus.getBiju().resetCooldown();
								}
							}
						}
						for (Titans titans : Titans.values()) {
							if (titans.getTitan().getOwner() != null&&titans.getTitan().getOwner() == player.getUniqueId()) {
								titans.getTitan().resetCooldown();
							}
						}
						return true;
					}
				}
			}
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("reset")) {
					if (gameState.getServerState() != ServerStates.InLobby) {
						sender.sendMessage("§7Vous ne pouvez pas faire ça en dehors du Lobby");
						return true;
					}
					sender.sendMessage("Commencement de la suppression des rôles");
					sender.sendMessage("");
					new BukkitRunnable() {
						@Override
						public void run() {
							if (gameState.getroleNMB() > 0) {
								for (Roles roles : gameState.getAvailableRoles().keySet()) {
									if (gameState.getAvailableRoles().get(roles) > 0) {
										int e = gameState.getAvailableRoles().get(roles);
										gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-gameState.getAvailableRoles().get(roles)));
										sender.sendMessage(roles.getTeam().getColor()+roles.name()+"§f (§7x"+e+"§f) à été retiré de la compo");
										sender.sendMessage("");
										return;
									}
								}
							} else {
								gameState.updateGameCanLaunch();
								sender.sendMessage("Suppression total de tout les rôles dans la partie fini");
								cancel();
							}
						}
					}.runTaskTimer(Main.getInstance(), 0, 20);
					return true;
				}
				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (player.isOp() || gameState.getHost().contains(player)) {
						if (gameState.getServerState() == ServerStates.InLobby) {
							if (args[0].equalsIgnoreCase("start")) {
								if (gameState.gameCanLaunch) {
									HubListener.getInstance().StartGame(player);
									player.sendMessage("Starting Game !");
									return true;
								} else {
									player.sendMessage("Impossible de commencer la partie, il manque des rôles");
									return true;
								}
							}
							if (args[0].equalsIgnoreCase("config")) {
								player.openInventory(GUIItems.getAdminWatchGUI());
								HubListener.getInstance().updateAdminInventory(player);
								return true;
							}
							if (args[0].equalsIgnoreCase("pregen")) {
								if (!gameState.pregenNakime) {
									ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
							        Bukkit.dispatchCommand(console, "nakime qF9JbNzW5R3s2ePk8mZr0HaS");
							        gameState.pregenNakime = true;
							        sender.sendMessage("Pregen en cours");
								}else {
									ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
							        Bukkit.dispatchCommand(console, "nakime delete");
							        gameState.pregenNakime = false;
							        sender.sendMessage("Suppression de la map \"nakime\"");
								}
						        return true;
							}
							if (args[0].equalsIgnoreCase("addAll")) {
								for (Roles r : Roles.values()) {
									gameState.addInAvailableRoles(r, Math.max(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(r)+1));
								}
								return true;
							}
						}
						
						if (gameState.getServerState() != ServerStates.InGame)return false;
						if (args[0].equalsIgnoreCase("detectwin") || args[0].equalsIgnoreCase("detect")) {
							GameListener.detectWin(gameState);
							sender.sendMessage("Detection de la victoire...");
							return true;
						}
						if (args[0].equalsIgnoreCase("nuit")) {
							ArrayList<String> message = new ArrayList<String>();
									gameState.nightTime = true;
									message.add("Exécution de la commande !");
									Bukkit.broadcastMessage("");
									Bukkit.broadcastMessage(ChatColor.RED+"!"+ChatColor.BOLD+"ALERT"+"! "+ChatColor.RESET+ChatColor.BOLD+"Un administrateur à changer le temp, il fait maintenant nuit");
									Bukkit.broadcastMessage("");
									Main.getInstance().gameWorld.setTime(13000);
									gameState.t = gameState.timeday;
									player.sendMessage(message.toArray(new String[message.size()]));
									return true;
									
						} else if (args[0].equalsIgnoreCase("jour")) {
							ArrayList<String> message = new ArrayList<String>();					
								if (sender instanceof Player) {
									gameState.nightTime = false;
									message.add("Exécution de la commande !");
									Bukkit.broadcastMessage("");
									Bukkit.broadcastMessage(ChatColor.RED+"!"+ChatColor.BOLD+"ALERT"+"! "+ChatColor.RESET+ChatColor.BOLD+"Un administrateur à changer le temp, il fait maintenant jour");
									Bukkit.broadcastMessage("");
									gameState.t = gameState.timeday;
									Main.getInstance().gameWorld.setTime(0);
									player.sendMessage(message.toArray(new String[message.size()]));
									return true;
								}
							}
					} else {
						player.sendMessage(ChatColor.RED+"Vous devez être OP ou Hostpour utiliser cette commande.");
					}
				}else {
					if (gameState.getServerState() == ServerStates.InLobby) {
						if (args[0].equalsIgnoreCase("start")) {
							if (gameState.gameCanLaunch) {
								HubListener.getInstance().StartGame();
								System.out.println("Starting Game !");
								return true;
							} else {
								System.out.println("Impossible de commencer la partie, il manque des rôles");
								return true;
							}
						}
					}
				}
				if (args[0].equalsIgnoreCase("giveblade")) {
					if (sender instanceof Player) {
						if (sender.isOp() || gameState.getHost().contains(sender)) {
							Player p = (Player) sender;
							ArrayList<String> message = new ArrayList<String>();
							message.add("Éxécution de la commande !");
							p.getInventory().addItem(Items.getLamedenichirin());
							Bukkit.broadcastMessage(sender.getName()+" à give une lame de nichirin au joueur nommé: "+sender.getName());
							sender.sendMessage(message.toArray(new String[message.size()]));
							return true;	
						} else {
							sender.sendMessage("Il faut être Host ou op pour faire cette commande");
							return true;
						}
					}else {
						sender.sendMessage("Seul un joueur peut effectuer cette commande");
						return true;
					}
				}
			}//args length == 1
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("addRole")) {
					if (args[1] != null) {
						if (!sender.isOp())return true;
						for (Roles roles : Roles.values()) {
							if (roles.name().equalsIgnoreCase(args[1])) {
								gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
								gameState.updateGameCanLaunch();
								if (gameState.getAvailableRoles().containsKey(roles)) {
									System.out.println(roles.name()+" a bien ete ajouter au roles valable pour la prochaine partie");
								}else {
									System.err.println(roles.name()+" n'a pas ete ajouter au roles valable pour la prochaine partie");
								}
								return true;
							}
						}
					}else {
						sender.sendMessage("La commande est /a addRole <nom du Role>");
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("delRole")) {
					if (args[1] != null) {
						for (Roles roles : Roles.values()) {
							if (roles.name().equalsIgnoreCase(args[1])) {
								gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
								gameState.updateGameCanLaunch();
								return true;
							}
						}
					}else {
						sender.sendMessage("La commande est /a delRole <nom du Role>");
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("infection") || args[0].equalsIgnoreCase("demon")) {
					if (sender instanceof Player) {
						if (sender.isOp() || gameState.getHost().contains(sender)) {
							if (args[1] != null) {
								Player p = Bukkit.getPlayer(args[1]);
								if (p == null) {
									sender.sendMessage("Veuiller indiquer un pseudo correcte");
									return true;
								} else {
									if (gameState.getServerState() == ServerStates.InGame) {
										if (gameState.getPlayerRoles().containsKey(p)) {
											if (gameState.getPlayerRoles().get(p).getTeam() != TeamList.Demon) {
												if (gameState.getInSpecPlayers().contains(p)) {
													sender.sendMessage("Impossible d'infecté un spectateur désolé.");
													return true;
												}
												gameState.getPlayerRoles().get(p).setTeam(TeamList.Demon);
												sender.sendMessage("Le joueur§6 "+p.getName()+"§r est bel et bien devenue§c Démon");
												p.sendMessage("Vous appartenez maintenant au camp des§c Démons§r");
												GameListener.SendToEveryone("Un joueur à été infecté par un Administrateur/Host");
												gameState.infectedbyadmin.add(p);
												return true;
											}
										} else {
											sender.sendMessage(p.getName()+" n'a pas de rôle il ne peux donc pas être infecté");
										}
									} else {
										sender.sendMessage("Il faut être en jeu pour faire cette commande !");
										return true;
									}
								}
							} else {
								sender.sendMessage("Il faut préciser un joueur");
								return true;
							}
						} else {
							sender.sendMessage("Il faut être Host ou op pour faire cette commande");
							return true;
						}
					} else {
						sender.sendMessage("Seul un joueur peut effectuer cette commande");
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("slayer")) {
					if (sender instanceof Player) {
						if (sender.isOp() || gameState.getHost().contains(sender)) {
							if (args[1] != null) {
								Player p = Bukkit.getPlayer(args[1]);
								if (p == null) {
									sender.sendMessage("Veuiller indiquer un pseudo correcte");
									return true;
								} else {
									if (gameState.getServerState() == ServerStates.InGame) {
										if (gameState.getPlayerRoles().containsKey(p)) {
											if (gameState.getPlayerRoles().get(p).getTeam() != TeamList.Slayer) {
												if (gameState.getInSpecPlayers().contains(p)) {
													sender.sendMessage("Impossible d'infecté un spectateur désolé.");
													return true;
												}
												gameState.getPlayerRoles().get(p).setTeam(TeamList.Slayer);
												sender.sendMessage("Le joueur§6 "+p.getName()+"§r est bel et bien devenue§a Slayer");
												p.sendMessage("Vous appartenez maintenant au camp des§a Slayers");
												GameListener.SendToEveryone("Un joueur à rejoint le camp des§a Slayers§r par un Administrateur/Host");
												return true;
											}
										} else {
											sender.sendMessage(p.getName()+" n'a pas de rôle il ne peux donc pas devenir§a Slayer");
										}
									} else {
										sender.sendMessage("Il faut être en jeu pour faire cette commande !");
										return true;
									}
								}
							} else {
								sender.sendMessage("Il faut préciser un joueur");
								return true;
							}
						} else {
							sender.sendMessage("Il faut être Host ou op pour faire cette commande");
							return true;
						}
					} else {
						sender.sendMessage("Seul un joueur peut effectuer cette commande");
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("solo")) {
					if (sender instanceof Player) {
						if (sender.isOp() || gameState.getHost().contains(sender)) {
							if (args[1] != null) {
								Player p = Bukkit.getPlayer(args[1]);
								if (p == null) {
									sender.sendMessage("Veuiller indiquer un pseudo correcte");
									return true;
								} else {
									if (gameState.getServerState() == ServerStates.InGame) {
										if (gameState.getPlayerRoles().containsKey(p)) {
											if (gameState.getPlayerRoles().get(p).getTeam() != TeamList.Solo) {
												if (gameState.getInSpecPlayers().contains(p)) {
													sender.sendMessage("Impossible d'infecté un spectateur désolé.");
													return true;
												}
												gameState.getPlayerRoles().get(p).setTeam(TeamList.Solo);
												sender.sendMessage("Le joueur§6 "+p.getName()+"§r est bel et bien devenue"+ChatColor.YELLOW+" Solo");
												p.sendMessage("Vous appartenez maintenant au camp des"+ChatColor.YELLOW+" Solo");
												GameListener.SendToEveryone("Un joueur à rejoint le camp des§e Solo§r par un Administrateur/Host");
												return true;
											}
										} else {
											sender.sendMessage(p.getName()+" n'a pas de rôle il ne peux donc pas devenir§e Solo");
										}
									} else {
										sender.sendMessage("Il faut être en jeu pour faire cette commande !");
										return true;
									}
								}
							} else {
								sender.sendMessage("Il faut préciser un joueur");
								return true;
							}
						} else {
							sender.sendMessage("Il faut être Host ou op pour faire cette commande");
							return true;
						}
					} else {
						sender.sendMessage("Seul un joueur peut effectuer cette commande");
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("jigoro")) {
					if (sender instanceof Player) {
						if (sender.isOp() || gameState.getHost().contains(sender)) {
							if (args[1] != null) {
								Player p = Bukkit.getPlayer(args[1]);
								if (p == null) {
									sender.sendMessage("Veuiller indiquer un pseudo correcte");
									return true;
								} else {
									if (gameState.getServerState() == ServerStates.InGame) {
										if (gameState.getPlayerRoles().containsKey(p)) {
											if (gameState.getPlayerRoles().get(p).getTeam() != TeamList.Jigoro) {
												if (gameState.getInSpecPlayers().contains(p)) {
													sender.sendMessage("Impossible d'infecté un spectateur désolé.");
													return true;
												}
												gameState.getPlayerRoles().get(p).setTeam(TeamList.Jigoro);
												sender.sendMessage("Le joueur§6 "+p.getName()+"§r est bel et bien devenue"+ChatColor.GOLD+" Jigoro");
												p.sendMessage("Vous appartenez maintenant au camp des"+ChatColor.GOLD+" Jigoro");
												GameListener.SendToEveryone("Un joueur à rejoint le camp des§a Slayers§r par un Administrateur/Host");
												return true;
											}
										} else {
											sender.sendMessage(p.getName()+" n'a pas de rôle il ne peux donc pas devenir§6 Jigoro");
										}
									} else {
										sender.sendMessage("Il faut être en jeu pour faire cette commande !");
										return true;
									}
								}
							} else {
								sender.sendMessage("Il faut préciser un joueur");
								return true;
							}
						} else {
							sender.sendMessage("Il faut être Host ou op pour faire cette commande");
							return true;
						}
					} else {
						sender.sendMessage("Seul un joueur peut effectuer cette commande");
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("setgroupe")) {
					if (sender instanceof Player) {
						if (sender.isOp() || gameState.getHost().contains(sender)) {
							if (args[1] != null) {
								if (gameState.getServerState() != null) {
									if (gameState.getServerState() == ServerStates.InGame) {
										int grp = Integer.parseInt(args[1]);
										if (grp > 0) {
											gameState.setGroupe(grp);
											for (Player p : gameState.getInGamePlayers()) {
												p.playSound(p.getLocation(), Sound.BLAZE_HIT, 1, 50);
												NMSPacket.sendTitle(p, 0, 20*3, 0, "§cGroupe de§6 "+args[1], "Veuillez les respectés");
											}
											return true;
										}
									} else {
										sender.sendMessage("§cIl faut être en jeux pour faire cette commande !");
										return true;
									}
								}
							} else {
								sender.sendMessage("Cette commande prend comme valeur un chiffre");
								return true;
							}
						} else {
							sender.sendMessage("Il faut être op pour effectué cette commande ! ");
							return true;
						}
					} else {
						sender.sendMessage("Il faut être un joueur pour faire cette commande !");
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("addHost")) {
					if (sender instanceof Player) {
						if (sender.isOp() || gameState.getHost().contains(sender)) {
							if (args[1] != null) {
								Player p = Bukkit.getPlayer(args[1]);
								if (p == null) {
									sender.sendMessage("Veuiller indiquer un pseudo correcte");
									return true;
								} else {
									if (gameState.getHost().contains(p)) {
										sender.sendMessage("Cette personne est déjà host...");
										return true;
									} else {
										p.addAttachment(Main.getInstance(), "Host", true);
										gameState.addHost(p);
										sender.sendMessage("Vous avez ajouter "+p.getName()+" à la list(e) des hosts");
										Bukkit.broadcastMessage(p.getName()+" est maintenant host");
										return true;
									}						
								}					
							} else {
								sender.sendMessage("Il faut préciser un joueur");
								return true;
							}
						} else {
							sender.sendMessage("Il faut être Host ou op pour faire cette commande");
							return true;
						}
					} else {
						sender.sendMessage("Seul un joueur peut effectuer cette commande");
						return true;
					}
					
				}
				if (args[0].equalsIgnoreCase("delHost") || args[0].equalsIgnoreCase("removeHost")) {
					if (sender instanceof Player) {
						if (sender.isOp() || gameState.getHost().contains(sender)) {
							if (args[1] != null) {
								Player p = Bukkit.getPlayer(args[1]);
								if (p == null) {
									sender.sendMessage("Veuiller indiquer un pseudo correcte");
									return true;
								} else {
									if (gameState.getHost().contains(p)) {
										gameState.delHost(p);
										p.addAttachment(Main.getInstance(), "Host", false);
										sender.sendMessage(p.getName()+" n'est plus host");
										return true;
									} else {
										sender.sendMessage(p.getName()+" n'est pas host");
										return true;
									}						
								}					
							} else {
								sender.sendMessage("Il faut préciser un joueur");
								return true;
							}
						} else {
							sender.sendMessage("Il faut être Host ou op pour faire cette commande");
							return true;
						}
					}else {
						sender.sendMessage("Seul un joueur peut effectuer cette commande");
						return true;
					}		
				}
				if (args[0].equalsIgnoreCase("op")) {
					if (args[1] != null) {
						Player p = Bukkit.getPlayer(args[1]);
						if (p == null) {
							sender.sendMessage("Veuiller indiquer un pseudo correcte");
							return true;
						} else {
							p.setOp(true);
							return true;
						}
					}
				}
				if (args[0].equalsIgnoreCase("giveblade")) {
					if (sender instanceof Player) {
						if (sender.isOp() || gameState.getHost().contains(sender)) {
							if (args[1] != null) {
								Player p = Bukkit.getPlayer(args[1]);
								if (p == null) {
									sender.sendMessage("Veuiller indiquer un pseudo correcte");
									return true;
								} else {
									ArrayList<String> message = new ArrayList<String>();
									message.add("Éxécution de la commande !");
									p.getInventory().addItem(Items.getLamedenichirin());
									Bukkit.broadcastMessage(sender.getName()+" à give une lame de nichirin au joueur nommé: "+p.getName());
									sender.sendMessage(message.toArray(new String[message.size()]));
									return true;						
								}					
							} else {
								sender.sendMessage("Il faut préciser un joueur");
								return true;
							}
						} else {
							sender.sendMessage("Il faut être Host ou op pour faire cette commande");
							return true;
						}
					}else {
						sender.sendMessage("Seul un joueur peut effectuer cette commande");
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("cheat")) {
					if (sender instanceof Player) {
						Player s = (Player) sender;
						if (s.isOp() || gameState.getHost().contains(s)) {
							if (args[1] != null) {
								Player p = Bukkit.getPlayer(args[1]);
								if (p == null) {
									sender.sendMessage("Veuiller indiquer un pseudo correcte");
									return true;
								} else {
									if (!gameState.hasRoleNull(p)) {
										if (gameState.getPlayerRoles().containsKey(p)) {
											if (gameState.getPlayerRoles().get(p).type == Roles.Slayer) {
												if (FFA.getFFA()) {
													RoleBase r = gameState.getPlayerRoles().get(p);
													FFA_Pourfendeur fp = (FFA_Pourfendeur) r;
													fp.cheat = true;
													fp.owner.sendMessage("Vous avez bien cheater pour obtenir le souffle de l'univers");
													return true;
												} else {
													RoleBase r = gameState.getPlayerRoles().get(s);
													Pourfendeur fp = (Pourfendeur) r;
													fp.owner.sendMessage("");
													return true;
												}
											}
										}
									}						
								}					
							} else {
								sender.sendMessage("Il faut préciser un joueur");
								return true;
							}
						}
					}
				}
				if (args[0].equalsIgnoreCase("effect")) {
					if (sender instanceof Player) {
						if (sender.isOp() || gameState.getHost().contains(sender)) {
							if (args[1] != null) {
								Player p = Bukkit.getPlayer(args[1]);
								if (p == null || args.length == 1) {
									sender.sendMessage("Veuiller indiquer un pseudo correcte");
									return true;
								} else {
									if (gameState.getPlayerRoles().containsKey(p)) {
										ArrayList<String> message = new ArrayList<String>();
										message.add(ChatColor.AQUA+"Voici les effets du joueur "+p.getName()+ChatColor.DARK_GRAY+"§o§m-----------------------------------");
										message.add("");
										message.add(AllDesc.Resi+": "+ gameState.getPlayerRoles().get(p).getResi()+"% + " +gameState.getPlayerRoles().get(p).getBonusResi()+"%");
										message.add("");
										message.add(ChatColor.RED+"Force: "+ gameState.getPlayerRoles().get(p).getForce()+"% + "+gameState.getPlayerRoles().get(p).getBonusForce()+"%");
										message.add("");
										message.add(ChatColor.AQUA+"Speed: "+gameState.getPlayerRoles().get(p).owner.getWalkSpeed());
										message.add("");
										message.add(ChatColor.DARK_GRAY+"§o§m-----------------------------------");
										sender.sendMessage(message.toArray(new String[message.size()]));
										return true;					
										}
								}
							}
						}
					}
				}
				if (args[0].equalsIgnoreCase("role")) {
					if (sender instanceof Player) {
						if (sender.isOp() || gameState.getHost().contains(sender)) {
							if (args[1] != null) {
								Player p = Bukkit.getPlayer(args[1]);
								if (p == null) {
									sender.sendMessage("Veuiller indiquer un pseudo correcte");
									return true;
								} else {
									if (gameState.getPlayerRoles().containsKey(p)) {
									Bukkit.broadcastMessage("Le joueur: "+sender.getName()+" connais maintenant le rôle du joueur: "+p.getName());
									sender.sendMessage("Le rôle de la personne: "+p.getName()+" est "+gameState.getPlayerRoles().get(p).getTeam().getColor()+gameState.getPlayerRoles().get(p).type.name());
									return true;
									}
								}
							} else {
								sender.sendMessage("Il faut préciser un joueur");
								return true;
							}
						}
					}
				}
				if (args[0].equalsIgnoreCase("revive")) {
					if (sender instanceof Player) {
						if (sender.isOp() || gameState.getHost().contains(sender)) {
							if (args[1] != null) {
								Player p = Bukkit.getPlayer(args[1]);
								if (p == null) {
									sender.sendMessage("Veuiller indiquer un pseudo correcte");
									return true;
								} else {
									if (gameState.getPlayerRoles().containsKey(p)) {
									gameState.RevivePlayer(p);
									sender.sendMessage(p.getName()+" à bien été réssucité");
									HubListener.getInstance().giveStartInventory(p);
									gameState.getPlayerRoles().get(p).GiveItems();
									gameState.getPlayerRoles().get(p).setMaxHealth(20.0);
									gameState.getPlayerRoles().get(p).RoleGiven(gameState);
									return true;
									}
								}
							} else {
								sender.sendMessage("Il faut préciser un joueur");
								return true;
							}
						}
					}
				}
				if (args[0].equalsIgnoreCase("info")) {
					if (sender instanceof Player) {
						if (sender.isOp() && ((Player)sender).getUniqueId().equals(UUID.fromString(""))) {
							if (args[1] != null) {
								Player p = Bukkit.getPlayer(args[1]);
								if (p == null) {
									sender.sendMessage("Veuiller indiquer un pseudo correcte");
									return true;
								} else {
									if (gameState.getPlayerRoles().containsKey(p)) {
									gameState.RevivePlayer(p);
									sender.sendMessage(p.getName()+" à bien été réssucité");
									HubListener.getInstance().giveStartInventory(p);
									gameState.getPlayerRoles().get(p).GiveItems();
									gameState.getPlayerRoles().get(p).RoleGiven(gameState);
									return true;
									}
								}
							} else {
								sender.sendMessage("Il faut préciser un joueur");
								return true;
							}
						}
					}
				}
			}
			if (args.length == 3) {
				if (args[0].equalsIgnoreCase("camp")) {
					Player p = Bukkit.getPlayer(args[1]);
					if (p != null) {
						if (!gameState.hasRoleNull(p)) {
							for (TeamList team : TeamList.values()) {
								if (team.name().equals(args[2])) {
									gameState.getPlayerRoles().get(p).setTeam(team);
									break;
								}
							}
						}
					}
				}
			}
		}
		return true;
	}
}
package fr.nicknqck.commands;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.HubListener;
import fr.nicknqck.Main;
import fr.nicknqck.entity.bijus.Bijus;
import fr.nicknqck.events.custom.DayEvent;
import fr.nicknqck.events.custom.NightEvent;
import fr.nicknqck.events.essential.inventorys.EasyRoleAdder;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.aot.builders.titans.Titans;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.packets.NMSPacket;
import fr.nicknqck.utils.powers.Power;
import fr.nicknqck.utils.rank.ChatRank;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class AdminCommands implements CommandExecutor{

	private GameState gameState;
	
	public AdminCommands(GameState gameState) {this.gameState = gameState;}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (gameState == null) {
			this.gameState = GameState.getInstance();
		}
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("vie")) {
				if (args.length == 3) {
					Player target = Bukkit.getPlayer(args[1]);
					try {
						double damage = Double.parseDouble(args[2]);
						if (target != null) {
							damage = Math.min(0.1, target.getHealth() - damage);
							target.setHealth(damage);
							sender.sendMessage("§c"+target.getName()+"§b à subit§c "+damage);
						} else {
							System.out.println("Player not found: " + args[1]);
						}
					} catch (NumberFormatException e) {
						System.out.println("Invalid number format: " + args[2]);
					}
				}
			}
			if (args[0].equalsIgnoreCase("list")) {
				if ((sender instanceof Player && ChatRank.hasRank(((Player) sender).getUniqueId()) && ChatRank.isHost(((Player) sender).getUniqueId())) || sender.hasPermission("Host")) {
					gameState.getGamePlayer().forEach((key, value) -> sender.sendMessage("§7 - "+value.getPlayerName()+"§7 -> "+value.getRole().getTeamColor()+value.getRole().getName()));
					return true;
				}
			}
			if (args[0].equalsIgnoreCase("name")) {
				if (sender instanceof Player) {
					if (ChatRank.isHost(sender)) {
						StringBuilder sb = new StringBuilder();
						for (int i = 1;i<args.length;i++) {
							sb.append(" ");
							if (args[i].contains("#MDJ")||args[i].contains("#MDj")||args[i].contains("#Mdj")||args[i].contains("#MdJ")||args[i].contains("#mdj")||args[i].contains("#mDj")){
								String replaced;
                                replaced = args[i].replace(args[i], gameState.getMdj().name());
                                sb.append(ChatColor.translateAlternateColorCodes('&', replaced));
							} else {
								sb.append(ChatColor.translateAlternateColorCodes('&', args[i]));
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
					if (!gameState.hasRoleNull(player.getUniqueId())) {
						gameState.getGamePlayer().get(player.getUniqueId()).getRole().resetCooldown();
						for (Power power :  gameState.getGamePlayer().get(player.getUniqueId()).getRole().getPowers()) {
							if (power.getCooldown() == null)continue;
							if (!power.isSendCooldown())continue;
							power.getCooldown().resetCooldown();
						}
						player.sendMessage("§fVos cooldown on été réinitialisé !");
						if (Main.getInstance().getGameConfig().isBijusEnable()) {
							for (Bijus bijus : Bijus.values()) {
								if (bijus.getBiju().getHote() != null &&bijus.getBiju().getHote() == player.getUniqueId()) {
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
					if (!gameState.hasRoleNull(player.getUniqueId())) {
						gameState.getGamePlayer().get(player.getUniqueId()).getRole().resetCooldown();
						player.sendMessage("§fVos cooldown on été réinitialisé !");
						if (Main.getInstance().getGameConfig().isBijusEnable()) {
							for (Bijus bijus : Bijus.values()) {
								if (bijus.getBiju().getHote() != null){
									if (bijus.getBiju().getHote().equals(player.getUniqueId())){
										bijus.getBiju().resetCooldown();
									}
								}
							}
						}
						for (Titans titans : Titans.values()) {
							if (titans.getTitan().getOwner() != null&&titans.getTitan().getOwner() == player.getUniqueId()) {
								titans.getTitan().resetCooldown();
							}
						}
						if (!gameState.getGamePlayer().get(player.getUniqueId()).getRole().getPowers().isEmpty()) {
							for (@NonNull final Power power : gameState.getGamePlayer().get(player.getUniqueId()).getRole().getPowers()) {
								if (power.getCooldown() != null) {
									if (power.getCooldown().isInCooldown()) {
										power.getCooldown().setActualCooldown(0);
									}
								}
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
					if (!ChatRank.isHost(sender)) {
						sender.sendMessage("§cVous n'avez pas la permission de faire cette commande !");
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
					if (ChatRank.isHost(sender)) {
						if (gameState.getServerState() == ServerStates.InLobby) {
							if (args[0].equalsIgnoreCase("start")) {
								if (gameState.gameCanLaunch) {
									HubListener.getInstance().StartGame(player);
									player.sendMessage("Starting Game !");
                                } else {
									player.sendMessage("Impossible de commencer la partie, il manque des rôles");
                                }
                                return true;
                            }
							if (args[0].equalsIgnoreCase("config")) {
								player.openInventory(GUIItems.getAdminWatchGUI());
								Main.getInstance().getInventories().updateAdminInventory(player);
								return true;
							}
							if (args[0].equalsIgnoreCase("pregen")) {
                                ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                                if (!gameState.pregenNakime) {
                                    Bukkit.dispatchCommand(console, "nakime qF9JbNzW5R3s2ePk8mZr0HaS");
							        gameState.pregenNakime = true;
							        sender.sendMessage("Pregen en cours");
								}else {
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
							if (args[0].equalsIgnoreCase("preview")) {
								World world = Main.getInstance().getWorldManager().getGameWorld();
								if (world == null) {
									player.sendMessage("§7Le monde de jeu n'a pas été crée");
                                } else {
									if (player.getWorld().equals(world)) {
										player.sendMessage("§7Vous quittez la§c preview§7 de§c arena");
										player.teleport(new Location(Main.getInstance().getWorldManager().getLobbyWorld(), 0, 152, 0));
										player.setGameMode(GameMode.ADVENTURE);
										return true;
									}
									player.teleport(new Location(Main.getInstance().getWorldManager().getGameWorld(), 0, Main.getInstance().getWorldManager().getGameWorld().getHighestBlockYAt(0, 0)+1, 0));
									player.setGameMode(GameMode.SPECTATOR);
									player.sendMessage("§7Pour quitter la§c preview§7 du monde il faudra faire la commande§6 /a preview");
									return true;
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
									gameState.nightTime = true;
									Bukkit.broadcastMessage("");
									Bukkit.broadcastMessage(ChatColor.RED+"!"+ChatColor.BOLD+"ALERT"+"! "+ChatColor.RESET+ChatColor.BOLD+"Un administrateur à changer le temp, il fait maintenant nuit");
									Bukkit.broadcastMessage("");
									Main.getInstance().getWorldManager().getGameWorld().setTime(13000);
									gameState.t = Main.getInstance().getGameConfig().getMaxTimeDay();
									Bukkit.getServer().getPluginManager().callEvent(new NightEvent(gameState, Main.getInstance().getGameConfig().getMaxTimeDay()));
									return true;
									
						} else if (args[0].equalsIgnoreCase("jour")) {
                            gameState.nightTime = false;
                            Bukkit.broadcastMessage("");
                            Bukkit.broadcastMessage(ChatColor.RED+"!"+ChatColor.BOLD+"ALERT"+"! "+ChatColor.RESET+ChatColor.BOLD+"Un administrateur à changer le temp, il fait maintenant jour");
                            Bukkit.broadcastMessage("");
                            gameState.t = Main.getInstance().getGameConfig().getMaxTimeDay();
                            Main.getInstance().getWorldManager().getGameWorld().setTime(0);
							Bukkit.getPluginManager().callEvent(new DayEvent(gameState));
                            return true;
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
                            } else {
								System.out.println("Impossible de commencer la partie, il manque des rôles");
                            }
                            return true;
                        }
					}
				}
				if (args[0].equalsIgnoreCase("giveblade")) {
					if (sender instanceof Player) {
						if (ChatRank.isHost(sender)) {
							Player p = (Player) sender;
							p.getInventory().addItem(Items.getLamedenichirin());
							Bukkit.broadcastMessage(sender.getName()+" à give une lame de nichirin au joueur nommé: "+sender.getName());
                        } else {
							sender.sendMessage("Il faut être Host ou op pour faire cette commande");
                        }
                    }else {
						sender.sendMessage("Seul un joueur peut effectuer cette commande");
                    }
                    return true;
                }
			}//args length == 1
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("setgroupe")) {
					if (sender instanceof Player) {
						if (ChatRank.isHost(sender)) {
							if (args[1] != null) {
								if (gameState.getServerState() != null) {
									if (gameState.getServerState() == ServerStates.InGame) {
										try {
											int grp = Integer.parseInt(args[1]);
											if (grp > 0) {
												Main.getInstance().getGameConfig().setGroupe(grp);
												for (UUID u : gameState.getInGamePlayers()) {
													Player p = Bukkit.getPlayer(u);
													if (p == null)continue;
													p.playSound(p.getLocation(), Sound.BLAZE_HIT, 1, 50);
													NMSPacket.sendTitle(p, 0, 20*3, 0, "§cGroupe de§6 "+args[1], "Veuillez les respectés");
													p.sendTitle("§cGroupe de§6 "+args[1],"§cVeuillez les respectez");
												}
												return true;
											}
										} catch (NumberFormatException e) {
											e.printStackTrace();
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
				if (args[0].equalsIgnoreCase("addRole")) {
					if (args[1] != null) {
						if (!sender.isOp())return true;
						EasyRoleAdder.addRoles(args[1]);
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
				if (sender instanceof Player) {
					if (ChatRank.isHost(((Player) sender).getUniqueId())){
						Player p = Bukkit.getPlayer(args[1]);
						if (p != null){
							if (gameState.getServerState().equals(ServerStates.InGame)){
								if (!gameState.hasRoleNull(p.getUniqueId()) && !gameState.getInSpecPlayers().contains(p)){
									for (TeamList team : TeamList.values()){
										if (args[0].equalsIgnoreCase(team.name())){
											if (!gameState.getGamePlayer().get(p.getUniqueId()).getRole().getTeam().equals(team)){
												gameState.getGamePlayer().get(p.getUniqueId()).getRole().setTeam(team);
												sender.sendMessage("Le joueur§6 "+p.getName()+"§r est bel et bien devenue"+team.getColor()+" "+team.name());
												p.sendMessage("Vous appartenez maintenant au camp des"+team.getColor()+" "+team.name());
												GameListener.SendToEveryone("Un joueur à rejoint le camp des "+team.getColor()+team.name()+"§r par un Administrateur/Host");
												return true;
											}
										}
									}
								}
							}
						}
					}
				}
				if (args[0].equalsIgnoreCase("addHost")) {
						if (sender.isOp()) {
							if (args[1] != null) {
								Player p = Bukkit.getPlayer(args[1]);
								if (p == null) {
									sender.sendMessage("Veuiller indiquer un pseudo correcte");
                                } else {
									if (ChatRank.isHost(p)) {
										sender.sendMessage("Cette personne est déjà host...");
                                    } else {
										p.addAttachment(Main.getInstance(), "Host", true);
										ChatRank.Host.add(p.getUniqueId());
										sender.sendMessage("Vous avez ajouter "+p.getName()+" à la list(e) des hosts");
										Bukkit.broadcastMessage("§c"+p.getName()+"§f est maintenant§c Host");
                                    }
                                }
                            } else {
								sender.sendMessage("Il faut préciser un joueur");
                            }
                        } else {
							sender.sendMessage("Il faut être Host ou op pour faire cette commande");
                        }
                    return true;

                }
				if (args[0].equalsIgnoreCase("delHost") || args[0].equalsIgnoreCase("removeHost")) {
					if (sender instanceof Player) {
						if (ChatRank.isHost(sender)) {
							if (args[1] != null) {
								Player p = Bukkit.getPlayer(args[1]);
								if (p == null) {
									sender.sendMessage("Veuiller indiquer un pseudo correcte");
                                } else {
									if (ChatRank.Host.contains(p.getUniqueId())) {
										ChatRank.Host.remove(p.getUniqueId());
										p.addAttachment(Main.getInstance(), "Host", false);
										sender.sendMessage(p.getName()+" n'est plus host");
                                    } else {
										sender.sendMessage(p.getName()+" n'est pas host");
                                    }
                                }
                            } else {
								sender.sendMessage("Il faut préciser un joueur");
                            }
                        } else {
							sender.sendMessage("Il faut être Host ou op pour faire cette commande");
                        }
                    }else {
						sender.sendMessage("Seul un joueur peut effectuer cette commande");
                    }
                    return true;
                }
				if (args[0].equalsIgnoreCase("op")) {
					if (args[1] != null) {
						Player p = Bukkit.getPlayer(args[1]);
						if (p == null) {
							sender.sendMessage("Veuiller indiquer un pseudo correcte");
                        } else {
							p.setOp(true);
                        }
                        return true;
                    }
				}
				if (args[0].equalsIgnoreCase("giveblade")) {
						if (ChatRank.isHost(sender)) {
							if (args[1] != null) {
								Player p = Bukkit.getPlayer(args[1]);
								if (p == null) {
									sender.sendMessage("Veuiller indiquer un pseudo correcte");
                                } else {
									p.getInventory().addItem(Items.getLamedenichirin());
									Bukkit.broadcastMessage(sender.getName()+" à give une lame de nichirin au joueur nommé: "+p.getName());
									sender.sendMessage("Vous avez donnez une Lame de nichirin a "+p.getDisplayName());
                                }
                            } else {
								sender.sendMessage("Il faut préciser un joueur");
                            }
                        } else {
							sender.sendMessage("Il faut être Host ou op pour faire cette commande");
                        }
                    return true;
                }
				if (args[0].equalsIgnoreCase("effect")) {
					if (sender instanceof Player) {
						if (ChatRank.isHost(sender)) {
							if (args[1] != null) {
								Player p = Bukkit.getPlayer(args[1]);
								if (p == null) {
									sender.sendMessage("Veuiller indiquer un pseudo correcte");
									return true;
								} else {
									if (!gameState.hasRoleNull(p.getUniqueId())) {
										final RoleBase role = gameState.getGamePlayer().get(p.getUniqueId()).getRole();
										final String speed = Bukkit.getPlayer(role.getPlayer()) != null ? Bukkit.getPlayer(role.getPlayer()).getWalkSpeed()+"" : "?";
										sender.sendMessage(new String[] {
												"§bVoici les effets du joueur "+p.getName()+ChatColor.DARK_GRAY+"§o§m-----------------------------------",
												"",
												AllDesc.Resi+": "+ role.getResi()+"% + " +role.getBonusResi()+"%",
												"",
												ChatColor.RED+"Force: 20% + "+role.getBonusForce()+"%",
												"",
												ChatColor.AQUA+"Speed: "+speed,
												"",
												ChatColor.DARK_GRAY+"§o§m-----------------------------------"
										});
										return true;					
										}
								}
							}
						}
					}
				}
				if (args[0].equalsIgnoreCase("role")) {
					if (sender instanceof Player) {
						if (ChatRank.isHost(sender)) {
							if (args[1] != null) {
								Player p = Bukkit.getPlayer(args[1]);
								if (p == null) {
									sender.sendMessage("Veuiller indiquer un pseudo correcte");
									return true;
								} else {
									if (!gameState.hasRoleNull(p.getUniqueId())) {
									Bukkit.broadcastMessage("Le joueur: "+sender.getName()+" connais maintenant le rôle du joueur: "+p.getName());
									sender.sendMessage("Le rôle de la personne: "+p.getName()+" est "+gameState.getGamePlayer().get(p.getUniqueId()).getRole().getTeam().getColor()+gameState.getGamePlayer().get(p.getUniqueId()).getRole().getName());
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
						if (ChatRank.isHost(sender)) {
							if (args[1] != null) {
								Player p = Bukkit.getPlayer(args[1]);
								if (p == null) {
									sender.sendMessage("Veuiller indiquer un pseudo correcte");
									return true;
								} else {
									if (!gameState.hasRoleNull(p.getUniqueId())) {
										gameState.RevivePlayer(p);
										sender.sendMessage(p.getName()+" à bien été réssucité");
										HubListener.getInstance().giveStartInventory(p);
										final RoleBase role = gameState.getGamePlayer().get(p.getUniqueId()).getRole();
										role.GiveItems();
										role.setMaxHealth(20.0);
										role.RoleGiven(gameState);
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
						if (sender.isOp()) {
							if (args[1] != null) {
								Player p = Bukkit.getPlayer(args[1]);
								if (p == null) {
									sender.sendMessage("Veuiller indiquer un pseudo correcte");
                                } else {
									sender.sendMessage("");
									TextComponent texte = new TextComponent("Voici les informations disponible sur§b "+p.getName()+"\n");
									texte.addExtra("\n");
									texte.addExtra("UUID:§b "+p.getUniqueId().toString()+"\n");
									texte.addExtra("\n");
									if (!gameState.hasRoleNull(p.getUniqueId())) {
										RoleBase role = gameState.getGamePlayer().get(p.getUniqueId()).getRole();
										texte.addExtra("Role:§b "+role.getOriginTeam().getColor()+role.getName());
										texte.addExtra("\n");
										texte.addExtra("Camp d'origine:§b "+ StringUtils.replaceUnderscoreWithSpace(role.getOriginTeam().getColor()+role.getOriginTeam().name()));
										texte.addExtra("\n");
										texte.addExtra("Camp actuel:§b "+StringUtils.replaceUnderscoreWithSpace(role.getTeamColor()+role.getTeam().name()));
										texte.addExtra("\n");
										texte.addExtra("Nombre de kill(s):§b "+gameState.getPlayerKills().get(p.getUniqueId()).size());
										texte.addExtra("\n");
										texte.addExtra("Joueurs tués: ");
										TextComponent hver = new TextComponent("§b[?]");
										hver.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent(!gameState.getPlayerKills().get(p.getUniqueId()).isEmpty() ? getListPlayers(gameState.getPlayerKills().get(p.getUniqueId())) : "§7Aucun")}));
										texte.addExtra(hver);
										texte.addExtra("\n");
									} else {
										texte.addExtra("Role:§b Aucun");

									}
									((Player) sender).spigot().sendMessage(texte);
									if (p.isOp()) {
										p.sendMessage(((Player) sender).getDisplayName()+" a obtenu des informations sur vous");
										p.spigot().sendMessage(texte);
									}
                                }
                            } else {
								sender.sendMessage("Il faut préciser un joueur");
                            }
                            return true;
                        }
					}
				}
			}//args.length == 2
			if (args.length == 3) {
				if (args[0].equalsIgnoreCase("time")) {
					if (args[1].equalsIgnoreCase("set")) {
						Integer anint = Integer.parseInt(args[2]);
						System.out.println(anint);
						gameState.setInGameTime(anint);
					}
				}
				if (args[0].equalsIgnoreCase("deadrole")) {
					if (args[1].equalsIgnoreCase("add")) {
						for (Roles roles : GameState.Roles.values()) {
							if (roles.name().equalsIgnoreCase(args[2])) {
								if (!gameState.getAttributedRole().contains(roles)) {
									gameState.getAttributedRole().add(roles);
								}
								gameState.getDeadRoles().add(roles);
								break;
							}
						}
					} else if (args[1].equalsIgnoreCase("del")) {
						for (Roles roles : GameState.Roles.values()) {
							if (roles.name().equalsIgnoreCase(args[2])) {
								gameState.getDeadRoles().remove(roles);
								break;
							}
						}
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("camp")) {
					Player p = Bukkit.getPlayer(args[1]);
					if (p != null) {
						if (!gameState.hasRoleNull(p.getUniqueId())) {
							for (TeamList team : TeamList.values()) {
								if (team.name().equalsIgnoreCase(args[2])) {
									gameState.getGamePlayer().get(p.getUniqueId()).getRole().setTeam(team);
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
	private String getListPlayers(HashMap<Player, RoleBase> hashMap) {
		StringBuilder sb = new StringBuilder();
		for (Player p : hashMap.keySet()) {
			sb.append("§f").append(p.getName()).append("§7 (").append(hashMap.get(p).getTeam().getColor()).append(hashMap.get(p).getName()).append("§7) ");
		}
		return sb.toString();
	}
}
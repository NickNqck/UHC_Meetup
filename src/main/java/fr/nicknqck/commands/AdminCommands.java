package fr.nicknqck.commands;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.HubListener;
import fr.nicknqck.Main;
import fr.nicknqck.bijus.Bijus;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.aot.builders.titans.Titans;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.slayers.FFA_Pourfendeur;
import fr.nicknqck.roles.ds.slayers.Pourfendeur;
import fr.nicknqck.scenarios.impl.FFA;
import fr.nicknqck.utils.CC;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.packets.NMSPacket;
import fr.nicknqck.utils.rank.ChatRank;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class AdminCommands implements CommandExecutor{

	private GameState gameState;
	
	public AdminCommands(GameState gameState) {this.gameState = gameState;}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (gameState == null) {
			this.gameState = GameState.getInstance();
		}
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("createloadworld")){
                try {
                    Main.createLoadWorld();
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
				return true;
			}
			if (args[0].equalsIgnoreCase("vie")){
				if (args.length == 3){
					Player target = Bukkit.getPlayer(args[1]);
					double damage = Double.parseDouble(args[2]);
					if (target != null){
						target.setHealth(target.getHealth()-damage);
					}
				}
			}
			if (args[0].equalsIgnoreCase("list")) {
				if ((sender instanceof Player && ChatRank.hasRank(((Player) sender).getUniqueId()) && ChatRank.isHost(((Player) sender).getUniqueId())) || sender.hasPermission("Host")) {
					gameState.getPlayerRoles().forEach((key, value) -> sender.sendMessage("§7 -§f "+key.getName()+"§7 -> "+value.getTeamColor()+value.getRoles().name()));
					return true;
				}
			}
			if (args[0].equalsIgnoreCase("name")) {
				if (sender instanceof Player) {
					if (sender.isOp() || gameState.getHost().contains(((Player) sender).getUniqueId())) {
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
					if (!gameState.hasRoleNull(player)) {
						gameState.getPlayerRoles().get(player).resetCooldown();
						player.sendMessage("§fVos cooldown on été réinitialisé !");
						if (gameState.BijusEnable) {
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
					if (player.isOp() || gameState.getHost().contains(player.getUniqueId())) {
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
									Main.getInstance().gameWorld.setTime(13000);
									gameState.t = gameState.timeday;
									return true;
									
						} else if (args[0].equalsIgnoreCase("jour")) {
                            gameState.nightTime = false;
                            Bukkit.broadcastMessage("");
                            Bukkit.broadcastMessage(ChatColor.RED+"!"+ChatColor.BOLD+"ALERT"+"! "+ChatColor.RESET+ChatColor.BOLD+"Un administrateur à changer le temp, il fait maintenant jour");
                            Bukkit.broadcastMessage("");
                            gameState.t = gameState.timeday;
                            Main.getInstance().gameWorld.setTime(0);
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
						if (sender.isOp() || gameState.getHost().contains(((Player) sender).getUniqueId())) {
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
						if (sender.isOp() || gameState.getHost().contains(((Player) sender).getUniqueId())) {
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
				if (sender instanceof Player) {
					if (ChatRank.isHost(((Player) sender).getUniqueId())){
						Player p = Bukkit.getPlayer(args[1]);
						if (p != null){
							if (gameState.getServerState().equals(ServerStates.InGame)){
								if (!gameState.hasRoleNull(p) && !gameState.getInSpecPlayers().contains(p)){
									for (TeamList team : TeamList.values()){
										if (args[0].equalsIgnoreCase(team.name())){
											if (!gameState.getPlayerRoles().get(p).getOriginTeam().equals(team)){
												gameState.getPlayerRoles().get(p).setTeam(team);
												sender.sendMessage("Le joueur§6 "+p.getName()+"§r est bel et bien devenue"+team.getColor()+" "+team.name());
												p.sendMessage("Vous appartenez maintenant au camp des"+team.getColor()+" "+team.name());
												GameListener.SendToEveryone("Un joueur à rejoint le camp des "+team.getColor()+team.name()+"§r par un Administrateur/Host");
												return true;
											}
										}
									}
								}
							}
						} else {
							sender.sendMessage(args[1]+"§c n'est pas connecté !");
							return true;
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
									if (gameState.getHost().contains(p.getUniqueId())) {
										sender.sendMessage("Cette personne est déjà host...");
                                    } else {
										p.addAttachment(Main.getInstance(), "Host", true);
										gameState.getHost().add(p.getUniqueId());
										sender.sendMessage("Vous avez ajouter "+p.getName()+" à la list(e) des hosts");
										Bukkit.broadcastMessage(p.getName()+" est maintenant host");
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
						if (sender.isOp() || gameState.getHost().contains(((Player) sender).getUniqueId())) {
							if (args[1] != null) {
								Player p = Bukkit.getPlayer(args[1]);
								if (p == null) {
									sender.sendMessage("Veuiller indiquer un pseudo correcte");
                                } else {
									if (gameState.getHost().contains(p.getUniqueId())) {
										gameState.getHost().remove(p.getUniqueId());
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
				if (args[0].equalsIgnoreCase("cheat")) {
					if (sender instanceof Player) {
						Player s = (Player) sender;
						if (s.isOp() || gameState.getHost().contains(s.getUniqueId())) {
							if (args[1] != null) {
								Player p = Bukkit.getPlayer(args[1]);
								if (p == null) {
									sender.sendMessage("Veuiller indiquer un pseudo correcte");
									return true;
								} else {
									if (!gameState.hasRoleNull(p)) {
										if (gameState.getPlayerRoles().containsKey(p)) {
											if (gameState.getPlayerRoles().get(p).getRoles() == Roles.Slayer) {
                                                RoleBase r;
                                                if (FFA.getFFA()) {
                                                    r = gameState.getPlayerRoles().get(p);
													FFA_Pourfendeur fp = (FFA_Pourfendeur) r;
													fp.cheat = true;
													fp.owner.sendMessage("Vous avez bien cheater pour obtenir le souffle de l'univers");
                                                } else {
                                                    r = gameState.getPlayerRoles().get(s);
													Pourfendeur fp = (Pourfendeur) r;
													fp.owner.sendMessage("");
                                                }
                                                return true;
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
						if (sender.isOp() || gameState.getHost().contains(((Player) sender).getUniqueId())) {
							if (args[1] != null) {
								Player p = Bukkit.getPlayer(args[1]);
								if (p == null) {
									sender.sendMessage("Veuiller indiquer un pseudo correcte");
									return true;
								} else {
									if (gameState.getPlayerRoles().containsKey(p)) {
										sender.sendMessage(new String[] {
												"§bVoici les effets du joueur "+p.getName()+ChatColor.DARK_GRAY+"§o§m-----------------------------------",
												"",
												AllDesc.Resi+": "+ gameState.getPlayerRoles().get(p).getResi()+"% + " +gameState.getPlayerRoles().get(p).getBonusResi()+"%",
												"",
												ChatColor.RED+"Force: "+ gameState.getPlayerRoles().get(p).getForce()+"% + "+gameState.getPlayerRoles().get(p).getBonusForce()+"%",
												"",
												ChatColor.AQUA+"Speed: "+gameState.getPlayerRoles().get(p).owner.getWalkSpeed(),
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
						if (sender.isOp() || gameState.getHost().contains(((Player) sender).getUniqueId())) {
							if (args[1] != null) {
								Player p = Bukkit.getPlayer(args[1]);
								if (p == null) {
									sender.sendMessage("Veuiller indiquer un pseudo correcte");
									return true;
								} else {
									if (gameState.getPlayerRoles().containsKey(p)) {
									Bukkit.broadcastMessage("Le joueur: "+sender.getName()+" connais maintenant le rôle du joueur: "+p.getName());
									sender.sendMessage("Le rôle de la personne: "+p.getName()+" est "+gameState.getPlayerRoles().get(p).getOriginTeam().getColor()+gameState.getPlayerRoles().get(p).getRoles().name());
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
						if (sender.isOp() || gameState.getHost().contains(((Player) sender).getUniqueId())) {
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
									if (!gameState.hasRoleNull(p)) {
										RoleBase role = gameState.getPlayerRoles().get(p);
										texte.addExtra("Role:§b "+role.getName());
										texte.addExtra("\n");
										texte.addExtra("Camp d'origine:§b "+ StringUtils.replaceUnderscoreWithSpace(role.getOriginTeam().getColor()+role.getOriginTeam().name()));
										texte.addExtra("\n");
										texte.addExtra("Camp actuel:§b "+StringUtils.replaceUnderscoreWithSpace(role.getTeamColor()+role.getTeam().name()));
										texte.addExtra("\n");
										texte.addExtra("Nombre de kill(s):§b "+gameState.getPlayerKills().get(p).size());
										texte.addExtra("\n");
										texte.addExtra("Joueurs tués: ");
										TextComponent hver = new TextComponent("§b[?]");
										hver.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent(!gameState.getPlayerKills().get(p).isEmpty() ? getListPlayers(gameState.getPlayerKills().get(p)) : "§7Aucun")}));
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
				if (args[0].equalsIgnoreCase("camp")) {
					Player p = Bukkit.getPlayer(args[1]);
					if (p != null) {
						if (!gameState.hasRoleNull(p)) {
							for (TeamList team : TeamList.values()) {
								if (team.name().equalsIgnoreCase(args[2])) {
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
	private String getListPlayers(HashMap<Player, RoleBase> hashMap) {
		StringBuilder sb = new StringBuilder();
		for (Player p : hashMap.keySet()) {
			sb.append("§f").append(p.getName()).append("§7 (").append(hashMap.get(p).getName()).append("§7) ");
		}
		return sb.toString();
	}
}
package fr.nicknqck.commands.roles;

import java.util.ArrayList;
import java.util.List;

import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.IRole;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.roles.ns.builders.UchiwaRoles;
import fr.nicknqck.roles.ns.orochimaru.edotensei.Kabuto;
import fr.nicknqck.roles.ns.solo.jubi.Obito;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Power;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.bijus.BijuListener;
import fr.nicknqck.bijus.Bijus;
import fr.nicknqck.roles.builder.TeamList;

public class NsCommands implements CommandExecutor {
	final GameState gameState;
	public NsCommands(final GameState gameState) {
		this.gameState = gameState;
	}
	public List<Player> getListPlayerFromRole(Roles roles){
		List<Player> toReturn = new ArrayList<>();
		Bukkit.getOnlinePlayers().stream().filter(e -> !gameState.hasRoleNull(e)).filter(e -> gameState.getPlayerRoles().get(e).getRoles() == roles).forEach(e -> toReturn.add(e.getPlayer()));
		return toReturn;
	}
	@Override
	public boolean onCommand(CommandSender send, Command cmd, String arg, String[] args) {
		if (send instanceof Player) {
			Player sender = (Player) send;
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("roles")) {
					sender.sendMessage(gameState.getRolesList());
					return true;
				}
				if (args[0].equalsIgnoreCase("intelligences")) {
					final List<NSRoles> genie = new ArrayList<>();
					final List<NSRoles> intelligent = new ArrayList<>();
					final List<NSRoles> moyenne = new ArrayList<>();
					final List<NSRoles> peu = new ArrayList<>();
					for (final GamePlayer gamePlayer : gameState.getGamePlayer().values()) {
						if (!gamePlayer.isAlive())continue;
						if (gamePlayer.getRole() == null)continue;
						if (gamePlayer.getRole() instanceof NSRoles) {
							NSRoles role = (NSRoles) gamePlayer.getRole();
							if (role.getIntelligence().equals(Intelligence.GENIE)) {
								genie.add(role);
							} else if (role.getIntelligence().equals(Intelligence.INTELLIGENT)) {
								intelligent.add(role);
							} else if (role.getIntelligence().equals(Intelligence.MOYENNE) || role.getIntelligence().equals(Intelligence.CONNUE)) {
								moyenne.add(role);
							} else if (role.getIntelligence().equals(Intelligence.PEUINTELLIGENT)) {
								peu.add(role);
							}
						}
					}
					if (genie.isEmpty() && intelligent.isEmpty() && moyenne.isEmpty() && peu.isEmpty()) {
						sender.sendMessage("§7Aucune personne n'a de rôle venant de l'univers de§a Naruto§7 n'est présent en jeu/vie");
						return true;
					}
					sender.sendMessage("§7Voici la liste des§c roles§7 avec leurs§a intelligence§7:");
					sender.sendMessage("");
					if (!genie.isEmpty()) {
						sender.sendMessage("§2§lGénie: ");
						StringBuilder sb = new StringBuilder();
						for (NSRoles roles : genie) {
							sb.append(roles.getOriginTeam().getColor()).append(roles.getName()).append("§7, ");
						}
						String string = sb.substring(0, sb.toString().length()-3)+".";
						sender.sendMessage(string);
					}
					if (!intelligent.isEmpty()) {
						sender.sendMessage("§a§lIntelligent: ");
						StringBuilder sb = new StringBuilder();
						for (NSRoles roles : intelligent) {
							sb.append(roles.getOriginTeam().getColor()).append(roles.getName()).append("§7, ");
						}
						String string = sb.substring(0, sb.toString().length()-3)+".";
						sender.sendMessage(string);
					}
					if (!moyenne.isEmpty()) {
						sender.sendMessage("§e§lMoyenne: ");
						StringBuilder sb = new StringBuilder();
						for (NSRoles roles : moyenne) {
							sb.append(roles.getOriginTeam().getColor()).append(roles.getName()).append("§7, ");
						}
						String string = sb.substring(0, sb.toString().length()-3)+".";
						sender.sendMessage(string);
					}
					if (!peu.isEmpty()) {
						sender.sendMessage("§c§lPeu Intelligent: ");
						StringBuilder sb = new StringBuilder();
						for (NSRoles roles : peu) {
							sb.append(roles.getOriginTeam().getColor()).append(roles.getName()).append("§7, ");
						}
						String string = sb.substring(0, sb.toString().length()-3)+".";
						sender.sendMessage(string);
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("uchirang")) {
					sender.sendMessage("§7Voici la liste des rangs des§4§l Uchiwas§7:");
					sender.sendMessage("");
					List<UchiwaRoles> legendaire = new ArrayList<>();
					List<UchiwaRoles> cool = new ArrayList<>();
					List<UchiwaRoles> useless = new ArrayList<>();
					if (gameState.getServerState().equals(GameState.ServerStates.InGame)) {
						for (GamePlayer gamePlayer : GameState.getInstance().getGamePlayer().values()) {
							if (gamePlayer.getRole() == null)continue;
							if (gamePlayer.getRole() instanceof UchiwaRoles) {
								if (((UchiwaRoles) gamePlayer.getRole()).getUchiwaType().equals(UchiwaRoles.UchiwaType.LEGENDAIRE)) {
									legendaire.add((UchiwaRoles) gamePlayer.getRole());
								} else if (((UchiwaRoles) gamePlayer.getRole()).getUchiwaType().equals(UchiwaRoles.UchiwaType.IMPORTANT)) {
									cool.add((UchiwaRoles) gamePlayer.getRole());
								} else if (((UchiwaRoles) gamePlayer.getRole()).getUchiwaType().equals(UchiwaRoles.UchiwaType.INUTILE)) {
									useless.add((UchiwaRoles) gamePlayer.getRole());
								}
							}
						}
					} else {
						for (IRole iRole : Main.getInstance().getRoleManager().getRolesRegistery().values()) {
							if (iRole instanceof UchiwaRoles) {
								if (((UchiwaRoles) iRole).getUchiwaType().equals(UchiwaRoles.UchiwaType.LEGENDAIRE)) {
									legendaire.add((UchiwaRoles) iRole);
								} else if (((UchiwaRoles) iRole).getUchiwaType().equals(UchiwaRoles.UchiwaType.IMPORTANT)) {
									cool.add((UchiwaRoles) iRole);
								} else if (((UchiwaRoles) iRole).getUchiwaType().equals(UchiwaRoles.UchiwaType.INUTILE)) {
									useless.add((UchiwaRoles) iRole);
								}
							}
						}
					}
					if (!legendaire.isEmpty()) {
						sender.sendMessage("§dLégendaire§7: ");
						StringBuilder sb = new StringBuilder();
						for (UchiwaRoles uRoles : legendaire) {
							sb.append(uRoles.getOriginTeam().getColor()).append(uRoles.getName()).append("§7, ");
						}
						String string = sb.substring(0, sb.toString().length()-3)+".";
						sender.sendMessage(string);
						sender.sendMessage("");
					}
					if (!cool.isEmpty()) {
						sender.sendMessage("§cImportant§7: ");
						StringBuilder sb = new StringBuilder();
						for (UchiwaRoles uRoles : cool) {
							sb.append(uRoles.getOriginTeam().getColor()).append(uRoles.getName()).append("§7, ");
						}
						String string = sb.substring(0, sb.toString().length()-3)+".";
						sender.sendMessage(string);
						sender.sendMessage("");
					}
					if (!useless.isEmpty()) {
						sender.sendMessage("§aInutile§7: ");
						StringBuilder sb = new StringBuilder();
						for (UchiwaRoles uRoles : useless) {
							sb.append(uRoles.getOriginTeam().getColor()).append(uRoles.getName()).append("§7, ");
						}
						String string = sb.substring(0, sb.toString().length()-3)+".";
						sender.sendMessage(string);
						sender.sendMessage("");
					}
					return true;
				}
				if (!gameState.hasRoleNull(sender)) {
					if (args[0].equalsIgnoreCase("me")) {
						gameState.sendDescription(sender);
						return true;
					}
					if (args[0].equalsIgnoreCase("see")) {
						if (args.length == 3) {
							Player target = Bukkit.getPlayer(args[1]);
							if (target != null) {
								String toRegister = "";
								for (Roles r : Roles.values()) {
									if (args[2].equalsIgnoreCase(r.name())) {
										toRegister = r.getTeam().getColor()+r.name()+" ";
										break;
									}
								}
								if (toRegister.isEmpty()) {
									for (TeamList t : TeamList.values()) {
										if (args[2].equalsIgnoreCase(t.name())) {
											toRegister = t.getColor()+t.name()+" ";
											break;
										}
									}
								}
						//		gameState.getPlayerRoles().get(sender).customName.remove(target.getUniqueId(), gameState.getPlayerRoles().get(sender));
								if (!toRegister.isEmpty()) {
                                    sender.sendMessage("§7Feature non dev sorry");
								}
                            } else {
								sender.sendMessage("§7Le joueur ciblé n'existe pas.");
                            }
                        } else {
							sender.sendMessage("§7La commande est§6 /ns see <joueur> <role/camp>");
                        }
                        return true;
                    }
					if (args[0].equalsIgnoreCase("jubicraft")) {
						if (getListPlayerFromRole(Roles.Obito).contains(sender) || getListPlayerFromRole(Roles.Madara).contains(sender)) {
							int countBiju = 0;
							for (Bijus b : Bijus.values()) {
								if (sender.getInventory().contains(b.getBiju().getItem())) {
									countBiju++;
								}
							}
							for (ItemStack item : sender.getInventory().getContents()) {
								if (item != null) {
									if (item.getType() != Material.AIR) {
										if (item.hasItemMeta()) {
											if (item.getItemMeta().hasDisplayName()) {
												if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§6Kyubi") || item.getItemMeta().getDisplayName().equalsIgnoreCase("§dGyûki")) {
													countBiju++;
												}
											}
										}
									}
								}
							}
							if (countBiju >= 6) {
								for (Bijus b : Bijus.values()) {
									sender.getInventory().removeItem(b.getBiju().getItem());
									b.getBiju().onJubiInvoc(sender);
									b.getBiju().setHote(sender.getUniqueId());
								}
								gameState.setJubiCrafter(sender);
								GameListener.SendToEveryone("");
								GameListener.SendToEveryone("§c§lLe Jûbi à été invoquée !");
								GameListener.SendToEveryone("");
								for (Player p : Bukkit.getOnlinePlayers()) {
									gameState.getPlayerRoles().get(sender).playSound(p, "mob.enderdragon.end");
								}
								//Pour la liste des sons
								//https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/mapping-and-modding-tutorials/2213619-1-8-all-playsound-sound-arguments
								gameState.getPlayerRoles().get(sender).giveItem(sender, true, BijuListener.getInstance().JubiItem());
                            } else {
								send.sendMessage("§7Vous n'avez pas asser de§d Biju");
                            }
                        } else {
							send.sendMessage("§7Vous n'avez pas la puissance pour devenir l'hôte de§d Jûbi");
                        }
                        return true;
                    }
					if (!gameState.hasRoleNull(sender) && gameState.getPlayerRoles().get(sender).getGamePlayer().isAlive() && gameState.getPlayerRoles().get(sender) instanceof NSRoles){
						NSRoles role = (NSRoles) gameState.getPlayerRoles().get(sender);
						role.onNsCommand(args);
						if (!role.getPowers().isEmpty()) {
							for (Power power : role.getPowers()) {
								if (power instanceof CommandPower) {
									((CommandPower) power).call(args, CommandPower.CommandType.NS, sender);
								}
							}
						}
						if (role instanceof Obito) {
							for (RoleBase r : gameState.getPlayerRoles().values()) {
								if (r.getGamePlayer().isAlive()) {
									if (r instanceof Kabuto) {
										Kabuto kabuto = (Kabuto) r;
										kabuto.onObitoCommand(args, (Obito)role);
									}
								}
							}
						}
					}
					return true;
				}
			}
		}
		return true;
	}
}
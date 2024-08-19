package fr.nicknqck.commands.roles;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.events.Events;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.roles.ds.demons.Muzan;
import fr.nicknqck.roles.ds.demons.lune.Kaigaku;
import fr.nicknqck.roles.ds.demons.lune.Kokushibo;
import fr.nicknqck.roles.ds.solos.JigoroV2;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class DSmtpCommands implements CommandExecutor {
	GameState gameState;
	
	public DSmtpCommands(GameState gameState) {this.gameState = gameState;}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		if (args.length >= 1) {
            for (Events e : Events.values()) {
                if (e.getEvent().onSubDSCommand((Player) sender, args)) {
                    return true;
                }
            }
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("role")) {
                    Player player = (Player) sender;
                    if (gameState.getInGamePlayers().contains(player.getUniqueId())
                            && gameState.getPlayerRoles().containsKey(player)) {
                        gameState.getPlayerRoles().get(player).OpenFormInventory(gameState);
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("list")) {
                    Player player = (Player) sender;
                    ArrayList<String> message = new ArrayList<>();
                    message.add(ChatColor.GOLD+"Liste des rôles potentiellement en jeu (composition de la partie non actuallisé):");
                    if (gameState.getServerState() == ServerStates.InLobby) {
                        int roleNmb = 0;
                        for (Roles r : gameState.getAvailableRoles().keySet()) {
                            System.out.println("role: "+r+", nmb: "+gameState.getAvailableRoles().get(r));
                            roleNmb += gameState.getAvailableRoles().get(r);
                            player.sendMessage("§crole§r:§6 "+r+"§r,§c nmb§r:§6 "+gameState.getAvailableRoles().get(r));
                            System.out.println(roleNmb);
                        }
                        return true;
                    }
                    for (Roles r : gameState.getAvailableRoles().keySet()) {
                        int nmb = 0;
                        for (RoleBase role : gameState.getPlayerRoles().values()) {
                            if (role.getOldRole() == r || role.getRoles() == r) {
                                nmb += 1;
                            }
                        }
                        if (nmb > 0) {
                            message.add(ChatColor.DARK_PURPLE+"("+ChatColor.RED+nmb+ChatColor.DARK_PURPLE+") : "+ChatColor.GOLD+r.name()+ChatColor.DARK_PURPLE+",");
                        }
                    }
                    player.sendMessage(message.toArray(new String[message.size()]));
                    return true;
                } else if (args[0].equalsIgnoreCase("roles")) {
                    //ArrayList<String> message = new ArrayList<String>();
                    sender.sendMessage(gameState.getRolesList());
                    return true;

                } else if (args[0].equalsIgnoreCase("doc")){
                    Player player = (Player) sender;
                    ArrayList<String> message = new ArrayList<String>();
                    message.add(ChatColor.BOLD+"Aucun document disponible, (pour l'instant)");
                    player.sendMessage(message.toArray(new String[message.size()]));
                    return true;
                } else if (args[0].equalsIgnoreCase("lame")){
                    Player player = (Player) sender;
                    ArrayList<String> message = getStrings();
                    player.sendMessage(message.toArray(new String[message.size()]));
                    return true;
                } else if (args[0].equalsIgnoreCase("effect")) {
						ArrayList<String> message = new ArrayList<String>();
						if (gameState.getPlayerRoles().get(sender) == null) return false;
						message.add(ChatColor.AQUA+"Effect: "+ChatColor.DARK_GRAY+"§o§m-----------------------------------");
						message.add("");
						message.add(ChatColor.GREEN+"Résistance: "+ gameState.getPlayerRoles().get(sender).getResi()+"% + " +gameState.getPlayerRoles().get(sender).getBonusResi()+"%");
						message.add("");
						message.add(ChatColor.RED+"Force: 20% + "+gameState.getPlayerRoles().get(sender).getBonusForce()+"%");
						message.add("");
						message.add(ChatColor.AQUA+"Speed: "+gameState.getPlayerRoles().get(sender).owner.getWalkSpeed());
						message.add("");
						message.add(ChatColor.DARK_GRAY+"§o§m-----------------------------------");
						message.add(ChatColor.RED+"Disclaimer: §ril faut avoir l'effet de potion (sauf pour le % de speed) concerné pour que le % sois appliqué");
						sender.sendMessage(message.toArray(new String[message.size()]));
						return true;
					}
				if (gameState.getServerState() == ServerStates.InGame) {
					if (args[0].equalsIgnoreCase("me") || args[0].equalsIgnoreCase("role")) {
                        gameState.sendDescription((Player) sender);
                        return true;
                    }
				}//vérification du serverstates.ingame
			}else if (args.length == 2) { //else du args.length == 1
				if (args[0].equalsIgnoreCase("dimtp")) {
                    Player player = (Player) sender;
                    int x = 0;
                    x = Integer.parseInt(args[1]);
                    player.teleport(new Location(Bukkit.getWorlds().get(x), 0, 152, 0));
                    return true;
                }
					}//} du args.length
			
			if (args[0].equalsIgnoreCase("chat")) {
                if (gameState.getPlayerRoles().containsKey(sender)) {
                    if (!gameState.getInGamePlayers().contains(((Player) sender).getUniqueId())) return false;
//Debut chat muzan kokushibo
                    if (gameState.getPlayerRoles().get(sender) instanceof Kokushibo || gameState.getPlayerRoles().get(sender) instanceof Muzan) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            sb.append(" ");
                            sb.append(args[i]);
                        }
                        String name2 = sb.toString();
                        String uwu = "(§c" + gameState.getPlayerRoles().get(sender).getRoles().name() + "§r)§c§l " + sender.getName() + "§r : " + name2;
                        for (UUID u : gameState.getInGamePlayers()) {
                            Player p = Bukkit.getPlayer(u);
                            if (p == null)continue;
                            if (gameState.getPlayerRoles().containsKey(p)) {
                                if (gameState.getPlayerRoles().get(sender) instanceof Kokushibo) {
                                    if (gameState.getPlayerRoles().get(p) instanceof Muzan) {
                                        p.sendMessage(uwu);
                                        sender.sendMessage(uwu);
                                        return true;
                                    }
                                }
                                if (gameState.getPlayerRoles().get(sender) instanceof Muzan) {
                                    if (gameState.getPlayerRoles().get(p) instanceof Kokushibo) {
                                        p.sendMessage(uwu);
                                        sender.sendMessage(uwu);
                                        return true;
                                    }
                                }
                            }
                        }
                    }//Fin chat muzan kokushibo
//Debut chat JigoroV2 Kaigaku
                    if (gameState.getPlayerRoles().get(sender) instanceof JigoroV2 || gameState.getPlayerRoles().get(sender) instanceof Kaigaku) {//vérifie si le rôle du sender
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            sb.append(" ");
                            sb.append(args[i]);
                        }
                        String name2 = sb.toString();
                        String owo = "(§6" + gameState.getPlayerRoles().get(sender).getRoles().name() + "§r)§6§l " + sender.getName() + "§r : " + name2;
                        if (gameState.JigoroV2Pacte2) {
                            for (UUID u : gameState.getInGamePlayers()) {
                                Player p = Bukkit.getPlayer(u);
                                if (p == null)continue;
                                if (gameState.getPlayerRoles().containsKey(p)) {
                                    if (gameState.getPlayerRoles().get(sender) instanceof JigoroV2) {
                                        if (gameState.getPlayerRoles().get(p) instanceof Kaigaku) {
                                            p.sendMessage(owo);
                                            sender.sendMessage(owo);
                                            return true;
                                        }
                                    }
                                    if (gameState.getPlayerRoles().get(sender) instanceof Kaigaku) {
                                        if (gameState.getPlayerRoles().get(p) instanceof JigoroV2) {
                                            p.sendMessage(owo);
                                            sender.sendMessage(owo);
                                            return true;
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }

			if (gameState.getServerState() == ServerStates.InGame) {
				if (gameState.getPlayerRoles().containsKey(sender) && gameState.getPlayerRoles().get(sender) instanceof DemonsSlayersRoles) {
					DemonsSlayersRoles role = (DemonsSlayersRoles) gameState.getPlayerRoles().get(sender);
					if (role.getGamePlayer().isAlive()) {
						((DemonsSlayersRoles) gameState.getPlayerRoles().get(sender)).onDSCommandSend(args, gameState);
					}
					return true;
				}
			}
		}
	  	if (gameState.getServerState() == ServerStates.InGame) {
			String premsg = "§7§l ┃ §r§6";
			sender.sendMessage(ChatColor.GOLD+"CommandHelper"+"§7§o§m-----------------------------------");//starting message
			sender.sendMessage(premsg+"/ds roles§r: Permet de voir la list (actualisé) des roles présent dans la partie");
			sender.sendMessage(premsg+"/ds list§r: Affiche la list (non actualisé) des roles présent dans la partie");
			sender.sendMessage(premsg+"/ds doc§r: Donne le lien contenant le doc du mode de jeux");
			sender.sendMessage(premsg+"/ds discord§r: Donne le lien du discord du mode de jeux");
			sender.sendMessage(premsg+"/ds lame§r: Donne des informations sur les lames du mode de jeux");
			sender.sendMessage(premsg+"/ds nuit§r: Met la nuit (si vous êtes en jeux)");
			sender.sendMessage(premsg+"/ds jour§r: Met le jour (si vous êtes en jeux)");
			sender.sendMessage(premsg+"/ds effect§r: Vous donne des informations sur vos effet");
			sender.sendMessage(ChatColor.GOLD+"CommandHelper"+"§7§o§m-----------------------------------");//end message
			return true;	
		} 
		sender.sendMessage("§cCommande inconnu !");
		return true;
	}

	private static ArrayList<String> getStrings() {
		ArrayList<String> message = new ArrayList<String>();
		message.add(ChatColor.GOLD+"List des lames possibles : ");
		message.add(ChatColor.GOLD + "Lame Coeur :" + ChatColor.LIGHT_PURPLE + " Donne 2 coeurs en plus permanent jusqu'a la fin de la partit.");
		message.add(ChatColor.GOLD+"Lame de Résistance :"+ChatColor.GRAY+" Donne + 10% de résistance jusqu'a la fin de la partit.");
		message.add(ChatColor.GOLD+"Lame de Speed :"+ ChatColor.GREEN+ " Donne + 10% de speed jusqu'a la fin de la partit.");
		message.add("§6Lame de Force:§c Donne +10% de force jusqu'a la fin de la partit");
		message.add(ChatColor.GOLD+"Lame de Fire Résistance :"+ ChatColor.YELLOW+" Donne l'effet fire résistance 1 jusqu'a la fin de la partit.");
		return message;
	}

}	
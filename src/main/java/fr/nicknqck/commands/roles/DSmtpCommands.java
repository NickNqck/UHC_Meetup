package fr.nicknqck.commands.roles;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.roles.ds.demons.lune.Kaigaku;
import fr.nicknqck.roles.ds.solos.JigoroV2;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Power;
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

    private final GameState gameState;
	
	public DSmtpCommands(GameState gameState) {this.gameState = gameState;}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		if (args.length >= 1) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("role")) {
                    Player player = (Player) sender;
                    if (gameState.getInGamePlayers().contains(player.getUniqueId()) && !gameState.hasRoleNull(player.getUniqueId())) {
                        gameState.getGamePlayer().get(player.getUniqueId()).getRole().OpenFormInventory(gameState);
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
                    for (final Roles r : gameState.getAvailableRoles().keySet()) {
                        int nmb = 0;
                        for (final GamePlayer gamePlayer : gameState.getGamePlayer().values()) {
                            if (gamePlayer.getRole() == null)continue;
                            final RoleBase role = gamePlayer.getRole();
                            if (role.getOldRole() == r || role.getRoles() == r) {
                                nmb += 1;
                            }
                        }
                        if (nmb > 0) {
                            message.add(ChatColor.DARK_PURPLE+"("+ChatColor.RED+nmb+ChatColor.DARK_PURPLE+") : "+ChatColor.GOLD+r.name()+ChatColor.DARK_PURPLE+",");
                        }
                    }
                    player.sendMessage(message.toArray(new String[0]));
                    return true;
                } else if (args[0].equalsIgnoreCase("roles")) {
                    //ArrayList<String> message = new ArrayList<String>();
                    sender.sendMessage(gameState.getRolesList());
                    return true;

                } else if (args[0].equalsIgnoreCase("doc")){
                    Player player = (Player) sender;
                    ArrayList<String> message = new ArrayList<>();
                    message.add(ChatColor.BOLD+"Aucun document disponible, (pour l'instant)");
                    player.sendMessage(message.toArray(new String[0]));
                    return true;
                } else if (args[0].equalsIgnoreCase("lame")){
                    Player player = (Player) sender;
                    ArrayList<String> message = getStrings();
                    player.sendMessage(message.toArray(new String[0]));
                    return true;
                } else if (args[0].equalsIgnoreCase("effect")) {
						ArrayList<String> message = new ArrayList<>();
						if (gameState.hasRoleNull(((Player) sender).getUniqueId())) return false;
                        final RoleBase role = gameState.getGamePlayer().get(((Player) sender).getUniqueId()).getRole();
                        final String speed = Bukkit.getPlayer(role.getPlayer()) != null ? ""+Bukkit.getPlayer(role.getPlayer()).getWalkSpeed() : "?";
						message.add("§bEffets: "+ChatColor.DARK_GRAY+"§o§m-----------------------------------");
						message.add("");
						message.add("§aRésistance: " +role.getBonusResi()+"%");
						message.add("");
						message.add("§cForce: "+role.getBonusForce()+"%");
						message.add("");
						message.add("§bSpeed: "+speed);
						message.add("");
						message.add(ChatColor.DARK_GRAY+"§o§m-----------------------------------");
						message.add("§cDisclaimer: §fHors effet de potions");
						sender.sendMessage(message.toArray(new String[0]));
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
                    int x;
                    x = Integer.parseInt(args[1]);
                    player.teleport(new Location(Bukkit.getWorlds().get(x), 0, 152, 0));
                    return true;
                }
					}//} du args.length
			
			if (args[0].equalsIgnoreCase("chat")) {
                if (!gameState.hasRoleNull(((Player) sender).getUniqueId())) {
                    if (!gameState.getInGamePlayers().contains(((Player) sender).getUniqueId())) return false;
//Debut chat JigoroV2 Kaigaku
                    if (gameState.getGamePlayer().get(((Player) sender).getUniqueId()).getRole() instanceof JigoroV2 || gameState.getGamePlayer().get(((Player) sender).getUniqueId()).getRole() instanceof Kaigaku) {//vérifie si le rôle du sender
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            sb.append(" ");
                            sb.append(args[i]);
                        }
                        String name2 = sb.toString();
                        String owo = "(§6" + gameState.getGamePlayer().get(((Player) sender).getUniqueId()).getRole().getRoles().name() + "§r)§6§l " + sender.getName() + "§r : " + name2;
                        if (gameState.JigoroV2Pacte2) {
                            for (UUID u : gameState.getInGamePlayers()) {
                                Player p = Bukkit.getPlayer(u);
                                if (p == null)continue;
                                if (!gameState.hasRoleNull(u)) {
                                    if (gameState.getGamePlayer().get(((Player) sender).getUniqueId()).getRole() instanceof JigoroV2) {
                                        if (gameState.getGamePlayer().get(u).getRole() instanceof Kaigaku) {
                                            p.sendMessage(owo);
                                            sender.sendMessage(owo);
                                            return true;
                                        }
                                    }
                                    if (gameState.getGamePlayer().get(((Player) sender).getUniqueId()).getRole() instanceof Kaigaku) {
                                        if (gameState.getGamePlayer().get(u).getRole() instanceof JigoroV2) {
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
				if (!gameState.hasRoleNull(((Player) sender).getUniqueId()) && gameState.getGamePlayer().get(((Player) sender).getUniqueId()).getRole() instanceof DemonsSlayersRoles) {
					DemonsSlayersRoles role = (DemonsSlayersRoles) gameState.getGamePlayer().get(((Player) sender).getUniqueId()).getRole();
					if (role.getGamePlayer().isAlive()) {
						role.onDSCommandSend(args, gameState);
                        if (!role.getPowers().isEmpty()) {
                            for (final Power power : role.getPowers()) {
                                if (power instanceof CommandPower) {
                                    ((CommandPower) power).call(args, CommandPower.CommandType.DS, (Player) sender);
                                }
                            }
                        }
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
		ArrayList<String> message = new ArrayList<>();
		message.add(ChatColor.GOLD+"List des lames possibles : ");
		message.add(ChatColor.GOLD + "Lame Coeur :" + ChatColor.LIGHT_PURPLE + " Donne 2 coeurs en plus permanent jusqu'a la fin de la partit.");
		message.add(ChatColor.GOLD+"Lame de Résistance :"+ChatColor.GRAY+" Donne + 10% de résistance jusqu'a la fin de la partit.");
		message.add(ChatColor.GOLD+"Lame de Speed :"+ ChatColor.GREEN+ " Donne + 10% de speed jusqu'a la fin de la partit.");
		message.add("§6Lame de Force:§c Donne +10% de force jusqu'a la fin de la partit");
		message.add(ChatColor.GOLD+"Lame de Fire Résistance :"+ ChatColor.YELLOW+" Donne l'effet fire résistance 1 jusqu'a la fin de la partit.");
		return message;
	}

}	
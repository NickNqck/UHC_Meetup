package fr.nicknqck.commands.roles;

import java.util.ArrayList;
import java.util.List;

import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.interfaces.IRole;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.enums.Intelligence;
import fr.nicknqck.roles.ns.builders.EUchiwaType;
import fr.nicknqck.roles.ns.builders.IUchiwa;
import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Power;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.nicknqck.GameState;

public class NsCommands implements CommandExecutor {
	final GameState gameState;
	public NsCommands(final GameState gameState) {
		this.gameState = gameState;
	}
	public List<Player> getListPlayerFromRole(Roles roles){
		List<Player> toReturn = new ArrayList<>();
		Bukkit.getOnlinePlayers().stream().filter(e -> !gameState.hasRoleNull(e.getUniqueId())).filter(e -> gameState.getGamePlayer().get(e.getUniqueId()).getRole().getRoles() == roles).forEach(e -> toReturn.add(e.getPlayer()));
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
					if (GameState.inGame()) {
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
					} else {
						for (IRole iRole : Main.getInstance().getRoleManager().getRolesRegistery().values()) {
							if (iRole instanceof NSRoles) {
								NSRoles role = (NSRoles) iRole;
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
						sender.sendMessage("");
					}
					if (!intelligent.isEmpty()) {
						sender.sendMessage("§a§lIntelligent: ");
						StringBuilder sb = new StringBuilder();
						for (NSRoles roles : intelligent) {
							sb.append(roles.getOriginTeam().getColor()).append(roles.getName()).append("§7, ");
						}
						String string = sb.substring(0, sb.toString().length()-3)+".";
						sender.sendMessage(string);
						sender.sendMessage("");
					}
					if (!moyenne.isEmpty()) {
						sender.sendMessage("§e§lMoyenne: ");
						StringBuilder sb = new StringBuilder();
						for (NSRoles roles : moyenne) {
							sb.append(roles.getOriginTeam().getColor()).append(roles.getName()).append("§7, ");
						}
						String string = sb.substring(0, sb.toString().length()-3)+".";
						sender.sendMessage(string);
						sender.sendMessage("");
					}
					if (!peu.isEmpty()) {
						sender.sendMessage("§c§lPeu Intelligent: ");
						StringBuilder sb = new StringBuilder();
						for (NSRoles roles : peu) {
							sb.append(roles.getOriginTeam().getColor()).append(roles.getName()).append("§7, ");
						}
						String string = sb.substring(0, sb.toString().length()-3)+".";
						sender.sendMessage(string);
						sender.sendMessage("");
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("uchirang")) {
					sender.sendMessage("§7Voici la liste des rangs des§4§l Uchiwas§7:");
					sender.sendMessage("");
					List<RoleBase> legendaire = new ArrayList<>();
					List<RoleBase> cool = new ArrayList<>();
					List<RoleBase> useless = new ArrayList<>();
					if (gameState.getServerState().equals(GameState.ServerStates.InGame)) {
						for (GamePlayer gamePlayer : GameState.getInstance().getGamePlayer().values()) {
							if (gamePlayer.getRole() == null)continue;
							if (gamePlayer.getRole() instanceof IUchiwa) {
								if (((IUchiwa) gamePlayer.getRole()).getUchiwaType().equals(EUchiwaType.LEGENDAIRE)) {
									legendaire.add(gamePlayer.getRole());
								} else if (((IUchiwa) gamePlayer.getRole()).getUchiwaType().equals(EUchiwaType.IMPORTANT)) {
									cool.add(gamePlayer.getRole());
								} else if (((IUchiwa) gamePlayer.getRole()).getUchiwaType().equals(EUchiwaType.INUTILE)) {
									useless.add(gamePlayer.getRole());
								}
							}
						}
					} else {
						for (IRole iRole : Main.getInstance().getRoleManager().getRolesRegistery().values()) {
							if (iRole instanceof IUchiwa) {
								if (((IUchiwa) iRole).getUchiwaType().equals(EUchiwaType.LEGENDAIRE)) {
									legendaire.add((RoleBase) iRole);
								} else if (((IUchiwa) iRole).getUchiwaType().equals(EUchiwaType.IMPORTANT)) {
									cool.add((RoleBase) iRole);
								} else if (((IUchiwa) iRole).getUchiwaType().equals(EUchiwaType.INUTILE)) {
									useless.add((RoleBase) iRole);
								}
							}
						}
					}
					if (!legendaire.isEmpty()) {
						sender.sendMessage("§dLégendaire§7: ");
						StringBuilder sb = new StringBuilder();
						for (RoleBase uRoles : legendaire) {
							sb.append(uRoles.getOriginTeam().getColor()).append(uRoles.getName()).append("§7, ");
						}
						String string = sb.substring(0, sb.toString().length()-3)+".";
						sender.sendMessage(string);
						sender.sendMessage("");
					}
					if (!cool.isEmpty()) {
						sender.sendMessage("§cImportant§7: ");
						StringBuilder sb = new StringBuilder();
						for (RoleBase uRoles : cool) {
							sb.append(uRoles.getOriginTeam().getColor()).append(uRoles.getName()).append("§7, ");
						}
						String string = sb.substring(0, sb.toString().length()-3)+".";
						sender.sendMessage(string);
						sender.sendMessage("");
					}
					if (!useless.isEmpty()) {
						sender.sendMessage("§aInutile§7: ");
						StringBuilder sb = new StringBuilder();
						for (RoleBase uRoles : useless) {
							sb.append(uRoles.getOriginTeam().getColor()).append(uRoles.getName()).append("§7, ");
						}
						String string = sb.substring(0, sb.toString().length()-3)+".";
						sender.sendMessage(string);
						sender.sendMessage("");
					}
					return true;
				}
				if (!gameState.hasRoleNull(sender.getUniqueId())) {
					if (args[0].equalsIgnoreCase("me")) {
						gameState.sendDescription(sender);
						return true;
					}
					if (!gameState.hasRoleNull(sender.getUniqueId()) && gameState.getGamePlayer().get(sender.getUniqueId()).getRole().getGamePlayer().isAlive() && gameState.getGamePlayer().get(sender.getUniqueId()).getRole() instanceof NSRoles){
						NSRoles role = (NSRoles) gameState.getGamePlayer().get(sender.getUniqueId()).getRole();
						role.onNsCommand(args);
						if (!role.getPowers().isEmpty()) {
							for (Power power : new ArrayList<>(role.getPowers())) {
								if (power == null)continue;
								if (power instanceof CommandPower) {
									((CommandPower) power).call(args, CommandPower.CommandType.NS, sender);
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
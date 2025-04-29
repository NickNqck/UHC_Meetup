package fr.nicknqck.commands.role;

import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.TeamList;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class TeamCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Il faut etre un joueur pour utiliser cette commande.");
            return true;
        }
        if (strings.length >= 2) {
            final TeamList team = findTeam(strings[0]);
            if (team != null) {
                for (String arg : strings) {
                    final Player target = Bukkit.getPlayer(arg);
                    if (target == null) {
                        commandSender.sendMessage(strings[1]+" n'est pas connectée");
                        continue;
                    }
                    final Player sender = (Player) commandSender;
                    if (Main.getInstance().getTabManager().getTeamTabMap().containsKey(sender.getUniqueId())) {
                        final Map<UUID, String> map = Main.getInstance().getTabManager().getTeamTabMap().get(sender.getUniqueId());
                        if (map.containsKey(target.getUniqueId())) {
                            String string = map.get(target.getUniqueId());
                            string = string.replace(map.get(target.getUniqueId()), team.getName());
                            map.replace(target.getUniqueId(), string);
                        } else {
                            map.put(target.getUniqueId(), team.getName());
                        }
                        Main.getInstance().getTabManager().getTeamTabMap().replace(sender.getUniqueId(), map);
                    }
                }
            } else {
                commandSender.sendMessage(strings[0] + "n'a pas été détecter comme étant une team");
            }
            return true;
        }
        return false;
    }
    private TeamList findTeam(@NonNull final String string) {
        TeamList toReturn = null;
        for (@NonNull final TeamList team : TeamList.values()) {
            if (team.getName().toLowerCase().contains(string.toLowerCase()) || team.name().equalsIgnoreCase(string)) {
                toReturn = team;
                break;
            }
        }
        return toReturn;
    }
}

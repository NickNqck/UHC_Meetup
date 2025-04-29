package fr.nicknqck.commands.role;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class RoleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Il faut etre un joueur pour utiliser cette commande.");
            return true;
        }
        if (strings.length == 2) {
            /*if (strings[0].equals("reset")) {
                final Player target = Bukkit.getPlayer(strings[1]);
                if (target != null) {
                    Player sender = (Player) commandSender;
                    if (Main.getInstance().getTabManager().getRoleTabMap().containsKey(sender.getUniqueId())) {
                        final Map<UUID, String> map = Main.getInstance().getTabManager().getRoleTabMap().get(sender.getUniqueId());
                        if (map.containsKey(target.getUniqueId())) {
                            String string = map.get(target.getUniqueId());
                            string = string.replace(map.get(target.getUniqueId()), roles.getTeam().getColor()+roles.getItem().getItemMeta().getDisplayName());
                            map.replace(target.getUniqueId(), string);
                        }
                        Main.getInstance().getTabManager().getRoleTabMap().replace(sender.getUniqueId(), map);
                        return true;
                    }
                } else {
                    commandSender.sendMessage(strings[1]+" n'est pas connectée");
                    return true;
                }
                return true;
            }*/
            GameState.Roles roles = findRoles(strings[0]);
            if (roles != null) {
                final Player target = Bukkit.getPlayer(strings[1]);
                if (target != null) {
                    Player sender = (Player) commandSender;
                    if (Main.getInstance().getTabManager().getRoleTabMap().containsKey(sender.getUniqueId())) {
                        final Map<UUID, String> map = Main.getInstance().getTabManager().getRoleTabMap().get(sender.getUniqueId());
                        if (map.containsKey(target.getUniqueId())) {
                            String string = map.get(target.getUniqueId());
                            string = string.replace(map.get(target.getUniqueId()), roles.getTeam().getColor()+roles.getItem().getItemMeta().getDisplayName());
                            map.replace(target.getUniqueId(), string);
                        } else {
                            map.put(target.getUniqueId(), roles.getTeam().getColor()+roles.getItem().getItemMeta().getDisplayName());
                        }
                        Main.getInstance().getTabManager().getRoleTabMap().replace(sender.getUniqueId(), map);
                        return true;
                    }
                } else {
                    commandSender.sendMessage(strings[1]+" n'est pas connectée");
                    return true;
                }
            } else {
                commandSender.sendMessage(strings[0] + "n'a pas été détecter comme étant un rôle");
                return true;
            }
        }
        return false;
    }

    private GameState.Roles findRoles(@NonNull final String string) {
        GameState.Roles roles = null;
        for (@NonNull final GameState.Roles r : GameState.Roles.values()) {
            if (r.name().toLowerCase().contains(string.toLowerCase())) {
                roles = r;
                break;
            }
        }
        return roles;
    }
}
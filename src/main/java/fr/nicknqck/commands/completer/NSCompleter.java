package fr.nicknqck.commands.completer;

import fr.nicknqck.GameState;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Power;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NSCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length <= 1) {
            final List<String> stringList = new ArrayList<>();
            stringList.add("roles");
            if (!(commandSender instanceof Player))return stringList;
            stringList.add("intelligences");
            stringList.add("uchirang");
            if (GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                stringList.add("me");
                stringList.add("jubicraft");
                if (!GameState.getInstance().hasRoleNull(((Player) commandSender).getUniqueId())) {
                    final List<Power> powerList = new ArrayList<>(GameState.getInstance().getGamePlayer().get(((Player) commandSender).getUniqueId()).getRole().getPowers());
                    for (final Power power : powerList) {
                        if (power instanceof CommandPower) {
                            if (((CommandPower) power).getCommandType().equals(CommandPower.CommandType.NS)) {
                                if (((CommandPower) power).getArg0() != null) {
                                    stringList.add(((CommandPower) power).getArg0());
                                }
                            }
                        }
                    }
                }
            }
            return stringList;
        }
        if (commandSender instanceof Player) {
            if (GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                if (!GameState.getInstance().hasRoleNull(((Player) commandSender).getUniqueId())) {
                    final List<Power> powerList = new ArrayList<>(GameState.getInstance().getGamePlayer().get(((Player) commandSender).getUniqueId()).getRole().getPowers());
                    for (final Power power : powerList) {
                        if (power instanceof CommandPower) {
                            if (((CommandPower) power).getCommandType().equals(CommandPower.CommandType.NS)) {
                                if (((CommandPower) power).getArg0() != null) {
                                    if (((CommandPower) power).getArg0().equalsIgnoreCase(strings[0])) {
                                        if (!((CommandPower) power).getCompletor(strings).isEmpty()) {
                                            return ((CommandPower) power).getCompletor(strings);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        final List<String> stringList = new ArrayList<>();
        for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            stringList.add(onlinePlayer.getName());
        }
        return stringList;
    }

}
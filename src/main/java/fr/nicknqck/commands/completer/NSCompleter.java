package fr.nicknqck.commands.completer;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.TeamList;
import fr.nicknqck.interfaces.IChakraV2;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
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
                final GamePlayer gamePlayer = GamePlayer.of(((Player) commandSender).getUniqueId());
                if (gamePlayer != null) {
                    if (gamePlayer.check()) {
                        if (strings.length == 1) {
                            for (IChakraV2 iChakraV2 : Main.getInstance().getBijuManager().getChakraManager().getLoadedChakra()) {
                                if (!iChakraV2.getMap().containsKey(((Player) commandSender).getUniqueId()))continue;
                                if (iChakraV2.getArg0().toLowerCase().startsWith(strings[0].toLowerCase())) {
                                    stringList.add(iChakraV2.getArg0().toLowerCase());
                                }
                            }
                        }
                        @NonNull final List<Power> powers = new ArrayList<>(gamePlayer.getRole().getPowers());
                        for (Power power : powers) {
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
            if (strings[0] != null) {
                final List<String> list = new ArrayList<>();
                for (final String string : stringList) {
                    if (string.contains(strings[0])) {
                        list.add(string);
                    }
                }
                return list;
            }
            return stringList;
        }
        final List<String> stringList = new ArrayList<>();
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
        for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getName().contains(strings[strings.length-1])){
                stringList.add(onlinePlayer.getName());
            }
        }
        return stringList;
    }

}
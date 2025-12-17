package fr.nicknqck.commands.completer;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.utils.rank.ChatRank;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AdminTabCompletor implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        @NonNull final List<String> stringList = new ArrayList<>();
        if (strings.length == 1) {
            stringList.add("vie");
            stringList.add("list");
            stringList.add("name");
            stringList.add("resetCooldown");
            if (GameState.getInstance().getServerState().equals(GameState.ServerStates.InLobby)) {
                stringList.add("reset");
                if (commandSender instanceof Player && ChatRank.isHost(((Player) commandSender).getUniqueId())) {
                    stringList.add("start");
                    stringList.add("config");
                    stringList.add("pregen");
                    stringList.add("addrole");
                    stringList.add("delrole");
                    stringList.add("addAll");
                    stringList.add("preview");
                }
            }
            if (GameState.inGame()) {
                stringList.add("detect");
                stringList.add("nuit");
                stringList.add("jour");
                if (commandSender instanceof Player) {
                    if (ChatRank.isHost(commandSender)){
                        stringList.add("giveblade");
                        stringList.add("setgroupe");
                        stringList.add("addHost");
                        stringList.add("delHost");
                        stringList.add("effect");
                        stringList.add("role");
                        stringList.add("revive");
                        stringList.add("info");
                        stringList.add("camp");
                    }
                }
            }
            if (strings[0] != null) {
                final List<String> list = new ArrayList<>();
                for (final String string : stringList) {
                    if (string.toLowerCase().startsWith(strings[0].toLowerCase())) {
                        list.add(string);
                    }
                }
                return list;
            }
        }
        if (strings.length == 2) {
            if (strings[0].equalsIgnoreCase("vie") || strings[0].equalsIgnoreCase("resetcooldown")) {
                stringList.addAll(getListOfPlayer(strings[1]));
            }
            if (strings[0].equalsIgnoreCase("addrole") || strings[0].equalsIgnoreCase("delrole")) {
                for (final Roles roles : Roles.values()) {
                    String name = roles.getItem().getItemMeta().getDisplayName();
                    stringList.add(name);
                }
            }
            if (strings[0].equalsIgnoreCase("addhost") ||
                    strings[0].equalsIgnoreCase("delhost") ||
                    strings[0].equalsIgnoreCase("giveblade") ||
                    strings[0].equalsIgnoreCase("effect") ||
                    strings[0].equalsIgnoreCase("role") ||
                    strings[0].equalsIgnoreCase("revive") ||
                    strings[0].equalsIgnoreCase("info") ||
                    strings[0].equalsIgnoreCase("camp")) {
                stringList.addAll(getListOfPlayer(strings[1]));
            }
            if (strings[1] != null) {
                final List<String> list = new ArrayList<>();
                for (final String string : stringList) {
                    if (string.toLowerCase().startsWith(strings[1].toLowerCase())) {
                        list.add(string);
                    }
                }
                return list;
            }
        }
        if (strings.length == 3) {
            if (strings[0].equalsIgnoreCase("camp")) {
                final Player target = Bukkit.getPlayer(strings[1]);
                if (target != null) {
                    for (TeamList value : TeamList.values()) {
                        stringList.add(value.name().toLowerCase());
                    }
                }
            }
            if (strings[2] != null) {
                final List<String> list = new ArrayList<>();
                for (final String string : stringList) {
                    if (string.toLowerCase().startsWith(strings[2].toLowerCase())) {
                        list.add(string);
                    }
                }
                return list;
            }
        }
        return stringList;
    }
    public List<String> getListOfPlayer(final String string) {
        final List<String> stringList = new ArrayList<>();
        for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (string.isEmpty()) {
                stringList.add(onlinePlayer.getName());
                continue;
            }
            if (onlinePlayer.getName().toLowerCase().contains(string.toLowerCase())){
                stringList.add(onlinePlayer.getName());
            }
        }
        return stringList;
    }
}
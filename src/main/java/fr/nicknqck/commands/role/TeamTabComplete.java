package fr.nicknqck.commands.role;

import fr.nicknqck.roles.builder.TeamList;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TeamTabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1) {
            final List<String> list = new ArrayList<>();
            list.add("reset");
            for (@NonNull final TeamList teamList : TeamList.values()) {
                list.add(teamList.name().toLowerCase());
            }
            return list;
        } else if (strings.length > 1) {
            List<String> playerNames = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerNames.add(player.getName());
            }
            return playerNames;
        }
        return Collections.emptyList();
    }
}

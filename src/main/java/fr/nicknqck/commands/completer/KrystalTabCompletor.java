package fr.nicknqck.commands.completer;

import fr.nicknqck.GameState;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Power;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KrystalTabCompletor implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        final List<String> stringList = new ArrayList<>();
        if (strings.length < 2) {
            stringList.add("bonusinfo");
        }
        if (commandSender instanceof Player) {
            if (GameState.inGame()) {
                if (!GameState.getInstance().hasRoleNull(((Player) commandSender).getUniqueId())) {
                    final List<Power> powerList = new ArrayList<>(GameState.getInstance().getGamePlayer().get(((Player) commandSender).getUniqueId()).getRole().getPowers());
                    for (final Power power : powerList) {
                        if (power instanceof CommandPower) {
                            if (((CommandPower) power).getCommandType().equals(CommandPower.CommandType.KRYSTAL)) {
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
}
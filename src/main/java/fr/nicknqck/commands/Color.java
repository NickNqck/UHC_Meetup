package fr.nicknqck.commands;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.scoreboard.PersonalScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Color implements CommandExecutor {

    private final GameState gameState;

    public Color(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            final Player sender = (Player) commandSender;
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                sender.sendMessage("§cCette commande n'est faisable qu'en jeu");
                return true;
            }
            if (!gameState.isRoleAttributed()) {
                sender.sendMessage("§cCette commande n'est faisable qu'après l'annonce des rôles ");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage("§cCette commande prend une couleur et un ou des joueurs en compte");
                return true;
            }
            if (!isColor(args[0])) {
                sender.sendMessage("§b"+args[0]+"§c n'est pas une couleur reconnue");
                return true;
            }
            final PersonalScoreboard personalScoreboard = Main.getInstance().getScoreboardManager().getScoreboards().get(sender.getUniqueId());
            if (personalScoreboard == null) {
                sender.sendMessage("§cDésoler, il semblerait que vous n'ayez aucun score board pour accueillir vos couleurs");
                return true;
            }
            final List<String> listargs = new ArrayList<>(Arrays.asList(args));
            listargs.remove(args[0]);
            for (final String string : listargs) {
                final Player target = Bukkit.getPlayer(string);
                if (target == null)continue;
                final String color = getColor(args[0]);
                personalScoreboard.changeDisplayName(sender, target, color);
            }
            return true;
        }
        commandSender.sendMessage("Pas valide sah");
        return false;
    }
    private boolean isColor(String arg) {
        return arg.equalsIgnoreCase("rouge");
    }
    private String getColor(String arg) {
        if (arg.equalsIgnoreCase("Rouge")) {
            return "§c";
        }
        return "";
    }
}

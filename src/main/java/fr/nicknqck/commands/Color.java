package fr.nicknqck.commands;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.scoreboard.PersonalScoreboard;
import fr.nicknqck.utils.event.EventUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class Color implements CommandExecutor, Listener {

    private final GameState gameState;
    private final Map<String, String> supportedColors;

    public Color(GameState gameState) {
        this.gameState = gameState;
        this.supportedColors = new HashMap<>();
        EventUtils.registerEvents(this);
        addSupportedColors();
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
                final String color = this.supportedColors.get(args[0]);
                personalScoreboard.changeDisplayName(sender, target, color);
            }
            return true;
        }
        commandSender.sendMessage("§cUne erreur c'est produite...");
        return false;
    }

    private void addSupportedColors() {
        this.supportedColors.put("Rouge", "§c");
        this.supportedColors.put("Bleu", "§9");
        this.supportedColors.put("Rose", "§d");
    }

    private boolean isColor(String arg) {
        for (final String string : this.supportedColors.keySet()) {
            if (string.equalsIgnoreCase(arg)) {
                return true;
            }
        }
        return false;
    }
    @EventHandler
    private void onEndGame(EndGameEvent event) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager != null) {
            Scoreboard mainScoreboard = manager.getMainScoreboard();
            for (Team team : mainScoreboard.getTeams()) {
                team.unregister();
            }
        }
    }
}

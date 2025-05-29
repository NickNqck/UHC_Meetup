package fr.nicknqck.commands;

import fr.nicknqck.Main;
import fr.nicknqck.player.PlayerInfo;
import fr.nicknqck.roles.builder.IRole;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class InfoCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Commande réservée aux joueurs.");
            return true;
        }

        Player player = (Player) sender;

        // Si un nom est donné
        if (args.length == 1) {
            if (!player.isOp()) {
                player.sendMessage("§cTu ne peux voir que tes propres informations.");
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                player.sendMessage("§cCe joueur est introuvable ou hors ligne.");
                return true;
            }

            PlayerInfo targetInfo = Main.getInstance().getInfoManager().getPlayerInfo(target.getUniqueId());
            if (targetInfo == null) {
                player.sendMessage("§cAucune information pour ce joueur.");
                return true;
            }

            displayInfo(player, target.getName(), targetInfo);
            return true;
        }

        // Si aucun argument : on affiche les infos du joueur appelant
        PlayerInfo selfInfo = Main.getInstance().getInfoManager().getPlayerInfo(player.getUniqueId());
        if (selfInfo == null) {
            player.sendMessage("§cAucune information trouvée.");
            return true;
        }

        displayInfo(player, player.getName(), selfInfo);
        return true;
    }

    private void displayInfo(Player viewer, String name, PlayerInfo info) {
        StringBuilder obtenedTeams = new StringBuilder();
        for (Map.Entry<TeamList, Integer> entry : info.getTeamPlayed().entrySet()) {
            String string = entry.getKey().getName();
            obtenedTeams.append("§7").append(string).append("§7: §a").append(entry.getValue()).append(" fois\n");
        }
        viewer.sendMessage(new String[]{
                "§e--- Statistiques de " + name + " ---",
                "§7Connexions: §a" + info.getJoinCount(),
                "§7Temp en jeu: §a" + StringUtils.secondsTowardsBeautiful(info.getTimePlayed()),
                "§7Flèches tirées: §a" + info.getArrowsShot(),
                "§7Nombre de changement de camp:§a "+info.getAmountTeamChange(),
                "§7Nombre de game joué:§a "+info.getGamePlayed(),
                "§7Nombre de game gagner: §a"+info.getGameWin(),
                "§7Nombre de game perdu:§a "+info.getGameLoose(),
                "§7Kills totaux:§a "+info.getTotalKills(),
                "§7Morts totals:§a "+info.getDeaths(),
                "§7Ratio K/D: §a"+info.getKDRatio(),
                "§7Joueur le plus tué: "+info.getMostKilledPlayerName(),
                "",
                "",
                "§7Camps obtenus: ",
                "",
                obtenedTeams.toString(),
                "",
                Main.getInstance().getInfoManager().getMostPlayedRoleInfo(info.getUuid())
        });


    }
}

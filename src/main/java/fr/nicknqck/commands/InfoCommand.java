package fr.nicknqck.commands;

import fr.nicknqck.Main;
import fr.nicknqck.player.PlayerInfo;
import fr.nicknqck.roles.builder.IRole;
import fr.nicknqck.roles.builder.TeamList;
import org.bukkit.Bukkit;
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
        StringBuilder obtenedRoles = new StringBuilder();
        StringBuilder obtenedTeams = new StringBuilder();
        for (Map.Entry<String, Integer> entry : info.getRolesPlayed().entrySet()) {
            String string = entry.getKey();
            for (final IRole iRole : Main.getInstance().getRoleManager().getRolesRegistery().values()) {
                if (iRole.getName().equals(string)) {
                    string = iRole.getRoles().getItem().getItemMeta().getDisplayName();
                    break;
                }
            }
            obtenedRoles.append("§7").append(string).append("§7: §a").append(entry.getValue()).append(" fois\n");
        }
        for (Map.Entry<TeamList, Integer> entry : info.getTeamPlayed().entrySet()) {
            String string = entry.getKey().getName();
            obtenedTeams.append("§7").append(string).append("§7: §a").append(entry.getValue()).append(" fois\n");
        }
        viewer.sendMessage(new String[]{
                "§e--- Statistiques de " + name + " ---",
                "§7Connexions: §a" + info.getJoinCount(),
                "§7Déconnexions: §a" + info.getQuitCount(),
                "§7Entités tuées: §a" + info.getEntitiesKilled(),
                "§7Flèches tirées: §a" + info.getArrowsShot(),
                "§7Nombre de changement de camp:§a "+info.getAmountTeamChange(),
                "",
                "§7Camps obtenus: ",
                "",
                obtenedTeams.toString(),
                "",
                "§7Rôles obtenus: ",
                "",
                obtenedRoles.toString()

        });

    }
}

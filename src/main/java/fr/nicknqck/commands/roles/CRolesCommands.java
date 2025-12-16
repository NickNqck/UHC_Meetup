package fr.nicknqck.commands.roles;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.custom.CustomRolesBase;
import fr.nicknqck.roles.krystal.Bonus;
import fr.nicknqck.roles.krystal.BonusKrystalBase;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Power;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CRolesCommands implements CommandExecutor {

    private final GameState gameState;

    public CRolesCommands(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            final Player sender = (Player) commandSender;
            if (!gameState.hasRoleNull(sender.getUniqueId())) {
                final RoleBase role = gameState.getGamePlayer().get(sender.getUniqueId()).getRole();
                if (role.getGamePlayer().isAlive()) {
                    if (!role.getPowers().isEmpty()) {
                        for (final Power power : role.getPowers()) {
                            if (power instanceof CommandPower) {
                                ((CommandPower) power).call(strings, CommandPower.CommandType.CUSTOM, sender);
                            }
                        }
                    }
                }
                if (role instanceof CustomRolesBase) {
                    CustomRolesBase customRolesBase = (CustomRolesBase) role;
                    if (customRolesBase.getGamePlayer().isAlive()) {
                        return customRolesBase.onCustomCommand(strings, sender);
                    }
                }
            }
            if (strings[0].equalsIgnoreCase("bonusinfo")) {
                BonusKrystalBase bonusKrystalBase = null;
                if (!gameState.hasRoleNull(sender.getUniqueId())) {
                    if (gameState.getGamePlayer().get(sender.getUniqueId()).getRole() instanceof BonusKrystalBase) {
                        bonusKrystalBase = (BonusKrystalBase) gameState.getGamePlayer().get(sender.getUniqueId()).getRole();
                    }
                }
                final List<String> list = new ArrayList<>();
                list.add("§7Le système de§d krystaux§7 permet aux rôles du mode de jeu§d Krystal UHC§7 d'obtenir un ou plusieurs§c bonus§7");
                list.add("");
                if (bonusKrystalBase == null){
                    list.add("§7Par exemple:§e Heldige§7 possède l'effet§c Force 1§7 tant qu'il a au minimum§c 50§d krystaux");
                } else {
                    final List<Bonus> arr = new ArrayList<>(bonusKrystalBase.getBonus());
                    for (Bonus bonus : arr) {
                        list.addAll(Arrays.asList(bonus.getDescriptions()));
                    }
                }
                commandSender.sendMessage(list.toArray(new String[0]));
                return false;
            }
        }
        return false;
    }
}

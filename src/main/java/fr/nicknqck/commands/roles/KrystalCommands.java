package fr.nicknqck.commands.roles;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Power;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KrystalCommands implements CommandExecutor {

    private final GameState gameState;

    public KrystalCommands(GameState gameState) {
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
                                ((CommandPower) power).call(strings, CommandPower.CommandType.KRYSTAL, sender);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}

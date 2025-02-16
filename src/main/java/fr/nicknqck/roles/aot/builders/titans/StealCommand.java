package fr.nicknqck.roles.aot.builders.titans;

import fr.nicknqck.GameState;
import fr.nicknqck.events.custom.roles.aot.PrepareStealCommandEvent;
import fr.nicknqck.roles.aot.builders.AotRoles;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.powers.CommandPower;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class StealCommand extends CommandPower {

    public StealCommand(@NonNull RoleBase role) {
        super("/aot steal", "steal", null, role, CommandType.AOT);
    }

    @Override
    public boolean onUse(Player player, Map<String, Object> map) {
        final GameState gameState = GameState.getInstance();
        if (gameState == null)return false;
        if (!gameState.hasRoleNull(player.getUniqueId())) {
            final RoleBase role = gameState.getGamePlayer().get(player.getUniqueId()).getRole();
            if (role instanceof AotRoles) {
                if (((AotRoles) role).isCanVoleTitan()) {
                    final PrepareStealCommandEvent stealEvent = new PrepareStealCommandEvent(player, (AotRoles) role);
                    Bukkit.getPluginManager().callEvent(stealEvent);
                }
            }
        }
        return false;
    }
}

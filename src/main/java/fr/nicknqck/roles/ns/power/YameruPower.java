package fr.nicknqck.roles.ns.power;

import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.KamuiUtils;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class YameruPower extends CommandPower {

    public YameruPower(@NonNull RoleBase role) {
        super("/ns yameru <joueur>", "yameru", null, role, CommandType.NS,
                "§7Vous permet d'éjecter une personne du§d Kamui");
    }

    @Override
    public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
        @NonNull final String[] args = (String[]) map.get("args");
        if (args.length == 2) {
            final Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage("§CVeuiller cibler un joueur éxistant !");
            } else {
                if (target.getWorld().equals(Bukkit.getWorld("Kamui"))) {
                    KamuiUtils.end(target);
                    return true;
                }
            }
        }
        return false;
    }
}

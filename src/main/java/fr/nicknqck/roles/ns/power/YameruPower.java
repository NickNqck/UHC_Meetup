package fr.nicknqck.roles.ns.power;

import fr.nicknqck.interfaces.IRoleGotSubWorld;
import fr.nicknqck.interfaces.ISubRoleWorld;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.KamuiDimension;
import fr.nicknqck.utils.powers.CommandPower;
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
                player.sendMessage("§cVeuiller cibler un joueur éxistant !");
            } else {
                if (target.getWorld().equals(Bukkit.getWorld("Kamui"))) {
                    if (getRole() instanceof IRoleGotSubWorld) {
                        final ISubRoleWorld iSubRoleWorld = ((IRoleGotSubWorld) getRole()).getSubWorld();
                        if (iSubRoleWorld instanceof KamuiDimension) {
                            if (((KamuiDimension) iSubRoleWorld).getBeforeTpMap().containsKey(target.getUniqueId())) {
                                target.teleport(((KamuiDimension) iSubRoleWorld).getBeforeTpMap().get(target.getUniqueId()));
                                ((KamuiDimension) iSubRoleWorld).getBeforeTpMap().remove(target.getUniqueId());
                                target.sendMessage("§7Vous avez été§c éjecter§7 du§d Kamui§7.");
                                player.sendMessage("§c"+player.getName()+"§7 a été§c éjecter§7 du§d Kamui§7.");
                                return true;
                            } else {
                                player.sendMessage("§c"+target.getName()+"§7 n'est pas dans le§d Kamui§7.");
                                return false;
                            }
                        }
                    }
                    player.sendMessage("§cVotre rôle n'a pas les autorisations nécéssaire pour utiliser cette commande.");
                    return false;
                }
            }
        }
        return false;
    }
}

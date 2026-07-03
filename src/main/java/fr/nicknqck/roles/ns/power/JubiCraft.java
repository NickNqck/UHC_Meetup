package fr.nicknqck.roles.ns.power;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.enums.TeamList;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.powers.CommandPower;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JubiCraft extends CommandPower {

    public JubiCraft(@NonNull RoleBase role) {
        super("§d/ns jubicraft§r", "jubicraft", null, role, CommandType.NS);
        setMaxUse(1);
    }

    @Override
    public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
        if (!getRole().getTeam().equals(TeamList.Jubi) || !getRole().getOriginTeam().equals(TeamList.Jubi)) {
            player.sendMessage("§cSeul le camp§d Jubi§c peut obtenir§d Jubi§c.");
            return false;
        }
        @NonNull
        final List<ItemStack> list = new ArrayList<>();
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null)continue;
            if (!GlobalUtils.hasNBT(item, "biju.power")) continue;
            list.add(item);
            count++;
        }
        if (count >= 6) {
            for (ItemStack itemStack : list) {
                player.getInventory().remove(itemStack);
            }
            player.setPlayerListName("§dJubi "+player.getName());
            GameListener.SendToEveryone("");
            GameListener.SendToEveryone("§c§lLe Jûbi à été invoquée !");
            GameListener.SendToEveryone("");
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!GameState.getInstance().hasRoleNull(p.getUniqueId())){
                    GameState.getInstance().getGamePlayer().get(player.getUniqueId()).getRole().playSound(p, "mob.enderdragon.end");
                }
            }
            //Pour la liste des sons
            //https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/mapping-and-modding-tutorials/2213619-1-8-all-playsound-sound-arguments
            this.getRole().addPower(new JubiPower2(getRole()), true);
            return true;
        } else {
            player.sendMessage("§cImpossible de craft§d Jubi§c il vous manque§d "+count+" bijuus§c.");
        }
        return false;
    }
}

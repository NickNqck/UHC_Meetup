package fr.nicknqck.roles.ns.power;

import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;

public class Izanagi extends CommandPower {

    public Izanagi(@NonNull RoleBase role) {
        super("/ns izanagi", "izanagi", null, role, CommandType.NS,"§7Vous permet de§d régénérer§7 intégralement votre vie et de gagner§c 5§e pommes d'or§7.",
                "",
                "§4!ATTENTION!§c Utilisez ce pouvoir vous coûtera 1❤ permanent ainsi que les pouvoirs du Susanô si jamais vous les aviez");
        setMaxUse(1);
    }
    @Override
    public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
        getRole().setMaxHealth(getRole().getMaxHealth()-2.0);
        player.setMaxHealth(getRole().getMaxHealth());
        player.setHealth(player.getMaxHealth());
        getRole().giveItem(player, false, new ItemBuilder(Material.GOLDEN_APPLE).setAmount(5).toItemStack());
        for (final Power power : getRole().getPowers()) {
            if (!(power instanceof ItemPower))continue;
            if (power.getName().contains("Susano")) {
                getRole().getPowers().remove(power);
                player.getInventory().remove(((ItemPower) power).getItem());
                break;
            }
        }
        return true;
    }
}

package fr.nicknqck.roles.mc.solo;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Warden extends RoleBase {
    public Warden(Player player, GameState.Roles roles, GameState gameState) {
        super(player, roles, gameState);

    }

    @Override
    public String[] Desc() {
        return new String[]{
                AllDesc.bar,
                AllDesc.role+"§9Warden",
                AllDesc.objectifsolo+"§e Seul",
                "",
                AllDesc.effet
        };
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public void resetCooldown() {

    }
}

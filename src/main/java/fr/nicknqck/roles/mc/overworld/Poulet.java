package fr.nicknqck.roles.mc.overworld;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.RoleBase;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Poulet extends RoleBase {
    public Poulet(Player player, GameState.Roles roles, GameState gameState) {
        super(player, roles, gameState);
    }

    @Override
    public String[] Desc() {
        return new String[]{

        };
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{

        };
    }

    @Override
    public void resetCooldown() {

    }
}

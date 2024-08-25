package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class RockLeeV2 extends ShinobiRoles {

    public RockLeeV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.PEUINTELLIGENT;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public String getName() {
        return "Rock Lee";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.RockLee;
    }

    @Override
    public void resetCooldown() {

    }
}

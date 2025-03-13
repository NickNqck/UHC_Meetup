package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class DakiV2 extends DemonsRoles {
    public DakiV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull DemonType getRank() {
        return null;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public GameState.@NonNull Roles getRoles() {
        return null;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return null;
    }

    @Override
    public void resetCooldown() {

    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }
}

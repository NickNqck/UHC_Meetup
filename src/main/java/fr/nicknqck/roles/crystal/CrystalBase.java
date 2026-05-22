package fr.nicknqck.roles.crystal;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.CrystalFaction;
import fr.nicknqck.roles.builder.RoleBase;
import lombok.NonNull;

import java.util.UUID;

public abstract class CrystalBase extends RoleBase {

    public CrystalBase(UUID player) {
        super(player);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        super.RoleGiven(gameState);
        this.onRoleGive(gameState);
    }
    public abstract void onRoleGive(@NonNull final GameState gameState);
    @NonNull
    public abstract CrystalFaction getCrystalFaction();
}

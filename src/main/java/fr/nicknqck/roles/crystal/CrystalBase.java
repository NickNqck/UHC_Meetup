package fr.nicknqck.roles.crystal;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.CrystalFaction;
import fr.nicknqck.interfaces.IGotReputation;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.RandomUtils;
import lombok.NonNull;

import java.util.UUID;

public abstract class CrystalBase extends RoleBase implements IGotReputation {

    private int actualReputation;

    public CrystalBase(UUID player) {
        super(player);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        if (getCrystalFaction().equals(CrystalFaction.PEUPLE)) {
            if (RandomUtils.getOwnRandomProbability(50.0)) {
                setReputation(5);
            } else {
                setReputation(4);
            }
        } else if (getCrystalFaction().equals(CrystalFaction.CLERGER) || getCrystalFaction().equals(CrystalFaction.NOBLE)) {
            if (RandomUtils.getOwnRandomProbability(50.0)) {
                setReputation(5);
            } else {
                setReputation(6);
            }
        } else if (getCrystalFaction().equals(CrystalFaction.ROYAL)) {
            if (RandomUtils.getOwnRandomProbability(50.0)) {
                setReputation(6);
            } else {
                setReputation(7);
            }
        }
        super.RoleGiven(gameState);
        this.onRoleGive(gameState);

    }
    public abstract void onRoleGive(@NonNull final GameState gameState);
    @NonNull
    public abstract CrystalFaction getCrystalFaction();
    @Override
    public void setReputation(int reputation) {
        if (reputation > 10) {
            reputation = 10;
        }
        if (reputation < 0) {
            reputation = 0;
        }
        this.actualReputation = reputation;
    }
    @Override
    public int getReputation() {
        return this.actualReputation;
    }
}

package fr.nicknqck.roles.crystal.guilde;

import fr.nicknqck.enums.CrystalTeam;
import fr.nicknqck.interfaces.ITeam;
import fr.nicknqck.roles.crystal.CrystalBase;
import lombok.NonNull;

import java.util.UUID;

public abstract class GuildeBase extends CrystalBase {

    public GuildeBase(UUID player) {
        super(player);
    }

    @Override
    public @NonNull ITeam getOriginTeam() {
        return CrystalTeam.Guilde;
    }
}
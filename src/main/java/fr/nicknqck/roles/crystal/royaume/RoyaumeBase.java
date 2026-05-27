package fr.nicknqck.roles.crystal.royaume;

import fr.nicknqck.enums.CrystalTeam;
import fr.nicknqck.interfaces.ITeam;
import fr.nicknqck.roles.crystal.CrystalBase;
import lombok.NonNull;

import java.util.UUID;

public abstract class RoyaumeBase extends CrystalBase {
    public RoyaumeBase(UUID player) {
        super(player);
    }

    @Override
    public @NonNull ITeam getOriginTeam() {
        return CrystalTeam.Royaume;
    }
}
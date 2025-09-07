package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import lombok.NonNull;

import java.util.UUID;

public class DakiV2 extends DemonsRoles {
    public DakiV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull DemonType getRank() {
        return DemonType.SUPERIEUR;
    }

    @Override
    public String getName() {
        return "Daki§7 (§6V2§7)";
    }

    @Override
    public GameState.@NonNull Roles getRoles() {
        return GameState.Roles.Daki;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Demon;
    }

}

package fr.nicknqck.roles.ns.builders;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ns.akatsuki.Konan;
import fr.nicknqck.roles.ns.akatsuki.NagatoV2;
import lombok.NonNull;

import java.util.UUID;

public abstract class AkatsukiRoles extends NSRoles{
    public AkatsukiRoles(UUID player) {
        super(player);
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Akatsuki;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        if (!(this instanceof NagatoV2) && !(this instanceof Konan)) {
            if (Main.RANDOM.nextInt(101) <= 50) {
                addKnowedRole(NagatoV2.class);
            } else {
                addKnowedRole(Konan.class);
            }
        }
        super.RoleGiven(gameState);
    }
}

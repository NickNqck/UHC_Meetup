package fr.nicknqck.roles.ns.builders;

import fr.nicknqck.roles.builder.TeamList;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class AkatsukiRoles extends NSRoles{
    public AkatsukiRoles(UUID player) {
        super(player);
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Akatsuki;
    }
}

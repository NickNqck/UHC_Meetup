package fr.nicknqck.roles.mc.builders;

import fr.nicknqck.roles.builder.TeamList;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class OverWorldRoles extends UHCMcRoles {
    public OverWorldRoles(UUID player) {
        super(player);
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.OverWorld;
    }
}

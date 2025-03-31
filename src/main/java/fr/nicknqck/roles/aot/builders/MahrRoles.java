package fr.nicknqck.roles.aot.builders;

import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.titans.TitanBase;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public abstract class MahrRoles extends AotRoles{

    private TitanBase titan;

    public MahrRoles(UUID player) {
        super(player);
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Mahr;
    }
}

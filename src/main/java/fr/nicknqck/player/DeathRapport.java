package fr.nicknqck.player;

import fr.nicknqck.interfaces.ITeam;
import fr.nicknqck.roles.builder.RoleBase;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.UUID;

@Getter
public final class DeathRapport {

    @NonNull
    private final UUID deadPlayerUUID;
    @NonNull
    private final GamePlayer deadPlayer;
    @Nullable
    private final GamePlayer gameKiller;
    @NonNull
    private final ITeam teamWhenDie;
    @NonNull
    private final RoleBase deadRoleWhenDie;

    public DeathRapport(@NonNull UUID deadPlayerUUID, @NonNull GamePlayer gamePlayer, @Nullable GamePlayer gameKiller, @NonNull ITeam teamWhenDie, @NonNull RoleBase deadRoleWhenDie) {
        this.deadPlayerUUID = deadPlayerUUID;
        this.deadPlayer = gamePlayer;
        this.gameKiller = gameKiller;
        this.teamWhenDie = teamWhenDie;
        this.deadRoleWhenDie = deadRoleWhenDie;
    }
}
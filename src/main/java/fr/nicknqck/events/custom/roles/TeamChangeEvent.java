package fr.nicknqck.events.custom.roles;

import fr.nicknqck.events.custom.GameEvent;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import lombok.Getter;
import org.bukkit.event.Cancellable;


public class TeamChangeEvent extends GameEvent implements Cancellable {

    @Getter
    private final RoleBase role;
    @Getter
    private final TeamList oldTeam;
    @Getter
    private final TeamList newTeam;
    private boolean cancel = false;

    public TeamChangeEvent(RoleBase role, TeamList oldTeam, TeamList newTeam) {
        this.role = role;
        this.oldTeam = oldTeam;
        this.newTeam = newTeam;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }
}
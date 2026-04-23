package fr.nicknqck.events.custom.roles;

import fr.nicknqck.events.custom.GameEvent;
import fr.nicknqck.interfaces.ITeam;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.enums.TeamList;
import lombok.Getter;
import org.bukkit.event.Cancellable;


public class TeamChangeEvent extends GameEvent implements Cancellable {

    @Getter
    private final RoleBase role;
    @Getter
    private final ITeam oldTeam;
    @Getter
    private final ITeam newTeam;
    private boolean cancel = false;

    public TeamChangeEvent(RoleBase role, ITeam oldTeam, ITeam newTeam) {
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
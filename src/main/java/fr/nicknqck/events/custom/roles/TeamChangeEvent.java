package fr.nicknqck.events.custom.roles;

import fr.nicknqck.interfaces.ITeam;
import fr.nicknqck.roles.builder.RoleBase;
import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class TeamChangeEvent extends Event implements Cancellable {

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
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
package fr.nicknqck.events.custom.roles.ns;

import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.builders.NSRoles;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class IzanamiFinishEvent extends Event {

    private final NSRoles infecteur;
    private final RoleBase infected;
    private final String infectColor;
    private final Player owner;
    private final Player target;
    private final boolean successful;

    public IzanamiFinishEvent(NSRoles infecteur, RoleBase infected, String infectColor, Player owner, Player target, boolean successful) {
        this.infecteur = infecteur;
        this.infected = infected;
        this.infectColor = infectColor;
        this.owner = owner;
        this.target = target;
        this.successful = successful;
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

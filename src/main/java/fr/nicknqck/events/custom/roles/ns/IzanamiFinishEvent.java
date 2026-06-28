package fr.nicknqck.events.custom.roles.ns;

import fr.nicknqck.events.custom.GameEvent;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.builders.NSRoles;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class IzanamiFinishEvent extends GameEvent {

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
}

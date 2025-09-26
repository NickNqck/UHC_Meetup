package fr.nicknqck.events.custom.roles.aot;

import fr.nicknqck.events.custom.GameEvent;
import fr.nicknqck.roles.aot.builders.AotRoles;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class PrepareStealCommandEvent extends GameEvent {

    private final Player player;
    private final AotRoles role;

    public PrepareStealCommandEvent(Player player, AotRoles role) {
        this.player = player;
        this.role = role;
    }
}

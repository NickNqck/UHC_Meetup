package fr.nicknqck.events.custom.roles.aot;

import fr.nicknqck.roles.aot.builders.AotRoles;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class PrepareStealCommandEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final Player player;
    private final AotRoles role;

    public PrepareStealCommandEvent(Player player, AotRoles role) {
        this.player = player;
        this.role = role;
    }


    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

package fr.nicknqck.events.ds;

import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class UrokodakiDsWaterEvent extends Event {

    private final Player urokodaki;
    private final RoleBase role;
    private final GamePlayer gameTarget;

    public UrokodakiDsWaterEvent(Player urokodaki, RoleBase role, GamePlayer gameTarget) {
        this.urokodaki = urokodaki;
        this.role = role;
        this.gameTarget = gameTarget;
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

package fr.nicknqck.events.power.aot;

import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public final class GamePlayerTridimentionnelEvent extends Event {

    private final GamePlayer gamePlayer;
    private final RoleBase role;

    public GamePlayerTridimentionnelEvent(GamePlayer gamePlayer, RoleBase role) {
        this.gamePlayer = gamePlayer;
        this.role = role;
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
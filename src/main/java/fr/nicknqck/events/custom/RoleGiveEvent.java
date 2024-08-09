package fr.nicknqck.events.custom;

import fr.nicknqck.GameState;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class RoleGiveEvent extends Event {

    private final GameState gameState;
    private final RoleBase role;
    private final GameState.Roles type;
    private final GamePlayer gamePlayer;
    private static final HandlerList handlers = new HandlerList();
    public RoleGiveEvent(GameState gameState, RoleBase role, GameState.Roles roleType, GamePlayer gamePlayer) {
        super();
        this.gameState = gameState;
        this.role = role;
        this.type = roleType;
        this.gamePlayer = gamePlayer;
    }
    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}

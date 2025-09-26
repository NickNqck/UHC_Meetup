package fr.nicknqck.events.custom;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class RoleGiveEvent extends Event {

    private final GameState gameState;
    private final RoleBase role;
    private final Roles type;
    private final GamePlayer gamePlayer;
    private static final HandlerList handlers = new HandlerList();
    private final boolean endGive;
    public RoleGiveEvent(GameState gameState, RoleBase role, Roles roleType, GamePlayer gamePlayer, final boolean end) {
        super();
        this.gameState = gameState;
        this.role = role;
        this.type = roleType;
        this.gamePlayer = gamePlayer;
        this.endGive = end;
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

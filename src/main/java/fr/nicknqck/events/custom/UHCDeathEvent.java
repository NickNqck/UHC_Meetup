package fr.nicknqck.events.custom;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.RoleBase;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;

@Getter
public class UHCDeathEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final GameState gameState;
    @Setter
    private boolean cancelled = false;
    @Nullable
    private final RoleBase role;
    public UHCDeathEvent(Player player, GameState gameState, @Nullable RoleBase role) {
        this.player = player;
        this.gameState = gameState;
        this.role = role;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }
}

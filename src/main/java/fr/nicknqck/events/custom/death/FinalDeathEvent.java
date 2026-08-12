package fr.nicknqck.events.custom.death;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.RoleBase;
import lombok.Getter;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class FinalDeathEvent extends Event {

    private final Player player;
    private final GameState gameState;
    private final RoleBase role;
    @Nullable
    private final Entity entityKiller;

    public FinalDeathEvent(Player player, GameState gameState, RoleBase role, Entity entityKiller) {
        this.player = player;
        this.gameState = gameState;
        this.role = role;
        this.entityKiller = entityKiller;
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

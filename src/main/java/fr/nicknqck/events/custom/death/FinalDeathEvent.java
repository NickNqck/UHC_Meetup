package fr.nicknqck.events.custom.death;

import fr.nicknqck.GameState;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import lombok.Getter;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class FinalDeathEvent extends Event {

    private final Player player;
    private final GameState gameState;
    private final RoleBase role;
    @Nullable
    private final Entity entityKiller;
    /** Joueurs ayant reçu une assistance sur cette mort (non modifiable, jamais null) */
    private final List<GamePlayer> assists;

    public FinalDeathEvent(Player player, GameState gameState, RoleBase role, Entity entityKiller) {
        this(player, gameState, role, entityKiller, Collections.emptyList());
    }

    public FinalDeathEvent(Player player, GameState gameState, RoleBase role, Entity entityKiller, List<GamePlayer> assists) {
        this.player = player;
        this.gameState = gameState;
        this.role = role;
        this.entityKiller = entityKiller;
        this.assists = Collections.unmodifiableList(new ArrayList<>(assists == null ? Collections.emptyList() : assists));
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

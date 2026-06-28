package fr.nicknqck.events.custom.death;

import fr.nicknqck.GameState;
import fr.nicknqck.events.custom.GameEvent;
import fr.nicknqck.roles.builder.RoleBase;
import lombok.Getter;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@Getter
public class FinalDeathEvent extends GameEvent {

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
}

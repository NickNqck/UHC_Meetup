package fr.nicknqck.events.custom.roles.aot;

import fr.nicknqck.events.custom.GameEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.AotRoles;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

@Getter
public class TitanStealEvent extends GameEvent implements Cancellable {

    private final AotRoles role;
    private final GamePlayer gamePlayer;
    private final Player player;
    private boolean cancelled = false;

    public TitanStealEvent(AotRoles role, GamePlayer gamePlayer, Player player) {
        this.role = role;
        this.gamePlayer = gamePlayer;
        this.player = player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}

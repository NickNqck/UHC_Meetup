package fr.nicknqck.events.custom.roles;

import fr.nicknqck.Main;
import fr.nicknqck.utils.powers.Power;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;

@Getter
public class PowerActivateEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private final Main plugin;
    private final Player player;
    private final Power power;
    @Setter
    @Nullable
    private String CancelMessage;
    public PowerActivateEvent(Main plugin, Player player, Power power) {
        this.player =player;
        this.plugin = plugin;
        this.power = power;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }
}

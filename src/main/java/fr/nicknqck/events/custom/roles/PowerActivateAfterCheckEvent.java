package fr.nicknqck.events.custom.roles;

import fr.nicknqck.Main;
import fr.nicknqck.utils.powers.Power;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class PowerActivateAfterCheckEvent extends Event {

    private final Main plugin;
    private final Player player;
    private final Power power;

    public PowerActivateAfterCheckEvent(Main plugin, Player player, Power power) {
        this.plugin = plugin;
        this.player = player;
        this.power = power;
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
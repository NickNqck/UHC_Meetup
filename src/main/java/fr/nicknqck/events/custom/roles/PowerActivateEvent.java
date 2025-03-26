package fr.nicknqck.events.custom.roles;

import fr.nicknqck.Main;
import fr.nicknqck.events.custom.GameEvent;
import fr.nicknqck.utils.powers.Power;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

@Getter
public class PowerActivateEvent extends GameEvent {
    @Setter
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
}

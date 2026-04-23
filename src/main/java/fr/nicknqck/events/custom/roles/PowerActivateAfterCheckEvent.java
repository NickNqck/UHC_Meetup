package fr.nicknqck.events.custom.roles;

import fr.nicknqck.Main;
import fr.nicknqck.events.custom.GameEvent;
import fr.nicknqck.utils.powers.Power;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class PowerActivateAfterCheckEvent extends GameEvent {

    private final Main plugin;
    private final Player player;
    private final Power power;

    public PowerActivateAfterCheckEvent(Main plugin, Player player, Power power) {
        this.plugin = plugin;
        this.player = player;
        this.power = power;
    }
}
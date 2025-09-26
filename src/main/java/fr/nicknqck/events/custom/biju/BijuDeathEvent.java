package fr.nicknqck.events.custom.biju;

import fr.nicknqck.entity.bijuv2.BijuBase;
import fr.nicknqck.events.custom.GameEvent;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
public class BijuDeathEvent extends GameEvent {

    private final Player killer;
    private final Location location;
    private final BijuBase biju;

    public BijuDeathEvent(BijuBase biju, Player killer, Location location) {
        this.biju = biju;
        System.out.println("called "+this);
        this.killer = killer;
        this.location = location;
    }
}

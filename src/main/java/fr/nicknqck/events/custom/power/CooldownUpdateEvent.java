package fr.nicknqck.events.custom.power;

import fr.nicknqck.events.custom.GameEvent;
import fr.nicknqck.utils.powers.Cooldown;
import lombok.Getter;

@Getter
public class CooldownUpdateEvent extends GameEvent {

    private final Cooldown cooldown;

    public CooldownUpdateEvent(Cooldown cooldown) {
        this.cooldown = cooldown;
    }

}

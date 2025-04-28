package fr.nicknqck.events.custom.power;

import com.avaje.ebean.validation.NotNull;
import fr.nicknqck.events.custom.GameEvent;
import fr.nicknqck.utils.powers.Cooldown;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class CooldownFinishEvent extends GameEvent {

    @NotNull
    private final Cooldown cooldown;

    public CooldownFinishEvent(@NonNull final Cooldown cooldown) {
        this.cooldown = cooldown;
    }
}
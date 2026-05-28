package fr.nicknqck.events.custom.info;

import fr.nicknqck.events.custom.GameEvent;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class InfoListAddEvent extends GameEvent {

    @Getter
    private final List<Class<? extends Enum<?>>> toRegister;

    public InfoListAddEvent() {
        this.toRegister = new ArrayList<>();
    }
    public void register(@Nonnull final Class<? extends Enum<?>> clazz) {
        this.toRegister.add(clazz);
    }
}
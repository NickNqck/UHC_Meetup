package fr.nicknqck.events.custom.info;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class InfoListAddEvent extends Event {

    @Getter
    private final List<Class<? extends Enum<?>>> toRegister;

    public InfoListAddEvent() {
        this.toRegister = new ArrayList<>();
    }
    public void register(@Nonnull final Class<? extends Enum<?>> clazz) {
        this.toRegister.add(clazz);
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
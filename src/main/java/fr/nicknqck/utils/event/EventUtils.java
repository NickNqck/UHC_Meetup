package fr.nicknqck.utils.event;

import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

@Getter
public class EventUtils implements Listener{

    private static final List<Listener> toUnregister = new ArrayList<>();

    public EventUtils() {
        registerEvents(this);
    }

    public static void registerEvents(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, Main.getInstance());
        System.out.println("Registered event " + listener.getClass().getName().toLowerCase() +" with plugin "+Main.getInstance().getName().toLowerCase());
    }

    public static void unregisterEvents(Listener listener) {
        HandlerList.unregisterAll(listener);
        toUnregister.remove(listener);
        System.out.println("Unregister event "+listener.getClass().getName().toLowerCase());
    }
    public static void registerRoleEvent(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, Main.getInstance());
        toUnregister.add(listener);
        System.out.println("Registered event " + listener.getClass().getName().toLowerCase() +" (role)");
    }
    @EventHandler
    private void onEndGame(EndGameEvent event) {
        assert !toUnregister.isEmpty();
        for (final Listener listener : toUnregister) {
            unregisterEvents(listener);
        }
        toUnregister.clear();
    }
}

package fr.nicknqck.utils.event;

import fr.nicknqck.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class EventUtils {

    public static void registerEvents(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, Main.getInstance());
        System.out.println("Registered event " + listener.getClass().getName().toLowerCase() +" with plugin "+Main.getInstance().getName().toLowerCase());
    }

    public static void unregisterEvents(Listener listener) {
        HandlerList.unregisterAll(listener);
        System.out.println("Unregister event "+listener.getClass().getName().toLowerCase());
    }
}

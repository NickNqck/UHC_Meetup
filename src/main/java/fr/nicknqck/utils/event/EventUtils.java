package fr.nicknqck.utils.event;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class EventUtils {
    public static void registerEvents(Listener listener, Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        System.out.println("Registered event " + listener.getClass().getName().toLowerCase() +" with plugin "+plugin.getName().toLowerCase());
    }
    public static void unregisterEvents(Listener listener) {
        HandlerList.unregisterAll(listener);
        System.out.println("Unregister event "+listener.getClass().getName().toLowerCase());
    }
}

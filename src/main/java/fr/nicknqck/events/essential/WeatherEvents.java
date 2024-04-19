package fr.nicknqck.events.essential;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WeatherEvents implements Listener {

    @EventHandler
    private void onWeatherChange(WeatherChangeEvent e){
        if (e.getWorld().isThundering()){
            e.getWorld().setThundering(false);
            System.out.println("Cancelled thunder storm");
        }
        if (e.getWorld().hasStorm()){
            e.getWorld().setStorm(false);
            System.out.println("Cancelled storm");
        }
    }
}

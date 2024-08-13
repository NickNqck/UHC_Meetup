package fr.nicknqck.events.essential;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WeatherEvents implements Listener {

    @EventHandler
    private void onWeatherChange(WeatherChangeEvent e){
        e.setCancelled(true);
    }
}

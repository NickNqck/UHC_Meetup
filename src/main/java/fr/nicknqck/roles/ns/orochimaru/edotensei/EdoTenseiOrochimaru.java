package fr.nicknqck.roles.ns.orochimaru.edotensei;

import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.roles.ns.builders.OrochimaruRoles;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;

public abstract class EdoTenseiOrochimaru extends OrochimaruRoles implements Listener {

    public EdoTenseiOrochimaru(UUID player) {
        super(player);
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }
    @EventHandler
    private void onEndGame(EndGameEvent event) {
        HandlerList.unregisterAll(this);
    }
    @EventHandler
    private void onUHCKill(UHCPlayerKillEvent event){

    }
}
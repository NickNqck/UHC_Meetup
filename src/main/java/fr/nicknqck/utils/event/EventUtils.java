package fr.nicknqck.utils.event;

import fr.nicknqck.Main;
import fr.nicknqck.events.custom.GameEndEvent;
import fr.nicknqck.events.custom.power.PowerItemRecupEvent;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class EventUtils implements Listener{

    private static final List<Listener> toUnregister = new ArrayList<>();
    @Getter
    private static final Map<ItemStack, ItemPower> powerCantBeDropMap = new HashMap<>();

    public EventUtils() {
        registerEvents(this);
    }

    public static void registerEvents(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, Main.getInstance());
        if (Main.isDebug()) {
            System.out.println("Registered event " + listener.getClass().getName().toLowerCase() +" with plugin "+Main.getInstance().getName().toLowerCase());
        }
    }

    public static void unregisterEvents(Listener listener) {
        HandlerList.unregisterAll(listener);
        if (Main.isDebug()) {
            System.out.println("Unregister event "+listener.getClass().getName().toLowerCase());
        }
    }
    public static void registerRoleEvent(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, Main.getInstance());
        toUnregister.add(listener);
        if (Main.isDebug()) {
            System.out.println("Registered event " + listener.getClass().getName().toLowerCase() +" (role)");
        }
    }
    @EventHandler
    private void onEndGame(GameEndEvent event) {
        assert !toUnregister.isEmpty();
        for (final Listener listener : toUnregister) {
            unregisterEvents(listener);
        }
        toUnregister.clear();
        for (Entity entity : Bukkit.getWorld("arena").getEntities()) {
            if (entity instanceof Player) {return;}
            entity.remove();
        }
    }
    @EventHandler
    private void onDie(final PlayerPickupItemEvent event) {
        if (powerCantBeDropMap.containsKey(event.getItem().getItemStack())) {
            final PowerItemRecupEvent powerItemRecupEvent = new PowerItemRecupEvent(event.getItem(), event.getItem().getItemStack(), powerCantBeDropMap.get(event.getItem().getItemStack()));
            Bukkit.getPluginManager().callEvent(powerItemRecupEvent);
            if (!powerItemRecupEvent.isCancelled()){
                event.setCancelled(true);
            }
        }
    }
}

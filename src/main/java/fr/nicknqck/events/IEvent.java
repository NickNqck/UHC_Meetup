package fr.nicknqck.events;

import fr.nicknqck.GameState;
import org.bukkit.inventory.ItemStack;

public interface IEvent {

    String getName();
    void onProc(final GameState gameState);
    ItemStack getMenuItem();
    boolean canProc(final GameState gameState);
}

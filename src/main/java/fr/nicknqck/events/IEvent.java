package fr.nicknqck.events;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.MDJ;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

public interface IEvent {

    String getName();
    void onProc(final GameState gameState);
    ItemStack getMenuItem();
    boolean canProc(final GameState gameState);
    String[] getExplications();
    @NonNull MDJ getMDJ();

}
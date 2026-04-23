package fr.nicknqck.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public interface IMDJ {

    ItemStack getItem();
    Consumer<Player> getConsumer();
    String name();

}

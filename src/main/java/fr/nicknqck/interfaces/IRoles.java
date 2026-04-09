package fr.nicknqck.interfaces;

import org.bukkit.inventory.ItemStack;

public interface IRoles<E extends Enum<E> & IRoles<E>>{

    ITeam getTeam();
    String getMdj();
    int getNmb();
    ItemStack getItem();
    String getGDesign();
    String name();

}
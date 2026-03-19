package fr.nicknqck.interfaces;

import fr.nicknqck.enums.TeamList;
import org.bukkit.inventory.ItemStack;

public interface IRoles<E extends Enum<E> & IRoles<E>>{

    TeamList getTeam();
    String getMdj();
    int getNmb();
    ItemStack getItem();
    String getGDesign();

}
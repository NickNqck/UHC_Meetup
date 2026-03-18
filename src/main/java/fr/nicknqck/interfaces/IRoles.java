package fr.nicknqck.interfaces;

import fr.nicknqck.enums.TeamList;
import org.bukkit.inventory.ItemStack;

public interface IRoles {

    TeamList getTeam();
    String getMdj();
    int getNmb();
    ItemStack getItem();
    String getGDesign();

}
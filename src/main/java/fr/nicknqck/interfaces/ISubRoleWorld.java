package fr.nicknqck.interfaces;

import org.bukkit.World;
import org.bukkit.WorldCreator;

public interface ISubRoleWorld {

    String getWorldName();
    String getZipFileName();
    World createWorld();
    double getActualPercentPregenTask();
    void startPregen(World world);
    WorldCreator getWorldCreator();

}

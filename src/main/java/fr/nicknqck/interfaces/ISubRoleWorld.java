package fr.nicknqck.interfaces;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.util.List;

public interface ISubRoleWorld {

    String getWorldName();
    String getZipFileName();
    World createWorld();
    double getActualPercentPregenTask();
    void startPregen(World world);
    WorldCreator getWorldCreator();
    void setHasBeenPregen(boolean pregen);
    boolean isPregen();
    List<Location> getPossibleTeleportLocations();

}

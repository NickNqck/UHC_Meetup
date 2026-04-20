package fr.nicknqck.interfaces;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;

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
    boolean isPlayerCanBreakNaturalBlock();
    boolean isPlayersCanPlaceBlocks();
    boolean isPlayerCanBreakOtherPlayersBlocks();
    List<Block> getPrecalculateList();
    void startPrecalculs(World world);

}

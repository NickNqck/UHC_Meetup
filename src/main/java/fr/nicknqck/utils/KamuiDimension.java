package fr.nicknqck.utils;

import fr.nicknqck.Main;
import fr.nicknqck.interfaces.ISubRoleWorld;
import fr.nicknqck.runnables.PregenerationTask;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class KamuiDimension implements ISubRoleWorld {

    private double percent = 0.0;
    private boolean pregen = false;
    private final List<Location> locationList;
    private final  List<Block> blockList;
    @Getter
    private final HashMap<UUID, Location> beforeTpMap;

    public KamuiDimension() {
        this.locationList = new ArrayList<>();
        this.blockList = new ArrayList<>();
        this.beforeTpMap = new HashMap<>();
    }

    @Override
    public String getWorldName() {
        return "Kamui";
    }

    @Override
    public String getZipFileName() {
        return "Kamui.zip";
    }

    @Override
    public World createWorld() {
        final World world = Bukkit.createWorld(getWorldCreator());
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("spectatorsGenerateChunks", "false");
        world.setGameRuleValue("naturalRegeneration", "false");
        world.setGameRuleValue("announceAdvancements", "false");
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("doFireTick", "false");
        this.locationList.add(new Location(world, 15.0, 22.0, 14.0, 133.7f, -1.3f));
        this.locationList.add(new Location(world, -14.0, 22.5, -12.5, -47.4f, 1.6f));
        this.locationList.add(new Location(world, 15.2, 21.1, -15.0, 44.4f, -0.5f));
        this.locationList.add(new Location(world, -15.3, 21.1, 15.2, -134.4f, -0.5f));
        Main.getInstance().debug(Main.getInstance().getNAME()+" Fin de creation du monde "+getWorldName());
        return world;
    }

    @Override
    public double getActualPercentPregenTask() {
        return this.percent;
    }

    @Override
    public void startPregen(World world) {
        final PregenerationTask pregenerationTask = new PregenerationTask(world, 10);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isPregen()) {
                    cancel();
                    return;
                }
                pregenerationTask.run();
                percent = pregenerationTask.getPercent();
                if (pregenerationTask.isFinished()) {
                    cancel();
                    setHasBeenPregen(true);
                }
            }
        }.runTaskTimer(Main.getInstance(), 1, 20);
    }

    @Override
    public WorldCreator getWorldCreator() {
        return new WorldCreator(getWorldName()).generateStructures(false);
    }

    @Override
    public void setHasBeenPregen(boolean pregen) {
        this.pregen = pregen;
    }

    @Override
    public boolean isPregen() {
        return this.pregen;
    }

    @Override
    public List<Location> getPossibleTeleportLocations() {
        return this.locationList;
    }

    @Override
    public boolean isPlayerCanBreakNaturalBlock() {
        return false;
    }

    @Override
    public boolean isPlayersCanPlaceBlocks() {
        return true;
    }

    @Override
    public boolean isPlayerCanBreakOtherPlayersBlocks() {
        return true;
    }

    @Override
    public List<Block> getPrecalculateList() {
        return this.blockList;
    }

    @Override
    public void startPrecalculs(World world) {
        if (!isPregen())return;
        for (int x = -30; x <= 30; x++) {
            for (int y = 1; y <= 36; y++) {
                for (int z = -30; z <= 30; z++) {
                    final Block block = world.getBlockAt(x, y, z);
                    if (block.getType().equals(Material.AIR))continue;
                    this.blockList.add(block);
                }
            }
        }
    }
}
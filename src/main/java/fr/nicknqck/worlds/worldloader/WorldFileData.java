package fr.nicknqck.worlds.worldloader;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class WorldFileData {
    private final World world;
    private File regionFolder = null;
    private File[] regionFiles = null;
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private final Map<CoordXZ, List<Boolean>> regionChunkExistence = Collections.synchronizedMap(new HashMap());

    public static WorldFileData create(World world) {
        WorldFileData newData = new WorldFileData(world);
        newData.regionFolder = new File(newData.world.getWorldFolder(), "region");
        if (!newData.regionFolder.exists() || !newData.regionFolder.isDirectory()) {
            File[] possibleDimFolders = newData.world.getWorldFolder().listFiles(new DimFolderFileFilter());
            File[] var3 = (File[])Objects.requireNonNull(possibleDimFolders);
            int var4 = var3.length;
            for(int var5 = 0; var5 < var4; ++var5) {
                File possibleDimFolder = var3[var5];
                File possible = new File(newData.world.getWorldFolder(), possibleDimFolder.getName() + File.separator + "region");
                if (possible.exists() && possible.isDirectory()) {
                    newData.regionFolder = possible;
                    break;
                }
            }
            if (!newData.regionFolder.exists() || !newData.regionFolder.isDirectory()) {
                newData.sendMessage("Could not validate folder for world's region files. Looked in " + newData.world.getWorldFolder().getPath() + " for valid DIM* folder with a region folder in it.");
                return null;
            }
        }
        newData.regionFiles = newData.regionFolder.listFiles(new ExtFileFilter(".MCA"));
        if (newData.regionFiles == null || newData.regionFiles.length == 0) {
            newData.regionFiles = newData.regionFolder.listFiles(new ExtFileFilter(".MCR"));
            if (newData.regionFiles == null || newData.regionFiles.length == 0) {
                newData.sendMessage("Could not find any region files. Looked in: " + newData.regionFolder.getPath());
                return null;
            }
        }
        return newData;
    }

    private WorldFileData(World world) {
        this.world = world;
    }

    public File regionFile(int index) {
        return this.regionFiles.length < index ? null : this.regionFiles[index];
    }

    public CoordXZ regionFileCoordinates(int index) {
        File regionFile = this.regionFile(index);
        String[] cords = regionFile.getName().split("\\.");
        try {
            int x = Integer.parseInt(cords[1]);
            int z = Integer.parseInt(cords[2]);
            return new CoordXZ(x, z);
        } catch (Exception var6) {
            this.sendMessage("Error! Region file found with abnormal name: " + regionFile.getName());
            return null;
        }
    }

    public boolean doesChunkNotExist(int x, int z) {
        CoordXZ region = new CoordXZ(CoordXZ.chunkToRegion(x), CoordXZ.chunkToRegion(z));
        List<Boolean> regionChunks = this.getRegionData(region);
        return !(Boolean)regionChunks.get(this.coordToRegionOffset(x, z));
    }

    public boolean isChunkFullyGenerated(int x, int z) {
        return !this.doesChunkNotExist(x, z) && !this.doesChunkNotExist(x + 1, z) && !this.doesChunkNotExist(x - 1, z) && !this.doesChunkNotExist(x, z + 1) && !this.doesChunkNotExist(x, z - 1);
    }

    public void chunkExistsNow(int x, int z) {
        CoordXZ region = new CoordXZ(CoordXZ.chunkToRegion(x), CoordXZ.chunkToRegion(z));
        List<Boolean> regionChunks = this.getRegionData(region);
        regionChunks.set(this.coordToRegionOffset(x, z), true);
    }

    private int coordToRegionOffset(int x, int z) {
        x %= 32;
        z %= 32;
        if (x < 0) {
            x += 32;
        }
        if (z < 0) {
            z += 32;
        }
        return x + z * 32;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Boolean> getRegionData(CoordXZ region) {
        List<Boolean> data = (List)regionChunkExistence.get(region);
        if (data != null) {
            return data;
        } else {
            data = new ArrayList(1024);
            int i;
            for(i = 0; i < 1024; ++i) {
                data.add(Boolean.FALSE);
            }
            for(i = 0; i < regionFiles.length; ++i) {
                CoordXZ coord = regionFileCoordinates(i);
                if (coord.equals(region)) {
                    try {
                        RandomAccessFile regionData = new RandomAccessFile(regionFile(i), "r");
                        for(int j = 0; j < 1024; ++j) {
                            if (regionData.readInt() != 0) {
                                data.set(j, true);
                            }
                        }
                        regionData.close();
                    } catch (FileNotFoundException var7) {
                        this.sendMessage("Error! Could not open region file to find generated chunks: " + regionFile(i).getName());
                    } catch (IOException var8) {
                        this.sendMessage("Error! Could not read region file to find generated chunks: " + regionFile(i).getName());
                    }
                }
            }
            regionChunkExistence.put(region, data);
            return data;
        }
    }

    private void sendMessage(String text) {
        Bukkit.getLogger().info("[WorldData] " + text);
    }

    private static class DimFolderFileFilter implements FileFilter {
        private DimFolderFileFilter() {
        }
        public boolean accept(File file) {
            return file.exists() && file.isDirectory() && file.getName().toLowerCase().startsWith("dim");
        }
        @SuppressWarnings("unused")
		DimFolderFileFilter(Object x0) {
            this();
        }
    }

    private static class ExtFileFilter implements FileFilter {
        final String ext;
        public ExtFileFilter(String extension) {
            ext = extension.toLowerCase();
        }
        public boolean accept(File file) {
            return file.exists() && file.isFile() && file.getName().toLowerCase().endsWith(ext);
        }
    }
}
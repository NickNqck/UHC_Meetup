package fr.nicknqck.utils.zephyr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Cuboid {
    private final Location minLoc;
    private final Location maxLoc;
    private final int minX;
    private final int minY;
    private final int minZ;
    private final int maxX;
    private final int maxY;
    private final int maxZ;

    public Cuboid(Location loc1, Location loc2) {
        this.minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        this.maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        this.minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        this.maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        this.minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        this.maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        if (loc1.getY() < loc2.getY()) {
            this.minLoc = loc1;
            this.maxLoc = loc2;
        } else if (loc2.getY() < loc1.getY()) {
            this.minLoc = loc2;
            this.maxLoc = loc1;
        } else {
            this.minLoc = loc1;
            this.maxLoc = loc2;
        }

    }

    public Location getMinLoc() {
        return this.minLoc;
    }

    public Location getMaxLoc() {
        return this.maxLoc;
    }

    public World getWorld() {
        return this.minLoc.getWorld();
    }

    public Location getCenter() {
        return new Location(this.getWorld(), (double)((this.maxX - this.minX) / 2 + this.minX), (double)((this.maxY - this.minY) / 2 + this.minY), (double)((this.maxZ - this.minZ) / 2 + this.minZ));
    }

    public boolean isInside(Location location) {
        if (this.getWorld() == null) {
            return false;
        } else {
            return location.getWorld().equals(this.getWorld()) && location.getX() >= (double)this.minX && location.getX() <= (double)this.maxX && location.getY() >= (double)this.minY && location.getY() <= (double)this.maxY && location.getZ() >= (double)this.minZ && location.getZ() <= (double)this.maxZ;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Block> getBlockList() {
        List<Block> blocks = new ArrayList();

        for(int x = this.minX; x <= this.maxX; ++x) {
            for(int y = this.minY; y <= this.maxY; ++y) {
                for(int z = this.minZ; z <= this.maxZ; ++z) {
                    blocks.add(this.getWorld().getBlockAt(x, y, z));
                }
            }
        }

        return blocks;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Block> getBlockListWithOnly(List<Material> materials) {
        List<Block> blocks = new ArrayList();
        Iterator var3 = this.getBlockList().iterator();

        while(var3.hasNext()) {
            Block block = (Block)var3.next();
            if (materials.contains(block.getType())) {
                blocks.add(block);
            }
        }

        return blocks;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Block> getBlockListWithExcpect(List<Material> materials) {
        List<Block> blocks = new ArrayList();
        Iterator var3 = this.getBlockList().iterator();

        while(var3.hasNext()) {
            Block block = (Block)var3.next();
            if (!materials.contains(block.getType())) {
                blocks.add(block);
            }
        }

        return blocks;
    }

    @SuppressWarnings("unchecked")
	public List<Block> getWalls() {
        @SuppressWarnings("rawtypes")
		List<Block> blocks = new ArrayList();

        int y;
        int z;
        for(y = this.minX; y <= this.maxX; ++y) {
            for(z = this.minY; z <= this.maxY; ++z) {
                blocks.add(this.getWorld().getBlockAt(y, z, this.minZ));
                blocks.add(this.getWorld().getBlockAt(y, z, this.maxZ));
            }
        }

        for(y = this.minY; y <= this.maxY; ++y) {
            for(z = this.minZ; z <= this.maxZ; ++z) {
                blocks.add(this.getWorld().getBlockAt(this.minX, y, z));
                blocks.add(this.getWorld().getBlockAt(this.maxX, y, z));
            }
        }

        return blocks;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Block> getFloor() {
        List<Block> blocks = new ArrayList();

        for(int x = this.minX; x < this.maxX; ++x) {
            for(int z = this.minZ; z < this.maxZ; ++z) {
                blocks.add(this.getWorld().getBlockAt(x, this.minY, z));
            }
        }

        return blocks;
    }

    public int getMinX() {
        return this.minX;
    }

    public int getMinY() {
        return this.minY;
    }

    public int getMinZ() {
        return this.minZ;
    }

    public int getMaxX() {
        return this.maxX;
    }

    public int getMaxY() {
        return this.maxY;
    }

    public int getMaxZ() {
        return this.maxZ;
    }
}
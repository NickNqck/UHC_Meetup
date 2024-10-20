package fr.nicknqck.worlds.worldloader;

public class CoordXZ {
    public int x;
    public int z;

    public CoordXZ(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public static int blockToChunk(int blockVal) {
        return blockVal >> 4;
    }

    public static int chunkToRegion(int chunkVal) {
        return chunkVal >> 5;
    }

    public static int chunkToBlock(int chunkVal) {
        return chunkVal << 4;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || obj.getClass() != getClass())
            return false;
        CoordXZ test = (CoordXZ) obj;
        return (test.x == x && test.z == z);
    }

    public int hashCode() {
        return (x << 9) + z;
    }
}
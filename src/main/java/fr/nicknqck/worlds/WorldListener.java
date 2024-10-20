package fr.nicknqck.worlds;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Chunk;
import org.bukkit.block.Biome;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;

@Setter
@Getter
public class WorldListener implements Listener {

    private boolean enable = false;

    @EventHandler
    public void onChunkPopulate(ChunkPopulateEvent event) {
        if (isEnable()){
            setRoofedForest(event.getChunk());
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (isEnable()){
            setRoofedForest(event.getChunk());
        }
    }

    private void setRoofedForest(Chunk chunk) {
        if (chunk.getX() <= 15 && chunk.getZ() <= 15 && chunk.getX() >= -15 && chunk.getZ() >= -15) {
            for (int x = 0; x <= 16; x++) {
                for (int z = 0; z <= 16; z++)
                    chunk.getBlock(x, 60, z).setBiome(Biome.ROOFED_FOREST);
            }
        }
    }
}
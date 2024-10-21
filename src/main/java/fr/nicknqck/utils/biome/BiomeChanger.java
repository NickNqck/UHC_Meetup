package fr.nicknqck.utils.biome;

import java.lang.reflect.Field;

import net.minecraft.server.v1_8_R3.BiomeBase;

public class BiomeChanger {
    public static void init() {
        try {
            Field biomeF = BiomeBase.class.getDeclaredField("biomes");
            biomeF.setAccessible(true);
            if (biomeF.get(null) instanceof BiomeBase[]) {
                BiomeBase[] biomes = (BiomeBase[]) biomeF.get(null);
                for (BiomeBase biome : biomes) {
                    if (biome != null) {
                    	if (biome != BiomeBase.PLAINS || biome != BiomeBase.RIVER) {
                    		swap(biomes, biome);
                    	}
                    }
                }
            }
            biomeF.setAccessible(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void swap(BiomeBase[] biomes, BiomeBase from) {
        swap(biomes, from.id);
    }

    private static void swap(BiomeBase[] biomes, int from) {
        biomes[from] = BiomeBase.ROOFED_FOREST;
    }
}
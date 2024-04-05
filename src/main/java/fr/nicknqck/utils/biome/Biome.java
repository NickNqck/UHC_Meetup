package fr.nicknqck.utils.biome;

import org.bukkit.Location;
import org.bukkit.World;

import net.minecraft.server.v1_8_R3.BiomeBase;

public enum Biome {
	OCEAN(BiomeBase.OCEAN, "§bOcean", "OCEAN", false),
	PLAINS(BiomeBase.PLAINS, "§aPlains", "PLAINS", true),
	DESERT(BiomeBase.DESERT, "§eDesert", "DESERT", true),
	EXTREME_HILLS(BiomeBase.EXTREME_HILLS, "§9Extreme Hills", "EXTREME_HILLS", false),
	FOREST(BiomeBase.FOREST, "§2Forest", "FOREST", true),
	TAIGA(BiomeBase.TAIGA, "§2Taiga", "TAIGA", true),
	SWAMPLAND(BiomeBase.SWAMPLAND, "§2Swapland", "SWAMPLAND", true),
	RIVER(BiomeBase.RIVER, "§bRiver", "RIVER", true),
	FROZEN_OCEAN(BiomeBase.FROZEN_OCEAN, "§bFrozen Ocean", "FROZEN_OCEAN", false),
	FROZEN_RIVER(BiomeBase.FROZEN_RIVER, "§bFrozen River", "FROZEN_RIVER", false),
	ICE_PLAINS(BiomeBase.ICE_PLAINS, "§bIce Plains", "ICE_PLAINS", false),
	ICE_MOUNTAINS(BiomeBase.ICE_MOUNTAINS, "§bIce Mountains", "ICE_MOUNTAINS", false),
	MUSHROOM_ISLAND(BiomeBase.MUSHROOM_ISLAND, "§5Mushroom Island", "MUSHROOM_ISLAND", false),
	MUSHROOM_SHORE(BiomeBase.MUSHROOM_SHORE, "§5Mushroom Shore", "MUSHROOM_SHORE", false),
	BEACH(BiomeBase.BEACH, "§eBeach", "BEACH", true),
	DESERT_HILLS(BiomeBase.DESERT_HILLS, "§eDesert Hills", "DESERT_HILLS", true),
	FOREST_HILLS(BiomeBase.FOREST_HILLS, "§2Forest Hills", "FOREST_HILLS", true),
	TAIGA_HILLS(BiomeBase.TAIGA_HILLS, "§2Taiga Hills", "TAIGA_HILLS", true),
	SMALL_MOUNTAINS(BiomeBase.SMALL_MOUNTAINS, "§9Small Mountains", "SMALL_MOUNTAINS", true),
	JUNGLE(BiomeBase.JUNGLE, "§aJungle", "JUNGLE", false),
	JUNGLE_HILLS(BiomeBase.JUNGLE_HILLS, "§aJungle Hills", "JUNGLE_HILLS", false),
	JUNGLE_EDGE(BiomeBase.JUNGLE_EDGE, "§aJungle Edge", "JUNGLE_EDGE", false),
	DEEP_OCEAN(BiomeBase.DEEP_OCEAN, "§9Deep Ocean", "DEEP_OCEAN", false),
	STONE_BEACH(BiomeBase.STONE_BEACH, "§8Stone Beach", "STONE_BEACH", false),
	COLD_BEACH(BiomeBase.COLD_BEACH, "§bCold Beach", "COLD_BEACH", false),
	BIRCH_FOREST(BiomeBase.BIRCH_FOREST, "§2Birch Forest", "BIRCH_FOREST", true),
	BIRCH_FOREST_HILLS(BiomeBase.BIRCH_FOREST_HILLS, "§2Birch Forest Hills", "BIRCH_FOREST_HILLS", true),
	ROOFED_FOREST(BiomeBase.ROOFED_FOREST, "§2Roofed Forest", "ROOFED_FOREST", true),
	COLD_TAIGA(BiomeBase.COLD_TAIGA, "§bCold Taiga", "COLD_TAIGA", true),
	COLD_TAIGA_HILLS(BiomeBase.COLD_TAIGA_HILLS, "§bCold Taiga Hills", "COLD_TAIGA_HILLS", true),
	MEGA_TAIGA(BiomeBase.MEGA_TAIGA, "§2Mega Taiga", "MEGA_TAIGA", true),
	MEGA_TAIGA_HILLS(BiomeBase.MEGA_TAIGA_HILLS, "§2Mega Taiga Hills", "MEGA_TAIGA_HILLS", true),
	EXTREME_HILLS_PLUS(BiomeBase.EXTREME_HILLS_PLUS, "§9Extreme Hills Plus", "EXTREME_HILLS_PLUS", false),
	SAVANNA(BiomeBase.SAVANNA, "§6Savanna", "SAVANNA", true),
	SAVANNA_PLATEAU(BiomeBase.SAVANNA_PLATEAU, "§6Savanna Plateau", "SAVANNA_PLATEAU", true),
	MESA(BiomeBase.MESA, "§6Mesa", "MESA", true),
	MESA_PLATEAU_F(BiomeBase.MESA_PLATEAU_F, "§6Mesa Plateau F", "MESA_PLATEAU_F", true),
	MESA_PLATEAU(BiomeBase.MESA_PLATEAU, "§6Mesa Plateau", "MESA_PLATEAU", true);
	
	private BiomeBase biome;
	private String name, bukkitName;
	private boolean isActived;
	
	private Biome(BiomeBase biome, String name, String bukkitName, boolean isActived) {
		this.biome = biome;
		this.name = name;
		this.bukkitName = bukkitName;
		this.isActived = isActived;
	}

	public BiomeBase getBiome() {
		return biome;
	}

	public void setBiome(BiomeBase biome) {
		this.biome = biome;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getBukkitName() {
		return this.bukkitName;
	}

	public boolean isActived() {
		return isActived;
	}

	public void setActived(boolean isActived) {
		this.isActived = isActived;
	}
	
	public static Location findBiome(World world, Biome biome) {
		for(int i = -2000; i < 2000; i += 16) {
			for(int j = -2000; j < 2000; j += 16) {
				if(world.getBiome(i, j) == org.bukkit.block.Biome.valueOf(biome.getBukkitName()))
					return new Location(world, i, 151.0D, j); 
			} 
		}
		return null;
	}
}

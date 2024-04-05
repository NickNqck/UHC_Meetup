package fr.nicknqck.pregen;

import java.lang.reflect.Field;

import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.generator.NormalChunkGenerator;

import net.minecraft.server.v1_8_R3.ChunkProviderGenerate;
import net.minecraft.server.v1_8_R3.ChunkProviderServer;
import net.minecraft.server.v1_8_R3.ChunkSnapshot;
import net.minecraft.server.v1_8_R3.IChunkProvider;
import net.minecraft.server.v1_8_R3.World;

public class WorldGenCaves extends net.minecraft.server.v1_8_R3.WorldGenCaves {
    private final int amount;

    public WorldGenCaves(int amountOfCaves) {
        this.amount = amountOfCaves;
    }

    public void a(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, ChunkSnapshot chunksnapshot) {
        for(int i = 0; i < this.amount; ++i) {
            int k = this.a;
            this.c = world;
            this.b.setSeed(world.getSeed() + (long)i);
            long l = this.b.nextLong();
            long i1 = this.b.nextLong();

            for(int j1 = chunkX - k; j1 <= chunkX + k; ++j1) {
                for(int k1 = chunkZ - k; k1 <= chunkZ + k; ++k1) {
                    long l1 = (long)j1 * l;
                    long i2 = (long)k1 * i1;
                    this.b.setSeed(l1 ^ i2 ^ world.getSeed());
                    this.a(world, j1, k1, chunkX, chunkZ, chunksnapshot);
                }
            }
        }

    }

    public static void load(org.bukkit.World world, int amountOfCaves) throws NoSuchFieldException, IllegalAccessException {
        World craftWorld = ((CraftWorld)world).getHandle();
        Field chunkProviderWorldField = World.class.getDeclaredField("chunkProvider");
        chunkProviderWorldField.setAccessible(true);
        Field chunkProviderChunkProviderField = ChunkProviderServer.class.getDeclaredField("chunkProvider");
        chunkProviderChunkProviderField.setAccessible(true);
        Field chunkProviderField = NormalChunkGenerator.class.getDeclaredField("provider");
        chunkProviderField.setAccessible(true);
        Field worldGenCaveField = ChunkProviderGenerate.class.getDeclaredField("u");
        worldGenCaveField.setAccessible(true);
        worldGenCaveField.set(chunkProviderField.get(chunkProviderChunkProviderField.get(chunkProviderWorldField.get(craftWorld))), new WorldGenCaves(amountOfCaves));
    }
}

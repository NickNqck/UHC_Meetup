package fr.nicknqck.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.EntityLightning;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityWeather;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

public class WorldUtils {

    @Getter
    private static final DecimalFormat decimalFormater = new DecimalFormat("#.##");

    public static void spawnParticle(final Location loc, final EnumParticle particle) {
        for (final Player online : Bukkit.getOnlinePlayers()) {
            spawnParticle(online, loc, particle);
        }
    }

    public static String getBeautyHealth(final Player player) {
        return getDecimalFormater().format(player.getHealth() + ((CraftPlayer) player).getHandle().getAbsorptionHearts());
    }

    public static void spawnColoredParticle(final Location loc, final EnumParticle particle, final float red, final float green, final float blue) {
        for (final Player online : Bukkit.getOnlinePlayers()) {
            spawnColoredParticle(online, loc, particle, red, green, blue);
        }
    }

    public static void spawnParticle(final Player player, final Location loc, final EnumParticle particle) {
        spawnParticle(player, loc, particle, 0.0f, 0.0f, 0.0f, 0.0f, 1);
    }

    public static void spawnParticle(final Player player, final Location loc, final EnumParticle particle, final float xOffset, final float yOffset, final float zOffset, final float speed, final int count) {
        final PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, true, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), xOffset, yOffset, zOffset, speed, count);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public static void spawnColoredParticle(final Player player, final Location loc, final EnumParticle particle, final float red, final float green, final float blue) {
        final PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, true, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), red, green, blue, 1.0f, 0);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public static double getDistanceBetweenTwoLocations(final Location one, final Location two) {
        final double minX = Math.min(one.getX(), two.getX());
        final double maxX = Math.max(one.getX(), two.getX());
        final double minZ = Math.min(one.getZ(), two.getZ());
        final double maxZ = Math.max(one.getZ(), two.getZ());
        final double xDiff = maxX - minX;
        final double zDiff = maxZ - minZ;
        return Math.sqrt(xDiff * xDiff + zDiff * zDiff);
    }

    public static void createBeautyExplosion(final Location loc, final int power, final boolean fire) {
        List<Location> blocks = generateSphere(loc, power, false);
        for (final Location blockLoc : blocks) {
            final Block block = blockLoc.getBlock();
            if (block.getType() != Material.AIR && block.getType() != Material.BEDROCK) {
                blockLoc.getBlock().setType(Material.AIR);
            }
        }
    }

    public static List<Location> generateSphere(final Location centerBlock, final int radius, final boolean hollow) {
        if (centerBlock == null) {
            return new ArrayList<Location>();
        }
        final List<Location> circleBlocks = new ArrayList<Location>();
        final int bx = centerBlock.getBlockX();
        final int by = centerBlock.getBlockY();
        final int bz = centerBlock.getBlockZ();
        for (int x = bx - radius; x <= bx + radius; ++x) {
            for (int y = by - radius; y <= by + radius; ++y) {
                for (int z = bz - radius; z <= bz + radius; ++z) {
                    final double distance = (bx - x) * (bx - x) + (bz - z) * (bz - z) + (by - y) * (by - y);
                    if (distance < radius * radius && (!hollow || distance >= (radius - 1) * (radius - 1))) {
                        final Location l = new Location(centerBlock.getWorld(), x, y, z);
                        circleBlocks.add(l);
                    }
                }
            }
        }
        return circleBlocks;
    }
    public static List<Location> generateCylinder(Location center, int radius) {
        List<Location> locs = new ArrayList<>();
        World world = center.getWorld();

        int cx = center.getBlockX();
        int cz = center.getBlockZ();
        int rSquared = radius * radius;
        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                if ((cx - x) * (cx - x) + (cz - z) * (cz - z) <= rSquared) {
                    locs.add(new Location(world, x, world.getHighestBlockYAt(x, z), z));
                }
            }
        }
        return locs;
    }
}

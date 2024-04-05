package fr.nicknqck.particles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicknqck.Main;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

public class MathUtil {
	
    public static void sendParticle(final EnumParticle particle, final Location location) {
        sendParticle(particle, location.getX(), location.getY(), location.getZ(), location.getWorld());
    }

    public static void sendParticle(final EnumParticle particle, final double x, final double y, final double z, World world) {
        final PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, true, (float) x, (float) y, (float) z, 0.0f, 0.0f, 0.0f, 0.0f, 10, (int[]) null);
        for (final Player p : world.getPlayers()) {
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }
    }
    public static void sendParticleTo(Player target, final EnumParticle particle, final double x, final double y, final double z) {
    	final PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, true, (float) x, (float) y, (float) z, 0.0f, 0.0f, 0.0f, 0.0f, 10, (int[]) null);
            ((CraftPlayer) target).getHandle().playerConnection.sendPacket(packet);
    }
    public static void sendParticleTo(Player target, final EnumParticle particle, final Location location) {
        sendParticleTo(target, particle, location.getX(), location.getY(), location.getZ());
    }
    public static void sendCircleParticle(final EnumParticle particle, final Location center, final double radius, final int amount) {
        final double increment = 6.283185307179586 / amount;
        for (int i = 0; i < amount; ++i) {
            final double angle = i * increment;
            final double x = center.getX() + radius * Math.cos(angle);
            final double z = center.getZ() + radius * Math.sin(angle);
            sendParticle(particle, x, center.getY() + 0.5, z, center.getWorld());
        }
    }
    public static List<Location> getCircle(final Location center, final double radius){
    	List<Location> e = new ArrayList<>();
    	double amount = radius*15;
    	final double increment = 6.283185307179586 / amount;
    	for (int i = 0; i < amount; ++i) {
    		final double angle = i * increment;
            final double x = center.getX() + radius * Math.cos(angle);
            final double z = center.getZ() + radius * Math.sin(angle);
            e.add(new Location(center.getWorld(), x, center.getY(), z));
    	}
    	return e;
    }
    public static void sendParticleLine(final Location startLocation, final Location endLocation, final EnumParticle particle, final int amount) {
        int numberOfPoints = amount; // Adjust the number of points to determine the smoothness of the line

        double distanceX = (endLocation.getX() - startLocation.getX()) / numberOfPoints;
        double distanceY = (endLocation.getY() - startLocation.getY()) / numberOfPoints;
        double distanceZ = (endLocation.getZ() - startLocation.getZ()) / numberOfPoints;

        for (int i = 0; i < numberOfPoints; i++) {
            double x = startLocation.getX() + (distanceX * i);
            double y = startLocation.getY() + (distanceY * i);
            double z = startLocation.getZ() + (distanceZ * i);

            sendParticle(particle, x, y+1, z, startLocation.getWorld());
        }
    }

    public static void sendHeadParticle(final EnumParticle particle, final Location center, final double radius, final int amount) {
        final double increment = 6.283185307179586 / amount;
        for (int i = 0; i < amount; ++i) {
            final double angle = i * increment;
            final double x = center.getX() + radius * Math.cos(angle);
            final double z = center.getZ() + radius * Math.sin(angle);
            sendParticle(particle, x, center.getY() + 2.5, z, center.getWorld());
        }
    }
    public static List<Location> getSphere(final Location centerBlock, final int radius, final boolean hollow) {
        final List<Location> circleBlocks = new ArrayList<>();
        final int bX = centerBlock.getBlockX();
        final int bY = centerBlock.getBlockY();
        final int bZ = centerBlock.getBlockZ();
        for (int x = bX - radius; x <= bX + radius; ++x) {
            for (int y = bY - radius; y <= bY + radius; ++y) {
                for (int z = bZ - radius; z <= bZ + radius; ++z) {
                    final double distance = (bX - x) * (bX - x) + (bZ - z) * (bZ - z) + (bY - y) * (bY - y);
                    if (distance < radius * radius && (!hollow || distance >= (radius - 1) * (radius - 1))) {
                        circleBlocks.add(new Location(centerBlock.getWorld(), (double) x, (double) y, (double) z));
                    }
                }
            }
        }
        return circleBlocks;
    }
    private List<FallingBlock> fallingBlocks = new ArrayList<FallingBlock>();
    public Set<Location> sphere(Location location, int radius, boolean hollow){
        Set<Location> blocks = new HashSet<Location>();
        World world = location.getWorld();
        int X = location.getBlockX();
        int Y = location.getBlockY();
        int Z = location.getBlockZ();
        int radiusSquared = radius * radius;

        if(hollow){
            for (int x = X - radius; x <= X + radius; x++) {
                for (int y = Y - radius; y <= Y + radius; y++) {
                    for (int z = Z - radius; z <= Z + radius; z++) {
                        if ((X - x) * (X - x) + (Y - y) * (Y - y) + (Z - z) * (Z - z) <= radiusSquared) {
                            Location block = new Location(world, x, y, z);
                            blocks.add(block);
                        }
                    }
                }
            }
            return makeHollow(blocks, true);
        } else {
            for (int x = X - radius; x <= X + radius; x++) {
                for (int y = Y - radius; y <= Y + radius; y++) {
                    for (int z = Z - radius; z <= Z + radius; z++) {
                        if ((X - x) * (X - x) + (Y - y) * (Y - y) + (Z - z) * (Z - z) <= radiusSquared) {
                            Location block = new Location(world, x, y, z);
                            blocks.add(block);
                        }
                    }
                }
            }
            return blocks;
        }
    }
    @SuppressWarnings("deprecation")
	public Set<FallingBlock> spawnFallingBlocks(Location location, Material material, int radius, boolean hollow, boolean disparition, int timerdisparition) {
        Set<FallingBlock> fallingBlocks = new HashSet<>();
        World world = location.getWorld();
        int X = location.getBlockX();
        int Y = location.getBlockY();
        int Z = location.getBlockZ();
        int radiusSquared = radius * radius;

        if (hollow) {
            for (int x = X - radius; x <= X + radius; x++) {
                for (int y = Y - radius; y <= Y + radius; y++) {
                    for (int z = Z - radius; z <= Z + radius; z++) {
                        if ((X - x) * (X - x) + (Y - y) * (Y - y) + (Z - z) * (Z - z) <= radiusSquared) {
                            Location blockLoc = new Location(world, x, y, z);
                            FallingBlock fallingBlock = world.spawnFallingBlock(blockLoc.add(0.5, 0, 0.5), material, (byte) 0);
                            fallingBlock.setDropItem(false);
                            fallingBlock.setHurtEntities(false);
                            fallingBlock.setFallDistance(0);
                            if (disparition) {
                            	fallingBlocks.add(fallingBlock);
                            }
                        }
                    }
                }
            }
        } else {
            for (int x = X - radius; x <= X + radius; x++) {
                for (int y = Y - radius; y <= Y + radius; y++) {
                    for (int z = Z - radius; z <= Z + radius; z++) {
                        if ((X - x) * (X - x) + (Y - y) * (Y - y) + (Z - z) * (Z - z) <= radiusSquared) {
                            Location blockLoc = new Location(world, x, y, z);
                            FallingBlock fallingBlock = world.spawnFallingBlock(blockLoc.add(0.5, 0, 0.5), material, (byte) 0);
                            fallingBlock.setDropItem(false);
                            fallingBlock.setHurtEntities(false);
                            fallingBlock.setFallDistance(0);
                            fallingBlocks.add(fallingBlock);
                            if (disparition) {
                            	this.fallingBlocks.add(fallingBlock);
                            }
                        }
                    }
                }
            }
        }
        if (disparition) {
            new BukkitRunnable() {
            	int i = 0;
                @Override
                public void run() {
                	i++;
                	if (i == timerdisparition) {
                        Iterator<FallingBlock> iterator = fallingBlocks.iterator();
                        while (iterator.hasNext()) {
                            FallingBlock fallingBlock = iterator.next();
                            if (fallingBlock.isDead()) {
                                Location blockLocation = fallingBlock.getLocation();
                                blockLocation.getBlock().setType(Material.AIR); // Transforme le bloc en air

                                iterator.remove(); // Supprime le bloc de la liste
                            }
                        }
                        cancel();
                	}
                }
            }.runTaskTimer(Main.getInstance(), 0, 20);
        }
        return fallingBlocks;
    }

    private Set<Location> makeHollow(Set<Location> blocks, boolean sphere){
        Set<Location> edge = new HashSet<Location>();
        if(!sphere){
            for(Location l : blocks){
                World w = l.getWorld();
                int X = l.getBlockX();
                int Y = l.getBlockY();
                int Z = l.getBlockZ();
                Location front = new Location(w, X + 1, Y, Z);
                Location back = new Location(w, X - 1, Y, Z);
                Location left = new Location(w, X, Y, Z + 1);
                Location right = new Location(w, X, Y, Z - 1);
                if(!(blocks.contains(front) && blocks.contains(back) && blocks.contains(left) && blocks.contains(right))){
                    edge.add(l);
                }
            }
        } else {
            for(Location l : blocks){
                World w = l.getWorld();
                int X = l.getBlockX();
                int Y = l.getBlockY();
                int Z = l.getBlockZ();
                Location front = new Location(w, X + 1, Y, Z);
                Location back = new Location(w, X - 1, Y, Z);
                Location left = new Location(w, X, Y, Z + 1);
                Location right = new Location(w, X, Y, Z - 1);
                Location top = new Location(w, X, Y + 1, Z);
                Location bottom = new Location(w, X, Y - 1, Z);
                if(!(blocks.contains(front) && blocks.contains(back) && blocks.contains(left) && blocks.contains(right) && blocks.contains(top) && blocks.contains(bottom))){
                    edge.add(l);
                }
            }
        }
        return edge;
    }
    public static boolean isEven(int number) {
        return number % 2 == 0;
    }

    public static boolean isOdd(int number) {
        return number % 2 != 0;
    }

    public static boolean isImpaire(int num) {
    	return isEven(num);
    }
    public static void spawnMoovingCircle(EnumParticle circleParticle,Location center, int maxRadius, int duration) {
        new BukkitRunnable() {
            int currentRadius = maxRadius;
            int step = 1;

            @Override
            public void run() {
                if (currentRadius <= 0) {
                    cancel();
                    return;
                }
                List<Location> circle = MathUtil.getCircle(center, currentRadius);
                for (Location loc : circle) {
                    MathUtil.sendParticle(circleParticle, loc);
                }
                currentRadius -= step;
            }
        }.runTaskTimer(Main.getInstance(), 0, 8);
    }
}
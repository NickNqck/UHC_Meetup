package fr.nicknqck.utils.particles;

import fr.nicknqck.Main;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

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
            final double x = center.getX() + radius * cos(angle);
            final double z = center.getZ() + radius * sin(angle);
            sendParticle(particle, x, center.getY() + 0.5, z, center.getWorld());
        }
    }
    public static List<Location> getCircle(final Location center, final double radius){
    	List<Location> e = new ArrayList<>();
    	double amount = radius*15;
    	final double increment = 6.283185307179586 / amount;
    	for (int i = 0; i < amount; ++i) {
    		final double angle = i * increment;
            final double x = center.getX() + radius * cos(angle);
            final double z = center.getZ() + radius * sin(angle);
            e.add(new Location(center.getWorld(), x, center.getY(), z));
    	}
    	return e;
    }
    public static void spawnMoovingCircle(EnumParticle circleParticle, Location center, int radius, int duration, UUID recever) {
        new BukkitRunnable() {
            private int time = 0;
            private final double amount = radius*15;
            final double increment = 6.283185307179586 / amount;
            private int i = 0;
            @Override
            public void run() {
                if (time == duration){
                    cancel();
                    return;
                }
                final double angle = i * increment;
                final double x = center.getX() + radius * cos(angle);
                final double z = center.getZ() + radius * sin(angle);
                if (recever == null){
                    sendParticle(circleParticle, x, center.getY(), z, center.getWorld());
                } else {
                    if (Bukkit.getPlayer(recever) != null){
                        sendParticleTo(Bukkit.getPlayer(recever), circleParticle, x, center.getY(), z);
                    } else {
                        sendParticle(circleParticle, x, center.getY(), z, center.getWorld());
                    }
                }
                i++;
                time++;
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }
    public static void sendParticleLine(final Location startLocation, final Location endLocation, final EnumParticle particle, final int amount) {

        double distanceX = (endLocation.getX() - startLocation.getX()) / amount;
        double distanceY = (endLocation.getY() - startLocation.getY()) / amount;
        double distanceZ = (endLocation.getZ() - startLocation.getZ()) / amount;

        for (int i = 0; i < amount; i++) {
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
            final double x = center.getX() + radius * cos(angle);
            final double z = center.getZ() + radius * sin(angle);
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
                        circleBlocks.add(new Location(centerBlock.getWorld(), x, y, z));
                    }
                }
            }
        }
        return circleBlocks;
    }
    private List<FallingBlock> fallingBlocks = new ArrayList<>();
    public Set<Location> sphere(Location location, int radius, boolean hollow){
        Set<Location> blocks = new HashSet<>();
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
    public static void spawnSimpleWave(Player player, int maxTimeInTick){
        /*
        Origin source code: https://pastebin.com/LRnqmx1J
         */
        new BukkitRunnable(){
            double t = Math.PI/4;
            Location loc = player.getLocation();
            int time = 0;
            public void run(){
                t = t + 0.1*Math.PI;
                time++;
                for (double theta = 0; theta <= 2*Math.PI; theta = theta + Math.PI/16){
                    theta = theta + Math.PI/48;

                    double x = t*cos(theta);
                    double y = Math.exp(-0.1*t) * sin(t);
                    double z = t*sin(theta);
                    loc.add(x,y,z);
                    sendParticle(EnumParticle.VILLAGER_HAPPY, loc);
                    loc.subtract(x,y,z);
                }
                if (time > maxTimeInTick){
                    this.cancel();
                }
            }

        }.runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
    }
    public static class SimpleWave extends BukkitRunnable {
        private Player player;
        private int maxTimeInTick;
        private int time = 0;
        double t = Math.PI/4;
        Location loc;
        public SimpleWave(Player player, int maxTimeInTick){
            this.player = player;
            this.maxTimeInTick = maxTimeInTick;
            this.loc = player.getLocation().clone();
        }

        @Override
        public void run() {
            t = t + 0.1*Math.PI;
            time++;
            for (double theta = 0; theta <= 2*Math.PI; theta = theta + Math.PI/16){
                theta = theta + Math.PI/48;

                double x = t*cos(theta);
                double y = Math.exp(-0.1*t) * sin(t);
                double z = t*sin(theta);
                loc.add(x,y,z);
                sendParticle(EnumParticle.VILLAGER_HAPPY, loc);
                loc.subtract(x,y,z);
            }
            if (time > maxTimeInTick){
                this.cancel();
            }
        }
    }
    public static void createLaser(Player user, int length){
        new BukkitRunnable() {
          //  int i = 20;
            @Override
            public void run() {
                double particleDistance = 0.5;

          //      for (Player online : Bukkit.getOnlinePlayers()) {
               //     ItemStack hand = online.getItemInHand();

             //       if (hand.hasItemMeta() && hand.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Laser Pointer")) {
                        Location location = user.getLocation().add(0, 1, 0);

                        for (double waypoint = 1; waypoint < length; waypoint += particleDistance) {
                            Vector vector = location.getDirection().multiply(waypoint);
                            location.add(vector);

                            if (location.getBlock().getType() != Material.AIR)
                                break;

                            sendParticle(EnumParticle.REDSTONE, location);
                        }
                        cancel();
                   // }
            //    }
            }

        }.runTaskTimer(Main.getInstance(), 0, 1);
    }
    public static void drawTornado(Location loc, double radius,double increase){
        new BukkitRunnable() {
            double y = loc.getY();
            double t = loc.getX();
            double Finalradius = radius;
            Location aLoc = loc.clone();
            @Override
            public void run() {
                if (aLoc.distance(loc) >= 10.0) {
                    cancel();
                    return;
                }

                double x = loc.getX()+(Finalradius*Math.sin(t-0.01));
                double z = loc.getZ()+(Finalradius*Math.cos(t-0.01));
                Location toLoc = new Location(loc.getWorld(), x, y, z);
                sendParticle(EnumParticle.REDSTONE, toLoc);
         //       Bukkit.broadcastMessage(toLoc.toString());
                y += 0.01;
                Finalradius+=increase;
                t+= 0.05;
                aLoc = toLoc;
             //   Bukkit.broadcastMessage("t ="+ t+", y= "+y+", radius = "+Finalradius);

            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 0,1);
    }
}
package fr.nicknqck.utils.raytrace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.nicknqck.Main;

public class RayTrace
{
    private final Vector origin;
    private final Vector direction;
    
    public RayTrace(final Vector origin, final Vector direction) {
        this.origin = origin;
        this.direction = direction;
    }
    
    public Vector getPostion(final double blocksAway) {
        return this.origin.clone().add(this.direction.clone().multiply(blocksAway));
    }
    
    public boolean isOnLine(final Vector position) {
        final double t = (position.getX() - this.origin.getX()) / this.direction.getX();
        return position.getBlockY() == this.origin.getY() + t * this.direction.getY() && position.getBlockZ() == this.origin.getZ() + t * this.direction.getZ();
    }
    
    public List<Vector> traverse(final double blocksAway, final double accuracy) {
        final List<Vector> positions = new ArrayList<>();
        for (double d = 0.0; d <= blocksAway; d += accuracy) {
            positions.add(this.getPostion(d));
        }
        return positions;
    }
    
    public Vector positionOfIntersection(final Vector min, final Vector max, final double blocksAway, final double accuracy) {
        final List<Vector> positions = this.traverse(blocksAway, accuracy);
        for (final Vector position : positions) {
            if (intersects(position, min, max)) {
                return position;
            }
        }
        return null;
    }
    
    public boolean intersects(final Vector min, final Vector max, final double blocksAway, final double accuracy) {
        final List<Vector> positions = this.traverse(blocksAway, accuracy);
        for (final Vector position : positions) {
            if (intersects(position, min, max)) {
                return true;
            }
        }
        return false;
    }
    
    public Vector positionOfIntersection(final BoundingBox boundingBox, final double blocksAway, final double accuracy) {
        final List<Vector> positions = this.traverse(blocksAway, accuracy);
        for (final Vector position : positions) {
            if (intersects(position, boundingBox.getMin(), boundingBox.getMax())) {
                return position;
            }
        }
        return null;
    }
    
    public boolean intersects(final BoundingBox boundingBox, final double blocksAway, final double accuracy) {
        final List<Vector> positions = this.traverse(blocksAway, accuracy);
        for (final Vector position : positions) {
            if (intersects(position, boundingBox.getMin(), boundingBox.getMax())) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean intersects(final Vector position, final Vector min, final Vector max) {
        return position.getX() >= min.getX() && position.getX() <= max.getX() && position.getY() >= min.getY() && position.getY() <= max.getY() && position.getZ() >= min.getZ() && position.getZ() <= max.getZ();
    }
    
    public void highlight(final World world, final double blocksAway, final double accuracy) {
        for (final Vector position : this.traverse(blocksAway, accuracy)) {
            world.playEffect(position.toLocation(world), Effect.COLOURED_DUST, 0);
        }
    }
    public Vector positionOfIntersection(double maxDistance, double accuracy) {
        for (double d = 0.0; d <= maxDistance; d += accuracy) {
            Vector position = getPostion(d);
            if (position.toLocation(Main.getInstance().getWorldManager().getGameWorld()).getBlock().getType().isSolid()) {
                return position;
            }
        }
        return null;
    }
    public static Player getTargetPlayer(Player player, double distanceMax, Predicate<Player> playerPredicate) {
        final RayTrace rayTrace = new RayTrace(player.getEyeLocation().toVector(), player.getEyeLocation().getDirection());
        final List<Vector> positions = rayTrace.traverse(distanceMax, 0.2D);
        for (Vector vector : positions) {
            final Location position = vector.toLocation(player.getWorld());
            final Collection<Entity> entities = player.getWorld().getNearbyEntities(position, 1.0D, 1.0D, 1.0D);
            for (Entity entity : entities) {
                if (entity instanceof Player && entity != player && rayTrace.intersects(new BoundingBox(entity), distanceMax, 0.2D)) {
                    final Player target = (Player) entity;
                    if (playerPredicate != null && !playerPredicate.test(target)) {
                        continue;
                    }
                    if (target.getGameMode().equals(GameMode.SPECTATOR) || target.getGameMode().equals(GameMode.CREATIVE)) {
                        return null;
                    }
                    return target;
                }
            }
        }
        return null;
    }
}
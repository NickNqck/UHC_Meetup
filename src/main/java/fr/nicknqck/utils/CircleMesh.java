package fr.nicknqck.utils;

import org.bukkit.Location;
import org.bukkit.World;

public class CircleMesh {

    private double radius;
    @SuppressWarnings("unused")
	private int y;
    private World world;

    public CircleMesh(double radius, int y, World world) {
        this.radius = radius;
        this.y = y;
        this.world = world;
    }

    public Location calc(int now, int max) {
        double radius = 6.283185307179586D - 6.283185307179586D / max * now;
        double x = this.radius * Math.cos(radius);
        double z = this.radius * Math.sin(radius);
        return new Location(this.world, x, world.getHighestBlockYAt((int)x,(int)z), z);
    }

}
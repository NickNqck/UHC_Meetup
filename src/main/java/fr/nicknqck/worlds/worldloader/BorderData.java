package fr.nicknqck.worlds.worldloader;

import java.text.DecimalFormat;

public class BorderData {
    private final double x;
    private final double z;
    private int radiusX = 0;
    private int radiusZ = 0;
    private double maxX;
    private double minX;
    private double maxZ;
    private double minZ;

    public BorderData(double x, double z, int radiusX, int radiusZ) {
        this.x = x;
        this.z = z;
        setRadiusX(radiusX);
        setRadiusZ(radiusZ);
    }

    public double getX() {
        return x;
    }

    public double getZ() {
        return z;
    }

    public int getRadiusX() {
        return radiusX;
    }

    public int getRadiusZ() {
        return radiusZ;
    }

    public void setRadiusX(int radiusX) {
        this.radiusX = radiusX;
        maxX = x + (double)radiusX;
        minX = x - (double)radiusX;
    }

    public void setRadiusZ(int radiusZ) {
        this.radiusZ = radiusZ;
        maxZ = z + (double)radiusZ;
        minZ = z - (double)radiusZ;
    }

    public void setRadius(int radius) {
        setRadiusX(radius);
        setRadiusZ(radius);
    }

    public String toString() {
        return "radius " + (radiusX == radiusZ ? radiusX : radiusX + "x" + radiusZ) + " at X: " + (new DecimalFormat("0.0")).format(x) + " Z: " + (new DecimalFormat("0.0")).format(z);
    }

    public boolean insideBorder(double xLoc, double zLoc) {
        return xLoc >= minX && xLoc <= maxX && zLoc >= minZ && zLoc <= maxZ;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            BorderData test = (BorderData)obj;
            return test.x == x && test.z == z && test.radiusX == radiusX && test.radiusZ == radiusZ;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return ((int)(x * 10.0D) << 4) + (int)z + (radiusX << 2) + (radiusZ << 3);
    }
}
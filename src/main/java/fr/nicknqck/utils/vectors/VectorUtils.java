package fr.nicknqck.utils.vectors;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class VectorUtils {

    /**
     * Rotates a vector around the X axis at an angle
     *
     * @param v     Starting vector
     * @param angle How much to rotate
     * @return The starting vector rotated
     */
    public static Vector rotateAroundAxisX(Vector v, double angle) {
        double y, z, cos, sin;
        cos = MathL.cos(angle);
        sin = MathL.sin(angle);
        y = v.getY() * cos - v.getZ() * sin;
        z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    /**
     * Rotates a vector around the Y axis at an angle
     *
     * @param v     Starting vector
     * @param angle How much to rotate
     * @return The starting vector rotated
     */
    public static Vector rotateAroundAxisY(Vector v, double angle) {
        double x, z, cos, sin;
        cos = MathL.cos(angle);
        sin = MathL.sin(angle);
        x = v.getX() * cos + v.getZ() * sin;
        z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }

    /**
     * Rotates a vector around the Z axis at an angle
     *
     * @param v     Starting vector
     * @param angle How much to rotate
     * @return The starting vector rotated
     */
    public static Vector rotateAroundAxisZ(Vector v, double angle) {
        double x, y, cos, sin;
        cos = MathL.cos(angle);
        sin = MathL.sin(angle);
        x = v.getX() * cos - v.getY() * sin;
        y = v.getX() * sin + v.getY() * cos;
        return v.setX(x).setY(y);
    }

    /**
     * Rotates a vector around the X, Y, and Z axes
     *
     * @param v      The starting vector
     * @param angleX The change angle on X
     * @param angleY The change angle on Y
     * @param angleZ The change angle on Z
     * @return The starting vector rotated
     */
    public static Vector rotateVector(Vector v, double angleX, double angleY, double angleZ) {
        rotateAroundAxisX(v, angleX);
        rotateAroundAxisY(v, angleY);
        rotateAroundAxisZ(v, angleZ);
        return v;
    }

    /**
     * Rotate a vector about a location using that location's direction
     *
     * @param v        The starting vector
     * @param location The location to rotate around
     * @return The starting vector rotated
     */
    public static Vector rotateVector(Vector v, Location location) {
        return rotateVector(v, location.getYaw(), location.getPitch());
    }

    /**
     * This handles non-unit vectors, with yaw and pitch instead of X,Y,Z angles.
     * <p>
     * Thanks to SexyToad!
     *
     * @param v            The starting vector
     * @param yawDegrees   The yaw offset in degrees
     * @param pitchDegrees The pitch offset in degrees
     * @return The starting vector rotated
     */
    public static Vector rotateVector(Vector v, float yawDegrees, float pitchDegrees) {
        double yaw = Math.toRadians(-1 * (yawDegrees + 90));
        double pitch = Math.toRadians(-pitchDegrees);

        double cosYaw = MathL.cos(yaw);
        double cosPitch = MathL.cos(pitch);
        double sinYaw = MathL.sin(yaw);
        double sinPitch = MathL.sin(pitch);

        double initialX, initialY, initialZ;
        double x, y, z;

        // Z_Axis rotation (Pitch)
        initialX = v.getX();
        initialY = v.getY();
        x = initialX * cosPitch - initialY * sinPitch;
        y = initialX * sinPitch + initialY * cosPitch;

        // Y_Axis rotation (Yaw)
        initialZ = v.getZ();
        initialX = x;
        z = initialZ * cosYaw - initialX * sinYaw;
        x = initialZ * sinYaw + initialX * cosYaw;

        return new Vector(x, y, z);
    }

    /**
     * Gets the angle toward the X axis
     *
     * @param vector The vector to check
     * @return The angle toward the X axis
     */
    public static double angleToXAxis(Vector vector) {
        return Math.atan2(vector.getX(), vector.getY());
    }

} 

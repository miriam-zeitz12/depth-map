package edu.ncf.miriam_zeitz12.depthmap;

/**
 * Represents a point in 3D space, for use in writing .OBJ files.
 * Also holds ID such that we can map it in .OBJ format f lines.
 * Created by Vinushka on 5/4/2015.
 */
public class Point3D {
    /**
     * ID of the point, which will represent its ordering in the .OBJ file.
     */
    private int pointID;
    /**
     * X-coordinate of the point.
     */
    private double x;
    /**
     * Y-coordinate of the point.
     */
    private double y;
    /**
     * Z-coordinate of the point.
     */
    private double z;

    public Point3D(int id, double newX, double newY, double newZ) {
        x = newX;
        y = newY;
        z = newZ;
        pointID = id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public int getPointID() {
        return pointID;
    }

    @Override
    public String toString() {
        return pointID+":["+x","+y+","+z+"]";
    }
}

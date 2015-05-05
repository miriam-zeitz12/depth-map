package edu.ncf.miriam_zeitz12.depthmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds the 3D mesh and writes a .OBJ file.
 * Created by Vinushka on 5/4/2015.
 */
public class MeshCreator {
    /**
     * Creates the 3D mesh from the depth map at inFilePath, and writes an .OBJ
     * file at outFilePath.
     * @param inFilePath - the path to the depth map.
     * @param outFilePath - the path to the output .OBJ file.
     * @throws IOException - if there's issues either reading the depth map,
     * or writing the .OBJ file.
     */
    public MeshCreator(String inFilePath, String outFilePath) throws IOException{
        //first get the data
        DataExtractor extract = new DataExtractor(new FileInputStream(inFilePath));
        String data = extract.getDepthData();
        double near = extract.getNear();
        double far = extract.getFar();
        //now make a Bitmap out of it, read stream so we don't have to
        //care about indexing directly
        InputStream imageStream = new ByteArrayInputStream(data.getBytes());
        Bitmap image = BitmapFactory.decodeStream(imageStream);
        List<Point3D> pointCloud = getPoints(near, far, image);
        //TODO: Add stuff for Context here? I really don't understand how this works

    }

    /**
     * Creates 3D points based on the depth data.
     * @param far - the "far" value in GDepth:Far.
     * @param near the "near" value in GDepth:Near.
     * @param image - the Bitmap object holding the depth map itself.
     * @return The list consisting of the "point cloud" created from the depth map.
     */
    public static List<Point3D> getPoints(double near, double far, Bitmap image) {
        //we have the bitmap so let's first get height and width
        List<Point3D> returnPoints = new ArrayList<Point3D>();
        int height = image.getHeight();
        int width = image.getWidth();
        //now make the points.
        int counter = 1; //counts the vectorIDs. Replace this with a static int in point3D.
        double minZ = Float.POSITIVE_INFINITY; // find the minimal as we go along
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double dn = image.getPixel(x,y) & 0xff; //this PROBABLY works. First debug to check what these values look like?
                dn = dn / 255.;
                double z = (far * near) / (far - dn * (far - near)); //see the Android depth map algorithm link
                //now make the point
                if (z < minZ) minZ = z;
                Point3D newPoint = new Point3D(x,y,z,counter);
                returnPoints.add(newPoint);
                counter++;
            }
        }
        //now do it again, but for the "base" layer
        //Commented out since Miriam does this in ObjWriter!
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                Point3D newPoint = new Point3D(x,y,minZ-0.05,counter);
//                returnPoints.add(newPoint);
//                counter++;
//            }
//        }
        //Now we've got the points, so just return them.
        return returnPoints;
    }
}

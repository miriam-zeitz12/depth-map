package edu.ncf.miriam_zeitz12.depthmap;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import com.adobe.xmp.impl.Base64;

import android.os.Environment;
//import android.util.Base64;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds the 3D mesh and writes a .OBJ file.
 * Created by Vinushka on 5/4/2015.
 */
public class MeshCreator {

    /**
     * Offset used to generate the "base" of the object.
     */
    private static final double OFFSET  = 0.05;
    /**
     * The depth map image to be processed.
     */
    private Bitmap storeImg;
    private double near;
    private double far;
    /**
     * Creates the 3D mesh from the depth map at inFilePath, and writes an .OBJ
     * file at outFilePath.
     * @param inFilePath - the path to the depth map as a Uri
     * @throws IOException - if there's issues reading the depth map
     */
    public MeshCreator(Uri inFilePath, Context context) throws IOException{
//        //first get the data
//        ContentResolver resolver = context.getContentResolver();
//        DataExtractor extract = new DataExtractor(resolver.openInputStream(inFilePath));
//        byte[] data = extract.getDepthData();
//        if (data == null) {
//            throw new IllegalArgumentException("Did not provide an image with depth map information.");
//        }
//        near = extract.getNear();
//        far = extract.getFar();
//        Log.d("Found data",Double.toString(near)+" "+Double.toString(far));
//        Log.d("Base64:",new String(data));
//        //now make a Bitmap out of it, read stream so we don't have to
//        //care about indexing directly
//        Log.d("Length of data:",Integer.toString(data.length));
//        Log.d("First byte in data:",Byte.toString(data[0]));
//        Log.d("Last byte in data:",Byte.toString(data[data.length-1]));
//        String fullFileName = "out.png";
//        String textFileName = "OUT-TEMP.TXT";
//        File outFile = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_DOCUMENTS), fullFileName);
//        String outPath = outFile.getAbsolutePath();
//        byte[] imgData = Base64.decode(data);
//        //ByteArrayInputStream in = new ByteArrayInputStream(imgData);
//        //FileOutputStream out = new FileOutputStream(outPath);
//        //IOUtils.copy(in,out);
//        //FlushedInputStream is = new FlushedInputStream(new ByteArrayInputStream(imgData));
//        //FileInputStream newFile = new FileInputStream(outFile);
////        DataExtractor.copy(is, out,1024);
////        out.close();
//        FileInputStream in = new FileInputStream(outPath);
//        //in.reset();
//        storeImg = BitmapFactory.decodeStream(in);
//        Log.d("Image is null:", Boolean.toString(storeImg == null));
    }

    /**
     * Returns the width of the image to be processed.
     * @return The width of the image to be processed.
     */
    public int getWidth() {
        return storeImg.getWidth();
    }

    /**
     * Returns the height of the image to be processed.
     * @return The height of the image to be processed.
     */
    public int getHeight() {
        return storeImg.getHeight();
    }

    /**
     * Creates 3D points based on the depth data.
     * @param far - the "far" value in GDepth:Far.
     * @param near the "near" value in GDepth:Near.
     * @param image - the Bitmap object holding the depth map itself.
     * @return The list consisting of the "point cloud" created from the depth map.
     */
    public List<Point3D> getPoints() {
        //we have the bitmap so let's first get height and width
        List<Point3D> returnPoints = new ArrayList<Point3D>();
        int height = storeImg.getHeight();
        int width = storeImg.getWidth();
        //now make the points.
        int counter = 1; //counts the vectorIDs. Replace this with a static int in point3D.
        double minZ = Float.POSITIVE_INFINITY; // find the minimal as we go along
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double dn = storeImg.getPixel(x,y) & 0xff; //this PROBABLY works. First debug to check what these values look like?
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
//                Point3D newPoint = new Point3D(x,y,minZ-OFFSET,counter);
//                returnPoints.add(newPoint);
//                counter++;
//            }
//        }
        //Now we've got the points, so just return them.
        return returnPoints;
    }
}
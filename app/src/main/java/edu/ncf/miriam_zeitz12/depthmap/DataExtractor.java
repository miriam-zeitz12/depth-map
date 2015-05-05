package edu.ncf.miriam_zeitz12.depthmap;

import java.io.*;
import java.util.*;

import android.util.Base64;
//import com.adobe.xmp.impl.Base64; //USED FOR TESTING IN JVM

/**
 * Extracts data from depth maps. Ideally this would just be static methods,
 * but we also need to store near and far values as opposed to just the depth
 * information, and thanks to the "magic" of static typing, we can't exactly
 * do that.
 * @author Vinushka
 *
 */
public class DataExtractor
{
    // An encoding should really be specified here, and for other uses of getBytes!
    private static final byte[] OPEN_ARR = "<x:xmpmeta".getBytes();
    private static final byte[] CLOSE_ARR = "</x:xmpmeta>".getBytes();
    private static final byte[] OPEN_DATA = "GDepth:Data=\"".getBytes();
    private static final byte[] CLOSE_DATA = "\"/>".getBytes();
    private static final byte[] OPEN_NEAR = "GDepth:Near=\"".getBytes();
    private static final byte[] OPEN_FAR = "GDepth:Far=\"".getBytes();
    private static final byte[] CLOSE_DEPTH_RANGE = "\"".getBytes();
    /**
     * "Near" value, the nearest point in the depth map.
     */
    private double near;
    /**
     * "Far" value, the farthest point in the depth map.
     */
    private double far;
    /**
     * The file we will extract the depth map from.
     */
    private InputStream inputFile;
    /**
     * Holds the depth data.
     */
    String depthData;

    /**
     * Creates a DataExtractor. Creating the object,
     * also extracts the data.
     * @param in - InputStream of the depth map.
     * @throws IOException - If there's issues reading the file
     */
    public DataExtractor(InputStream in) throws IOException {
        inputFile = in;
        depthData = findDepthData();
    }

    /**
     * The "near" value in GDepth:Near. Used to determine minimum depth.
     * @return - the "near" value in depth units.
     */
    public double getNear(){
        return near;
    }

    /**
     * The far value in GDepth:far. Used to determine the maximum depth.
     * @return - The "far" value in depth units.
     */
    public double getFar(){
        return far;
    }

    /**
     * Actually gets the depth data.
     * @return - the depth data as a Base64-encoded String.
     */
    public String getDepthData(){
        return depthData;
    }

    private static void copy(InputStream in, OutputStream out, int bufferSize) throws IOException
    {
        byte[] buf = new byte[bufferSize];
        int bytesRead = in.read(buf);
        while(bytesRead != -1)
        {
            out.write(buf, 0, bytesRead);
            bytesRead = in.read(buf);
        }
        in.close();
        out.flush();
    }

    private static int indexOf(byte[] arr, byte[] sub, int start)
    {
        int subIdx = 0;

        for(int x = start;x < arr.length;x++)
        {
            if(arr[x] == sub[subIdx])
            {
                if(subIdx == sub.length - 1)
                {
                    return x - subIdx;
                }
                subIdx++;
            }
            else
            {
                subIdx = 0;
            }
        }

        return -1;
    }

    private static String fixString(String str)
    {
        int idx = 0;
        StringBuilder buf = new StringBuilder(str);
        while((idx = buf.indexOf("http")) >= 0)
        {
            buf.delete(idx - 4, idx + 75);
        }

        return buf.toString();
    }

    private String findDepthData() throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        copy(inputFile, out,1024);


        byte[] fileData = out.toByteArray();

        //get the Near and Far data first, since there's only one of them in the file
        int nearStart = indexOf(fileData, OPEN_NEAR, 0) + OPEN_NEAR.length;
        int farStart = indexOf(fileData, OPEN_FAR, 0) + OPEN_FAR.length;
        int nearEnd = indexOf(fileData, CLOSE_DEPTH_RANGE, nearStart);
        int farEnd = indexOf(fileData, CLOSE_DEPTH_RANGE, farStart);
        //now get near and far for it
        Double nearVal = Double.parseDouble(new String(Arrays.copyOfRange(fileData, nearStart, nearEnd)));
        Double farVal = Double.parseDouble(new String(Arrays.copyOfRange(fileData, farStart, farEnd)));
        near = nearVal;
        far = farVal;

        int openIdx = indexOf(fileData, OPEN_ARR, 0);

        while(openIdx >= 0)
        {
            int closeIdx = indexOf(fileData, CLOSE_ARR, openIdx + 1) + CLOSE_ARR.length;

            byte[] segArr = Arrays.copyOfRange(fileData, openIdx, closeIdx);
            //instead of relying on the slow XMP parser, parse the bytes ourselves.
            int dataStart = indexOf(segArr,OPEN_DATA,0);
            int dataEnd = indexOf(segArr,CLOSE_DATA,dataStart+1);
            String test = "";
            if (dataStart != -1) {
                test = new String(Arrays.copyOfRange(segArr, dataStart + 13, dataEnd));
                return fixString(test);
            }
            openIdx = indexOf(fileData, OPEN_ARR, closeIdx + 1);
        }

        return null;
    }

    public static void main(String[] args) throws Exception
    {
        DataExtractor extract = new DataExtractor(new FileInputStream(new File("IMG_20150116_143419.jpg")));
        System.out.println(extract.getNear());
        System.out.println(extract.getFar());
        String data = extract.getDepthData();
        if(data != null)
        {
            byte[] imgData = Base64.decode(data.getBytes(),Base64.DEFAULT);
            ByteArrayInputStream in = new ByteArrayInputStream(imgData);
            FileOutputStream out = new FileOutputStream(new File("out.png"));
            copy(in, out,1024);
        }
    }
}
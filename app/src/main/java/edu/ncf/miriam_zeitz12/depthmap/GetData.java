package edu.ncf.miriam_zeitz12.depthmap;

import java.io.*;
import java.util.*;
import android.util.Base64;

public class GetData
{
    // An encoding should really be specified here, and for other uses of getBytes!
    private static final byte[] OPEN_ARR = "<x:xmpmeta".getBytes();
    private static final byte[] CLOSE_ARR = "</x:xmpmeta>".getBytes();
    private static final byte[] OPEN_DATA = "GDepth:Data=\"".getBytes();
    private static final byte[] CLOSE_DATA = "\"/>".getBytes();

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

    public static void copyMetadata(InputStream in, OutputStream out, int bufferSize) {
        
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

    private static String findDepthData(InputStream in) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        copy(in, out,1024);


        byte[] fileData = out.toByteArray();


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
        String data = findDepthData(new FileInputStream(new File("IMG_20150116_143419.jpg")));
        if(data != null)
        {
            byte[] imgData = Base64.decode(data.getBytes(), Base64.DEFAULT);
            ByteArrayInputStream in = new ByteArrayInputStream(imgData);
            FileOutputStream out = new FileOutputStream(new File("out.png"));
            copy(in, out,1024);
        }
    }
}
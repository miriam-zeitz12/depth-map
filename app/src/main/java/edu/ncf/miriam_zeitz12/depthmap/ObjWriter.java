package edu.ncf.miriam_zeitz12.depthmap;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by miriamzeitz on 5/4/15.
 */
public class ObjWriter {

    protected FileOutputStream outputStream;
    protected String fileName;
    private final Context baseContext;
    protected int vertexCount = 0;
    protected PrintWriter writer;
    protected final String commentTag = "# ";
    protected String fileHeader = "Default header";

    public ObjWriter(Context context, String file){
        fileName = file;
        baseContext = context;


    }

    public void setHeader(String header){
        fileHeader = header;
    }
    public String getHeader(){
        return fileHeader;
    }

    public int getVertexCount(){
        return vertexCount;
    }


    public void beginWrite(){

        try {
            outputStream = baseContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            writer = new PrintWriter(outputStream);
            writeHeader();

        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }

    }

    //not sure of the best way to ensure that this method is called
    public void endWrite(){
        try {
            writer.flush();
            writer.close();
            outputStream.flush();
            outputStream.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void writeHeader(){
        writer.print(commentTag);
        writer.println(fileHeader);
    }

    public void addVertex(Point3D vertex){
        String vertexString = "v " + vertex.getX() + " " + vertex.getY() + " " + vertex.getZ();
        writer.println(vertexString);
        vertexCount++;
    }

    public void addFace(int vertexA, int vertexB, int vertexC, int vertexD){
        writer.println("f " + vertexA + " " + vertexB + " " + vertexC + " " + vertexD);
    }


    public void addComment(String comment){
        writer.print(commentTag);
        writer.println(comment);
    }




}

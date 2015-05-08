package edu.ncf.miriam_zeitz12.depthmap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;


public class MeshActivity extends ActionBarActivity {

    private Uri imageUri;
    private static final String baseObjName = "_3d_model.obj";
    public static final String EMAIL_IMAGE_URI = "com.example.miriamzeitz.IMAGE_PROCESSED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        imageUri = Uri.parse(intent.getStringExtra(MainActivity.EXTRA_IMAGE_URI));
        setContentView(R.layout.activity_mesh);
        try {
            //first check if we can write the OBJ
            if (isExternalStorageWritable()) {
                //we take a random UUID to generate the file name we need
                String fullFileName  = UUID.randomUUID().toString() + baseObjName;
                File outFile = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOCUMENTS), fullFileName);
                String outPath = outFile.getAbsolutePath();
                MeshCreator creator = new MeshCreator(imageUri.toString());
                List<Point3D> pointCloud = creator.getPoints();
                PhotoObjWriter writer = new PhotoObjWriter(this,outPath);
                writer.writePhotoObj(creator.getWidth(), creator.getHeight(), pointCloud);
                //done, now email
                Intent emailIntent = new Intent(this,EmailFileActivity.class);
                emailIntent.putExtra(EMAIL_IMAGE_URI,fullFileName);
                startActivity(emailIntent);
            } else {
                displayNoAccessDialog();
            }
        } catch (IOException e) {
            //if we can't, then display this error message
            displayBadFileDialog();
        }
    }
    /**
     * Displays an alert dialog for when we can't get the file for some reason
     * or can't close the input stream for some reason.
     */
    private void displayBadFileDialog() {
        final Activity us = this;
        new AlertDialog.Builder(this)
                .setTitle("File not found")
                .setMessage("The image file you wished to process could not be found." +
                        " Please try to take your picture again")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(us, MainActivity.class);
                        startActivity(intent);
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    private void displayNoAccessDialog() {
        final Activity us = this;
        new AlertDialog.Builder(this)
                .setTitle("Can't write .OBJ")
                .setMessage("This application cannot write your mesh to your device's external"
                        + " storage. Please verify that your external storage is connected.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(us, MainActivity.class);
                        startActivity(intent);
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mesh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

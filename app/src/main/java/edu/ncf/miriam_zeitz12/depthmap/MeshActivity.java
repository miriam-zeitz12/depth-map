package edu.ncf.miriam_zeitz12.depthmap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class MeshActivity extends ActionBarActivity {

    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        imageUri = Uri.parse(intent.getStringExtra(MainActivity.EXTRA_IMAGE_URI));
        setContentView(R.layout.activity_mesh);
        try {
            //try to get the file
            //MeshCreator creator = new MeshCreator(imageUri.toString(),//other stuff)
            File file = new File(imageUri.toString());
            FileInputStream inputStream = new FileInputStream(file);
        } catch (IOException e) {
            //if we can't, then display this error message
            displayBadFileDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mesh, menu);
        return true;
    }

    /**
     * Displays an alert dialog for when we can't get the file for some reason
     * or can't close the input stream for some reason.
     */
     public void displayBadFileDialog() {
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

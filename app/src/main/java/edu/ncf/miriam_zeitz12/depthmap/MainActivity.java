package edu.ncf.miriam_zeitz12.depthmap;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {

    private static final int ACTIVITY_SELECT_IMAGE = 100;

    public static final String EXTRA_IMAGE_URI = "com.example.miriamzeitz.IMAGE_SELECTED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void getHelp(View view) {
        Intent intent = new Intent(this, HelpScreenActivity.class);
        startActivity(intent);
    }

    public void getPhotoFromFiles(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, ACTIVITY_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageChosenIntent) {
        super.onActivityResult(requestCode, resultCode, imageChosenIntent);

        if (requestCode == ACTIVITY_SELECT_IMAGE) {
            Uri chosenImage = imageChosenIntent.getData();
            startMeshActivity(chosenImage);
        }

    }

    public void startMeshActivity(Uri imageUri) {


         Intent intent = new Intent(this, MeshActivity.class);
        intent.putExtra(EXTRA_IMAGE_URI,imageUri.toString());

         startActivity(intent);

    }

    public void getPhotoFromCamera(View view) {

        
    }
}

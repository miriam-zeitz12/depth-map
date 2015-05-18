package edu.ncf.miriam_zeitz12.depthmap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class MeshActivity extends ActionBarActivity {

    private Uri imageUri;
    private static final String baseObjName = "_3d_model.obj";
    public static final String EMAIL_IMAGE_URI = "com.example.miriamzeitz.IMAGE_PROCESSED";
    public static final String REQUEST_PATH = "162.243.209.132:9999/create";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        imageUri = Uri.parse(intent.getStringExtra(MainActivity.EXTRA_IMAGE_URI));
        setContentView(R.layout.activity_mesh);
        Context context = this.getApplicationContext();
        try {
            //first check if we can write the OBJ
            if (isExternalStorageWritable()) {
                //we take a random UUID to generate the file name we need
                String fullFileName  = UUID.randomUUID().toString() + baseObjName;
                File outFile = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOCUMENTS), fullFileName);
                String fullPath = outFile.getAbsolutePath();
                ContentResolver resolver = context.getContentResolver();
                DataExtractor extractor = new DataExtractor((resolver.openInputStream(imageUri)));
                byte[] data = extractor.getDepthData();
                if (data == null) {
                    displayBadFileDialog();
                }
                double near = extractor.getNear();
                double far = extractor.getFar();
                Log.d("Found data",Double.toString(near)+" "+Double.toString(far));
                Log.d("Length of data:",Integer.toString(data.length));
                Log.d("First byte in data:",Byte.toString(data[0]));
                Log.d("Last byte in data:",Byte.toString(data[data.length-1]));
                //create HttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();

                //make a POST request object to our path
                HttpPost httpPost = new HttpPost(REQUEST_PATH);

                //make a Map for JSON to work
                Map<String, String> jsonSource = new HashMap<>();

                JSONObject jsonHolder = new JSONObject();
                jsonHolder.put("near",near);
                jsonHolder.put("far",far);
                jsonHolder.put("imageData",new String(data));
                Log.d("Starting POST","JSON Data is ready.");

                //pass this to a StringEntity, because abstractions
                StringEntity stringEntity = new StringEntity(jsonHolder.toString());

                httpPost.setEntity(stringEntity);

                //make sure we get the right headers so the server knows what to do with it
                httpPost.setHeader("Accept","application/json");
                httpPost.setHeader("Content-type","application/json");

                //and now execute it
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String response = httpClient.execute(httpPost,responseHandler);
                //this is the header so now let's write to the file...
                PrintWriter outputStream = new PrintWriter(new FileOutputStream(outFile));
                outputStream.write(response);
                outputStream.close();

                //and now email this thing
                Intent emailIntent = new Intent(this,EmailFileActivity.class);
                emailIntent.putExtra(EMAIL_IMAGE_URI,fullPath);
                startActivity(emailIntent);
            } else {
                displayNoAccessDialog();
            }
        } catch (IOException e) {
            //if we can't, then display this error message
            Log.wtf("arf", "no file?", e);
            displayBadFileDialog();
        } catch (JSONException f) {
            Log.wtf("No JSON?","JSONException thrown",f);
            displayBadJSONDialog();
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

    /**
     * Displays an alert dialog for when JSON fails.
     */
    private void displayBadJSONDialog() {
        final Activity us = this;
        new AlertDialog.Builder(this)
                .setTitle("File not found")
                .setMessage("The image file you wished to process could not be converted to Base64." +
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

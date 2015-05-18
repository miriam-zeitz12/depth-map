package edu.ncf.miriam_zeitz12.depthmap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import edu.ncf.miriam_zeitz12.depthmap.display3d.Display3dFragment;


public class MeshActivity extends ActionBarActivity {

    private String fullPath;
    private Uri imageUri;
    private static final String baseObjName = "_3d_model.obj";
    public static final String EMAIL_IMAGE_URI = "com.example.miriamzeitz.IMAGE_PROCESSED";
    public static final String REQUEST_PATH = "http://162.243.209.132:9999/create";

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

                //
                ContentResolver resolver = context.getContentResolver();
                DataExtractor extractor = new DataExtractor((resolver.openInputStream(imageUri)));
                byte[] data = extractor.getDepthData();
                if (data == null) {
                    displayBadFileDialog();
                }

                String near = Double.toString(extractor.getNear());
                String far = Double.toString(extractor.getFar());
                ImageDataMap imageDataMap = new ImageDataMap(near, far, data);
                Map<String, Map<String, String>> executeData = new HashMap<String, Map<String, String>>();
                executeData.put(REQUEST_PATH, imageDataMap.getDataMap());

                //make JSON using a JSONObject

//                JSONObject jsonHolder = new JSONObject();
//                jsonHolder.put("near",near);
//                jsonHolder.put("far",far);
//                jsonHolder.put("imageData",new String(data));
//                Log.d("Starting POST","JSON Data is ready.");

//                BufferedOutputStream writer = new BufferedOutputStream(conn.getOutputStream(),1024);
//                //can just write the raw string
//                byte[] toWrite = body.getBytes("UTF-8");
//                Log.d("Status code",Integer.toString(conn.getResponseCode()));
//                //IOUtils.copy(new ByteArrayInputStream(toWrite), writer);
//                writer.write(toWrite,0,toWrite.length);
//                writer.flush();

            DataTask dataTask = new DataTask();
                dataTask.execute(executeData);
            } else {
                displayNoAccessDialog();
            }
        } catch (IOException e) {
            //if we can't, then display this error message
            e.printStackTrace();
            //Log.wtf("arf", "no file?", e.printStackTrace());
            displayBadFileDialog();
        }
    }

    private void handleResponse(InputStream responseIn){
        String fullFileName  = UUID.randomUUID().toString() + baseObjName;
        File outFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), fullFileName);
         fullPath = outFile.getAbsolutePath();
        FileOutputStream testFileOut = null;
        try {
            testFileOut = new FileOutputStream(outFile);
            byte[] buf = new byte[1024];
            for (int nChunk = responseIn.read(buf); nChunk != -1; nChunk = responseIn.read(buf)) {
                testFileOut.write(buf,0,nChunk);
            }
            testFileOut.close();
            displayMesh();
            //and now email this thing

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void displayMesh(){
        getFragmentManager().beginTransaction()
                .replace(R.id.container, Display3dFragment.newInstance(fullPath))
                .addToBackStack(null)
                .commit();
    }

    public void emailFile(View view){
        Intent emailIntent = new Intent(this,EmailFileActivity.class);
        emailIntent.putExtra(EMAIL_IMAGE_URI,fullPath);
        startActivity(emailIntent);
    }

    private class DataTask extends AsyncTask<Map<String, Map<String,String>>, Void, String> {


        @Override
        protected String doInBackground(Map<String, Map<String, String>>... params) {

            for (Map<String, Map<String, String>> p:params){
                for (String url:p.keySet()){
                    postData(url, p.get(url));
                }

            }
            return "success";
        }

        public void postData(String url, Map<String, String> data) {

            JSONObject json = new JSONObject(data);
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> postData = new ArrayList<>();
            postData.add(new BasicNameValuePair("data[]", json.toString()));
            HttpClient client = HttpClientBuilder.create().build();
            try {
                StringEntity entity = new StringEntity(json.toString());


                httpPost.setEntity(entity);
                httpPost.setHeader("Content-Type", "application/json");
                httpPost.setHeader("charset", "utf-8");
                httpPost.setHeader("Content-Length", Integer.toString(json.toString().getBytes("UTF-8").length));
                HttpResponse response = client.execute(httpPost);
                if (response != null) {
                    InputStream in = new BufferedInputStream(response.getEntity().getContent()); //Get the data in the entity
                    handleResponse(in);
                }
                else {
                    displayNoAccessDialog();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
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

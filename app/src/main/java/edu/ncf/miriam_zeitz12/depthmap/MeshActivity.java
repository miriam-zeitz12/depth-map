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
    import android.widget.Toast;

    import com.adobe.xmp.XMPException;

    import org.apache.commons.io.IOUtils;
    import org.apache.http.HttpResponse;
    import org.apache.http.client.HttpClient;
    import org.apache.http.client.methods.HttpPost;
    import org.apache.http.entity.StringEntity;
    import org.apache.http.impl.client.DefaultHttpClient;
    import org.json.JSONObject;

    import java.io.BufferedInputStream;
    import java.io.BufferedOutputStream;
    import java.io.BufferedReader;
    import java.io.ByteArrayInputStream;
    import java.io.DataOutputStream;
    import java.io.File;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.io.InputStream;
    import java.io.OutputStream;
    import java.net.HttpURLConnection;
    import java.net.URL;
    import java.util.UUID;


    public class MeshActivity extends ActionBarActivity {

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
                //first check if we can write the OBJ
                if (isExternalStorageWritable()) {
                    StrictMode.ThreadPolicy policy =
                            new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    //we take a random UUID to generate the file name we need
                    String fullFileName  = UUID.randomUUID().toString() + baseObjName;
                    File outFile = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOCUMENTS), fullFileName);
                    String fullPath = outFile.getAbsolutePath();
                    String newFullPath = fullPath.replace("obj","txt");

//                    CharSequence download = "Your 3D model is being downloaded...";
//                    int duration = Toast.LENGTH_LONG;
//                    Toast downToast = Toast.makeText(context, download, duration);
//                    downToast.show();
                    new HttpTest().execute(this.getApplicationContext());

                } else {
                    displayNoAccessDialog();
                }

        }

        public class HttpTest extends AsyncTask<Context, Void, Void>
        {

            @Override
            protected Void doInBackground(Context... params)
            {
                BufferedReader inBuffer = null;
                try {
                    String fullFileName  = UUID.randomUUID().toString() + baseObjName;
                    File outFile = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOCUMENTS), fullFileName);
                    String fullPath = outFile.getAbsolutePath();
                    URL url = new URL(REQUEST_PATH);
                    Context context = params[0];
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //lets us read from the connection
                    conn.setDoInput(true);
                    //makes it POST

                    ContentResolver resolver = context.getContentResolver();
                    InputStream inImage = (resolver.openInputStream(imageUri));
                    conn.setDoOutput(true);
                    conn.setInstanceFollowRedirects(false);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type","application/json");
                    conn.setRequestProperty("charset", "utf-8");
                    //conn.setRequestProperty("Content-Length",Integer.toString(body.getBytes("UTF-8").length));
                    conn.setConnectTimeout(20000);
                    DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
                    IOUtils.copy(inImage, dataOutputStream);
                    //dataOutputStream.write(body.getBytes("UTF-8"));
                    dataOutputStream.flush();
                    dataOutputStream.close();

                    InputStream reader = new BufferedInputStream(conn.getInputStream());
                    FileOutputStream testFileOut = new FileOutputStream(outFile);
                    byte[] buf = new byte[1024];
                    for (int nChunk = reader.read(buf); nChunk != -1; nChunk = reader.read(buf)) {
                        testFileOut.write(buf,0,nChunk);
                    }
                    testFileOut.close();

                    //and now email this thing
                    Intent emailIntent = new Intent(context,EmailFileActivity.class);
                    emailIntent.putExtra(EMAIL_IMAGE_URI,fullPath);
                    startActivity(emailIntent);
                } catch(IOException e) {
                    // Do something about exceptions
                    //result = e.getMessage();
                    displayBadFileDialog();
                }
                return null;
            }

            protected Void onPostExecute(String page)
            {
                return null;
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
                    .setMessage("The image file you wished to process does not appear to have depth data" +
                            "we can process. Please try to upload a different image.")
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

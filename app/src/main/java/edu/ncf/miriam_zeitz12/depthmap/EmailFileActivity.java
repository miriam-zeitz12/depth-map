package edu.ncf.miriam_zeitz12.depthmap;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.protocol.HTTP;

import java.io.File;


public class EmailFileActivity extends ActionBarActivity {

    private Uri filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String f = intent.getStringExtra(MeshActivity.EMAIL_IMAGE_URI);
        File n = new File(f);
        filePath = Uri.fromFile(n);
        setContentView(R.layout.activity_email_file);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_email_file, menu);
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

    private boolean isValidEmail(String email) {
        // Email must contain an @ to separate the local and domain
        if (!(email.contains("@"))) return false;
        // Only one @ can be in an email address
        if (email.indexOf("@") != email.lastIndexOf("@")) return false;
        // An email cannot have two consecutive periods (..)
        for (int i = 0; i < email.length(); i++) {
            char c = email.charAt(i);
            if (c == '.') {
                // Cannot have a leading or trailing period
                if (i == 0 || i== email.length() - 1) return false;
                if (email.charAt(i + 1) == '.') return false;
            }
        }
        // This is probably nowhere near as complete a check of a valid email address
        // Just check out the wiki, the stuff with special characters is very complex
        // and gives a lot of leniency.  This covers the basics of common email addresses
        return true;
    }

    public void sendEmail(View view) {
        EditText editText = (EditText) findViewById(R.id.enter_email);
        String email = editText.getText().toString();
        // Trim leading and trailing whitespace
        email = email.trim();
        //Code taken from the android studio tutorials
        if (isValidEmail(email)) {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType(HTTP.PLAIN_TEXT_TYPE);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, email); // recipients
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your 3D Printable File");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello! \n\n A request has been made to send a "
            + "3D-printable file to this E-Mail address from an application on a mobile phone.  "
            + "Attached is the file requested. Enjoy!");
            emailIntent.putExtra(Intent.EXTRA_STREAM, filePath);

            startActivity(emailIntent);
        } else {
            Toast.makeText(this, "Not a valid email format try again.", Toast.LENGTH_LONG).show();
        }
    }
}

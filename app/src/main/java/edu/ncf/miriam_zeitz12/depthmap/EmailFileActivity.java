package edu.ncf.miriam_zeitz12.depthmap;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.apache.http.protocol.HTTP;


public class EmailFileActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void sendEmail(View view) {
        EditText editText = (EditText) findViewById(R.id.enter_email);
        String email = editText.getText().toString();
        //Code taken from the android studio tutorials
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType(HTTP.PLAIN_TEXT_TYPE);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, email); // recipients
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your 3D Printable File");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello! \n\n A request has been made to send a "
        + "3D-printable file to this E-Mail address from an application on a mobile phone.  "
        + "Attatched is the file requested. Enjoy!");
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://createdFiles/file"));

        startActivity(emailIntent);
    }
}

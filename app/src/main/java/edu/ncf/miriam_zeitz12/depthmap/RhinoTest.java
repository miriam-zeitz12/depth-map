package edu.ncf.miriam_zeitz12.depthmap;

import org.mozilla.javascript.*;
import android.app.Activity;
import android.os.Bundle;

/**
 * "Hello, World!", Rhino style.
 * Created by Vinushka on 5/2/2015.
 */
public class RhinoTest extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executeJS(
                "var widgets = Packages.android.widget;\n" +
                "var view = new widgets.TextView(TheActivity);\n"+
                "TheActivity.setContentView(view);\n" +
        );
    }
    public void executeJS(String code) {

    }
}

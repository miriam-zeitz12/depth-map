package edu.ncf.miriam_zeitz12.depthmap.display3d;

import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import org.rajawali3d.IRajawaliDisplay;
import org.rajawali3d.surface.IRajawaliSurface;
import org.rajawali3d.surface.IRajawaliSurfaceRenderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import edu.ncf.miriam_zeitz12.depthmap.R;

/**
 * Created by miriamzeitz on 5/17/15.
 */
public class Display3dFragment extends Fragment implements IRajawaliDisplay, DialogInterface.OnClickListener {

    private static final String DISPLAY_FILE = "displayFile";
    protected String displayFile;
    protected FrameLayout layout;

    protected IRajawaliSurfaceRenderer renderer;
    protected IRajawaliSurface displaySurface;

    protected ProgressBar progressBar;

    @Override
    public IRajawaliSurfaceRenderer createRenderer() {
        return new ObjRenderer(getActivity(), displayFile);
    }

    public static Fragment newInstance(String displayFile){
        Fragment fragment = new Display3dFragment();
        Bundle args = new Bundle();
        args.putString(DISPLAY_FILE, displayFile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        final  Bundle bundle = getArguments();
        if (bundle != null){
            displayFile = bundle.getString(DISPLAY_FILE);
        }
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        layout = (FrameLayout)inflater.inflate(getLayoutID(), container, false);
        layout.findViewById(getLayoutID()).bringToFront();
        displaySurface = (IRajawaliSurface)layout.findViewById(R.id.display_surface);
        progressBar = (ProgressBar)layout.findViewById(R.id.progress_bar_loader);
        progressBar.setVisibility(View.GONE);
        renderer = createRenderer();
        displaySurface.setSurfaceRenderer(renderer);
        return layout;

    }
    @Override
    public int getLayoutID() {
        return R.layout.fragment_display_3d;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }



}

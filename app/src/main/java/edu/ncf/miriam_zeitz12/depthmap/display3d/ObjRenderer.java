package edu.ncf.miriam_zeitz12.depthmap.display3d;

import android.content.Context;
import android.view.MotionEvent;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation3D;
import org.rajawali3d.animation.EllipticalOrbitAnimation3D;
import org.rajawali3d.animation.RotateOnAxisAnimation;
import org.rajawali3d.lights.PointLight;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.RajawaliRenderer;

/**
 * Created by miriamzeitz on 5/17/15.
 */
public class ObjRenderer extends RajawaliRenderer {

    private String meshFile;
    private PointLight pointLight;
    private Object3D meshObject;
    private Animation3D cameraAnim;
    private Animation3D lightAnim;
    private final int LIGHT_POWER = 3;
    private final int CAMERA_Z = 16;
    private final int ROTATE_DEGREES = 360;
    private final int ROTATE_DURATION = 8000;

    private final int LIGHT_DURATION = 3000;

    public ObjRenderer(Context context, String mesh){
        super(context);
        meshFile = mesh;
    }

    @Override
    protected void initScene() {
        pointLight = new PointLight();
        pointLight.setPosition(0, 0, 4);
        pointLight.setPower(LIGHT_POWER);

        getCurrentScene().addLight(pointLight);
        getCurrentCamera().setZ(CAMERA_Z);

        LoaderOBJ objParser = new LoaderOBJ(this, meshFile);
        try {
            objParser.parse();
            meshObject = objParser.getParsedObject();
            getCurrentScene().addChild(meshObject);

            cameraAnim = new RotateOnAxisAnimation(Vector3.Axis.Y, ROTATE_DEGREES);
            cameraAnim.setDurationMilliseconds(ROTATE_DURATION);
            cameraAnim.setTransformable3D(meshObject);
        } catch (ParsingException e) {
            e.printStackTrace();
        }

        lightAnim = new EllipticalOrbitAnimation3D(new Vector3(), new Vector3(0, 10, 0), Vector3.getAxisVector(Vector3.Axis.Z), 0, 360, EllipticalOrbitAnimation3D.OrbitDirection.CLOCKWISE);
        lightAnim.setDurationMilliseconds(LIGHT_DURATION);
        lightAnim.setTransformable3D(pointLight);

        getCurrentScene().registerAnimation(cameraAnim);
        getCurrentScene().registerAnimation(lightAnim);

        cameraAnim.play();
        lightAnim.play();
    }

    @Override
    public void onOffsetsChanged(float v, float v1, float v2, float v3, int i, int i1) {

    }

    @Override
    public void onTouchEvent(MotionEvent motionEvent) {

    }
}

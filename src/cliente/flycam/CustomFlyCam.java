/*
 * Clase para tunear la Flycam
 */
package cliente.flycam;

import com.jme3.input.FlyByCamera;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 *
 * @author cyberbask
 */
public class CustomFlyCam extends FlyByCamera{
    /**
     *
     * @param cam
     */
    public CustomFlyCam(Camera cam){
        super(cam);
    }
    
    /**
     *
     * @param value
     * @param axis
     */
    @Override
    protected void rotateCamera(float value, Vector3f axis){
        if (dragToRotate){
            if (canRotate){
//                value = -value;
            }else{
                return;
            }
        }

        Matrix3f mat = new Matrix3f();
        mat.fromAngleNormalAxis(rotationSpeed * value, axis);

        Vector3f up = cam.getUp();
        Vector3f left = cam.getLeft();
        Vector3f dir = cam.getDirection();

        mat.mult(up, up);
        mat.mult(left, left);
        mat.mult(dir, dir);
        
        if (up.getY() < 0) {
            up.setY(0f);
            return;
        }

        Quaternion q = new Quaternion();
        q.fromAxes(left, up, dir);
        q.normalizeLocal();

        cam.setAxes(q);
    }
}

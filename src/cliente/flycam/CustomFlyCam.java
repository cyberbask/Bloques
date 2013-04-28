/*
 * Clase para tunear la Flycam
 */
package cliente.flycam;

import com.jme3.input.FlyByCamera;
import com.jme3.input.Joystick;
import com.jme3.input.JoystickAxis;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import utiles.AppUtiles;

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
    
    private float deadZone = 0f;
    
    /**
     *
     * @param joystick
     */
    @Override
    protected void mapJoystick( Joystick joystick ) {
        if (joystick.getName().equals("Controller (Xbox 360 Wireless Receiver for Windows)")){
            //ejes
            // x - y -> id 1 - id 0 -> stick izquierdo
            // rx - ry ->  id 3 - id 2 -> stick derecho
            // z ->  id 4 -> Gatillos
            //pov_y - pov_x -> id 6 - id 7 -> DPAD
            
            setDeadZone(AppUtiles.JOY_XBOX360_DEADZONE_CAMARA);
            
            joystick.getAxis( "ry" ).assignAxis( "FLYCAM_JOY_Down", "FLYCAM_JOY_Up" );
            joystick.getAxis( "rx" ).assignAxis(  "FLYCAM_JOY_Right", "FLYCAM_JOY_Left" );
            
            inputManager.addListener(this, "FLYCAM_JOY_Down");
            inputManager.addListener(this, "FLYCAM_JOY_Up");
            inputManager.addListener(this, "FLYCAM_JOY_Right");
            inputManager.addListener(this, "FLYCAM_JOY_Left");
        }else{
            // Map it differently if there are Z axis
            if( joystick.getAxis( JoystickAxis.Z_ROTATION ) != null && joystick.getAxis( JoystickAxis.Z_AXIS ) != null ) {

                // Make the left stick move
                joystick.getXAxis().assignAxis( "FLYCAM_StrafeRight", "FLYCAM_StrafeLeft" );
                joystick.getYAxis().assignAxis( "FLYCAM_Backward", "FLYCAM_Forward" );

                // And the right stick control the camera                       
                joystick.getAxis( JoystickAxis.Z_ROTATION ).assignAxis( "FLYCAM_Down", "FLYCAM_Up" );
                joystick.getAxis( JoystickAxis.Z_AXIS ).assignAxis(  "FLYCAM_Right", "FLYCAM_Left" );

                // And let the dpad be up and down           
                joystick.getPovYAxis().assignAxis("FLYCAM_Rise", "FLYCAM_Lower");

                if( joystick.getButton( "Button 8" ) != null ) { 
                    // Let the stanard select button be the y invert toggle
                    joystick.getButton( "Button 8" ).assignButton( "FLYCAM_InvertY" );
                }                

            } else {             
                joystick.getPovXAxis().assignAxis("FLYCAM_StrafeRight", "FLYCAM_StrafeLeft");
                joystick.getPovYAxis().assignAxis("FLYCAM_Forward", "FLYCAM_Backward");
                joystick.getXAxis().assignAxis("FLYCAM_Right", "FLYCAM_Left");
                joystick.getYAxis().assignAxis("FLYCAM_Down", "FLYCAM_Up");
            }     
        }
    }
    
    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (!enabled)
            return;

        if (name.equals("FLYCAM_Left")){
            rotateCamera(value, initialUpVec);
        }else if (name.equals("FLYCAM_Right")){
            rotateCamera(-value, initialUpVec);
        }else if (name.equals("FLYCAM_Up")){
            rotateCamera(-value * (invertY ? -1 : 1), cam.getLeft());
        }else if (name.equals("FLYCAM_Down")){
            rotateCamera(value * (invertY ? -1 : 1), cam.getLeft());
        }
        
        else if (name.equals("FLYCAM_JOY_Left")){
            if (value > deadZone){
                rotateCamera(value / 2, initialUpVec);
            }
        }else if (name.equals("FLYCAM_JOY_Right")){
            if (value > deadZone){
                rotateCamera(-(value / 2), initialUpVec);
            }
        }else if (name.equals("FLYCAM_JOY_Up")){
            if (value > deadZone){
                rotateCamera(-(value / 4) * (invertY ? -1 : 1), cam.getLeft());
            }
        }else if (name.equals("FLYCAM_JOY_Down")){
            if (value > deadZone){
                rotateCamera((value / 4)* (invertY ? -1 : 1), cam.getLeft());
            }
        }
        
        else if (name.equals("FLYCAM_Forward")){
            moveCamera(value, false);
        }else if (name.equals("FLYCAM_Backward")){
            moveCamera(-value, false);
        }else if (name.equals("FLYCAM_StrafeLeft")){
            moveCamera(value, true);
        }else if (name.equals("FLYCAM_StrafeRight")){
            moveCamera(-value, true);
        }else if (name.equals("FLYCAM_Rise")){
            riseCamera(value);
        }else if (name.equals("FLYCAM_Lower")){
            riseCamera(-value);
        }else if (name.equals("FLYCAM_ZoomIn")){
            zoomCamera(value);
        }else if (name.equals("FLYCAM_ZoomOut")){
            zoomCamera(-value);
        }
    }
    
    /**
     *
     * @return
     */
    public float getDeadZone() {
        return deadZone;
    }

    /**
     *
     * @param deadZone
     */
    public void setDeadZone(float deadZone) {
        this.deadZone = deadZone;
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

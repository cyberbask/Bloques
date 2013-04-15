/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente.flycam;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.FlyByCamera;

/**
 *
 * @author cyberbask
 */
public class CustomFlyCamAppState extends AbstractAppState {

    private Application app;
    private FlyByCamera flyCam;

    /**
     *
     */
    public CustomFlyCamAppState() {
    }    

    /**
     *  This is called during initialize().
     * @param cam 
     */
    public void setCamera( FlyByCamera cam ) {
        this.flyCam = cam;
    }
    
    /**
     *
     * @return
     */
    public FlyByCamera getCamera() {
        return flyCam;
    }

    /**
     *
     * @param stateManager
     * @param app
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        this.app = app;

        if (app.getInputManager() != null) {
        
            if (flyCam == null) {
                flyCam = new FlyByCamera(app.getCamera());
            }
            
            flyCam.registerWithInput(app.getInputManager());            
        }               
    }
            
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        
        flyCam.setEnabled(enabled);
    }
    
    @Override
    public void cleanup() {
        super.cleanup();

        flyCam.unregisterInput();        
    }


}
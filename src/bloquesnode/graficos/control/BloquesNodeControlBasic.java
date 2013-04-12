/*
 * Clase con lo que se va a usar en todas las clases que la extienden
 */
package bloquesnode.graficos.control;

import bloquesnode.graficos.generabloque.BloquesNodeGeneraBloque;
import bloquesnode.manejo.generaterreno.BloquesNodeGeneraTerreno;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author mcarballo
 */
public class BloquesNodeControlBasic extends AbstractControl implements Savable, Cloneable{
    /**
     *
     */
    protected SimpleApplication app;
    /**
     *
     */
    protected Node              rootNode;
    /**
     *
     */
    protected AssetManager      assetManager;
    /**
     *
     */
    protected AppStateManager   stateManager;
    /**
     *
     */
    protected InputManager      inputManager;
    /**
     *
     */
    protected ViewPort          viewPort;
    /**
     *
     */
    protected BulletAppState    physics;
    /**
     *
     */
    protected Camera       cam;
        
    /**
     *
     */
    protected BloquesNodeGeneraTerreno bloqueGeneraTerreno;
    
    /**
     *
     */
    protected Node chunks = new Node("Chunks");
    
    BloquesNodeGeneraBloque bloques;
    
    
    /**
     *
     * @param app 
     */
    public BloquesNodeControlBasic(Application app){
        this.app = (SimpleApplication) app;
        this.rootNode     = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.viewPort     = this.app.getViewPort();
        this.physics      = this.stateManager.getState(BulletAppState.class);
        this.cam          = this.app.getCamera();
                
        bloques = new BloquesNodeGeneraBloque(this.app);
        
        bloqueGeneraTerreno = new BloquesNodeGeneraTerreno(this.app,bloques);
    }
    
    /**
     *
     * @param tpf
     */
    @Override
    protected void controlUpdate(float tpf) {
    }
    
    /**
     * Inicializador del Spatial
     * @param spatial
     */
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
    }    

    /**
     *
     * @param rm
     * @param vp
     */
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    /**
     *
     * @param spatial
     * @return
     */
    public Control cloneForSpatial(Spatial spatial) {
        final BloquesNodeControlBasic control = new BloquesNodeControlBasic(this.app);
   
        control.setSpatial(spatial);
        return control;
    }

    /**
     *
     * @param im
     * @throws IOException
     */
    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
    }

    /**
     *
     * @param ex
     * @throws IOException
     */
    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
    }
    
    /**
     *
     */
    public void destroy(){
        
    }
    
}

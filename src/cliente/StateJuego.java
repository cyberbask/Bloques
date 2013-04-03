/*
 * Application State para el juego principal
 */
package cliente;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import utiles.AppUtiles;

/**
 *
 * @author mcarballo
 */
public class StateJuego extends AbstractAppState implements ActionListener{
    private SimpleApplication app;
    private Node              rootNode;
    private AssetManager      assetManager;
    private AppStateManager   stateManager;
    private InputManager      inputManager;
    private ViewPort          viewPort;
    private BulletAppState    physics;
    
    //teclas
    private boolean left = false, right = false, up = false, down = false;
    private boolean vsync = true;
    
    //Graficos
    GraficosJuego graficos;
    
 
    /**
     *
     * @param stateManager
     * @param app
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); 
        
        this.app = (SimpleApplication) app;
        this.rootNode     = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.viewPort     = this.app.getViewPort();
        this.physics      = this.stateManager.getState(BulletAppState.class);
        
        setupKeys();
        
        StateJuegoGui juegoGui = new StateJuegoGui(app);
        juegoGui.initPuntoMira();
        
        //manejo de graficos
        graficos = new GraficosJuego(app);
        
        //esta linea impide que la ejecucion se pare aunque se pierda el foco
        app.setPauseOnLostFocus(false);
        
        //cambiamos el color del fondo
        viewPort.setBackgroundColor(new ColorRGBA(0.7f,0.8f,1f,1f));
    }
 
    @Override
    public void cleanup() {
        super.cleanup();
    }
 
    @Override
    public void setEnabled(boolean enabled) {
        // Pause and unpause
        super.setEnabled(enabled);
        
        if(enabled){
            setupKeys();
        } else {

        }
    }
 
    // Note that update is only called while the state is both attached and enabled.
    @Override
    public void update(float tpf) {
        graficos.generarTerreno();
    }
 
    
    /**
     * Configuracion de las Teclas basicas del juego
     */
    protected void setupKeys(){
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("VSync", new KeyTrigger(KeyInput.KEY_V));
        
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Jump");
        inputManager.addListener(this, "VSync");
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("Left")) {
            left = isPressed;
        } else if (name.equals("Right")) {
            right = isPressed;
        } else if (name.equals("Up")) {
            up = isPressed;
        } else if (name.equals("Down")) {
            down = isPressed;
        } else if (name.equals("VSync") && !isPressed) {
            vsync = !vsync;
            AppSettings Appsett = AppUtiles.getSettings(app);
            Appsett.put("VSync", vsync);
            this.app.getContext().restart();
        } else if (name.equals("Jump")) {
            //player.jump();
        }
    }
    
    /**
     *
     */
    public void destroy() {
        graficos.destroy();
    }
    
}

/*
 * Application State para el juego principal
 */
package cliente.juego;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import utiles.AppUtiles;

/**
 *
 * @author mcarballo
 */
public class JuegoState extends AbstractAppState implements ActionListener{
    private SimpleApplication app;
    private Node              rootNode;
    private AssetManager      assetManager;
    private AppStateManager   stateManager;
    private InputManager      inputManager;
    private ViewPort          viewPort;
    private BulletAppState    physics;
    
    //teclas
    private boolean vsync = true;
    private boolean setups = false;
    
    //Graficos
    /**
     *
     */
    protected JuegoGraficos graficos;
    
    private Boolean cerrandoState = false;
    
 
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
        
        //manejo de graficos
        graficos = new JuegoGraficos(app);
        
        //esta linea impide que la ejecucion se pare aunque se pierda el foco
        app.setPauseOnLostFocus(false);
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
            unSetupKeys();
        }
    }
 
    // Note that update is only called while the state is both attached and enabled.
    @Override
    public void update(float tpf) {
        graficos.update(tpf);
        
        //cuando el personaje esta en su sitio se activan las teclas
        if (graficos.posicionarCamara == 2){
            graficos.posicionarCamara = 3;
            setupKeys();
        }
        
        if (cerrandoState){
            Boolean cerrado = graficos.cerrarGraficos();
            
            if (cerrado){
                app.stop();
            }
        }
    }
 
    
    /**
     * Configuracion de las Teclas basicas del juego
     */
    protected void setupKeys(){
        inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);
        
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("VSync", new KeyTrigger(KeyInput.KEY_RBRACKET));
        inputManager.addMapping("SetUps", new KeyTrigger(KeyInput.KEY_N));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Correr", new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping("MouseLeftButton", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("MouseRightButton", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping("MouseCentralButton", new KeyTrigger(KeyInput.KEY_F), new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
        inputManager.addMapping("Salir", new KeyTrigger(KeyInput.KEY_ESCAPE));
        
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "VSync");
        inputManager.addListener(this, "SetUps");
        inputManager.addListener(this, "MouseLeftButton");
        inputManager.addListener(this, "MouseRightButton");
        inputManager.addListener(this, "MouseCentralButton");
        inputManager.addListener(this, "Correr");
        inputManager.addListener(this, "Salir");
        
        inputManager.addListener(analogListener, "Jump");
    }
    
    /**
     *
     */
    protected void unSetupKeys(){
        inputManager.deleteMapping("Left");
        inputManager.deleteMapping("Right");
        inputManager.deleteMapping("Up");
        inputManager.deleteMapping("Down");
        inputManager.deleteMapping("VSync");
        inputManager.deleteMapping("SetUps");
        inputManager.deleteMapping("Jump");
        inputManager.deleteMapping("Correr");
        inputManager.deleteMapping("MouseLeftButton");
        inputManager.deleteMapping("MouseRightButton");
        inputManager.deleteMapping("MouseCentralButton");
        inputManager.deleteMapping("Salir");
        
        app.getFlyByCamera().setEnabled(false);
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("Left")) {
            graficos.personaje.left = isPressed;
        } else if (name.equals("Right")) {
            graficos.personaje.right = isPressed;
        } else if (name.equals("Up")) {
            graficos.personaje.up = isPressed;
        } else if (name.equals("Down")) {
            graficos.personaje.down = isPressed;
        } else if (name.equals("VSync") && !isPressed) {
            vsync = !vsync;
            AppSettings Appsett = AppUtiles.getSettings(app);
            Appsett.put("VSync", vsync);
            this.app.getContext().restart();
        } else if (name.equals("SetUps") && !isPressed) {
            setups = !setups;
            if (setups){
                graficos.setUps();
            }else{
                graficos.unsetUps();
            }
        } else if (name.equals("Correr")) {
           graficos.personaje.correr = isPressed;
        } else if (name.equals("MouseLeftButton") && !isPressed) {
            graficos.accionBloque("destruir");
        } else if (name.equals("MouseRightButton") && !isPressed) {
            graficos.accionBloque("colocar");
        } else if (name.equals("MouseCentralButton") && !isPressed) {
            graficos.accionBloque("seleccionar");
        }else if (name.equals("Salir") && !isPressed) {
            cerrandoState = true;
            unSetupKeys();
        }
    }
    
    private AnalogListener analogListener = new AnalogListener() {
      public void onAnalog(String name, float value, float tpf) {
            if (name.equals("Jump")) {
                graficos.personaje.player.jump();
            }
        }
    };
    
    /**
     *
     */
    public void stop(){
        graficos.stop();
    }
    
    /**
     *
     */
    public void destroy() {
        graficos.destroy();
    }
    
}

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
import com.jme3.input.JoyInput;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.JoyAxisTrigger;
import com.jme3.input.controls.JoyButtonTrigger;
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
        
        inputManager.addMapping("JOY_Left", new JoyAxisTrigger(0, 1, true));
        inputManager.addMapping("JOY_Right",new JoyAxisTrigger(0, 1, false));
        inputManager.addMapping("JOY_Up", new JoyAxisTrigger(0, 0, true));
        inputManager.addMapping("JOY_Down", new JoyAxisTrigger(0, 0, false));
        
        inputManager.addMapping("VSync", new KeyTrigger(KeyInput.KEY_RBRACKET));
        inputManager.addMapping("SetUps", new KeyTrigger(KeyInput.KEY_N), new JoyButtonTrigger(0, 7)); //button start
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE), new JoyButtonTrigger(0, 0)); //Button A
        inputManager.addMapping("Correr", new KeyTrigger(KeyInput.KEY_LSHIFT),new JoyButtonTrigger(0, 2)); //Button X
        inputManager.addMapping("MouseLeftButton", new MouseButtonTrigger(MouseInput.BUTTON_LEFT), new JoyAxisTrigger(0, 4, true)); //gatillos
        inputManager.addMapping("MouseRightButton", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT), new JoyAxisTrigger(0, 4, false)); //gatillos
        inputManager.addMapping("MouseCentralButton", new KeyTrigger(KeyInput.KEY_F), new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE), new JoyButtonTrigger(0, 5)); //boton superior derecho;
        inputManager.addMapping("Salir", new KeyTrigger(KeyInput.KEY_ESCAPE));
        
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        
        inputManager.addListener(analogListener, "JOY_Left");
        inputManager.addListener(analogListener, "JOY_Right");
        inputManager.addListener(analogListener, "JOY_Up");
        inputManager.addListener(analogListener, "JOY_Down");
        
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
        
        inputManager.deleteMapping("JOY_Left");
        inputManager.deleteMapping("JOY_Right");
        inputManager.deleteMapping("JOY_Up");
        inputManager.deleteMapping("JOY_Down");
        
        inputManager.deleteMapping("VSync");
        inputManager.deleteMapping("SetUps");
        inputManager.deleteMapping("Jump");
        inputManager.deleteMapping("Correr");
        inputManager.deleteMapping("MouseLeftButton");
        inputManager.deleteMapping("MouseRightButton");
        inputManager.deleteMapping("MouseCentralButton");
        inputManager.deleteMapping("Salir");
        
        app.getFlyByCamera().setEnabled(false);
        
        graficos.personaje.left = false;
        graficos.personaje.right = false;
        graficos.personaje.up = false;
        graficos.personaje.down = false;
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
            
            else if (name.equals("JOY_Left")) {
                if (value > AppUtiles.JOY_XBOX360_DEADZONE_MOVIMIENTO){
                    graficos.personaje.left = true;
                }else{
                    graficos.personaje.left = false;
                }
            } else if (name.equals("JOY_Right")) {
                if (value > AppUtiles.JOY_XBOX360_DEADZONE_MOVIMIENTO){
                    graficos.personaje.right = true;
                }else{
                    graficos.personaje.right = false;
                }
            } else if (name.equals("JOY_Up")) {
                if (value > AppUtiles.JOY_XBOX360_DEADZONE_MOVIMIENTO){
                    graficos.personaje.up = true;
                }else{
                    graficos.personaje.up = false;
                }
            } else if (name.equals("JOY_Down")) {
                if (value > AppUtiles.JOY_XBOX360_DEADZONE_MOVIMIENTO){
                    graficos.personaje.down = true;
                }else{
                    graficos.personaje.down = false;
                }
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

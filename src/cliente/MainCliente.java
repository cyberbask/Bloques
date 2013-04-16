package cliente;

import bloques.manejo.utiles.BloquesUtiles;
import cliente.flycam.CustomFlyCam;
import cliente.flycam.CustomFlyCamAppState;
import cliente.juego.JuegoState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.renderer.RenderManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import utiles.AppUtiles;

/**
 * test
 * @author normenhansen
 */
public class MainCliente extends SimpleApplication {
    private JuegoState stateJuego;
    private BulletAppState bulletAppState;
            
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        MainCliente app = new MainCliente();
        
        //inicializamos las settings del app
        app = AppUtiles.initSettings(app);
        
        
        app.start();
    }
    

    /**
     *
     */
    @Override
    public void simpleInitApp() {     
        Logger.getLogger("").setLevel(Level.WARNING);
        
        //creamos las carpetas basicas del juego
        AppUtiles.creaCarpetasIniciales();
        
        //Fisicas
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        
        //capturas de pantalla
        ScreenshotAppState screenShotState = new ScreenshotAppState();
        screenShotState.setFilePath(AppUtiles.PATH_SCREENSHOTS);
        this.stateManager.attach(screenShotState);
        
        //Seteamos la "Applicacion State" principal del juego
        stateJuego = new JuegoState();
        stateManager.attach(stateJuego);
        
        //setamos la flycam
        stateManager.detach(stateManager.getState(FlyCamAppState.class));
        CustomFlyCamAppState stateFlyCam = new CustomFlyCamAppState();
        stateManager.attach(stateFlyCam);
        flyCam = new CustomFlyCam(cam);
        flyCam.setMoveSpeed(5f);             
        flyCam.setRotationSpeed(5f);
        stateManager.getState(CustomFlyCamAppState.class).setCamera( flyCam ); 

        //setemos la distancia de dibujado de la camara
        cam.setFrustumFar(BloquesUtiles.CAM_FRUSTUMFAR);
        //float aspect = (float)cam.getWidth() / (float)cam.getHeight();
        //cam.setFrustumPerspective( 60f, aspect, 0.1f, cam.getFrustumFar() );
        cam.update();
    }

    /**
     *
     * @param tpf
     */
    @Override
    public void simpleUpdate(float tpf) {
        
    }

    /**
     *
     * @param rm
     */
    @Override
    public void simpleRender(RenderManager rm) {
        
    }
    
    @Override
    public void destroy() {
        super.destroy();
        stateJuego.destroy(); //lo ejecutamos para cerrar cualquier posible hilo
    }
       
}

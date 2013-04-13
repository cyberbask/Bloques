package cliente;

import bloquesnode.manejo.utiles.BloquesNodeUtiles;
import cliente.juego.JuegoState;
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
        
        //Fisicas
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        
        //capturas de pantalla
        ScreenshotAppState screenShotState = new ScreenshotAppState();
        screenShotState.setFilePath("");
        this.stateManager.attach(screenShotState);
        
        //Seteamos la "Applicacion State" principal del juego
        stateJuego = new JuegoState();
        stateManager.attach(stateJuego);
        
        //setamos la velocidad estandar de la flycam
        flyCam.setMoveSpeed(5f);             
        flyCam.setRotationSpeed(5f);

        //setemoa la distancia de dibujado de la camara
        cam.setFrustumFar(BloquesNodeUtiles.CAM_FRUSTUMFAR);
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

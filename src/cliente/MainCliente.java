package cliente;

import bloques.manejo.utiles.BloquesUtiles;
import cliente.juego.JuegoState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.renderer.RenderManager;
import java.io.File;
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
        String ScreenshotsPath = System.getProperty("user.dir")+"/screenshots/";
        File ssp = new File(ScreenshotsPath);
        try{
            ssp.mkdir();
        }catch(Exception e){
            ScreenshotsPath = "";
        }
        screenShotState.setFilePath(ScreenshotsPath);
        this.stateManager.attach(screenShotState);
        
        //ruta para los savegames
        String savesPath = System.getProperty("user.dir")+"/saves/";
        File sp = new File(savesPath);
        try{
            sp.mkdir();
        }catch(Exception e){

        }
        
        //Seteamos la "Applicacion State" principal del juego
        stateJuego = new JuegoState();
        stateManager.attach(stateJuego);
        
        //setamos la velocidad estandar de la flycam
        flyCam.setMoveSpeed(5f);             
        flyCam.setRotationSpeed(5f);

        //setemoa la distancia de dibujado de la camara
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

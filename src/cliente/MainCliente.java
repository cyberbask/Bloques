package cliente;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import utiles.AppUtiles;

/**
 * test
 * @author normenhansen
 */
public class MainCliente extends SimpleApplication {
    StateJuego stateJuego;
            
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
        
        //Seteamos la "Applicacion State" principal del juego
        stateJuego = new StateJuego();
        stateManager.attach(stateJuego);
        
        //setamos la velocidad estandar de la flycam
        flyCam.setMoveSpeed(30f);       
    }

    /**
     *
     * @param tpf
     */
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    /**
     *
     * @param rm
     */
    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    @Override
    public void destroy() {
        super.destroy();
        stateJuego.destroy(); //lo ejecutamos para cerrar cualquier posible hilo
    }
       
}

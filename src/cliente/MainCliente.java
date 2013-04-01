package cliente;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import utiles.AppUtiles;

/**
 * test
 * @author normenhansen
 */
public class MainCliente extends SimpleApplication {

    public static void main(String[] args) {
        MainCliente app = new MainCliente();
        
        //inicializamos las settings del app
        app = AppUtiles.initSettings(app);
        
        app.start();
    }

    @Override
    public void simpleInitApp() {        
        //Seteamos la "Applicacion State" principal del juego
        StateJuego stateJuego = new StateJuego();
        stateManager.attach(stateJuego);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}

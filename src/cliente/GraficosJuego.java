/*
 * Clase para manejar los graficos del juego principal, generar chunks,
 * mostrarlos, quitarlos ...
 */
package cliente;

import bloques.BloqueGeneraTerreno;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author mcarballo
 */
public class GraficosJuego {
    private SimpleApplication app;
    private Node              rootNode;
    
    protected BloqueGeneraTerreno bloqueGeneraTerreno;
    
    public GraficosJuego(Application app){
        this.app = (SimpleApplication) app;
        this.rootNode     = this.app.getRootNode();
        
        bloqueGeneraTerreno = new BloqueGeneraTerreno(app);
    }
    
    
    public void generarTerreno(){
        Spatial devueltoGeneraTerreno = bloqueGeneraTerreno.generaTerreno();
        
        if (devueltoGeneraTerreno != null) {
            rootNode.attachChild(devueltoGeneraTerreno);
        }   
        
    
    }
    
    public void destroy() {
        bloqueGeneraTerreno.destroy(); //lo ejecutamos para cerrar los hilos que pueda haber abiertos
    }
}

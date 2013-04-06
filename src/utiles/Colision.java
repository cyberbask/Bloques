/*
 * Clase para calcular las colisiones e intersecciones
 */
package utiles;

import bloques.BloqueChunkUtiles;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

/**
 *
 * @author cyberbask
 */
public class Colision {
    private SimpleApplication app;
    private Node              rootNode;
    private AssetManager      assetManager;
    private Camera       cam;
    
    public Colision(Application app){
        this.app = (SimpleApplication) app;
        this.assetManager = this.app.getAssetManager();
        this.cam          = this.app.getCamera();
        this.rootNode     = this.app.getRootNode();
    }
    
    public int[] getCoordenadasColision(){
        // 1. Reset results list.
        CollisionResults results = new CollisionResults();
        
        // 2. Aim the ray from cam loc to cam direction.
        Ray ray = new Ray(cam.getLocation(), cam.getDirection());
        
        // 3. Collect intersections between Ray and Shootables in results list.
        rootNode.collideWith(ray, results);
        
        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            Vector3f contactPoint = closest.getContactPoint();
            
            //closest.getGeometry().getMaterial().getAdditionalRenderState().setWireframe(true);
            
            System.out.println("Detecta1: "+contactPoint.x+"-"+contactPoint.y+"-"+contactPoint.z);
            
            int[] redondeaCoordenadas = BloqueChunkUtiles.redondeaCoordenadasContacto(contactPoint);

            
            return redondeaCoordenadas;
        }
        
        return null;
    }
    
}

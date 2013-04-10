/*
 * Clase para calcular las colisiones e intersecciones
 */
package bloques.utiles;

import bloques.manejo.BloqueChunkDatos;
import bloques.manejo.BloqueChunkUtiles;
import bloques.manejo.BloqueChunks;
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
public class BloqueColision {
    private SimpleApplication app;
    private Node              rootNode;
    private AssetManager      assetManager;
    private Camera       cam;
    
    /**
     *
     */
    public int[] coorUltCol;
    /**
     *
     */
    public int[] coorUltColBloque;
    /**
     *
     */
    public int[] coorUltColBloqueVecino;
    
    /**
     *
     * @param app
     */
    public BloqueColision(Application app){
        this.app = (SimpleApplication) app;
        this.assetManager = this.app.getAssetManager();
        this.cam          = this.app.getCamera();
        this.rootNode     = this.app.getRootNode();
    }
    
    /**
     *
     * @param chunks
     */
    public void getCoordenadasColision(BloqueChunks chunks){
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
            
            //System.out.println("Detecta1: "+contactPoint.x+"-"+contactPoint.y+"-"+contactPoint.z);
            
            int ejeComprobar = BloqueChunkUtiles.averiguaCoordenadasContacto(contactPoint);
            
            Vector3f vecComprobar = contactPoint.clone();
            
            Boolean bloqueVecino1 = false;
            Boolean bloqueVecino2 = false;
            int[] redondeaCoordenadas1 = null;
            int[] redondeaCoordenadas2 = null;
            
            switch(ejeComprobar){
                case 1: //x
                    vecComprobar.x = vecComprobar.x + 0.5f;
                    redondeaCoordenadas1 = BloqueChunkUtiles.redondeaCoordenadasContacto(vecComprobar);
                    bloqueVecino1 = chunks.getBloqueVecino(redondeaCoordenadas1[0], redondeaCoordenadas1[1], redondeaCoordenadas1[2]);
                    vecComprobar.x = vecComprobar.x - 1f;
                    redondeaCoordenadas2 = BloqueChunkUtiles.redondeaCoordenadasContacto(vecComprobar);
                    bloqueVecino2 = chunks.getBloqueVecino(redondeaCoordenadas2[0], redondeaCoordenadas2[1], redondeaCoordenadas2[2]);
                break;
                case 2: //y
                    vecComprobar.y = vecComprobar.y + 0.5f;
                    redondeaCoordenadas1 = BloqueChunkUtiles.redondeaCoordenadasContacto(vecComprobar);
                    bloqueVecino1 = chunks.getBloqueVecino(redondeaCoordenadas1[0], redondeaCoordenadas1[1], redondeaCoordenadas1[2]);
                    vecComprobar.y = vecComprobar.y - 1f;
                    redondeaCoordenadas2 = BloqueChunkUtiles.redondeaCoordenadasContacto(vecComprobar);
                    bloqueVecino2 = chunks.getBloqueVecino(redondeaCoordenadas2[0], redondeaCoordenadas2[1], redondeaCoordenadas2[2]);
                break;
                case 3: //z
                    vecComprobar.z = vecComprobar.z + 0.5f;
                    redondeaCoordenadas1 = BloqueChunkUtiles.redondeaCoordenadasContacto(vecComprobar);
                    bloqueVecino1 = chunks.getBloqueVecino(redondeaCoordenadas1[0], redondeaCoordenadas1[1], redondeaCoordenadas1[2]);
                    vecComprobar.z = vecComprobar.z - 1f;
                    redondeaCoordenadas2 = BloqueChunkUtiles.redondeaCoordenadasContacto(vecComprobar);
                    bloqueVecino2 = chunks.getBloqueVecino(redondeaCoordenadas2[0], redondeaCoordenadas2[1], redondeaCoordenadas2[2]);
                break;
            }
            
            if (bloqueVecino1 && !bloqueVecino2 || bloqueVecino2 && !bloqueVecino1){
                if(bloqueVecino1){
                    coorUltCol = redondeaCoordenadas1;
                    coorUltColBloque = redondeaCoordenadas1;
                    coorUltColBloqueVecino = redondeaCoordenadas2;
                }else{
                    coorUltCol = redondeaCoordenadas2;
                    coorUltColBloque = redondeaCoordenadas2;
                    coorUltColBloqueVecino = redondeaCoordenadas1; 
                }                    
                //System.out.println("Detecta Final: "+coorUltCol[0]+"-"+coorUltCol[1]+"-"+coorUltCol[2]);
            }else{
                coorUltCol = null;
                coorUltColBloque = null;
                coorUltColBloqueVecino = null;   
                //System.out.println("Detecta null");
            }
        }else{
            coorUltCol = null;
            coorUltColBloque = null;
            coorUltColBloqueVecino = null;
        }
    }
    
    /**
     *
     * @param coordBloque
     * @param coordObjeto
     * @return
     */
    public static Boolean calculaColisionObjeto(int[] coordBloque, Vector3f coordObjeto){
        int[] dentroChunkBloque = BloqueChunkUtiles.calculaCoordenadasBloqueDentroDeChunk(coordBloque[0], coordBloque[1], coordBloque[2]);
        
        int[] dentroChunkPlayer = BloqueChunkUtiles.calculaCoordenadasBloqueDentroDeChunk((int) coordObjeto.x, (int)coordObjeto.y, (int)coordObjeto.z);
        
        if (dentroChunkBloque[0] == dentroChunkPlayer[0]
                &&dentroChunkBloque[1] == dentroChunkPlayer[1]
                &&dentroChunkBloque[2] == dentroChunkPlayer[2]){
            
            return true;
        }
        
        //comprobamos tambien que no este en el bloque superior que seria el de la camara en principio
        
        dentroChunkPlayer = BloqueChunkUtiles.calculaCoordenadasBloqueDentroDeChunk((int) coordObjeto.x, (int)coordObjeto.y+BloqueChunkUtiles.TAMANO_BLOQUE, (int)coordObjeto.z);
        
        if (dentroChunkBloque[0] == dentroChunkPlayer[0]
                &&dentroChunkBloque[1] == dentroChunkPlayer[1]
                &&dentroChunkBloque[2] == dentroChunkPlayer[2]){
            
            return true;
        }
        
        return false;
    }
    
    /**
     *
     * @param coordObjeto
     * @param chunks
     * @return
     */
    public static Boolean calculaDentroBloqueObjeto(Vector3f coordObjeto, BloqueChunks chunks){
        BloqueChunkDatos bloque = chunks.getBloque((int)coordObjeto.x, (int)coordObjeto.y, (int)coordObjeto.z);
        
        if (bloque != null){
            return true;
        }
        
        return false;
    }
    
}

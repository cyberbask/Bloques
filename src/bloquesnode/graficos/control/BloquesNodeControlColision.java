/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bloquesnode.graficos.control;

import bloquesnode.manejo.utiles.BloquesNodeUtiles;
import com.jme3.app.Application;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;

/**
 *
 * @author cyberbask
 */
public class BloquesNodeControlColision extends BloquesNodeControlUpdates {
    /**
     *
     */
    public Vector3f coorUltCol;
    /**
     *
     */
    public Vector3f coorUltColBloque;
    /**
     *
     */
    public Vector3f coorUltColBloqueVecino;

    /**
     *
     * @param app
     */
    public BloquesNodeControlColision(Application app){
        super(app);
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        super.controlUpdate(tpf);
        
        getCoordenadasColision();
    }
    
    /**
     *
     */
    public void getCoordenadasColision(){
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
            
            int ejeComprobar = BloquesNodeUtiles.averiguaCoordenadasContacto(contactPoint);
            
            Vector3f vecComprobar = contactPoint.clone();
            
            Boolean bloqueVecino1 = false;
            Boolean bloqueVecino2 = false;
            Vector3f coodFinales1 = null;
            Vector3f coodFinales2 = null;
            
            switch(ejeComprobar){
                case 1: //x
                    vecComprobar.x = vecComprobar.x + 0.5f;
                    coodFinales1 = vecComprobar.clone();
                    bloqueVecino1 = chunks.getBloqueVecino(coodFinales1);
                    vecComprobar.x = vecComprobar.x - 1f;
                    coodFinales2 = vecComprobar.clone();
                    bloqueVecino2 = chunks.getBloqueVecino(coodFinales2);
                break;
                case 2: //y
                    vecComprobar.y = vecComprobar.y + 0.5f;
                    coodFinales1 = vecComprobar.clone();
                    bloqueVecino1 = chunks.getBloqueVecino(coodFinales1);
                    vecComprobar.y = vecComprobar.y - 1f;
                    coodFinales2 = vecComprobar.clone();
                    bloqueVecino2 = chunks.getBloqueVecino(coodFinales2);
                break;
                case 3: //z
                    vecComprobar.z = vecComprobar.z + 0.5f;
                    coodFinales1 = vecComprobar.clone();
                    bloqueVecino1 = chunks.getBloqueVecino(coodFinales1);
                    vecComprobar.z = vecComprobar.z - 1f;
                    coodFinales2 = vecComprobar.clone();
                    bloqueVecino2 = chunks.getBloqueVecino(coodFinales2);
                break;
            }
            
            if (bloqueVecino1 && !bloqueVecino2 || bloqueVecino2 && !bloqueVecino1){
                if(bloqueVecino1){
                    coorUltCol = coodFinales1;
                    coorUltColBloque = coodFinales1;
                    coorUltColBloqueVecino = coodFinales2;
                }else{
                    coorUltCol = coodFinales2;
                    coorUltColBloque = coodFinales2;
                    coorUltColBloqueVecino = coodFinales1; 
                }                    
                //System.out.println("Detecta Final: "+coorUltCol.x+"-"+coorUltCol.y+"-"+coorUltCol.z);
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
    public static Boolean calculaColisionObjeto(Vector3f coordBloque, Vector3f coordObjeto){
        Vector3f dentroChunkBloque = BloquesNodeUtiles.redondeaCoordenadasContacto(coordBloque);
        Vector3f dentroChunkPlayer = BloquesNodeUtiles.redondeaCoordenadasContacto(coordObjeto);
        
        dentroChunkBloque = BloquesNodeUtiles.calculaCoordenadasBloque(dentroChunkBloque);
        dentroChunkPlayer = BloquesNodeUtiles.calculaCoordenadasBloque(dentroChunkPlayer);
        
        if (dentroChunkBloque.x == dentroChunkPlayer.x
                &&dentroChunkBloque.y == dentroChunkPlayer.y
                &&dentroChunkBloque.z == dentroChunkPlayer.z){
            
            return true;
        }
        
        //comprobamos tambien que no este en el bloque superior que seria el de la camara en principio
        Vector3f dentroChunkPlayerNuevo = dentroChunkPlayer.clone();
        dentroChunkPlayerNuevo.y = dentroChunkPlayerNuevo.y + BloquesNodeUtiles.TAMANO_BLOQUE;
        dentroChunkPlayer = BloquesNodeUtiles.redondeaCoordenadasContacto(dentroChunkPlayerNuevo);
        
        if (dentroChunkBloque.x == dentroChunkPlayerNuevo.x
                &&dentroChunkBloque.y == dentroChunkPlayerNuevo.y
                &&dentroChunkBloque.z == dentroChunkPlayerNuevo.z){
            
            return true;
        }
        
        return false;
    }
    
}

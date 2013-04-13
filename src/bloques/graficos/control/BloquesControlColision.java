/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bloques.graficos.control;

import bloques.manejo.chunks.BloquesChunkDatos;
import bloques.manejo.utiles.BloquesUtiles;
import com.jme3.app.Application;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;

/**
 *
 * @author cyberbask
 */
public class BloquesControlColision extends BloquesControlUpdates {
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
     */
    public Vector3f coorAntCol;

    /**
     *
     * @param app
     */
    public BloquesControlColision(Application app){
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
        
        Boolean ponerNull = false;
        
        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            Vector3f contactPoint = closest.getContactPoint();
            float distancia = closest.getDistance();
            
            //System.out.println("Detecta1: "+contactPoint.x+"-"+contactPoint.y+"-"+contactPoint.z);
            if (distancia <= (BloquesUtiles.TAMANO_BLOQUE * 6)){
            
                int ejeComprobar = BloquesUtiles.averiguaCoordenadasContacto(contactPoint);

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
                    
                    setBloqueWireframe();
                    
                }else{
                    ponerNull = true;
                }
            }else{
                ponerNull = true;
            }
        }else{
            ponerNull = true;
        }
        
        if (ponerNull){
            coorUltCol = null;
            coorUltColBloque = null;
            coorUltColBloqueVecino = null; 
            
            quitaBloqueWireframe();
        }
            
    }
    
    /**
     *
     * @param coordBloque
     * @param coordObjeto
     * @return
     */
    public static Boolean calculaColisionObjeto(Vector3f coordBloque, Vector3f coordObjeto){
        Vector3f dentroChunkBloque = BloquesUtiles.redondeaCoordenadasContacto(coordBloque);
        Vector3f dentroChunkPlayer = BloquesUtiles.redondeaCoordenadasContacto(coordObjeto);
        
        dentroChunkBloque = BloquesUtiles.calculaCoordenadasBloque(dentroChunkBloque);
        dentroChunkPlayer = BloquesUtiles.calculaCoordenadasBloque(dentroChunkPlayer);
        
        if (dentroChunkBloque.x == dentroChunkPlayer.x
                &&dentroChunkBloque.y == dentroChunkPlayer.y
                &&dentroChunkBloque.z == dentroChunkPlayer.z){
            
            return true;
        }
        
        //comprobamos tambien que no este en el bloque superior que seria el de la camara en principio
        Vector3f dentroChunkPlayerNuevo = dentroChunkPlayer.clone();
        dentroChunkPlayerNuevo.y = dentroChunkPlayerNuevo.y + BloquesUtiles.TAMANO_BLOQUE;
        
        if (dentroChunkBloque.x == dentroChunkPlayerNuevo.x
                &&dentroChunkBloque.y == dentroChunkPlayerNuevo.y
                &&dentroChunkBloque.z == dentroChunkPlayerNuevo.z){
            
            return true;
        }
        
        return false;
    }
    
    /**
     *
     * @param coordObjeto
     * @return
     */
    public Boolean calculaDentroBloqueObjeto(Vector3f coordObjeto){
        BloquesChunkDatos bloque = chunks.getBloque(coordObjeto);
        
        if (bloque != null){
            return true;
        }
        
        return false;
    }
    
    /**
     *
     */
    public void setBloqueWireframe(){
        Node terreno = spatial.getParent();
        
        if (coorUltCol != null){
            if (coorAntCol != null &&
                (coorUltCol.x == coorAntCol.x
                &&coorUltCol.y == coorAntCol.y
                &&coorUltCol.z == coorAntCol.z)){


            }else{
                quitaBloqueWireframe();
                        
                Node bloqueClonado = bloques.getBloqueGenerado("WireFrame");
                String nomBloque = BloquesUtiles.generarNombreBloque(coorUltCol);
                Vector3f coordenadas = BloquesUtiles.devuelveCoordenadasBloque(nomBloque);

                float correcion = BloquesUtiles.TAMANO_BLOQUE / 2;

                bloqueClonado.setLocalTranslation(coordenadas.x + correcion,coordenadas.y + correcion,coordenadas.z + correcion);

                terreno.attachChild(bloqueClonado);

                coorAntCol = coorUltCol.clone(); 
            }
        }
    }
    
    /**
     *
     */
    public void quitaBloqueWireframe(){
        Node terreno = spatial.getParent();
        
        Spatial bloqueAntiguo = terreno.getChild("WireFrame");
        if (bloqueAntiguo != null){
            terreno.detachChild(bloqueAntiguo);
        }
    }
    
}

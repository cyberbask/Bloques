/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bloques.graficos.control;

import bloques.manejo.chunks.BloquesChunkDatos;
import bloques.manejo.utiles.BloquesUtiles;
import com.jme3.app.Application;
import com.jme3.math.Vector3f;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author cyberbask
 */
public class BloquesControlAcciones extends BloquesControlColision{
    
    /**
     *
     * @param app
     */
    public BloquesControlAcciones(Application app){
        super(app);
    }
    
    /**
     *
     * @param nomBloque
     * @param posicionColision
     */
    public void nuevoBloque(String nomBloque, Vector3f posicionColision){
        Vector3f coordUsar;
        
        if (coorUltColBloqueVecino != null){
            coordUsar = coorUltColBloqueVecino;
            
            //tenemos que comprobar si la posicion de colision(normalmente el player) no esta en el mismo lugar que el bloque a colocar
            if (posicionColision != null && !calculaColisionObjeto(coordUsar, posicionColision)){
                BloquesChunkDatos bloqueDatos = new BloquesChunkDatos();
                bloqueDatos.setNomBloque(nomBloque);
                chunks.setBloque(coordUsar, bloqueDatos);
                
                refrescaBloque(coordUsar);
            }
        }
    }
    
    /**
     *
     * @param nomBloque
     * @param posicionColision
     */
    public void quitaBloque(String nomBloque, Vector3f posicionColision){
        Vector3f coordUsar;
        
        if (coorUltCol != null){
            coordUsar = coorUltCol;
            chunks.quitaBloque(coordUsar);
                
            refrescaBloque(coordUsar);
        }
    }
    
    /**
     *
     * @return
     */
    public String seleccionarBloque(){      
        if (coorUltCol != null){
            Vector3f coordUsar = coorUltCol;
            
            BloquesChunkDatos datosBloque = chunks.getBloque(coordUsar);
            if (datosBloque != null){
                String nomBloqueClonar = datosBloque.getNomBloque();
                if (datosBloque.getNomBloque() != null){
                    return nomBloqueClonar;
                }
            }
        }
        
        return null;
    }
    
    /**
     *
     * @param coordUsar
     */
    public void refrescaBloque(Vector3f coordUsar){
        Map<String,Integer> chunksAUpdatar=new HashMap<String,Integer>();      
        String nombreChunk;

        int[][] bloquesVecinos = chunks.setCarasVecinas(coordUsar,true);

        //recargamos el chunk donde esta el bloque de la colision
        nombreChunk = BloquesUtiles.generarNombreChunk(coordUsar);
        if (chunksAUpdatar.get(nombreChunk) == null){
            chunksAUpdatar.put(nombreChunk,1);
        }
        
        if (bloquesVecinos != null){
            for(int i=0;i<6;i++) {
                if (bloquesVecinos[i] != null){ //si hay bloque vecino
                    //calculamos sus caras
                    Vector3f coordVecino = new Vector3f(bloquesVecinos[i][0], bloquesVecinos[i][1], bloquesVecinos[i][2]);

                    chunks.setCarasVecinas(coordVecino,false);

                    nombreChunk = BloquesUtiles.generarNombreChunk(coordVecino);
                    if (chunksAUpdatar.get(nombreChunk) == null){
                        chunksAUpdatar.put(nombreChunk,1);
                    }  
                }
            }
        }
        
        for(String s : chunksAUpdatar.keySet()){
            updateChunkUrgentes.put(contadorUpdatesChunkUrgentes, s);
            contadorUpdatesChunkUrgentes++; 
        }
    }
}
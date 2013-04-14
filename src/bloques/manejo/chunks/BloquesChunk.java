/*
 * Manejo de un Chunk de Bloques
 */
package bloques.manejo.chunks;

import bloques.manejo.utiles.BloquesUtiles;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mcarballo
 */
public class BloquesChunk{
    /**
     * Contiene los datos del bloque segun coordenadas
     */
    private Map<String,BloquesChunkDatos> bloquesDatos = new HashMap<String, BloquesChunkDatos>();
    
    private String nombreChunk = null;

    private Node nodos = new Node();
    
    /**
     * Constructor
     */
    public BloquesChunk(){
    }
    
    /**
     *
     * @param indiceBloque
     * @param datosBloque
     */
    public void setBloque(String indiceBloque, BloquesChunkDatos datosBloque){
        bloquesDatos.put(indiceBloque, datosBloque);
    }
    
    /**
     *
     * @param indiceBloque
     * @return
     */
    public BloquesChunkDatos getBloque(String indiceBloque){
        return bloquesDatos.get(indiceBloque);
    }

    /**
     *
     * @param indiceBloque
     */
    public void quitaBloque(String indiceBloque){ 
        bloquesDatos.remove(indiceBloque);
    }
    
    /**
     *
     * @return
     */
    public Map<String,BloquesChunkDatos> getAllBloquesDatos(){
        return bloquesDatos;
    }
    
    /**
     *
     * @return
     */
    public String getNombreChunk() {
        return nombreChunk;
    }

    /**
     *
     * @param nombreChunk
     */
    public void setNombreChunk(String nombreChunk) {
        this.nombreChunk = nombreChunk;
        this.nodos.setName(nombreChunk);
    }
    
    /**
     *
     * @return
     */
    public Node getAllNodos(){
        return this.nodos;
    }
    
    /**
     *
     * @param coord
     * @param bloque
     */
    public void setNodo(Vector3f coord, Node bloque){
        String nombreNodo = BloquesUtiles.generarNombreBloque(coord);
        setNodo(nombreNodo, bloque);
    }
    
    /**
     *
     * @param nomNodo
     * @param bloque
     */
    public void setNodo(String nomNodo, Node bloque){
        Spatial nodo = getNodo(nomNodo);
        if (nodo != null){
            quitaNodo(nodo);
        }
        
        this.nodos.attachChild(bloque);
    }
    
    /**
     *
     * @param nomNodo
     * @return
     */
    public Spatial getNodo(String nomNodo){
        return this.nodos.getChild(nomNodo); 
    }
    
    /**
     *
     * @param coord
     */
    public void quitaNodo(Vector3f coord){
        String nombreBloque = BloquesUtiles.generarNombreBloque(coord);
        quitaNodo(nombreBloque);
    }
    
    /**
     *
     * @param nomNodo
     */
    public void quitaNodo(String nomNodo){
        Spatial nodo = getNodo(nomNodo);
        if (nodo != null){
            quitaNodo(nodo);
        }
    }
    
    /**
     *
     * @param nodo
     */
    public void quitaNodo(Spatial nodo){
        this.nodos.detachChild(nodo);
    }
    
}

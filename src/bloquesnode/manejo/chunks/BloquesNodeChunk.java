/*
 * Manejo de un Chunk de Bloques
 */
package bloquesnode.manejo.chunks;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mcarballo
 */
public class BloquesNodeChunk{
    /**
     * Contiene los datos del bloque segun coordenadas
     */
    Map<String,BloquesNodeChunkDatos> bloquesDatos = new HashMap<String, BloquesNodeChunkDatos>();
    
    String nombreChunk = null;

    /**
     * Constructor
     */
    public BloquesNodeChunk(){
    }
    
    /**
     *
     * @param indiceBloque
     * @param datosBloque
     */
    public void setBloque(String indiceBloque, BloquesNodeChunkDatos datosBloque){
        bloquesDatos.put(indiceBloque, datosBloque);
    }
    
    /**
     *
     * @param indiceBloque
     * @return
     */
    public BloquesNodeChunkDatos getBloque(String indiceBloque){
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
    public Map<String,BloquesNodeChunkDatos> getAllBloquesDatos(){
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
    }
    
}

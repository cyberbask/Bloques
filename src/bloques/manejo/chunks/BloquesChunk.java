/*
 * Manejo de un Chunk de Bloques
 */
package bloques.manejo.chunks;

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
    Map<String,BloquesChunkDatos> bloquesDatos = new HashMap<String, BloquesChunkDatos>();
    
    String nombreChunk = null;

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
    }
    
}

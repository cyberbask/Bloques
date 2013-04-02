/*
 * Clase para manejar un grupo de chunks
 */
package bloques;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author cyberbask
 */
public class BloqueChunks {
    Map<String,BloqueChunk> chunks = new HashMap<String, BloqueChunk>();
    
    public BloqueChunks(){
        
    }
    
    public void setChunk(int x, int y, int z, BloqueChunk chunk){
        chunks.put(BloqueChunkUtiles.generarNombreChunk(BloqueChunkUtiles.calculaCoordenadasChunk(x, y, z)), chunk);
    }
    
    public BloqueChunk getChunk(int x, int y, int z){
        return chunks.get(BloqueChunkUtiles.generarNombreChunk(BloqueChunkUtiles.calculaCoordenadasChunk(x, y, z)));
    }
    
    public void setBloque(int x, int y, int z, BloqueChunkDatos datos){
        BloqueChunk chunk = getChunk(x, y, z);
        
        //si es nuevo creamos un nuevo chunk y le asignamos el bloque
        if (chunk == null){ 
            chunk = new BloqueChunk(); 
        }
      
        chunk.setDatosBloque(BloqueChunkUtiles.calculaCoordenadasBloqueDentroDeChunk(x, y, z), datos);
            
        setChunk(x, y, z, chunk);
        
        
    }
    
    public BloqueChunks getGrupoChunks(int x, int z){
        int maxbucle = BloqueChunkUtiles.MAX_ALTURA_BLOQUES / BloqueChunkUtiles.TAMANO_CHUNK;
        
        BloqueChunks grupoChunks = new BloqueChunks();
        
        for (int i=0;i<maxbucle;i++){
            int y = i * BloqueChunkUtiles.TAMANO_CHUNK;
            
            BloqueChunk chunk = getChunk(x, y, z);
            if (chunk != null){
               grupoChunks.setChunk(x, y, z, chunk);
            }
        }

        return grupoChunks;
    }
    
    public Map<String, BloqueChunk> getAllChunks(){
        return chunks;
    }
}

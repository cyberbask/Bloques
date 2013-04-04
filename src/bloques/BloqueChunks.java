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
    
    /**
     *
     */
    public BloqueChunks(){
        
    }
    
    /**
     *
     * @param x
     * @param y
     * @param z
     * @param chunk
     */
    public void setChunk(int x, int y, int z, BloqueChunk chunk){
        chunks.put(BloqueChunkUtiles.generarNombreChunk(BloqueChunkUtiles.calculaCoordenadasChunk(x, y, z)), chunk);
    }
    
    /**
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public BloqueChunk getChunk(int x, int y, int z){
        return chunks.get(BloqueChunkUtiles.generarNombreChunk(BloqueChunkUtiles.calculaCoordenadasChunk(x, y, z)));
    }
    
    /**
     *
     * @param x
     * @param y
     * @param z
     * @param datos
     */
    public void setBloque(int x, int y, int z, BloqueChunkDatos datos){
        BloqueChunk chunk = getChunk(x, y, z);
        
        //si es nuevo creamos un nuevo chunk y le asignamos el bloque
        if (chunk == null){ 
            chunk = new BloqueChunk(); 
        }
      
        chunk.setDatosBloque(BloqueChunkUtiles.calculaCoordenadasBloqueDentroDeChunk(x, y, z), datos);
            
        setChunk(x, y, z, chunk);
        
        
    }
    
    /**
     *
     * @param x
     * @param y
     * @param z
     * @param chunk
     * @return
     */
    public BloqueChunkDatos getBloque(int x, int y, int z, BloqueChunk chunk){        
        if (chunk != null){ 
            return chunk.getDatosBloque(BloqueChunkUtiles.calculaCoordenadasBloqueDentroDeChunk(x, y, z));
        }
            
        return null;
    }
    
    /**
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public BloqueChunkDatos getBloquePorCoordenadas(int x, int y, int z){  
        BloqueChunk chunk = getChunk(x, y, z);
        return getBloque(x, y, z,chunk);
    }
    
    /**
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public BloqueChunkDatos getBloqueAPartirDeChunk(int x, int y, int z){
        BloqueChunk chunk = getChunk(x, y, z);
        
        if (chunk != null){ 
            return chunk.getDatosBloque(BloqueChunkUtiles.calculaCoordenadasBloqueDentroDeChunk(x, y, z));
        }
            
        return null;
    }
    
    /**
     *
     * @param x
     * @param z
     * @return
     */
    public BloqueChunks getGrupoChunks(int x, int z){
        int maxbucle = BloqueChunkUtiles.MAX_ALTURA_BLOQUES / BloqueChunkUtiles.TAMANO_CHUNK;
        
        BloqueChunks grupoChunks = new BloqueChunks();
        if (x >= 60){
            int casi = 1;
        }
        
        for (int i=0;i<maxbucle;i++){
            int y = i * BloqueChunkUtiles.TAMANO_CHUNK * BloqueChunkUtiles.TAMANO_BLOQUE;
            
            BloqueChunk chunk = getChunk(x, y, z);
            if (chunk != null){
               grupoChunks.setChunk(x, y, z, chunk);
            }
        }

        return grupoChunks;
    }
    
    /**
     *
     * @return
     */
    public Map<String, BloqueChunk> getAllChunks(){
        return chunks;
    }
    
    /**
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public int[] getBloquesVecinos(int x, int y, int z){
        int caras[] = new int[6];
        
        for(int i=0;i<6;i++) {
            caras[i] = 0;
        }
        
        BloqueChunkDatos bloque;
        BloqueChunk chunk;
        
        //tenemos que sacar los bloques alrededor al proporcionado
        //TODO  Por ahora si el chunk vecino no existe directamente no se muestra la cara
        //      con esto evitamos que salgan las caras de los bordes del mundo
        
        //frontal z+1
        chunk = getChunk(x, y, z + BloqueChunkUtiles.TAMANO_BLOQUE);
        if (chunk == null){
            caras[0] = 1;
        }else{
            bloque = getBloque(x, y, z + BloqueChunkUtiles.TAMANO_BLOQUE,chunk);
            if (bloque != null) {
                caras[0] = 1;
            }
        }
        
        //lateral derecha x+1
        chunk = getChunk(x + BloqueChunkUtiles.TAMANO_BLOQUE, y, z);
        if (chunk == null){
            caras[1] = 1;
        }else{
            bloque = getBloque(x + BloqueChunkUtiles.TAMANO_BLOQUE, y, z,chunk);
            if (bloque != null) {
                caras[1] = 1;
            }
        }
        
        //trasera z-1
        chunk = getChunk(x, y, z - BloqueChunkUtiles.TAMANO_BLOQUE);
        if (chunk == null){
            caras[2] = 1;
        }else{
            bloque = getBloque(x, y, z - BloqueChunkUtiles.TAMANO_BLOQUE,chunk);
            if (bloque != null) {
                caras[2] = 1;
            }
        }
        
        //lateral izquierda x-1
        chunk = getChunk(x - BloqueChunkUtiles.TAMANO_BLOQUE, y, z);
        if (chunk == null){
            caras[3] = 1;
        }else{
            bloque = getBloque(x - BloqueChunkUtiles.TAMANO_BLOQUE, y, z,chunk);
            if (bloque != null) {
                caras[3] = 1;
            }
        }
        
        //Superior y+1
        chunk = getChunk(x, y + BloqueChunkUtiles.TAMANO_BLOQUE, z);
        if (chunk == null){
            caras[4] = 1;
        }else{
            bloque = getBloque(x, y + BloqueChunkUtiles.TAMANO_BLOQUE, z,chunk);
            if (bloque != null) {
                caras[4] = 1;
            }
        }
        
        //inferior y-1
        chunk = getChunk(x, y - BloqueChunkUtiles.TAMANO_BLOQUE, z);
        if (chunk == null){
            caras[5] = 1;
        }else{
            bloque = getBloque(x, y - BloqueChunkUtiles.TAMANO_BLOQUE, z,chunk);
            if (bloque != null) {
                caras[5] = 1;
            }
        }
        
        return caras;
    }    
    
    /**
     *
     * @param x
     * @param z
     * @return
     */
    public int getBloqueConMasAltura(int x, int z){
        int devolverAltura = 0;
        
        int maxbucle = BloqueChunkUtiles.MAX_ALTURA_BLOQUES / BloqueChunkUtiles.TAMANO_CHUNK;
        
        for (int i=(maxbucle - 1);i>=0;i--){
            int a = i * BloqueChunkUtiles.TAMANO_CHUNK * BloqueChunkUtiles.TAMANO_BLOQUE;
            
            BloqueChunk chunk = getChunk(x, a, z);
            if (chunk != null){
               for(int y = (maxbucle - 1);y>=0;y--){
                    int b = y + a;
                    BloqueChunkDatos datosBloque = getBloque(x, y, z, chunk);
                            
                    if (datosBloque != null){
                        devolverAltura = b;
                        return devolverAltura;
                    }
                }
            }
        }
        
        return devolverAltura;
    }
            
}

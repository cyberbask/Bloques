/*
 * Clase para manejar un grupo de chunks
 */
package bloquesnode.manejo.chunks;

import bloquesnode.manejo.utiles.BloquesNodeUtiles;
import com.jme3.math.Vector3f;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author cyberbask
 */
public class BloquesNodeChunks{
    Map<String,BloquesNodeChunk> chunks = new HashMap<String, BloquesNodeChunk>();
    
    /**
     *
     */
    public BloquesNodeChunks(){
        
    }
    
    /**
     *
     * @param coord
     * @param chunk
     */
    public void setChunk(Vector3f coord, BloquesNodeChunk chunk){
        String nombreChunk = BloquesNodeUtiles.generarNombreChunk(coord);
        
        chunks.put(nombreChunk,chunk);
    }
    
    /**
     *
     * @param coord
     * @return
     */
    public BloquesNodeChunk getChunk(Vector3f coord){
        String nombreChunk = BloquesNodeUtiles.generarNombreChunk(coord);
        
        return getChunk(nombreChunk);
    }
    
    /**
     *
     * @param nombreChunk
     * @return
     */
    public BloquesNodeChunk getChunk(String nombreChunk){
        BloquesNodeChunk chunk = chunks.get(nombreChunk);
        
        if (chunk != null){
            return chunk;  
        }
        
        return null;
    }
    
    /**
     *
     * @param coord
     * @param bloqueDatos
     */
    public void setBloque(Vector3f coord, BloquesNodeChunkDatos bloqueDatos){
        String nombreChunk = BloquesNodeUtiles.generarNombreChunk(coord);
        
        BloquesNodeChunk chunk = getChunk(nombreChunk);
        
        if (chunk == null){
            chunk = new BloquesNodeChunk();
            chunk.setNombreChunk(nombreChunk);
            chunks.put(nombreChunk,chunk);
        }
        
        String nombreBloque = BloquesNodeUtiles.generarNombreBloque(nombreChunk,coord);
        
        chunk.setBloque(nombreBloque, bloqueDatos);
    }
    
    /**
     *
     * @param coord
     * @return
     */
    public BloquesNodeChunkDatos getBloque(Vector3f coord){
        String nombreBloque = BloquesNodeUtiles.generarNombreBloque(coord);
        
        return getChunk(coord).getBloque(nombreBloque);
    }
    
    /**
     *
     * @param coord
     */
    public void quitaBloque(Vector3f coord){
        String nombreBloque = BloquesNodeUtiles.generarNombreBloque(coord);
        
        getChunk(coord).quitaBloque(nombreBloque);
    }
    
    /**
     *
     * @return
     */
    public Map<String,BloquesNodeChunk> getAllChunks(){
        return chunks;
    }
    
    /**
     *
     * @param x
     * @param z
     * @return
     */
    public BloquesNodeChunks getGrupoChunks(int x, int z){
        int maxbucle = BloquesNodeUtiles.MAX_ALTURA_BLOQUES / BloquesNodeUtiles.TAMANO_CHUNK_Y;
        
        BloquesNodeChunks grupoChunks = new BloquesNodeChunks();
        
        for (int i=0;i<maxbucle;i++){
            int y = i * BloquesNodeUtiles.TAMANO_CHUNK_Y * BloquesNodeUtiles.TAMANO_BLOQUE;
            
            Vector3f coord = new Vector3f(x, y, z);
            
            BloquesNodeChunk chunk = getChunk(coord);
            if (chunk != null){
               grupoChunks.setChunk(coord, chunk);
            }
        }

        return grupoChunks;
    }
    
    /**
     *
     * @param x
     * @param z
     * @return
     */
    public String[] getGrupoChunksNombres(int x, int z){
        int maxbucle = BloquesNodeUtiles.MAX_ALTURA_BLOQUES / BloquesNodeUtiles.TAMANO_CHUNK_Y;
        
        String[] grupoChunks = new String[maxbucle];
        
        for (int i=0;i<maxbucle;i++){
            int y = i * BloquesNodeUtiles.TAMANO_CHUNK_Y * BloquesNodeUtiles.TAMANO_BLOQUE;
            
            Vector3f coord = new Vector3f(x, y, z);
                        
            grupoChunks[i] = BloquesNodeUtiles.generarNombreChunk(coord);

        }

        return grupoChunks;
    }
    
    /**
     *
     * @param coord
     * @return
     */
    public int[][] getBloquesVecinos(Vector3f coord){
        //la ultima posicion del array, la 3 nos dice si es de un chunk vecino
        int bloques[][] = new int[6][4]; 
        
        //inicializamos
        for(int i=0;i<6;i++) {
            bloques[i] = null;
        }
        
        BloquesNodeChunkDatos bloque;
        BloquesNodeChunk chunk;
        
        Vector3f vVecino;
        
        String nombreChunkOriginal = BloquesNodeUtiles.generarNombreChunk(coord);
        
        //tenemos que sacar los bloques alrededor al proporcionado
        for(int i=0;i<6;i++) {
            vVecino = coord.clone();
            switch(i){
                case 0:
                  //frontal z+1
                    vVecino.z = vVecino.z + BloquesNodeUtiles.TAMANO_BLOQUE;  
                break;
                case 1:
                    //lateral derecha x+1
                    vVecino.x = vVecino.x + BloquesNodeUtiles.TAMANO_BLOQUE;  
                break;
                case 2:
                    //trasera z-1
                    vVecino.z = vVecino.z - BloquesNodeUtiles.TAMANO_BLOQUE;  
                break;
                case 3:
                    //lateral izquierda x-1
                    vVecino.x = vVecino.x - BloquesNodeUtiles.TAMANO_BLOQUE;  
                break;
                case 4:
                    //Superior y+1
                    vVecino.y = vVecino.y + BloquesNodeUtiles.TAMANO_BLOQUE;
                break;
                case 5:
                    //inferior y-1
                    vVecino.y = vVecino.y - BloquesNodeUtiles.TAMANO_BLOQUE;  
                break;
            }
            
            chunk = getChunk(vVecino);
            
            if (chunk == null){
                //por ahora si no hay chunk vecino, se marca para que no se muestre la cara
                bloques[i] = new int[4];
                bloques[i][3] = 2; //sin chunk vecino
            }else{
                bloque = getBloque(vVecino);
                if (bloque != null) {
                    bloques[i] = new int[4];
                    bloques[i][0] = (int) vVecino.x;
                    bloques[i][1] = (int) vVecino.y;
                    bloques[i][2] = (int) vVecino.z;

                    String nombreChunkVecino = BloquesNodeUtiles.generarNombreChunk(vVecino);

                    if (nombreChunkVecino.equals(nombreChunkOriginal)){
                        bloques[i][3] = 0;  
                    }else{
                        bloques[i][3] = 1; //chunk vecino
                    }
                }
            }
        }
        
        return bloques;
    }
    
    /**
     *
     * @param bloques
     * @return
     */
    public int[] getCarasAPartirDeBloquesVecinos(int[][] bloques){
        int caras[] = new int[6];
        
        for(int i=0;i<6;i++) {
            caras[i] = 1;
        }
        
        for(int i=0;i<6;i++) {
            if (bloques[i] != null){ //si hay bloque
                caras[i] = 0;
            }else{
                caras[i] = 1;
            }
        }
        
        return caras;
    }
    
}

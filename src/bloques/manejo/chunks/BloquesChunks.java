/*
 * Clase para manejar un grupo de chunks
 */
package bloques.manejo.chunks;

import bloques.manejo.utiles.BloquesUtiles;
import com.jme3.math.Vector3f;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author cyberbask
 */
public class BloquesChunks{
    Map<String,BloquesChunk> chunks = new HashMap<String, BloquesChunk>();
    
    /**
     *
     */
    public BloquesChunks(){
        
    }
    
    /**
     *
     * @param coord
     * @param chunk
     */
    public void setChunk(Vector3f coord, BloquesChunk chunk){
        String nombreChunk = BloquesUtiles.generarNombreChunk(coord);
        
        chunks.put(nombreChunk,chunk);
    }
    
    /**
     *
     * @param coord
     */
    public void setChunkSiNoExiste(Vector3f coord){
        String nombreChunk = BloquesUtiles.generarNombreChunk(coord);
        
        BloquesChunk chunk = getChunk(nombreChunk);
        
        if (chunk == null){
            chunk = new BloquesChunk();
            chunk.setNombreChunk(nombreChunk);
            chunks.put(nombreChunk,chunk);
        }
    }
    
    /**
     *
     * @param coord
     * @return
     */
    public BloquesChunk getChunk(Vector3f coord){
        String nombreChunk = BloquesUtiles.generarNombreChunk(coord);
        
        return getChunk(nombreChunk);
    }
    
    /**
     *
     * @param nombreChunk
     * @return
     */
    public BloquesChunk getChunk(String nombreChunk){
        BloquesChunk chunk = chunks.get(nombreChunk);
        
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
    public void setBloque(Vector3f coord, BloquesChunkDatos bloqueDatos){
        String nombreChunk = BloquesUtiles.generarNombreChunk(coord);
        
        BloquesChunk chunk = getChunk(nombreChunk);
        
        if (chunk == null){
            chunk = new BloquesChunk();
            chunk.setNombreChunk(nombreChunk);
            chunks.put(nombreChunk,chunk);
        }
        
        String nombreBloque = BloquesUtiles.generarNombreBloque(nombreChunk,coord);
        
        chunk.setBloque(nombreBloque, bloqueDatos);
    }
    
    /**
     *
     * @param coord
     * @return
     */
    public BloquesChunkDatos getBloque(Vector3f coord){
        String nombreBloque = BloquesUtiles.generarNombreBloque(coord);
        
        BloquesChunk chunk = getChunk(coord);
        
        if (chunk != null){
            return chunk.getBloque(nombreBloque);
        }else{
            return null;
        }
    }
    
    /**
     *
     * @param coord
     */
    public void quitaBloque(Vector3f coord){
        String nombreBloque = BloquesUtiles.generarNombreBloque(coord);
        
        getChunk(coord).quitaBloque(nombreBloque);
    }
    
    /**
     *
     * @return
     */
    public Map<String,BloquesChunk> getAllChunks(){
        return chunks;
    }
    
    /**
     *
     * @param x
     * @param z
     * @return
     */
    public BloquesChunks getGrupoChunks(int x, int z){
        int maxbucle = BloquesUtiles.MAX_ALTURA_BLOQUES / BloquesUtiles.TAMANO_CHUNK_Y;
        
        BloquesChunks grupoChunks = new BloquesChunks();
        
        for (int i=0;i<maxbucle;i++){
            int y = i * BloquesUtiles.TAMANO_CHUNK_Y * BloquesUtiles.TAMANO_BLOQUE;
            
            Vector3f coord = new Vector3f(x, y, z);
            
            BloquesChunk chunk = getChunk(coord);
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
        int maxbucle = BloquesUtiles.MAX_ALTURA_BLOQUES / BloquesUtiles.TAMANO_CHUNK_Y;
        
        String[] grupoChunks = new String[maxbucle];
        
        for (int i=0;i<maxbucle;i++){
            int y = i * BloquesUtiles.TAMANO_CHUNK_Y * BloquesUtiles.TAMANO_BLOQUE;
            
            Vector3f coord = new Vector3f(x, y, z);
                        
            grupoChunks[i] = BloquesUtiles.generarNombreChunk(coord);

        }

        return grupoChunks;
    }
    
    /**
     *
     * @param x
     * @param z
     * @return
     */
    public int getBloqueConMasAltura(int x, int z){
        int maxbucle = BloquesUtiles.MAX_ALTURA_BLOQUES / BloquesUtiles.TAMANO_CHUNK_Y;
        
        for (int i=(maxbucle-1);i>=0;i--){
            int a = i * BloquesUtiles.TAMANO_CHUNK_Y * BloquesUtiles.TAMANO_BLOQUE;
            
            Vector3f coordChunk = new Vector3f(x, a, z);
            
            BloquesChunk chunk = getChunk(coordChunk);
            
            if (chunk != null){
               for(int y = 0;y < (BloquesUtiles.TAMANO_CHUNK_Y * BloquesUtiles.TAMANO_BLOQUE);y = y + BloquesUtiles.TAMANO_BLOQUE){
                   int b; 
                   if (a == 0){
                        b = (BloquesUtiles.TAMANO_CHUNK_Y * BloquesUtiles.TAMANO_BLOQUE) - y;
                    }else{
                        b = a - y;
                    }
                    
                    Vector3f coodBloque = new Vector3f(x, b, z);
                    
                    BloquesChunkDatos datosBloque = getBloque(coodBloque);
                            
                    if (datosBloque != null){
                        return b;
                    }
                }
            }
        }
        
        return BloquesUtiles.MAX_ALTURA_BLOQUES;
    }
    
    /**
     *
     * @param coord
     * @return
     */
    public Boolean getBloqueVecino(Vector3f coord){
        BloquesChunkDatos bloque = getBloque(coord);
        
        if (bloque != null){
            return true;
        }
        
        return false;
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
        
        BloquesChunkDatos bloque;
        BloquesChunk chunk;
        
        Vector3f vVecino;
        
        String nombreChunkOriginal = BloquesUtiles.generarNombreChunk(coord);
        
        //tenemos que sacar los bloques alrededor al proporcionado
        for(int i=0;i<6;i++) {
            vVecino = coord.clone();
            switch(i){
                case 0:
                  //frontal z+1
                    vVecino.z = vVecino.z + BloquesUtiles.TAMANO_BLOQUE;  
                break;
                case 1:
                    //lateral derecha x+1
                    vVecino.x = vVecino.x + BloquesUtiles.TAMANO_BLOQUE;  
                break;
                case 2:
                    //trasera z-1
                    vVecino.z = vVecino.z - BloquesUtiles.TAMANO_BLOQUE;  
                break;
                case 3:
                    //lateral izquierda x-1
                    vVecino.x = vVecino.x - BloquesUtiles.TAMANO_BLOQUE;  
                break;
                case 4:
                    //Superior y+1
                    vVecino.y = vVecino.y + BloquesUtiles.TAMANO_BLOQUE;
                break;
                case 5:
                    //inferior y-1
                    vVecino.y = vVecino.y - BloquesUtiles.TAMANO_BLOQUE;  
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

                    String nombreChunkVecino = BloquesUtiles.generarNombreChunk(vVecino);

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
    
    /**
     *
     * @param coord
     * @param withReturn
     * @return
     */
    public int[][] setCarasVecinas(Vector3f coord,Boolean withReturn){
        int[][] bloquesVecinosDelBloqueVecino = getBloquesVecinos(coord);
        int[] carasbloquesVecinos = getCarasAPartirDeBloquesVecinos(bloquesVecinosDelBloqueVecino);

        //guardamos sus caras
        BloquesChunkDatos datosBloque = getBloque(coord);
        if (datosBloque != null){
            datosBloque.setCaras(carasbloquesVecinos);
            datosBloque.setMostrar(true);
        }
        
        if (withReturn){
            return bloquesVecinosDelBloqueVecino;
        }

        return null;
    }
}

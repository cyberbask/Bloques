/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bloques;

/**
 *
 * @author cyberbask
 */
public class BloqueChunkUtiles {
    /**
     *
     */
    public static final int TAMANO_BLOQUE = 8;
    /**
     *
     */
    public static final int TAMANO_CHUNK = 16; //16x16x16
    /**
     *
     */
    public static final int TOTAL_BLOQUES = 4096; //16x16x16
    
    /**
     *
     */
    public static final int MAX_ALTURA_BLOQUES = 256;
    /**
     *
     */
    public static final int MIN_ALTURA_BLOQUES = 0; 
    
    
    
    /**
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static int[] calculaCoordenadasChunk(int x, int y, int z){
        int nuevasCoordenadas[] = new int[3];
        
        nuevasCoordenadas[0] = x / TAMANO_BLOQUE / TAMANO_CHUNK;
        if (x > -TAMANO_CHUNK && x < 0){
            nuevasCoordenadas[0] = nuevasCoordenadas[0] -1;
        
        }
        nuevasCoordenadas[1] = y / TAMANO_BLOQUE / TAMANO_CHUNK;
        if (y > -TAMANO_CHUNK && y < 0){
            nuevasCoordenadas[1] = nuevasCoordenadas[1] -1;
        
        }
        nuevasCoordenadas[2] = z / TAMANO_BLOQUE / TAMANO_CHUNK;
        if (z > -TAMANO_CHUNK && z < 0){
            nuevasCoordenadas[2] = nuevasCoordenadas[2] -1;
        }
        
        return nuevasCoordenadas;
    }
    
    /**
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static int[] calculaCoordenadasBloqueDentroDeChunk(int x, int y, int z){
        int nuevasCoordenadas[] = calculaCoordenadasChunk(x, y, z);
        
        int nuevasCoordenadasBloque[] = new int[3];
        
        nuevasCoordenadasBloque[0] = (x / TAMANO_BLOQUE) - (nuevasCoordenadas[0] * TAMANO_CHUNK);
        nuevasCoordenadasBloque[1] = (y / TAMANO_BLOQUE) - (nuevasCoordenadas[1] * TAMANO_CHUNK);
        nuevasCoordenadasBloque[2] = (z / TAMANO_BLOQUE) - (nuevasCoordenadas[2] * TAMANO_CHUNK);
        
        if (nuevasCoordenadasBloque[0] < 0) {
            nuevasCoordenadasBloque[0] = TAMANO_CHUNK + nuevasCoordenadasBloque[0];
        }
        if (nuevasCoordenadasBloque[1] < 0) {
            nuevasCoordenadasBloque[1] = TAMANO_CHUNK + nuevasCoordenadasBloque[1];
        }
        if (nuevasCoordenadasBloque[2] < 0) {
            nuevasCoordenadasBloque[2] = TAMANO_CHUNK + nuevasCoordenadasBloque[2];
        }
        
        return nuevasCoordenadasBloque;
    }
    
    /**
     *
     * @param chunkX
     * @param chunkY
     * @param chunkZ
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static int[] calculaCoordenadasBloqueAPartirDeChunk(int chunkX, int chunkY, int chunkZ, int x, int y, int z){
        int nuevasCoordenadas[] = new int[3];
 
        nuevasCoordenadas[0] = (chunkX * TAMANO_BLOQUE) * TAMANO_CHUNK;
        nuevasCoordenadas[1] = (chunkY * TAMANO_BLOQUE) * TAMANO_CHUNK;
        nuevasCoordenadas[2] = (chunkZ * TAMANO_BLOQUE) * TAMANO_CHUNK;
        
        if (nuevasCoordenadas[0] < 0){
            nuevasCoordenadas[0] = nuevasCoordenadas[0] + TAMANO_CHUNK;
        }
        if (nuevasCoordenadas[1] < 0){
            nuevasCoordenadas[1] = nuevasCoordenadas[1] + TAMANO_CHUNK;
        }
        if (nuevasCoordenadas[2] < 0){
            nuevasCoordenadas[2] = nuevasCoordenadas[2] + TAMANO_CHUNK;
        }
        
        int nuevasCoordenadasBloque[] = new int[3];
        
        nuevasCoordenadasBloque[0] = nuevasCoordenadas[0] + x;
        nuevasCoordenadasBloque[1] = nuevasCoordenadas[1] + y;
        nuevasCoordenadasBloque[2] = nuevasCoordenadas[2] + z;
        
        return nuevasCoordenadasBloque;
    }
    
    /**
     *
     * @param nomChunk
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static int[] calculaCoordenadasBloqueAPartirDeChunk(String nomChunk, int x, int y, int z){
        int nuevasCoordenadas[] = new int[3];
        
        String[] tokens = nomChunk.split("__");
 
        nuevasCoordenadas[0] = (Integer.valueOf(tokens[0]) * TAMANO_BLOQUE) * TAMANO_CHUNK;
        nuevasCoordenadas[1] = (Integer.valueOf(tokens[1]) * TAMANO_BLOQUE) * TAMANO_CHUNK;
        nuevasCoordenadas[2] = (Integer.valueOf(tokens[2]) * TAMANO_BLOQUE) * TAMANO_CHUNK;
        if (nuevasCoordenadas[0] < 0){
            nuevasCoordenadas[0] = nuevasCoordenadas[0] + TAMANO_CHUNK;
        }
        if (nuevasCoordenadas[1] < 0){
            nuevasCoordenadas[1] = nuevasCoordenadas[1] + TAMANO_CHUNK;
        }
        if (nuevasCoordenadas[2] < 0){
            nuevasCoordenadas[2] = nuevasCoordenadas[2] + TAMANO_CHUNK;
        }
        
        int nuevasCoordenadasBloque[] = new int[3];
        
        nuevasCoordenadasBloque[0] = nuevasCoordenadas[0] + x;
        nuevasCoordenadasBloque[1] = nuevasCoordenadas[1] + y;
        nuevasCoordenadasBloque[2] = nuevasCoordenadas[2] + z;
        
        return nuevasCoordenadasBloque;
    }
    
    /**
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static String generarNombreChunk(int x, int y, int z){
        return String.valueOf(x)+"__"+String.valueOf(y)+"__"+String.valueOf(z);
    }
    
    /**
     *
     * @param coordenadas
     * @return
     */
    public static String generarNombreChunk(int[] coordenadas){
        return String.valueOf(coordenadas[0])+"__"+String.valueOf(coordenadas[1])+"__"+String.valueOf(coordenadas[2]);
    }
}

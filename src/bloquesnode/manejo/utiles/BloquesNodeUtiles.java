/*
 * Clase para guardar constante, y funciones estaticas
 */
package bloquesnode.manejo.utiles;

import com.jme3.math.Vector3f;

/**
 *
 * @author mcarballo
 */
public class BloquesNodeUtiles {
     /**
     *
     */
    public static final int TAMANO_BLOQUE = 6;
    /**
     *
     */
    public static final int TAMANO_CHUNK_X = 16;
    /**
     *
     */
    public static final int TAMANO_CHUNK_Y = 64;
    /**
     *
     */
    public static final int TAMANO_CHUNK_Z = 16;
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
     */
    public static final int TAMANO_GENERA_TERRENO = 128; 
    
    
    /**
     *
     * @param coord
     * @return
     */
    public static Vector3f calculaCoordenadasChunk(Vector3f coord){
        return calculaCoordenadasChunk((int) coord.x, (int) coord.y, (int) coord.z);
    }
    
    /**
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static Vector3f calculaCoordenadasChunk(float x, float y, float z){
        return calculaCoordenadasChunk((int) x, (int) y, (int) z);
    }
    
    
    /**
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static Vector3f calculaCoordenadasChunk(int x, int y, int z){
        Vector3f nuevasCoordenadas = new Vector3f();
        
        nuevasCoordenadas.x = (int) (x / TAMANO_BLOQUE / TAMANO_CHUNK_X);
        if (x > - TAMANO_CHUNK_X && x < 0){
            nuevasCoordenadas.x = nuevasCoordenadas.x -1;
        
        }
        nuevasCoordenadas.y = (int) (y / TAMANO_BLOQUE / TAMANO_CHUNK_Y);
        if (y > - TAMANO_CHUNK_Y && y < 0){
            nuevasCoordenadas.y = nuevasCoordenadas.y -1;
        
        }
        nuevasCoordenadas.z = (int) (z / TAMANO_BLOQUE / TAMANO_CHUNK_Z);
        if (z > - TAMANO_CHUNK_Z && z < 0){
            nuevasCoordenadas.z = nuevasCoordenadas.z -1;
        }
        
        return nuevasCoordenadas;
    }
    
    /**
     *
     * @param cood 
     * @return
     */
    public static String generarNombreChunk(Vector3f cood){
        Vector3f nuevasCoord = calculaCoordenadasChunk(cood);
        return "Chunk>>"+String.valueOf((int) nuevasCoord.x)+"__"+String.valueOf((int) nuevasCoord.y)+"__"+String.valueOf((int) nuevasCoord.z);
    }
    
    /**
     *
     * @param nomChunk
     * @param cood
     * @return
     */
    public static String generarNombreBloque(String nomChunk,Vector3f cood){
        return nomChunk+",Bloque>>"+String.valueOf((int) cood.x)+"__"+String.valueOf((int) cood.y)+"__"+String.valueOf((int) cood.z);
    }
    
    /**
     *
     * @param cood
     * @return
     */
    public static String generarNombreBloque(Vector3f cood){
        String nomChunk = generarNombreChunk(cood);
        return nomChunk+",Bloque>>"+String.valueOf((int) cood.x)+"__"+String.valueOf((int) cood.y)+"__"+String.valueOf((int) cood.z);
    }
    
    /**
     *
     * @param nomBloque
     * @return
     */
    public static Vector3f devuelveCoordenadasBloque(String nomBloque){
        Vector3f coord = new Vector3f();
        
        String[] split = nomBloque.split(",");
        split = split[1].split(">>");
        split = split[1].split("__");
        
        coord.x = Integer.valueOf(split[0]);
        coord.y = Integer.valueOf(split[1]);
        coord.z = Integer.valueOf(split[2]);
        
        return coord;
    }
}

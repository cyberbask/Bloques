/*
 * Clase para guardar constante, y funciones estaticas
 */
package bloques.manejo.utiles;

import com.jme3.math.Vector3f;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author mcarballo
 */
public class BloquesUtiles {
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
    public static final int TAMANO_CHUNK_Y = 256;
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
    public static final int TAMANO_GENERA_TERRENO = 256; //320 limite maximo por ahora,256 lo normal
    
    /**
     *
     */
    public static final Boolean TEST_FINAL = true;
    
    /**
     *
     */
    public static final Boolean ESTADISTICAS = false;
    
    /**
     *
     */
    public static final int TAMANO_DISTANCIA_DIBUJADO = 128;
    
    /**
     *
     */
    public static final int NIVEL_MAR = 10; 
    
    /**
     *
     */
    public static final int CAM_FRUSTUMFAR = (int) ((TAMANO_DISTANCIA_DIBUJADO * TAMANO_BLOQUE) * 2f);
    
    /**
     *
     */
    public static final Boolean SOMBRAS = true; 
    
    /**
     *
     */
    public static final float SOMBRAS_INTENSIDAD = 0.25f; 
    
    /**
     *
     */
    public static final int SOMBRAS_CALIDAD1 = 1024; 
    
    /**
     *
     */
    public static final int SOMBRAS_CALIDAD2 = 3; 
    
    /**
     *
     */
    public static final float NIEBLA_DISTANCIA = (TAMANO_DISTANCIA_DIBUJADO * TAMANO_BLOQUE) * 1.5f;
    
    /**
     *
     */
    public static final float NIEBLA_INTENSIDAD = 0.20f;
    
    /**
     *
     */
    public static final float BLUR_FOCUS_RANGE = TAMANO_DISTANCIA_DIBUJADO;
    
    /**
     *
     */
    public static final float BLUR_SCALE = 2f;
    
    
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
     * @param coord
     * @return
     */
    public static Vector3f calculaCoordenadasBloque(Vector3f coord){
        return calculaCoordenadasBloque((int) coord.x, (int) coord.y, (int) coord.z);
    }
    
    /**
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static Vector3f calculaCoordenadasBloque(float x, float y, float z){
        return calculaCoordenadasBloque((int) x, (int) y, (int) z);
    }
    
    /**
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static Vector3f calculaCoordenadasBloque(int x, int y, int z){
        Vector3f nuevasCoordenadas = new Vector3f();
        
        if (x >= 0){
            nuevasCoordenadas.x = ((int) (x / TAMANO_BLOQUE) * TAMANO_BLOQUE);
        }else{
            nuevasCoordenadas.x = ((int) (x / TAMANO_BLOQUE) * TAMANO_BLOQUE) - TAMANO_BLOQUE;
        }
        
        if (y >= 0){
            nuevasCoordenadas.y = ((int) (y / TAMANO_BLOQUE) * TAMANO_BLOQUE);
        }else{
            nuevasCoordenadas.y = ((int) (y / TAMANO_BLOQUE) * TAMANO_BLOQUE) - TAMANO_BLOQUE;
        }
        
        if (z >= 0){
            nuevasCoordenadas.z = ((int) (z / TAMANO_BLOQUE) * TAMANO_BLOQUE);
        }else{
            nuevasCoordenadas.z = ((int) (z / TAMANO_BLOQUE) * TAMANO_BLOQUE) - TAMANO_BLOQUE;
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
        Vector3f coordBloque = calculaCoordenadasBloque(cood);
        return nomChunk+",Bloque>>"+String.valueOf((int) coordBloque.x)+"__"+String.valueOf((int) coordBloque.y)+"__"+String.valueOf((int) coordBloque.z);
    }
    
    /**
     *
     * @param cood
     * @return
     */
    public static String generarNombreBloque(Vector3f cood){
        String nomChunk = generarNombreChunk(cood);
        Vector3f coordBloque = calculaCoordenadasBloque(cood);
        return nomChunk+",Bloque>>"+String.valueOf((int) coordBloque.x)+"__"+String.valueOf((int) coordBloque.y)+"__"+String.valueOf((int) coordBloque.z);
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
    
    /**
     *
     * @param coordenadas
     * @return
     */
    public static int averiguaCoordenadasContacto(Vector3f coordenadas){       
        float calculo = 0.0001f;
        
        if (coordenadas.x - Math.floor(coordenadas.x) <= calculo && coordenadas.x - Math.floor(coordenadas.x) >= 0f) {
            return 1;
        }else if(coordenadas.x - Math.ceil(coordenadas.x) >= -calculo && coordenadas.x - Math.ceil(coordenadas.x) <= 0f){
            return 1;
        }
        
        if (coordenadas.y - Math.floor(coordenadas.y) <= calculo && coordenadas.y - Math.floor(coordenadas.y) >= 0f) {
            return 2;
        }else if(coordenadas.y - Math.ceil(coordenadas.y) >= -calculo && coordenadas.y - Math.ceil(coordenadas.y) <= 0f){
            return 2;
        }
        
        if (coordenadas.z - Math.floor(coordenadas.z) <= calculo && coordenadas.z - Math.floor(coordenadas.z) >= 0f) {
            return 3;
        }else if(coordenadas.z - Math.ceil(coordenadas.z) >= -calculo && coordenadas.z - Math.ceil(coordenadas.z) <= 0f){
            return 3;
        }
        
        return 0;
    }
    
    /**
     *
     * @param coordenadas
     * @return
     */
    public static Vector3f redondeaCoordenadasContacto(Vector3f coordenadas){
        Vector3f devolver = new Vector3f();
        
        devolver.x = (int) coordenadas.x;
        devolver.y = (int) coordenadas.y;
        devolver.z = (int) coordenadas.z;

        return devolver;
    }
    
    /**
     *
     * @param min
     * @param max
     * @return
     */
    public static int aleatorio(int min, int max){
        Random r = new Random();
        
        int Low = min;
        int High = max;
        int R = r.nextInt(High-Low) + Low;
        
        return R;
    }
    
    /**
     *
     * @param coord
     * @return
     */
    public static Map<Integer,String> calculaChunksAlrededor(Vector3f coord){
        Map<Integer,String> chunksAlrededor = new HashMap<Integer, String>();
        
        Vector3f coordChunk = calculaCoordenadasChunk(coord);
        Vector3f nuevasCoordChunk;
        
        int cuantosChunksX = (TAMANO_DISTANCIA_DIBUJADO / TAMANO_CHUNK_X) / 2;
        int cuantosChunksZ = (TAMANO_DISTANCIA_DIBUJADO / TAMANO_CHUNK_Z) / 2;
        int cuantosChunksY = (MAX_ALTURA_BLOQUES / TAMANO_CHUNK_Y);
        int contador = 0;
        
        for (int x = ((int)coordChunk.x - cuantosChunksX);x <= ((int)coordChunk.x + cuantosChunksX); x++){
            if (x < 0){
                continue;
            }
            
            for (int z = ((int)coordChunk.z - cuantosChunksZ);z <= ((int)coordChunk.z + cuantosChunksZ); z++){
                if (z < 0){
                    continue;
                }
                
                for(int y=0;y<cuantosChunksY;y++){
                    nuevasCoordChunk = new Vector3f();
                    nuevasCoordChunk.x = (int) (x * TAMANO_BLOQUE * TAMANO_CHUNK_X);
                    nuevasCoordChunk.z = (int) (z * TAMANO_BLOQUE * TAMANO_CHUNK_Z);
                    nuevasCoordChunk.y = (int) (y * TAMANO_BLOQUE * TAMANO_CHUNK_Y);
                    
                    chunksAlrededor.put(contador,generarNombreChunk(nuevasCoordChunk));
                    
                    contador++;
                }
            }            
        }
        
        return chunksAlrededor;
    }
}

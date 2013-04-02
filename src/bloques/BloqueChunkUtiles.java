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
    public static final int TAMANO_CHUNK = 16; //16x16x16
    public static final int TOTAL_BLOQUES = 4096; //16x16x16
    
    public static final int MAX_ALTURA_BLOQUES = 256;
    public static final int MIN_ALTURA_BLOQUES = 0; 
    
    public static int[] calculaCoordenadasChunk(int x, int y, int z){
        int nuevasCoordenadas[] = new int[3];
        
        nuevasCoordenadas[0] = x / TAMANO_CHUNK;
        nuevasCoordenadas[1] = y / TAMANO_CHUNK;
        nuevasCoordenadas[2] = z / TAMANO_CHUNK;
        
        return nuevasCoordenadas;
    }
    
    public static int[] calculaCoordenadasBloqueDentroDeChunk(int x, int y, int z){
        int nuevasCoordenadas[] = new int[3];
 
        nuevasCoordenadas[0] = x / TAMANO_CHUNK;
        nuevasCoordenadas[1] = y / TAMANO_CHUNK;
        nuevasCoordenadas[2] = z / TAMANO_CHUNK;
        
        int nuevasCoordenadasBloque[] = new int[3];
        
        nuevasCoordenadasBloque[0] = x - (nuevasCoordenadas[0] * TAMANO_CHUNK);
        nuevasCoordenadasBloque[1] = y - (nuevasCoordenadas[1] * TAMANO_CHUNK);
        nuevasCoordenadasBloque[2] = z - (nuevasCoordenadas[2] * TAMANO_CHUNK);
        
        return nuevasCoordenadasBloque;
    }
    
    public static int[] calculaCoordenadasBloqueAPartirDeChunk(int chunkX, int chunkY, int chunkZ, int x, int y, int z){
        int nuevasCoordenadas[] = new int[3];
 
        nuevasCoordenadas[0] = chunkX * TAMANO_CHUNK;
        nuevasCoordenadas[1] = chunkY * TAMANO_CHUNK;
        nuevasCoordenadas[2] = chunkZ * TAMANO_CHUNK;
        
        int nuevasCoordenadasBloque[] = new int[3];
        
        nuevasCoordenadasBloque[0] = nuevasCoordenadas[0] + x;
        nuevasCoordenadasBloque[1] = nuevasCoordenadas[1] + y;
        nuevasCoordenadasBloque[2] = nuevasCoordenadas[2] + z;
        
        return nuevasCoordenadasBloque;
    }
    
    public static int[] calculaCoordenadasBloqueAPartirDeChunk(String nomChunk, int x, int y, int z){
        int nuevasCoordenadas[] = new int[3];
        
        String[] tokens = nomChunk.split("-");
 
        nuevasCoordenadas[0] = Integer.valueOf(tokens[0]) * TAMANO_CHUNK;
        nuevasCoordenadas[1] = Integer.valueOf(tokens[1]) * TAMANO_CHUNK;
        nuevasCoordenadas[2] = Integer.valueOf(tokens[2]) * TAMANO_CHUNK;
        
        int nuevasCoordenadasBloque[] = new int[3];
        
        nuevasCoordenadasBloque[0] = nuevasCoordenadas[0] + x;
        nuevasCoordenadasBloque[1] = nuevasCoordenadas[1] + y;
        nuevasCoordenadasBloque[2] = nuevasCoordenadas[2] + z;
        
        return nuevasCoordenadasBloque;
    }
    
    public static String generarNombreChunk(int x, int y, int z){
        return String.valueOf(x)+"-"+String.valueOf(y)+"-"+String.valueOf(z);
    }
    
    public static String generarNombreChunk(int[] coordenadas){
        return String.valueOf(coordenadas[0])+"-"+String.valueOf(coordenadas[1])+"-"+String.valueOf(coordenadas[2]);
    }
}

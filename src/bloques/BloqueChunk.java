/*
 * Manejo de un Chunk de Bloques
 */
package bloques;

/**
 *
 * @author mcarballo
 */
public class BloqueChunk {
    /**
     * Contiene el numero de bloque dentro del chunk segun coordenadas
     */
    protected int bloquesPosiciones[][][];
    /**
     * Contiene los datos del bloque segun su numero
     */
    protected BloqueChunkDatos bloquesDatos[];        
    
    /**
     * Contador que se utiliza para rellenar los bloques secuencialmente
     */
    protected int bloqueActualContador = 0;
    
    /**
     * Constructorr
     */
    public BloqueChunk(){
        int tamano = BloqueChunkUtiles.TAMANO_CHUNK;
        int totalBloques = BloqueChunkUtiles.TOTAL_BLOQUES + 1;
        
        bloquesPosiciones = new int[tamano][tamano][tamano];
        bloquesDatos = new BloqueChunkDatos[totalBloques];
        
        //inicializamos
        for(int x = 0;x<tamano;x++){
            for(int y = 0;y<tamano;y++){
                for(int z = 0;z<tamano;z++){
                    bloquesPosiciones[x][y][z] = 0;
                }
            }
        }
    }
    
    /**
     * Devuelve el numero de bloque segun su posicion en el chunk
     * @param x
     * @param y
     * @param z
     * @return
     */
    public int getNumBloque(int x, int y, int z){
        int bloquePeticion = bloquesPosiciones[x][y][z];
        
        return bloquePeticion;
    }
    
    /**
     * Devuelve los datos del bloque segun su posicion en el chunk
     * @param x
     * @param y
     * @param z
     * @return
     */
    public BloqueChunkDatos getDatosBloque(int x, int y, int z){
        int bloquePeticion = this.getNumBloque(x, y, z);
        
        if (bloquePeticion > 0){
            return bloquesDatos[bloquePeticion];
        }else{
            return null;
        }
    }
    
    /**
     *
     * @param coordenadas
     * @return
     */
    public BloqueChunkDatos getDatosBloque(int[] coordenadas){
        int bloquePeticion = this.getNumBloque(coordenadas[0], coordenadas[1], coordenadas[2]);
        
        if (bloquePeticion > 0){
            return bloquesDatos[bloquePeticion];
        }else{
            return null;
        }
    }
    
    /**
     * Actualiza los datos del bloque segun su posicion
     * @param x
     * @param y
     * @param z
     * @param datos  
     */
    public void setDatosBloque(int x, int y, int z, BloqueChunkDatos datos){
        int bloquePeticion = this.getNumBloque(x, y, z);

        if (bloquePeticion == 0){
            bloqueActualContador++;  
            bloquePeticion = bloqueActualContador;
        }
        
        bloquesPosiciones[x][y][z] = bloquePeticion;
        bloquesDatos[bloquePeticion] = datos;
    }
    
    /**
     * Actualiza los datos del bloque segun su posicion
     * @param coodernadas 
     * @param datos 
     */
    public void setDatosBloque(int[] coodernadas, BloqueChunkDatos datos){
        int bloquePeticion = this.getNumBloque(coodernadas[0], coodernadas[1], coodernadas[2]);

        if (bloquePeticion == 0){
            bloqueActualContador++;  
            bloquePeticion = bloqueActualContador;
        }
        
        bloquesPosiciones[coodernadas[0]][coodernadas[1]][coodernadas[2]] = bloquePeticion;
        bloquesDatos[bloquePeticion] = datos;
    }
}

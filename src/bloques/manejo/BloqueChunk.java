/*
 * Manejo de un Chunk de Bloques
 */
package bloques.manejo;

/**
 *
 * @author mcarballo
 */
public class BloqueChunk {
    /**
     * Contiene los datos del bloque segun coordenadas
     */
    protected BloqueChunkDatos bloquesDatos[][][];

    /**
     * Constructor
     */
    public BloqueChunk(){
        int tamano = BloqueChunkUtiles.TAMANO_CHUNK;
        bloquesDatos = new BloqueChunkDatos[tamano][tamano][tamano];
        
        //inicializamos
        for(int x = 0;x<tamano;x++){
            for(int y = 0;y<tamano;y++){
                for(int z = 0;z<tamano;z++){
                    bloquesDatos[x][y][z] = null;
                }
            }
        }
    }
    
    /**
     * Devuelve los datos del bloque segun su posicion en el chunk
     * @param x
     * @param y
     * @param z
     * @return
     */
    public BloqueChunkDatos getDatosBloque(int x, int y, int z){
        return bloquesDatos[x][y][z];
    }
    
    /**
     *
     * @param coordenadas
     * @return
     */
    public BloqueChunkDatos getDatosBloque(int[] coordenadas){
        return bloquesDatos[coordenadas[0]][coordenadas[1]][coordenadas[2]];
    }
    
    /**
     * Actualiza los datos del bloque segun su posicion
     * @param x
     * @param y
     * @param z
     * @param datos  
     */
    public void setDatosBloque(int x, int y, int z, BloqueChunkDatos datos){
        bloquesDatos[x][y][z] = datos;
    }
    
    /**
     * Actualiza los datos del bloque segun su posicion
     * @param coodernadas 
     * @param datos 
     */
    public void setDatosBloque(int[] coodernadas, BloqueChunkDatos datos){
        bloquesDatos[coodernadas[0]][coodernadas[1]][coodernadas[2]] = datos;
    }
}

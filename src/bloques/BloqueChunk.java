/*
 * Manejo de un Chunk de Bloques
 */
package bloques;

/**
 *
 * @author mcarballo
 */
public class BloqueChunk {
    protected int bloquesPosiciones[][][];
    protected BloqueChunkDatos bloquesDatos[];        
    
    protected int bloqueActualContador = 0;
    
    
    public BloqueChunk(){
        bloquesPosiciones = new int[1000][1000][1000];
    }
    
    public void setBloque(){

    }
}

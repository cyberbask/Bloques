/*
 * Clase para manejar los datos del chunk
 */
package bloques;

/**
 *
 * @author mcarballo
 */
public class BloqueChunkDatos {
    /**
     * Nombre o Tipo del Bloque
     */
    protected String nomBloque;

    /**
     * Constructor
     * @param nomBloque
     */
    public BloqueChunkDatos(String nomBloque){
        setNomBloque(nomBloque);
    }
    
    /**
     * Devuelve el Nombre o Tipo de Bloque
     * @return
     */
    public String getNomBloque() {
        return nomBloque;
    }

    /**
     * Guarda el Nombre o Tipo de Bloque
     * @param nomBloque
     */
    public void setNomBloque(String nomBloque) {
        this.nomBloque = nomBloque;
    } 
}

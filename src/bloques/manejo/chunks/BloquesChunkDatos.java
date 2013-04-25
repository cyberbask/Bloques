/*
 * Clase para manejar los datos del chunk
 */
package bloques.manejo.chunks;

/**
 *
 * @author mcarballo
 */
public class BloquesChunkDatos {
    /**
     * Nombre o Tipo del Bloque
     */
    protected String nomBloque;
    
    /**
     *
     */
    protected int[] caras;
    
    /**
     *
     */
    protected Boolean mostrar = true;
    
    /**
     *
     */
    protected Boolean irrompible = false;

    /**
     * Constructor
     */
    public BloquesChunkDatos(){
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
    
    /**
     *
     * @return
     */
    public int[] getCaras() {
        return caras;
    }

    /**
     *
     * @param caras
     */
    public void setCaras(int[] caras) {
        this.caras = caras;
    }
    
    /**
     *
     * @param cara
     * @return
     */
    public int getCara(int cara) {
        return caras[cara];
    }

    /**
     *
     * @param cara
     * @param valor
     */
    public void setCara(int cara, int valor) {
        this.caras[cara] = valor;
    }
    
    /**
     *
     * @return
     */
    public Boolean getMostrar() {
        return mostrar;
    }

    /**
     *
     * @param mostrar
     */
    public void setMostrar(Boolean mostrar) {
        this.mostrar = mostrar;
    }
    
    /**
     *
     * @return
     */
    public Boolean getIrrompible() {
        return irrompible;
    }

    /**
     *
     * @param irrompible
     */
    public void setIrrompible(Boolean irrompible) {
        this.irrompible = irrompible;
    }

}

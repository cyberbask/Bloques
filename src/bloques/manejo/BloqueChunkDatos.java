/*
 * Clase para manejar los datos del chunk
 */
package bloques.manejo;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import java.io.IOException;

/**
 *
 * @author mcarballo
 */
public class BloqueChunkDatos implements Savable{
    /**
     * Nombre o Tipo del Bloque
     */
    protected String nomBloque;
    
    /**
     *
     */
    protected int[] caras;

    /**
     * Constructor
     */
    public BloqueChunkDatos(){
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
     * @param ex
     * @throws IOException
     */
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);

        capsule.write(getNomBloque(), "nomBloque", null);
        capsule.write(getCaras(), "caras", null);
    }

    /**
     *
     * @param im
     * @throws IOException
     */
    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);

        setNomBloque(capsule.readString("nomBloque", null));
        setCaras(capsule.readIntArray("caras", null));
    }
}

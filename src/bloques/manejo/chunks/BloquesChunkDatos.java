/*
 * Clase para manejar los datos del chunk
 */
package bloques.manejo.chunks;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.scene.Node;
import java.io.IOException;
import java.util.Map;


/**
 *
 * @author mcarballo
 */
public class BloquesChunkDatos implements Savable   {
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
     * @param ex
     * @throws IOException
     */
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        
        System.out.println(nomBloque);
        
        capsule.write(nomBloque,  "nomBloque",  null);
        
        capsule.write(caras,  "caras",  null);
    }

    /**
     *
     * @param im
     * @throws IOException
     */
    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        
        nomBloque = capsule.readString("nomBloque",   null);
        
        caras = capsule.readIntArray("caras",   null);
    }
}

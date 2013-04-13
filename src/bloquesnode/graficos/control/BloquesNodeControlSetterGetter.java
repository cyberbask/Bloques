/*
 * Setters y Getters varios
 */
package bloquesnode.graficos.control;

import com.jme3.app.Application;

/**
 *
 * @author mcarballo
 */
public class BloquesNodeControlSetterGetter extends BloquesNodeControlBasic{
    /**
     *
     * @param app
     */
    public BloquesNodeControlSetterGetter(Application app){
        super(app);
    }
    
    /**
     *
     * @return
     */
    public int getPorcentajeGeneradoTerreno(){
        return bloqueGeneraTerreno.porcentajeGenerado;
    }
    
    /**
     *
     * @param x
     * @param z
     * @return
     */
    public int getBloqueConMasAltura(int x, int z){
        return chunks.getBloqueConMasAltura(x, z);
    }
}

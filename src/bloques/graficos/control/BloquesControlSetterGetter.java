/*
 * Setters y Getters varios
 */
package bloques.graficos.control;

import com.jme3.app.Application;

/**
 *
 * @author mcarballo
 */
public class BloquesControlSetterGetter extends BloquesControlBasic{
    /**
     *
     * @param app
     */
    public BloquesControlSetterGetter(Application app){
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
     * @return
     */
    public int getPorcentajeCarga(){
        return bloquesSaveLoad.porcentageCargado;
    }
    
    /**
     *
     * @return
     */
    public int getPorcentajeGuardado(){
        return bloquesSaveLoad.porcentageGuardado;
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
    
    /**
     *
     * @return
     */
    public Boolean isChunksEmpty(){
        if (chunks.getAllChunks().size() <= 0){
            return true;
        }else{
            return false;
        }
    }
    
}

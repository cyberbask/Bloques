/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bloquesnode.graficos.control;

import com.jme3.app.Application;

/**
 *
 * @author mcarballo
 */
public class BloquesNodeControl extends BloquesNodeControlUpdates{
    /**
     *
     * @param app
     */
    public BloquesNodeControl(Application app){
        super(app);
    }
    
    public Boolean generaTerrenoInicial(){
        bloqueGeneraTerreno.generaTerreno();
            
        //ya termino de generar el terreno
        if (!bloqueGeneraTerreno.generandoTerreno){           
            chunks = bloqueGeneraTerreno.getChunks();
            
            /*//tenemos que pasar a updates estos chunks
            for (int x = 0;x<bloqueGeneraTerreno.totalTamano;x = x + BloqueChunkUtiles.TAMANO_CHUNK){
                for (int z = 0;z<bloqueGeneraTerreno.totalTamano;z = z + BloqueChunkUtiles.TAMANO_CHUNK){
                    BloqueChunks grupoChunks = chunks.getGrupoChunks(x * BloqueChunkUtiles.TAMANO_BLOQUE, z * BloqueChunkUtiles.TAMANO_BLOQUE);
                    updates.put(contadorUpdates,grupoChunks);
                    contadorUpdates++;
                }
            }*/

            bloqueGeneraTerreno.vaciaChunks();
            
            return true;
        }
        
        return false;
    }
    
    
    @Override
    protected void controlUpdate(float tpf) {
        super.controlUpdate(tpf);
    }
    
    @Override
    public void destroy(){
        super.destroy();
        bloqueGeneraTerreno.destroy();
    }
}

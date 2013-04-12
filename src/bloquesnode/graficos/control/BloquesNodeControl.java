/*
 * Custom control para el manejo de bloques y chunks
 */
package bloquesnode.graficos.control;

import bloquesnode.manejo.utiles.BloquesNodeUtiles;
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
    
    /**
     *
     * @return
     */
    public Boolean generaTerrenoInicial(){
        bloqueGeneraTerreno.generaTerreno();
            
        //ya termino de generar el terreno
        if (!bloqueGeneraTerreno.generandoTerreno){           
            chunks = bloqueGeneraTerreno.getChunks();
            
            //tenemos que pasar a updates estos chunks
            for (int x = 0;x<BloquesNodeUtiles.TAMANO_GENERA_TERRENO;x = x + BloquesNodeUtiles.TAMANO_CHUNK_X){
                for (int z = 0;z<BloquesNodeUtiles.TAMANO_GENERA_TERRENO;z = z + BloquesNodeUtiles.TAMANO_CHUNK_Z){
                    String[] grupoChunks = chunks.getGrupoChunksNombres(x * BloquesNodeUtiles.TAMANO_BLOQUE, z * BloquesNodeUtiles.TAMANO_BLOQUE);
                    
                    for (String s : grupoChunks){
                        updatesChunk.put(contadorUpdates,s);
                        contadorUpdates++;
                    }
                }
            }

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

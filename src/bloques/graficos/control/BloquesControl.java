/*
 * Custom control para el manejo de bloques y chunks
 */
package bloques.graficos.control;

import bloques.manejo.utiles.BloquesUtiles;
import com.jme3.app.Application;

/**
 *
 * @author mcarballo
 */
public class BloquesControl extends BloquesControlAcciones{
    /**
     *
     * @param app
     */
    public BloquesControl(Application app){
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
            for (int x = 0;x<BloquesUtiles.TAMANO_GENERA_TERRENO;x = x + BloquesUtiles.TAMANO_CHUNK_X){
                for (int z = 0;z<BloquesUtiles.TAMANO_GENERA_TERRENO;z = z + BloquesUtiles.TAMANO_CHUNK_Z){
                    String[] grupoChunks = chunks.getGrupoChunksNombres(x * BloquesUtiles.TAMANO_BLOQUE, z * BloquesUtiles.TAMANO_BLOQUE);
                    
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
    }
}

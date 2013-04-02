/*
 * Aqui se generaran los chunks y el terreno en general
 */
package bloques;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.terrain.heightmap.HillHeightMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 *
 * @author mcarballo
 */
public class BloqueGeneraTerreno{
    protected SimpleApplication app;
    
    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(4);
    Future future = null;
    
    //chunks
    BloqueChunks chunks = null;
    Map<Integer,BloqueChunks> updates=new HashMap<Integer, BloqueChunks>();
    int contadorUpdates = 0;
    
    /**
     *
     * @param app
     */
    public BloqueGeneraTerreno(Application app){
        this.app = (SimpleApplication) app;
    }
    
    private HillHeightMap generateTerrainconReturn(){
        byte[] result= new byte[5];
        Random random= new Random();
        random.nextBytes(result);


        HillHeightMap heightmap = null;
        HillHeightMap.NORMALIZE_RANGE = 100; // optional
        try {
            heightmap = new HillHeightMap(513, 1000, 50, 100, result[0]); // byte 3 is a random seed
        } catch (Exception ex) {

        }
        //heightmap.setHeightScale(0.001f);
        heightmap.load();

        return heightmap;     
    }
    
    /**
     *
     */
    public void generaTerrenoInicialPosiciones(){
        HillHeightMap heightmap = generateTerrainconReturn();
        
        int y = 0;
        int minY = BloqueChunkUtiles.MIN_ALTURA_BLOQUES;
        int maxY = BloqueChunkUtiles.MAX_ALTURA_BLOQUES;
        
        int ultimoGrupoChunkX = 0;
        int ultimoGrupoChunkZ = 0;
        
        int x;
        int z;
        
        for (x = 0;x<32;x++){
            for (z = 0;z<32;z++){
                int originalY = y;
                
                y = (int) heightmap.getScaledHeightAtPoint(x,z);
                
                if (y == 0){
                    y = originalY;
                }
                
                //minY = y; //sin relleno;
                
                for (int a=maxY; a>=minY; a--){ 
                    if (a <= y){
                        chunks.setBloque(x, a, z, new BloqueChunkDatos("Tierra"));
                    }
                    
                }
                
                
                if ((x / BloqueChunkUtiles.TAMANO_CHUNK) > ultimoGrupoChunkX || (z / BloqueChunkUtiles.TAMANO_CHUNK) > ultimoGrupoChunkZ){
                    final BloqueChunks grupoChunks = chunks.getGrupoChunks(x-1, z-1);
                    
                    app.enqueue(new Callable() {
                        public Object call() throws Exception {
                            updates.put(contadorUpdates, grupoChunks);
                            contadorUpdates++;
                            return null;
                        }
                    });
                    
                    if ((x / BloqueChunkUtiles.TAMANO_CHUNK) > ultimoGrupoChunkX){
                        ultimoGrupoChunkX++;
                    }
                    
                    if ((z / BloqueChunkUtiles.TAMANO_CHUNK) > ultimoGrupoChunkZ){
                        ultimoGrupoChunkZ++;
                    }
                }
            }
            
            ultimoGrupoChunkZ = 0;
        }
        
         
    }
    
    // A self-contained time-intensive task:
    private Callable<Boolean> generaTerrenoInicialHilo = new Callable<Boolean>(){
        public Boolean call() throws Exception {
            generaTerrenoInicialPosiciones();
            
            return false;
        }
    };
    
    /**
     *
     * @return
     */
    public Map<Integer, BloqueChunks> generaTerreno(){
        try{
            if(future == null && chunks == null){
                chunks = new BloqueChunks();
                future = executor.submit(generaTerrenoInicialHilo);
            }
            else if(future != null){
                if(future.isDone()){
                    future = null;
                }
                else if(future.isCancelled()){
                    future = null;
                }
            }
        } 
        catch(Exception e){ 

        }
        
        Integer[] keys = (Integer[])( updates.keySet().toArray( new Integer[updates.size()] ) );
        
        if (keys.length > 0){
            Map<Integer,BloqueChunks> chunksDevolver=new HashMap<Integer, BloqueChunks>();
            
            for(int i=0; i<keys.length; i++){
                int claveActual = keys[i];
                
                chunksDevolver.put(i, updates.get(claveActual));

                updates.remove(claveActual);
            }
        
            return chunksDevolver;
        }
        
        return null;
    }
    
    /**
     *
     */
    public void destroy() {
        //asi parece que cierra bien
        //el problema esta en el do while con el sleep de hilo
        executor.shutdown();
        executor.shutdownNow();
    }
}

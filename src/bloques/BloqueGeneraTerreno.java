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
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 *
 * @author mcarballo
 */
public class BloqueGeneraTerreno{
    /**
     *
     */
    private SimpleApplication app;
    
    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(4);
    Future future = null;
    
    //chunks
    /**
     *
     */
    public BloqueChunks chunks = null;
    Map<Integer,BloqueChunks> updates=new HashMap<Integer, BloqueChunks>();
    int contadorUpdates = 0;
    
    //tamaño del mundo a generar
    //int totalTamano = 192;
    int totalTamano = 192;
    
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
            heightmap = new HillHeightMap(totalTamano + 1, 100, 90, 100, result[0]); // byte 3 is a random seed
        } catch (Exception ex) {

        }
        //heightmap.setHeightScale(0.001f);
        heightmap.load();

        return heightmap;     
    }
    
    /**
     *
     * @throws InterruptedException 
     * @throws ExecutionException 
     */
    @SuppressWarnings("SleepWhileInLoop")
    public void generaTerrenoInicialPosiciones() throws InterruptedException, ExecutionException{
        //en este caso mantenemos las x y z sin multiplicar por el tamaño de bloque
        //ya que el generador de terrenos sigue con sus coordenadas normales
        //solo lo multiplicamos cuando pasamos por nuestras funciones
        
        HillHeightMap heightmap = generateTerrainconReturn();
        
        int y = 0;
        int minY = BloqueChunkUtiles.MIN_ALTURA_BLOQUES;
        int maxY = BloqueChunkUtiles.MAX_ALTURA_BLOQUES;
        
        int ultimoGrupoChunkX = (BloqueChunkUtiles.TAMANO_CHUNK - 1);
        int ultimoGrupoChunkZ = 0;
        
        int x;
        int z;
        
        int variacion = 0;
        
        for (x = 0;x<totalTamano;x++){
            for (z = 0;z<totalTamano;z++){
                int originalY = y;
                
                y = (int) heightmap.getScaledHeightAtPoint(x,z);
                
                if (y == 0){
                    y = originalY;
                }
                
                
                for (int a=maxY; a>=minY; a--){ 
                    //if (a == y){ //sin relleno                   
                    if (a <= y){
                        String tipoTerreno;
                        if (variacion == 0){
                            tipoTerreno = "Tierra";
                            variacion = 1;
                        }else if(variacion == 1){
                            tipoTerreno = "Roca";
                            variacion = 2;
                        }else{
                            tipoTerreno = "Arena";
                            variacion = 0;
                        }
    
                        chunks.setBloque(x * BloqueChunkUtiles.TAMANO_BLOQUE, a * BloqueChunkUtiles.TAMANO_BLOQUE, z * BloqueChunkUtiles.TAMANO_BLOQUE, new BloqueChunkDatos(tipoTerreno));
                        
                    }else{
                        chunks.setBloque(x * BloqueChunkUtiles.TAMANO_BLOQUE, a * BloqueChunkUtiles.TAMANO_BLOQUE, z * BloqueChunkUtiles.TAMANO_BLOQUE, null);
                    }
                    
                }
                
                //TODO esto deberia ir guardado en algun sitio, y luego cargarlo desde ahi
                if (x >= ultimoGrupoChunkX && (z / (BloqueChunkUtiles.TAMANO_CHUNK - 1)) > ultimoGrupoChunkZ){
                    //System.out.println("chunkear x"+x+"z"+z);
                    final BloqueChunks grupoChunks = chunks.getGrupoChunks(x * BloqueChunkUtiles.TAMANO_BLOQUE, z * BloqueChunkUtiles.TAMANO_BLOQUE);
                    
                    if (x >= 60){
                        int yo = 0;
                        
                    }
                    
                    app.enqueue(new Callable() {
                        public Object call() throws Exception {
                            updates.put(contadorUpdates, grupoChunks);
                            //System.out.println("chunkeado");
                            contadorUpdates++;
                            return null;
                        }
                    });
                    
                    if ((z / (BloqueChunkUtiles.TAMANO_CHUNK - 1)) > ultimoGrupoChunkZ){
                        ultimoGrupoChunkZ++;
                    }
                    
                    /*int contaBucles = 0;
                    
                    int cuentaClaves;
                    
                    do {
                        Thread.sleep(1000);
                        contaBucles += 1;

                        cuentaClaves = app.enqueue(new Callable<Integer>() {
                            public Integer call() throws Exception {
                                Integer[] keys = (Integer[])( updates.keySet().toArray( new Integer[updates.size()] ) );
                                return keys.length;
                            }
                        }).get();
                    } while(cuentaClaves > 0 && contaBucles < 5);*/
                    
                    //Thread.sleep(10);
                }
            }
            
            ultimoGrupoChunkZ = 0;
            
            if (x >= ultimoGrupoChunkX){
                ultimoGrupoChunkX = ultimoGrupoChunkX + (BloqueChunkUtiles.TAMANO_CHUNK - 1);
            }
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
     */
    public void generaTerreno(){
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
    }
    
    /**
     *
     * @return
     */
    public Map<Integer,BloqueChunks> getUpdates(){
        return updates;
    }
    
    /**
     *
     * @param vaciarUpdate
     * @return
     */
    public Map<Integer,BloqueChunks> getUpdates(Boolean vaciarUpdate){
        if (vaciarUpdate){
            Map<Integer,BloqueChunks> updatesCopia = new HashMap<Integer, BloqueChunks>();

            Boolean hayDatos = false;
            
            SortedSet<Integer> keys = new TreeSet<Integer>(updates.keySet());
            for (Integer key : keys) { 
                updatesCopia.put(key,updates.get(key));
                updates.remove(key);
                hayDatos = true;
                break;
            }
            
            if (hayDatos){
                return updatesCopia;
            }else{
               return null;
            }
        }else{
            return getUpdates();
        }
    }
    
    /**
     *
     * @return
     */
    public BloqueChunks getChunks() {
        return chunks;
    }

    /**
     *
     * @param chunks
     */
    public void setChunks(BloqueChunks chunks) {
        this.chunks = chunks;
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

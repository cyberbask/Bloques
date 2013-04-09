/*
 * Aqui se generaran los chunks y el terreno en general
 */
package bloques.manejo;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.system.Timer;
import com.jme3.terrain.heightmap.HillHeightMap;
import java.util.Random;
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
    protected BloqueChunks chunks = null;
    int contadorUpdates = 0;
    
    /**
     *
     */
    public Boolean generandoTerreno = false;
    /**
     *
     */
    public int porcentajeGenerado = 0;
    
    //tamaño del mundo a generar
    //int totalTamano = 192;
    //int totalTamano = 400;
    /**
     *
     */
    public int totalTamano = 32;
    
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
        
        int x;
        int z;
        
        int variacion = 0;
        
        for (x = 0;x<totalTamano;x++){
            for (z = 0;z<totalTamano;z++){                
                y = (int) heightmap.getScaledHeightAtPoint(x,z);
 
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
                        
                        BloqueChunkDatos bloqueChunkDatos = new BloqueChunkDatos();
                        bloqueChunkDatos.setNomBloque(tipoTerreno);
                        chunks.setBloque(x * BloqueChunkUtiles.TAMANO_BLOQUE, a * BloqueChunkUtiles.TAMANO_BLOQUE, z * BloqueChunkUtiles.TAMANO_BLOQUE,bloqueChunkDatos);
                        
                    }else{
                        chunks.setBloque(x * BloqueChunkUtiles.TAMANO_BLOQUE, a * BloqueChunkUtiles.TAMANO_BLOQUE, z * BloqueChunkUtiles.TAMANO_BLOQUE, null);
                    }
                    
                }
            }
            
            //porcentage generado
            final int xx = x;
            app.enqueue(new Callable() {
                public Object call() throws Exception {
                    porcentajeGenerado =  (xx * 100) / totalTamano;
                    return null;
                }
            });
            
        }
        
         
    }
    
    // A self-contained time-intensive task:
    private Callable<Boolean> generaTerrenoInicialHilo = new Callable<Boolean>(){
        public Boolean call() throws Exception {
            Timer timer = app.getTimer();
            float totalInicio = timer.getTimeInSeconds();
            
            generaTerrenoInicialPosiciones();
            
            float totalFin = timer.getTimeInSeconds();
            System.out.println("Generar Terreno "+(totalFin-totalInicio));
            
            return false;
        }
    };
    
    /**
     *
     */
    public void generaTerreno(){
        try{
            if(future == null && chunks == null && !generandoTerreno){
                generandoTerreno = true;
                chunks = new BloqueChunks();
                future = executor.submit(generaTerrenoInicialHilo);
            }
            else if(future != null){
                if(future.isDone()){
                    generandoTerreno = false;
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
    public void vaciaChunks(){
        chunks = null;
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

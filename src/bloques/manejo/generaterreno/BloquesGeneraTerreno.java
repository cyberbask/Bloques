/*
 * Aqui se generaran los chunks y el terreno en general
 */
package bloques.manejo.generaterreno;

import bloques.graficos.generabloque.BloquesGeneraBloque;
import bloques.manejo.chunks.BloquesChunkDatos;
import bloques.manejo.chunks.BloquesChunks;
import bloques.manejo.utiles.BloquesUtiles;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
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
public class BloquesGeneraTerreno{  
    /**
     *
     */
    protected SimpleApplication app;
    
    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(4);
    private Future future = null;
    
    //chunks
    /**
     *
     */
    private BloquesChunks chunks = null;
    
    /**
     *
     */
    public Boolean generandoTerreno = false;
    
    /**
     *
     */
    public int porcentajeGenerado = 0;
    
    /**
     *
     */
    private int totalTamano = BloquesUtiles.TAMANO_GENERA_TERRENO;
    
    
    /**
     *
     * @param app
     * @param bloques  
     */
    public BloquesGeneraTerreno(Application app, BloquesGeneraBloque bloques){
        this.app = (SimpleApplication) app;
    }
    
    private HillHeightMap generateTerrainconReturn(){
        byte[] result= new byte[5];
        Random random= new Random();
        random.nextBytes(result);


        HillHeightMap heightmap = null;
        HillHeightMap.NORMALIZE_RANGE = 100; // optional
        try {
            heightmap = new HillHeightMap(totalTamano + 1, 750, 90, 100, result[0]); // byte 3 is a random seed
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
        
        int y;
        int x;
        int z;
        int minY = BloquesUtiles.MIN_ALTURA_BLOQUES;
        int maxY = BloquesUtiles.MAX_ALTURA_BLOQUES;
        int variacion = 0;
        String tipoTerreno;
        
        for (x = 0;x<totalTamano;x++){
            for (z = 0;z<totalTamano;z++){                
                y = (int) heightmap.getScaledHeightAtPoint(x,z);
 
                for (int a=maxY; a>=minY; a--){ 
                    int aGuardar = a / 2;
                    
                    if (a<=y){
                        tipoTerreno = "Hierba";
                        
                        if (a == y){
                            tipoTerreno = "Hierba";
                        }else{
                            if (a < y && (y-a <= 4)){
                                tipoTerreno = "Tierra";
                            }
                            if (a < y && (y-a > 4)){
                                tipoTerreno = "Roca";
                            }
                            if (a > minY && (a-minY <= 2)){
                                tipoTerreno = "Arena";
                            }
                        }

                        BloquesChunkDatos bloqueDatos = new BloquesChunkDatos();
                        bloqueDatos.setNomBloque(tipoTerreno);

                        chunks.setBloque(new Vector3f(x * BloquesUtiles.TAMANO_BLOQUE, aGuardar * BloquesUtiles.TAMANO_BLOQUE, z * BloquesUtiles.TAMANO_BLOQUE), bloqueDatos);
                    }else{
                        chunks.setChunkSiNoExiste(new Vector3f(x * BloquesUtiles.TAMANO_BLOQUE, aGuardar * BloquesUtiles.TAMANO_BLOQUE, z * BloquesUtiles.TAMANO_BLOQUE));
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
                //BloqueChunks loaded = (BloqueChunks) SaveGame.loadGame("Bloques/terreno/", "allchunks");
                porcentajeGenerado = 0; //ponemos el porcentaje a cero
                
                BloquesChunks loaded=null;
                
                if (loaded == null){
                    generandoTerreno = true;
                    chunks = new BloquesChunks();
                    future = executor.submit(generaTerrenoInicialHilo);
                }else{
                    chunks = loaded;
                }
            }
            else if(future != null){
                if(future.isDone()){
                    generandoTerreno = false;
                    future = null;
                    //SaveGame.saveGame("Bloques/terreno/", "allchunks", chunks);
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
    public BloquesChunks getChunks() {
        return chunks;
    }

    /**
     *
     * @param chunks
     */
    public void setChunks(BloquesChunks chunks) {
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
        executor.shutdown();
        executor.shutdownNow();
    }
}

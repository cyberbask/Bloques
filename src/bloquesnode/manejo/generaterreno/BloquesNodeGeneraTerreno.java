/*
 * Aqui se generaran los chunks y el terreno en general
 */
package bloquesnode.manejo.generaterreno;

import bloquesnode.graficos.generabloque.BloquesNodeGeneraBloque;
import bloquesnode.manejo.chunks.BloquesNodeChunkDatos;
import bloquesnode.manejo.chunks.BloquesNodeChunks;
import bloquesnode.manejo.utiles.BloquesNodeUtiles;
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
import jme3tools.savegame.SaveGame;

/**
 *
 * @author mcarballo
 */
public class BloquesNodeGeneraTerreno{  
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
    private BloquesNodeChunks chunks = null;
    
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
    private int totalTamano = BloquesNodeUtiles.TAMANO_GENERA_TERRENO;
    
    
    /**
     *
     * @param app
     * @param bloques  
     */
    public BloquesNodeGeneraTerreno(Application app, BloquesNodeGeneraBloque bloques){
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
        
        int y;
        int x;
        int z;
        int minY = BloquesNodeUtiles.MIN_ALTURA_BLOQUES;
        int maxY = BloquesNodeUtiles.MAX_ALTURA_BLOQUES;
        int variacion = 0;
        
        for (x = 0;x<totalTamano;x++){
            for (z = 0;z<totalTamano;z++){                
                y = (int) heightmap.getScaledHeightAtPoint(x,z);
 
                for (int a=maxY; a>=minY; a--){ 
                    if (a<=y){
                        String tipoTerreno;
                        if (variacion == 0){
                            tipoTerreno = "Tierra";
                            variacion = 1;
                        }else if(variacion == 1){
                            tipoTerreno = "Roca";
                            variacion = 2;
                        }else if(variacion == 2){
                            tipoTerreno = "Arena";
                            variacion = 3;
                        }else{
                            tipoTerreno = "Hierba";
                            variacion = 0;
                        }

                        BloquesNodeChunkDatos bloqueDatos = new BloquesNodeChunkDatos();
                        bloqueDatos.setNomBloque(tipoTerreno);

                        chunks.setBloque(new Vector3f(x * BloquesNodeUtiles.TAMANO_BLOQUE, a * BloquesNodeUtiles.TAMANO_BLOQUE, z * BloquesNodeUtiles.TAMANO_BLOQUE), bloqueDatos);
                    }else{
                        chunks.setBloque(new Vector3f(x * BloquesNodeUtiles.TAMANO_BLOQUE, a * BloquesNodeUtiles.TAMANO_BLOQUE, z * BloquesNodeUtiles.TAMANO_BLOQUE), null);
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
                
                BloquesNodeChunks loaded=null;
                
                if (loaded == null){
                    generandoTerreno = true;
                    chunks = new BloquesNodeChunks();
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
    public BloquesNodeChunks getChunks() {
        return chunks;
    }

    /**
     *
     * @param chunks
     */
    public void setChunks(BloquesNodeChunks chunks) {
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

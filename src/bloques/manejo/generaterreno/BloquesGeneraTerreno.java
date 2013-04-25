/*
 * Aqui se generaran los chunks y el terreno en general
 */
package bloques.manejo.generaterreno;

import bloques.graficos.generabloque.BloquesGeneraBloque;
import bloques.manejo.chunks.BloquesChunk;
import bloques.manejo.chunks.BloquesChunkDatos;
import bloques.manejo.chunks.BloquesChunks;
import bloques.manejo.utiles.BloquesSaveLoad;
import bloques.manejo.utiles.BloquesUtiles;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.Timer;
import com.jme3.terrain.heightmap.HillHeightMap;
import java.util.Map;
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
    private AssetManager      assetManager;
    
    private ScheduledThreadPoolExecutor executor;
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
    
    private BloquesGeneraBloque bloques;
    
    
    /**
     *
     * @param app
     * @param bloques
     * @param executor  
     */
    public BloquesGeneraTerreno(Application app, BloquesGeneraBloque bloques, ScheduledThreadPoolExecutor executor){
        this.app = (SimpleApplication) app;
        this.assetManager = this.app.getAssetManager();
        this.executor = executor;
        
        this.bloques = bloques;
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
     * @param coord
     */
    public void generarArbol(Vector3f coord){
        BloquesChunkDatos bloqueDatos;
        
        //primero el tronco, unos cuantos bloques parriba
        int alturaTronco = BloquesUtiles.aleatorio(6, 10);
        for (int conta=1;conta<=alturaTronco; conta++){
            bloqueDatos = new BloquesChunkDatos();bloqueDatos.setNomBloque("Madera");bloqueDatos.setMostrar(true);
            chunks.setBloque(new Vector3f(coord.x, coord.y + (conta * BloquesUtiles.TAMANO_BLOQUE), coord.z), bloqueDatos);
        }
        
        //hojas
        bloqueDatos = new BloquesChunkDatos();bloqueDatos.setNomBloque("Hojas");bloqueDatos.setMostrar(true);
        chunks.setBloque(new Vector3f(coord.x, coord.y + ((alturaTronco + 1) * BloquesUtiles.TAMANO_BLOQUE), coord.z), bloqueDatos);
        
        int minAltTronco =  BloquesUtiles.aleatorio(4, 6);
        int calculo;
        int contador = 0;
        int calCoord;
        
        do{
            calculo = (alturaTronco + 1) - minAltTronco;
            calCoord = contador * BloquesUtiles.TAMANO_BLOQUE;

            for (int alt = alturaTronco + 1; alt >=minAltTronco; alt--){
                bloqueDatos = new BloquesChunkDatos();bloqueDatos.setNomBloque("Hojas");bloqueDatos.setMostrar(true);
                chunks.setBloque(new Vector3f(coord.x + calCoord + BloquesUtiles.TAMANO_BLOQUE, coord.y + ((alt) * BloquesUtiles.TAMANO_BLOQUE), coord.z), bloqueDatos);
                bloqueDatos = new BloquesChunkDatos();bloqueDatos.setNomBloque("Hojas");bloqueDatos.setMostrar(true);
                chunks.setBloque(new Vector3f(coord.x - calCoord - BloquesUtiles.TAMANO_BLOQUE, coord.y + ((alt) * BloquesUtiles.TAMANO_BLOQUE), coord.z), bloqueDatos);
                bloqueDatos = new BloquesChunkDatos();bloqueDatos.setNomBloque("Hojas");bloqueDatos.setMostrar(true);
                chunks.setBloque(new Vector3f(coord.x, coord.y + ((alt) * BloquesUtiles.TAMANO_BLOQUE), coord.z + calCoord +BloquesUtiles.TAMANO_BLOQUE), bloqueDatos);
                bloqueDatos = new BloquesChunkDatos();bloqueDatos.setNomBloque("Hojas");bloqueDatos.setMostrar(true);
                chunks.setBloque(new Vector3f(coord.x, coord.y + ((alt) * BloquesUtiles.TAMANO_BLOQUE), coord.z - calCoord -BloquesUtiles.TAMANO_BLOQUE), bloqueDatos);

                bloqueDatos = new BloquesChunkDatos();bloqueDatos.setNomBloque("Hojas");bloqueDatos.setMostrar(true);
                chunks.setBloque(new Vector3f(coord.x+BloquesUtiles.TAMANO_BLOQUE, coord.y + ((alt) * BloquesUtiles.TAMANO_BLOQUE), coord.z-BloquesUtiles.TAMANO_BLOQUE), bloqueDatos);
                bloqueDatos = new BloquesChunkDatos();bloqueDatos.setNomBloque("Hojas");bloqueDatos.setMostrar(true);
                chunks.setBloque(new Vector3f(coord.x+BloquesUtiles.TAMANO_BLOQUE, coord.y + ((alt) * BloquesUtiles.TAMANO_BLOQUE), coord.z+BloquesUtiles.TAMANO_BLOQUE), bloqueDatos);
                bloqueDatos = new BloquesChunkDatos();bloqueDatos.setNomBloque("Hojas");bloqueDatos.setMostrar(true);
                chunks.setBloque(new Vector3f(coord.x-BloquesUtiles.TAMANO_BLOQUE, coord.y + ((alt) * BloquesUtiles.TAMANO_BLOQUE), coord.z-BloquesUtiles.TAMANO_BLOQUE), bloqueDatos);
                bloqueDatos = new BloquesChunkDatos();bloqueDatos.setNomBloque("Hojas");bloqueDatos.setMostrar(true);
                chunks.setBloque(new Vector3f(coord.x-BloquesUtiles.TAMANO_BLOQUE, coord.y + ((alt) * BloquesUtiles.TAMANO_BLOQUE), coord.z+BloquesUtiles.TAMANO_BLOQUE), bloqueDatos);
            }
            
            alturaTronco--;
            minAltTronco++;
            
            contador++;
            if (contador > 6){
                break;
            }
            
        }while(calculo >= 1);
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
                
                
                
                y = (y / 3) + BloquesUtiles.NIVEL_MAR ;
 
                for (int a=maxY; a>=minY; a--){ 
                    
                    if (a<=y){
                        tipoTerreno = "Hierba";
                        
                        if (a == y){
                            tipoTerreno = "Hierba";
                            
                            if (a >= BloquesUtiles.NIVEL_MAR && a <= (BloquesUtiles.NIVEL_MAR + 3)){
                                tipoTerreno = "Arena";
                            }
                            
                        }else{
                            if (a < y && (y-a <= 4)){
                                tipoTerreno = "Tierra";
                            }
                            if (a < y && (y-a > 4)){
                                tipoTerreno = "Roca";
                            }
                            if (a == minY){
                                tipoTerreno = "Base";
                            }
                        }

                        BloquesChunkDatos bloqueDatos = new BloquesChunkDatos();
                        bloqueDatos.setNomBloque(tipoTerreno);
                        bloqueDatos.setIrrompible(bloques.bloquesGenericos.getBloqueTipo(tipoTerreno).getIrrompible());
                        
                        if (a == y){
                            bloqueDatos.setMostrar(true);
                        }else{
                            bloqueDatos.setMostrar(false);
                        }

                        chunks.setBloque(new Vector3f(x * BloquesUtiles.TAMANO_BLOQUE, a * BloquesUtiles.TAMANO_BLOQUE, z * BloquesUtiles.TAMANO_BLOQUE), bloqueDatos);
                    }else{
                        chunks.setChunkSiNoExiste(new Vector3f(x * BloquesUtiles.TAMANO_BLOQUE, a * BloquesUtiles.TAMANO_BLOQUE, z * BloquesUtiles.TAMANO_BLOQUE));
                    }
                }
            }
            
            //porcentage generado
            final int xx = x;
            app.enqueue(new Callable() {
                public Object call() throws Exception {
                    porcentajeGenerado =  ((xx * 100) / totalTamano) / 2;
                    return null;
                }
            });
            
        }
        
        int espacioArbol = 32;
        int veces;
        for (x = 0;x<totalTamano;x = x + espacioArbol){
            for (z = 0;z<totalTamano;z = z + espacioArbol){  
                veces = BloquesUtiles.aleatorio(1, 2);
                
                for (int v=1;v<=veces;v++){
                    int arbolX = (BloquesUtiles.aleatorio(1, espacioArbol - 1) + x) * BloquesUtiles.TAMANO_BLOQUE;
                    int arbolZ = (BloquesUtiles.aleatorio(1, espacioArbol - 1) + z) * BloquesUtiles.TAMANO_BLOQUE;

                    int bloqueConMasAltura = chunks.getBloqueConMasAltura(arbolX, arbolZ);

                    if (bloqueConMasAltura > 0){
                        Vector3f bloqueArbol = new Vector3f(arbolX, bloqueConMasAltura - BloquesUtiles.TAMANO_BLOQUE, arbolZ);

                        this.generarArbol(bloqueArbol);
                    }
                }
            }
        }
        
        
        
        BloquesChunk chunkActual;
        BloquesChunkDatos datosBloque;
        String nomDatosBloque;
        Node bloqueClonado;
        
        final int tamano = chunks.getAllChunks().entrySet().size();
        int contador = 0;
        
        for (Map.Entry<String,BloquesChunk> entryChunk : chunks.getAllChunks().entrySet()){                
            chunkActual = entryChunk.getValue();
                        
            if (chunkActual != null){
                for (Map.Entry<String,BloquesChunkDatos> entryBloquesDatos : chunkActual.getAllBloquesDatos().entrySet()){ 
                    datosBloque = entryBloquesDatos.getValue();

                    if (datosBloque != null && datosBloque.getMostrar()){
                        nomDatosBloque = entryBloquesDatos.getKey();

                        bloques.generaBloqueClonado(nomDatosBloque, datosBloque, chunks, false);
                        
                        /** /
                        bloqueClonado = bloques.generaBloqueClonado(nomDatosBloque, datosBloque, chunks, true);
                        if (bloqueClonado != null){
                            chunkActual.setNodo(nomDatosBloque, (Node) bloqueClonado.clone());
                        }
                        /**/
                    }
                }
            }
            
            final int xx = contador;
            app.enqueue(new Callable() {
                public Object call() throws Exception {
                    porcentajeGenerado =  (((xx * 100) / tamano) / 2) + 50;
                    return null;
                }
            });
            
            contador ++;
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
                chunks = new BloquesChunks();
                future = executor.submit(generaTerrenoInicialHilo);
            }
            else if(future != null){
                if(future.isDone()){
                    generandoTerreno = false;
                    future = null;
                    
                    Timer timer = app.getTimer();
                    float totalInicio = timer.getTimeInSeconds();
                    
                    BloquesSaveLoad.saveChunks(chunks);
                    
                    float totalFin = timer.getTimeInSeconds();
                    System.out.println("Guardado Terreno en Disco Duro "+(totalFin-totalInicio));
                    
                    int interrumpe = 9;
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

    }
}

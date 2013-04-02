/*
 * Aqui se generaran los chunks y el terreno en general
 */
package bloques;

import com.jme3.app.Application;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.heightmap.HillHeightMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import jme3tools.optimize.GeometryBatchFactory;

/**
 *
 * @author mcarballo
 */
public class BloqueGeneraTerreno extends BloqueGeneraBloque{
    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(4);
    Future future = null;
    Boolean primeravez = true;
    
    //graficos
    Map<Integer,Spatial> bloquesMostrar=new HashMap<Integer, Spatial>();
    
    
    /**
     *
     * @param app
     */
    public BloqueGeneraTerreno(Application app){
        super(app);
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
     * @throws InterruptedException
     * @throws ExecutionException  
     */
    @SuppressWarnings("SleepWhileInLoop")
    public void generaTerrenoCompleto() throws InterruptedException, ExecutionException{
        Map<String,Node> bloquesGenerados = new HashMap<String,Node>();
        
        //TODO material con iluminacion
        
        //tierra
        BloquesDatos bloquesDatos = bloques.getBloqueTipo("Tierra");
        
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setTexture("ColorMap", atlas.getAtlasTexture(bloquesDatos.getNombreTextura()));    
        mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha); //transparencia
        
        BloqueGeneraBloque generaBloque = new BloqueGeneraBloque(app);
        Node bloque = generaBloque.makeBloque(1,"Tierra");
        
        bloquesGenerados.put("Tierra",bloque);
        
        //roca
        bloquesDatos = bloques.getBloqueTipo("Roca");
        
        mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setTexture("ColorMap", atlas.getAtlasTexture(bloquesDatos.getNombreTextura()));    
        mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha); //transparencia
        
        generaBloque = new BloqueGeneraBloque(app);
        bloque = generaBloque.makeBloque(1,"Roca");
        
        bloquesGenerados.put("Roca",bloque);
        
        
        HillHeightMap heightmap = generateTerrainconReturn();
        
        int conta = 0;
        int y = 0;
        int minY = 0;
        
        int contaBloquesActual = 0;
        int contaBuffer = 0;
        
        for (int x = 0;x<300;x++){
            for (int z = 0;z<300;z++){
                int originalY = y;
                
                y = (int) heightmap.getScaledHeightAtPoint(x,z);
                
                if (y == 0){
                    y = originalY;
                }
                
                //minY = y; //sin relleno;
                
                for (int a=y; a>=minY; a--){    
                    final Spatial bloqueClonado;
                    if (conta == 0){ //tierra
                        bloqueClonado = bloquesGenerados.get("Tierra").clone();
                        conta = 1;
                    }else{ //roca
                        bloqueClonado = bloquesGenerados.get("Roca").clone();
                        conta = 0;
                    }
                    
                    bloqueClonado.setMaterial(mat1);
                    
                    bloqueClonado.move(x,a,z);
                    
                    final int contaBloquesActualFinal = contaBloquesActual;
                    
                    contaBloquesActual++;
                    contaBuffer++;
                    
                    app.enqueue(new Callable() {
                        public Object call() throws Exception {
                            bloquesMostrar.put(contaBloquesActualFinal, bloqueClonado);
                            return null;
                        }
                    });
                    
                    
                    /**/
                    if (contaBuffer > 1000){
                        contaBuffer = 0;
                        //this.rootNode.attachChild(buffer);
                        //buffer = new Node("buffer");

                        int cuentaClaves;

                        int contaBucles = 0;

                        do {
                           Thread.sleep(100);
                           contaBucles += 1;

                           cuentaClaves = app.enqueue(new Callable<Integer>() {
                              public Integer call() throws Exception {
                                  Integer[] keys = (Integer[])( bloquesMostrar.keySet().toArray( new Integer[bloquesMostrar.size()] ) );
                                  return keys.length;
                              }
                          }).get();
                        } while(cuentaClaves > 0 && contaBucles < 5);

                    }
                    /**/
                    
                    //bloquesFinales.attachChild(bloqueClonado);
                }
            }
        }
        
        //bloquesFinales.setMaterial(mat1);   
    }
    
    // A self-contained time-intensive task:
    private Callable<Boolean> generaTerrenoHilo = new Callable<Boolean>(){
        public Boolean call() throws Exception {
            //BloqueGeneraTerreno bloqueGeneraTerreno = new BloqueGeneraTerreno(app);
            generaTerrenoCompleto();
            
            return false;
        }
    };
    
    /**
     *
     * @return
     */
    public Spatial generaTerreno(){
        //TODO - esto deberia hacer en una libreria para los graficos, no las librerias de bloques
        try{
            if(future == null && primeravez){
                future = executor.submit(generaTerrenoHilo);
            }
            else if(future != null){
                if(future.isDone()){
                    //Boolean devuelto = (Boolean) future.get();
                    future = null;
                }
                else if(future.isCancelled()){
                    future = null;
                }
            }
        } 
        catch(Exception e){ 

        }
        
        primeravez = false;
        
        Node bloquesAcumulados = new Node("bloquesAcumulados");
        
        Integer[] keys = (Integer[])( bloquesMostrar.keySet().toArray( new Integer[bloquesMostrar.size()] ) );
        
        if (keys.length > 0){
        
            for(int i=0; i<keys.length; i++){
                int claveActual = keys[i];
                Spatial cuboActual = bloquesMostrar.get(claveActual);

                bloquesAcumulados.attachChild(cuboActual);

                bloquesMostrar.remove(claveActual);
            }

            Spatial optimizado = GeometryBatchFactory.optimize(bloquesAcumulados);
        
            return optimizado;
        }else{
            return null;
        }
    }
    
    /**
     *
     */
    public void destroy() {
        //asi parece que cierra bien
        //el problema esta en el do while con el sleep de hilo
        executor.shutdownNow();
        executor.shutdown();
    }
}

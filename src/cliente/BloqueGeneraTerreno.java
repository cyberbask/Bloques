/*
 * Aqui se generaran los chunks y el terreno en general
 */
package cliente;

import bloques.BloquesDatos;
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
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 *
 * @author mcarballo
 */
public class BloqueGeneraTerreno extends BloqueGeneraBloque{
    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(4);
    Future future = null;
    Boolean primeravez = true;
    
    
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
            heightmap = new HillHeightMap(513, 1000, 20, 40, result[0]); // byte 3 is a random seed
        } catch (Exception ex) {

        }
        //heightmap.setHeightScale(0.001f);
        heightmap.load();

        return heightmap;     
    }
        
    /**
     *
     * @return
     */
    public void generaTerrenoCompleto(){
        Node bloquesFinales = new Node("bloques");
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
        int minY = -5;
        for (int x = 0;x<10;x++){
            for (int z = 0;z<10;z++){
                int originalY = y;
                
                y = (int) heightmap.getScaledHeightAtPoint(x,z);
                
                if (y == 0){
                    y = originalY;
                }
                
                for (int a=y; a>=minY; a--){    
                    Spatial bloqueClonado;
                    if (conta == 0){ //tierra
                        bloqueClonado = bloquesGenerados.get("Tierra").clone();
                        conta = 1;
                    }else{ //roca
                        bloqueClonado = bloquesGenerados.get("Roca").clone();
                        conta = 0;
                    }

                    bloqueClonado.move(x,a,z);

                    /*this.enqueue(new Callable() {
                        public Object call() throws Exception {
                            mapaCajitas.put(contaCajasActual, cubo);
                            return null;
                        }
                    });*/
                    
                    //bloquesFinales.attachChild(bloqueClonado);
                }
            }
        }
        
        bloquesFinales.setMaterial(mat1);   
    }
    
    // A self-contained time-intensive task:
    private Callable<Boolean> generaTerrenoHilo = new Callable<Boolean>(){
        public Boolean call() throws Exception {
            BloqueGeneraTerreno bloqueGeneraTerreno = new BloqueGeneraTerreno(app);
            bloqueGeneraTerreno.generaTerrenoCompleto();
            
            return false;
        }
    };
    
    public void generaTerreno(){
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
    }
    
    public void destroy() {
        executor.shutdown();
    }
}

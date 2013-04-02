/*
 * Clase para manejar los graficos del juego principal, generar chunks,
 * mostrarlos, quitarlos ...
 */
package cliente;

import bloques.BloqueChunk;
import bloques.BloqueChunkDatos;
import bloques.BloqueChunkUtiles;
import bloques.BloqueChunks;
import bloques.BloqueGeneraTerreno;
import bloques.BloqueGenericosDatos;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import jme3tools.optimize.GeometryBatchFactory;

/**
 *
 * @author mcarballo
 */
public class GraficosJuego {
    private SimpleApplication app;
    private Node              rootNode;
    private AssetManager      assetManager;
    private AppStateManager   stateManager;
    private InputManager      inputManager;
    private ViewPort          viewPort;
    private BulletAppState    physics;
    
    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(4);
    Future future = null;
    protected boolean generandoGraficos = false;
    
    //graficos
    Map<Integer,BloqueChunks> updates=new HashMap<Integer, BloqueChunks>();
    int contadorUpdates = 0;
    
    //clase para manejo de bloques
    BloqueGeneraJuego bloques;
    
    
    /**
     * Objeto que contiene todo el procesado de posiciones y bloques
     */
    protected BloqueGeneraTerreno bloqueGeneraTerreno;
    
    /**
     * Constructor
     * @param app
     */
    public GraficosJuego(Application app){
        this.app = (SimpleApplication) app;
        this.rootNode     = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.viewPort     = this.app.getViewPort();
        this.physics      = this.stateManager.getState(BulletAppState.class);
        
        bloqueGeneraTerreno = new BloqueGeneraTerreno(app);
        
        bloques = new BloqueGeneraJuego(app);
    }
    
    public void updateaRootNode() throws InterruptedException, ExecutionException{
        //TODO - Clase para Materiales
        
        Map<String,Node> bloquesGenerados = new HashMap<String,Node>();
        
        //tierra
        BloqueGenericosDatos bloquesDatos = bloques.bloquesGenericos.getBloqueTipo("Tierra");
        
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setTexture("ColorMap", bloques.atlas.getAtlasTexture(bloquesDatos.getNombreTextura()));    
        mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha); //transparencia
        
        BloqueGeneraJuego generaBloque = new BloqueGeneraJuego(app);
        Node bloque = bloques.makeBloque(1,"Tierra");
        
        bloquesGenerados.put("Tierra",bloque);
        
        //roca
        bloquesDatos = bloques.bloquesGenericos.getBloqueTipo("Roca");
        
        mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setTexture("ColorMap", bloques.atlas.getAtlasTexture(bloquesDatos.getNombreTextura()));    
        mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha); //transparencia
        
        generaBloque = new BloqueGeneraJuego(app);
        bloque = generaBloque.makeBloque(1,"Roca");
        
        bloquesGenerados.put("Roca",bloque);
        
        
        //sacamos el array de chunks a updatar
        Map updatear = app.enqueue(new Callable<Map<Integer,BloqueChunks>>() {
            public Map<Integer,BloqueChunks> call() throws Exception {
                return updates;
            }
        }).get();
        
        //recorremos el array
        Integer[] keys = (Integer[])( updatear.keySet().toArray( new Integer[updatear.size()] ) );    
        if (keys.length > 0){
            for(int i=0; i<keys.length; i++){
                int claveActual = keys[i];
                
                BloqueChunks updatando = (BloqueChunks) updatear.get(claveActual);
                
                //sacamos todos los chunks a updatar y lo recorremos chunk a chunk
                Map<String, BloqueChunk> allChunks = updatando.getAllChunks();
                String[] keysAllChunks = (String[])( allChunks.keySet().toArray( new String[allChunks.size()] ) );
                if (keysAllChunks.length > 0){
                    for(int j=0; j<keysAllChunks.length; j++){
                        String claveActualAllChunks = keysAllChunks[j];
                        
                        BloqueChunk chunkActual = allChunks.get(claveActualAllChunks);
                        
                        int tamano = BloqueChunkUtiles.TAMANO_CHUNK;
                        
                        Node bloquesMostrar = new Node(claveActualAllChunks);
                        
                        System.out.println(claveActualAllChunks);
                        
                        int mostrar = 0;
                        
                        for(int x = 0;x<tamano;x++){
                            for(int y = 0;y<tamano;y++){
                                for(int z = 0;z<tamano;z++){
                                    BloqueChunkDatos datosBloque = chunkActual.getDatosBloque(x, y, z);
                                    
                                    if (datosBloque != null){
                                        Spatial bloqueClonado;
                                        bloqueClonado = bloquesGenerados.get(datosBloque.getNomBloque()).clone();

                                        bloqueClonado.setMaterial(mat1);
                                        //se le pasan las coordenadas reales del cubo, no las del chunk
                                        int[] coordenadas = BloqueChunkUtiles.calculaCoordenadasBloqueAPartirDeChunk(claveActualAllChunks, x, y, z);
                                        bloqueClonado.move(coordenadas[0],coordenadas[1],coordenadas[2]);          

                                        bloquesMostrar.attachChild(bloqueClonado);
                                        
                                        mostrar = 1;
                                    }
                                }
                            }
                        }
                        
                        if (mostrar == 1){
                            final Spatial optimizado = GeometryBatchFactory.optimize(bloquesMostrar);
                            
                            app.enqueue(new Callable() {
                                public Object call() throws Exception {
                                    rootNode.attachChild(optimizado);
                                    return null;
                                }
                            });
                            
                        }
                    }
                }
                
                updatear.remove(claveActual);
            }
        }
        
    }
    
    // A self-contained time-intensive task:
    private Callable<Boolean> generaGraficos = new Callable<Boolean>(){
        public Boolean call() throws Exception {
            updateaRootNode();
            
            return false;
        }
    };
    
    /**
     * Esta funcion se llamara en el update del AppState
     */
    public void generarTerreno(){
        /*Spatial devueltoGeneraTerreno = bloqueGeneraTerreno.generaTerreno();
        if (devueltoGeneraTerreno != null) {
        rootNode.attachChild(devueltoGeneraTerreno);
        }*/
        
        Map<Integer, BloqueChunks> terrenoGenerado = bloqueGeneraTerreno.generaTerreno();
           
        if (terrenoGenerado != null){
            
            
            Integer[] keys = (Integer[])( terrenoGenerado.keySet().toArray( new Integer[terrenoGenerado.size()] ) );
            
            if (keys.length > 0){
                for(int i=0; i<keys.length; i++){
                    int claveActual = keys[i];
                    updates.put(contadorUpdates, terrenoGenerado.get(claveActual));
                    contadorUpdates++;
                }
            }
            
            int yo=0;
        }
        
        try{
            if(future == null && !generandoGraficos){
                generandoGraficos = true;
                future = executor.submit(generaGraficos);
            }
            else if(future != null){
                if(future.isDone()){
                    generandoGraficos = false;
                    future = null;
                }
                else if(future.isCancelled()){
                    generandoGraficos = false;
                    future = null;
                }
            }
        } 
        catch(Exception e){ 

        }
    }
    
    /**
     *
     */
    public void destroy() {
        bloqueGeneraTerreno.destroy(); //lo ejecutamos para cerrar los hilos que pueda haber abiertos
        executor.shutdown();
        executor.shutdownNow();
    }
}

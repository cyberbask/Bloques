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
    GeneraBloqueJuego bloques;
    
    
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
        
        bloques = new GeneraBloqueJuego(app);
    }
    
    @SuppressWarnings("SleepWhileInLoop")
    public void updateaRootNode() throws InterruptedException, ExecutionException{
        //TODO - Clase para Materiales
        
        //sacamos el array de chunks a updatar
        Map updatear = app.enqueue(new Callable<Map<Integer,BloqueChunks>>() {
            public Map<Integer,BloqueChunks> call() throws Exception {
                return bloqueGeneraTerreno.getUpdates(true);
            }
        }).get();
        
        //recorremos el array
        Integer[] keys = (Integer[])( updatear.keySet().toArray( new Integer[updatear.size()] ) ); 
        
        if (keys.length > 0){
            
            Map<String,Node> bloquesGenerados = new HashMap<String,Node>();
        
            //tierra
            BloqueGenericosDatos bloquesDatos = bloques.bloquesGenericos.getBloqueTipo("Tierra");

            Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat1.setTexture("ColorMap", bloques.atlas.getAtlasTexture(bloquesDatos.getNombreTextura()));    
            mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha); //transparencia

            GeneraBloqueJuego generaBloque = new GeneraBloqueJuego(app);
            Node bloque = bloques.makeBloque(1,"Tierra");

            bloquesGenerados.put("Tierra",bloque);

            //roca
            bloquesDatos = bloques.bloquesGenericos.getBloqueTipo("Roca");

            mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat1.setTexture("ColorMap", bloques.atlas.getAtlasTexture(bloquesDatos.getNombreTextura()));    
            mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha); //transparencia

            generaBloque = new GeneraBloqueJuego(app);
            bloque = generaBloque.makeBloque(1,"Roca");

            bloquesGenerados.put("Roca",bloque);
            
            
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

                        System.out.println("chunk "+claveActualAllChunks);

                        int mostrar = 0;

                        for(int x = 0;x<tamano;x++){
                            for(int y = 0;y<tamano;y++){
                                for(int z = 0;z<tamano;z++){
                                    BloqueChunkDatos datosBloque = chunkActual.getDatosBloque(x, y, z);

                                    if (datosBloque != null){
                                        Node bloqueClonado;
                                        bloqueClonado = (Node) bloquesGenerados.get(datosBloque.getNomBloque()).clone();

                                        //coordenadas reales del cubo, no las del chunk
                                        final int[] coordenadas = BloqueChunkUtiles.calculaCoordenadasBloqueAPartirDeChunk(claveActualAllChunks, x, y, z);

                                        //le quitamos las caras que no se ven
                                        int contaCarasQuitadas = 0;
                                        int[] bloquesVecinos = bloqueGeneraTerreno.chunks.getBloquesVecinos(coordenadas[0],coordenadas[1],coordenadas[2]);
                                        for(int h = 0;h<6;h++){
                                            if (bloquesVecinos[h] == 1){ //si hay vecino
                                                bloqueClonado.detachChildNamed("Cara-"+h); 
                                                contaCarasQuitadas++;
                                            }
                                        }

                                        if (contaCarasQuitadas < 6){
                                            bloqueClonado.move(coordenadas[0],coordenadas[1],coordenadas[2]); 

                                            bloqueClonado.setMaterial(mat1);

                                            bloquesMostrar.attachChild(bloqueClonado);

                                            mostrar = 1;
                                        }
                                    }
                                }
                            }
                        }

                        System.out.println("chunk "+claveActualAllChunks+" Terminado");

                        if (mostrar == 1){
                            //bloquesMostrar.setMaterial(mat1);

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
                Thread.sleep(10);
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
        bloqueGeneraTerreno.generaTerreno();
        
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

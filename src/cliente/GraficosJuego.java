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
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.InputManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.TangentBinormalGenerator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import jme3tools.optimize.GeometryBatchFactory;
import personaje.Personaje;

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
    private Camera       cam;
    
    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(4);
    Future future = null;
    /**
     *
     */
    protected boolean generandoGraficos = false;
    
    //Niebla
    FilterPostProcessor fogPPS;
    FogFilter fog;
    
    //graficos
    Map<Integer,BloqueChunks> updates=new HashMap<Integer, BloqueChunks>();
    int contadorUpdates = 0;
    
    //clase para manejo de bloques
    GeneraBloqueJuego bloques;
   
    /**
     * Objeto que contiene todo el procesado de posiciones y bloques
     */
    protected BloqueGeneraTerreno bloqueGeneraTerreno;
    
    //Personaje
    public Personaje personaje;
    
    //variable para controlar si posicionamos la camara
    //o activamos el personaje
    int posicionarCamara = 0;  
    int bloqueConMasAltura; //ñapa para posiconar al personaje
    Boolean terrenoInicialCargado = false;
    
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
        this.cam          = this.app.getCamera();
        
        bloqueGeneraTerreno = new BloqueGeneraTerreno(app);
        
        bloques = new GeneraBloqueJuego(app);
        
        personaje = new Personaje(app);
           
        //cambiamos el color del fondo
        //viewPort.setBackgroundColor(new ColorRGBA(0.7f,0.8f,1f,1f));
        viewPort.setBackgroundColor(ColorRGBA.Blue);
        
        //Luces basicas
        setUpLight();
        
        //Niebla
        setUpFog();
    }
    
    private void setUpLight() {
        // We add light so we see the scene
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.6f));
        rootNode.addLight(al);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White.mult(0.5f));
        dl.setDirection(new Vector3f(10,0,-1).normalizeLocal());
        //dl.setDirection(new Vector3f(50f, -50f, -50f).normalizeLocal());
        //dl.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal());
        rootNode.addLight(dl);
    }
    
    private void setUpFog(){
        fogPPS=new FilterPostProcessor(assetManager);
        fog = new FogFilter(ColorRGBA.White, 1.25f, 6000f);
        fogPPS.addFilter(fog);
        viewPort.addProcessor(fogPPS);
    }
    
    /**
     *
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @SuppressWarnings("SleepWhileInLoop")
    public void updateaRootNode() throws InterruptedException, ExecutionException{
        //TODO - los calculos de bloques vecinos y similares quizan deberian hacerse antes.
        
        //sacamos el array de chunks a updatar
        Map updatear = app.enqueue(new Callable<Map<Integer,BloqueChunks>>() {
            public Map<Integer,BloqueChunks> call() throws Exception {
                return bloqueGeneraTerreno.getUpdates(true);
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

                        //System.out.println("chunk "+claveActualAllChunks);

                        int mostrar = 0;

                        for(int x = 0;x<tamano;x++){
                            for(int y = 0;y<tamano;y++){
                                for(int z = 0;z<tamano;z++){
                                    BloqueChunkDatos datosBloque = chunkActual.getDatosBloque(x, y, z);

                                    if (datosBloque != null){
                                        Node bloqueClonado;
                                        bloqueClonado = bloques.getBloqueGenerado(datosBloque.getNomBloque());
                                        
                                        //coordenadas reales del cubo, no las del chunk
                                        final int[] coordenadas = BloqueChunkUtiles.calculaCoordenadasBloqueAPartirDeChunk(claveActualAllChunks, x * BloqueChunkUtiles.TAMANO_BLOQUE, y  * BloqueChunkUtiles.TAMANO_BLOQUE, z * BloqueChunkUtiles.TAMANO_BLOQUE);

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
                                            
                                            TangentBinormalGenerator.generate(bloqueClonado);
                                            
                                            bloquesMostrar.attachChild(bloqueClonado);

                                            mostrar = 1;
                                        }
                                    }
                                }
                            }
                        }

                        //System.out.println("chunk "+claveActualAllChunks+" Terminado");

                        if (mostrar == 1){
                            final Spatial optimizado = GeometryBatchFactory.optimize(bloquesMostrar);

                            app.enqueue(new Callable() {
                                public Object call() throws Exception {
                                    CollisionShape bloquesMostrarShape = CollisionShapeFactory.createMeshShape((Node) optimizado);
                                    RigidBodyControl bloquesMostrarControl = new RigidBodyControl(bloquesMostrarShape, 0);
                                    optimizado.addControl(bloquesMostrarControl);
                                    
                                    physics.getPhysicsSpace().add(optimizado);
                                    
                                    rootNode.attachChild(optimizado);
                                    
                                    terrenoInicialCargado = true;
                                    
                                    return null;
                                }
                            });

                        }
                    }
                }
                //Thread.sleep(10);
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
    public void update(float tpf){        
        bloqueGeneraTerreno.generaTerreno();
        
        if (posicionarCamara == 1 && terrenoInicialCargado){
            //Añadimos el personaje
            personaje.generaPersonaje(10,bloqueConMasAltura,10);
            posicionarCamara = 2;
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
                    
                    if (posicionarCamara == 0){
                        bloqueConMasAltura = bloqueGeneraTerreno.chunks.getBloqueConMasAltura(20, 20);
                        
                        if (bloqueConMasAltura > 0){           
                            /**/
                            bloqueConMasAltura = bloqueConMasAltura + (10 * BloqueChunkUtiles.TAMANO_BLOQUE * 2);
                            cam.setLocation(new Vector3f(10, bloqueConMasAltura, 10));
                            cam.setRotation(new Quaternion().fromAngleAxis(90*FastMath.DEG_TO_RAD, new Vector3f(0,1,0)));
                            cam.update();
                            cam.setRotation(new Quaternion().fromAngleAxis(60*FastMath.DEG_TO_RAD, new Vector3f(0,1,0)));
                            cam.update();
                            /**/
                            posicionarCamara = 1;
                        }
                    }
                }
                else if(future.isCancelled()){
                    generandoGraficos = false;
                    future = null;
                }
            }
        } 
        catch(Exception e){ 

        }
        
        personaje.update(tpf);
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

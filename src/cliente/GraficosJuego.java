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
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.Timer;
import com.jme3.util.SkyFactory;
import com.jme3.util.TangentBinormalGenerator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
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
    Future futureChunk = null;
    /**
     *
     */
    protected boolean generandoGraficos = false;
    
    //Niebla
    FilterPostProcessor fogPPS;
    FogFilter fog;
    
    //graficos
    BloqueChunks chunks;
    //aqui meteremos los updates del terreno nuevo que van sin prisa
    public Map<Integer,BloqueChunks> updates=new HashMap<Integer, BloqueChunks>();
    //aqui meteremos los updates urgentes de cambios en los chunks, nuevos bloques ...
    public Map<Integer,BloqueChunks> updateChunk=new HashMap<Integer, BloqueChunks>();
    public int contadorUpdates = 0;
    public int contadorUpdatesChunk = 0;
    
    //clase para manejo de bloques
    GeneraBloqueJuego bloques;
   
    /**
     * Objeto que contiene todo el procesado de posiciones y bloques
     */
    protected BloqueGeneraTerreno bloqueGeneraTerreno;
    
    //Personaje
    /**
     *
     */
    public Personaje personaje;
    
    StateJuegoGui juegoGui;
    
    //variable para controlar si posicionamos la camara
    //o activamos el personaje
    int posicionarCamara = 0;  
    int bloqueConMasAltura; //ñapa para posiconar al personaje
    Boolean terrenoInicialCargado = false;
    
    //primera carga
    Boolean primeraCarga = true;
    
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
        
        juegoGui = new StateJuegoGui(app);
        juegoGui.initPuntoMira();
           
        //cambiamos el color del fondo
        //viewPort.setBackgroundColor(new ColorRGBA(0.7f,0.8f,1f,1f));
        viewPort.setBackgroundColor(ColorRGBA.Blue);
        
        //Sombras Basicas
        setUpShadows();
        
        //Luces basicas
        setUpLight();
        
        //Niebla
        setUpFog();
        
        //Cielo
        //setUpSky();
        
        chunks = null;
    }
    
    private void setUpLight() {
        // We add light so we see the scene
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.6f));
        rootNode.addLight(al);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White.mult(0.5f));
        dl.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal());
        //dl.setDirection(new Vector3f(50f, -50f, -50f).normalizeLocal());
        //dl.setDirection(new Vector3f(-5f,-5f,-5f).normalizeLocal());
        rootNode.addLight(dl);
    }
    
    private void setUpShadows(){
        rootNode.setShadowMode(ShadowMode.Off);
        
        /** /
        BasicShadowRenderer bsr = new BasicShadowRenderer(assetManager, 512);
        bsr.setDirection(new Vector3f(-.5f, -1f, -.5f).normalizeLocal()); // light direction
        viewPort.addProcessor(bsr);
        /**/
        
        /** /
        PssmShadowRenderer pssmRenderer = new PssmShadowRenderer(assetManager, 512, 1);
        pssmRenderer.setDirection(new Vector3f(-.5f, -1f, -.5f).normalizeLocal()); // light direction
        pssmRenderer.setShadowIntensity(0.2f);
        pssmRenderer.setEdgesThickness(100);
        pssmRenderer.setFilterMode(FilterMode.Bilinear);
        viewPort.addProcessor(pssmRenderer);
        /**/
        
        /** /
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        SSAOFilter ssaoFilter = new SSAOFilter(12.94f, 43.92f, 0.33f, 0.61f);
        fpp.addFilter(ssaoFilter);
        viewPort.addProcessor(fpp);
        /**/
    }
    
    private void setUpFog(){
        fogPPS=new FilterPostProcessor(assetManager);
        fog = new FogFilter(ColorRGBA.White, 1f, 2000f);
        fogPPS.addFilter(fog);
        viewPort.addProcessor(fogPPS);
    }
    
    private void setUpSky(){
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/skybox_blue_sphere.jpg", true));
    }
    
    /**
     *
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @SuppressWarnings("SleepWhileInLoop")
    public void updateaRootNode(int tipo) throws InterruptedException, ExecutionException{
        Timer timer = app.getTimer();
        float totalInicio = timer.getTimeInSeconds();
       
        //TODO - los calculos de bloques vecinos y similares quizan deberian hacerse antes.
        
        //sacamos el array de chunks a updatar
        Map updatear = null;
        if (tipo == 1){
            updatear = app.enqueue(new Callable<Map<Integer,BloqueChunks>>() {
                public Map<Integer,BloqueChunks> call() throws Exception {
                    return getUpdates(true);
                }
            }).get();
        }
        if (tipo == 2){
            updatear = app.enqueue(new Callable<Map<Integer,BloqueChunks>>() {
                public Map<Integer,BloqueChunks> call() throws Exception {
                    return getUpdatesUrgentes(true);
                }
            }).get();
        }
        
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
                        
                        float inicio = timer.getTimeInSeconds();
                        
                        BloqueChunk chunkActual = allChunks.get(claveActualAllChunks);
  
                        int tamano = BloqueChunkUtiles.TAMANO_CHUNK;

                        Node bloquesMostrar = new Node("Chunk: "+claveActualAllChunks);

                        //System.out.println("chunk "+claveActualAllChunks);

                        int mostrar = 0;

                        for(int x = 0;x<tamano;x++){
                            for(int y = 0;y<tamano;y++){
                                for(int z = 0;z<tamano;z++){
                                    BloqueChunkDatos datosBloque = chunkActual.getDatosBloque(x, y, z);

                                    if (datosBloque != null){
                                        Node bloqueClonado;
                                        bloqueClonado = bloques.getBloqueGenerado(datosBloque.getNomBloque());
                                        bloqueClonado.setName("Chunk:::"+claveActualAllChunks);
                                        
                                        //coordenadas reales del cubo, no las del chunk
                                        final int[] coordenadas = BloqueChunkUtiles.calculaCoordenadasBloqueAPartirDeChunk(claveActualAllChunks, x * BloqueChunkUtiles.TAMANO_BLOQUE, y  * BloqueChunkUtiles.TAMANO_BLOQUE, z * BloqueChunkUtiles.TAMANO_BLOQUE);

                                        //le quitamos las caras que no se ven
                                        int contaCarasQuitadas = 0;
                                        int[] bloquesVecinos = chunks.getBloquesVecinos(coordenadas[0],coordenadas[1],coordenadas[2]);
                                        for(int h = 0;h<6;h++){
                                            if (bloquesVecinos[h] == 1){ //si hay vecino
                                                bloqueClonado.detachChildNamed("Cara-"+h); 
                                                contaCarasQuitadas++;
                                            }
                                        }
                                        
                                        datosBloque.setCaras(bloquesVecinos);

                                        if (contaCarasQuitadas < 6){
                                            bloqueClonado.move(coordenadas[0],coordenadas[1],coordenadas[2] + BloqueChunkUtiles.TAMANO_BLOQUE);
                                            
                                            //TangentBinormalGenerator.generate(bloqueClonado);
                                            //bloqueClonado.setShadowMode(ShadowMode.CastAndReceive);
                                            
                                            bloquesMostrar.attachChild(bloqueClonado);

                                            mostrar = 1;
                                        }
                                    }
                                }
                            }
                        }
                        
                        float fin = timer.getTimeInSeconds();
                        
                        //System.out.println("Tiempo chunk "+claveActualAllChunks+" "+(fin-inicio));
                        //System.out.println("chunk "+claveActualAllChunks+" Terminado");

                        if (mostrar == 1){
                            final Spatial optimizado = GeometryBatchFactory.optimize(bloquesMostrar);
                            final int tipoFinal = tipo;
                            final String claveActualAllChunksFinal = claveActualAllChunks;

                            app.enqueue(new Callable() {
                                public Object call() throws Exception {
                                    if (tipoFinal == 2){
                                        Spatial child = rootNode.getChild("Chunk: "+claveActualAllChunksFinal);
                                        physics.getPhysicsSpace().remove(child.getControl(0)) ; 
                                        rootNode.detachChildNamed("Chunk: "+claveActualAllChunksFinal);
                                    }
                                    
                                    CollisionShape bloquesMostrarShape = CollisionShapeFactory.createMeshShape((Node) optimizado);
                                    RigidBodyControl bloquesMostrarControl = new RigidBodyControl(bloquesMostrarShape, 0);
                                    optimizado.addControl(bloquesMostrarControl);
                                    
                                    physics.getPhysicsSpace().add(optimizado);
                                    
                                    rootNode.attachChild(optimizado);
                                    
                                    TangentBinormalGenerator.generate(optimizado);
                                    optimizado.setShadowMode(ShadowMode.CastAndReceive);
                                    
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
        
        float totalFin = timer.getTimeInSeconds();
        //System.out.println("Tiempo update"+(totalFin-totalInicio));
        
    }
    
    // A self-contained time-intensive task:
    private Callable<Boolean> procesaGraficosUpdates = new Callable<Boolean>(){
        public Boolean call() throws Exception {
            updateaRootNode(1); //1 es para los updates lentos del terreno
            return false;
        }
    };
    
    // A self-contained time-intensive task:
    private Callable<Boolean> procesaGraficosUpdatesUrgentes = new Callable<Boolean>(){
        public Boolean call() throws Exception {
            updateaRootNode(2); //2 es para los rapidos, destruir bloques por ejemplo
            return false;
        }
    };
    
    /**
     * Esta funcion se llamara en el update del AppState
     * @param tpf 
     */
    public void update(float tpf){  
        //las siguientes veces que se hace update se actualizan los chunks
        if (!primeraCarga){
            try{
                if(future == null && !generandoGraficos){
                    generandoGraficos = true;
                    future = executor.submit(procesaGraficosUpdates);
                }
                else if(future != null){
                    if (posicionarCamara == 0){
                        if (bloqueConMasAltura > 0){ 
                            String nombreChunk = BloqueChunkUtiles.generarNombreChunk(BloqueChunkUtiles.calculaCoordenadasChunk(20, bloqueConMasAltura , 20));
                            if (rootNode.getChild("Chunk: "+nombreChunk) != null){
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
            
            //updatemos los chunks urgentes
            try{
                if(futureChunk == null){
                    futureChunk = executor.submit(procesaGraficosUpdatesUrgentes);
                }
                else if(futureChunk != null){
                    if(futureChunk.isDone()){
                        futureChunk = null;
                    }
                    else if(futureChunk.isCancelled()){
                        futureChunk = null;
                    }
                }
            } 
            catch(Exception e){ 

            }
            
            personaje.update(tpf);
        }
        
        //la primera vez que se entra aqui se genera el terreno
        if (primeraCarga){
            juegoGui.textoEnPantalla("... Generando Terreno por primera vez ("+bloqueGeneraTerreno.porcentajeGenerado+"%)... ");
            
            bloqueGeneraTerreno.generaTerreno();
            
            //ya termino de generar el terreno
            if (!bloqueGeneraTerreno.generandoTerreno){
                juegoGui.textoEnPantalla("");
                
                chunks = bloqueGeneraTerreno.getChunks();
                             
                bloqueConMasAltura = chunks.getBloqueConMasAltura(20, 20);
                
                //tenemos que pasar a updates estos chunks
                for (int x = 0;x<bloqueGeneraTerreno.totalTamano;x = x + BloqueChunkUtiles.TAMANO_CHUNK){
                    for (int z = 0;z<bloqueGeneraTerreno.totalTamano;z = z + BloqueChunkUtiles.TAMANO_CHUNK){
                        BloqueChunks grupoChunks = chunks.getGrupoChunks(x * BloqueChunkUtiles.TAMANO_BLOQUE, z * BloqueChunkUtiles.TAMANO_BLOQUE);
                        updates.put(contadorUpdates,grupoChunks);
                        contadorUpdates++;
                    }
                }
                
                bloqueGeneraTerreno.vaciaChunks();
                        
                primeraCarga = false;   
            }
        }
        
        if (posicionarCamara == 1 && terrenoInicialCargado){
            //Añadimos el personaje
            personaje.generaPersonaje(10,bloqueConMasAltura,10);
            posicionarCamara = 2;
        }
    }
    
    public Map<Integer,BloqueChunks> getUpdates(Boolean vaciarUpdate){
        if (vaciarUpdate){
            if (updates == null){
                return null;
            }
            
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
            //quiza se use mas adelante
            return null;
        }
    }
    
    public Map<Integer,BloqueChunks> getUpdatesUrgentes(Boolean vaciarUpdate){
        if (vaciarUpdate){
            if (updateChunk == null){
                return null;
            }
            
            Map<Integer,BloqueChunks> updatesCopia = new HashMap<Integer, BloqueChunks>();

            Boolean hayDatos = false;
            
            SortedSet<Integer> keys = new TreeSet<Integer>(updateChunk.keySet());
            for (Integer key : keys) { 
                updatesCopia.put(key,updateChunk.get(key));
                updateChunk.remove(key);
                hayDatos = true;
            }
            
            if (hayDatos){
                return updatesCopia;
            }else{
               return null;
            }
        }else{
            //quiza se use mas adelante
            return null;
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

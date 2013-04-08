/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import bloques.BloqueGeneraTerreno;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.util.SkyFactory;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import personaje.Personaje;
import utiles.Colision;

/**
 *
 * @author cyberbask
 */
public class GraficosJuegosSetUp {
    /**
     *
     */
    protected ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(4);
    /**
     *
     */
    protected Future future = null;
    /**
     *
     */
    protected Future futureChunkUrgentes = null;
    
    /**
     *
     */
    protected SimpleApplication app;
    /**
     *
     */
    protected Node              rootNode;
    /**
     *
     */
    protected AssetManager      assetManager;
    /**
     *
     */
    protected AppStateManager   stateManager;
    /**
     *
     */
    protected InputManager      inputManager;
    /**
     *
     */
    protected ViewPort          viewPort;
    /**
     *
     */
    protected BulletAppState    physics;
    /**
     *
     */
    protected Camera       cam;
    
    /**
     * Objeto que contiene todo el procesado de posiciones y bloques
     */
    protected BloqueGeneraTerreno bloqueGeneraTerreno;
    
    //clase para manejo de bloques
    GeneraBloqueJuego bloques;
    
    //Personaje
    /**
     *
     */
    public Personaje personaje;
    
    StateJuegoGui juegoGui;
   
    //colision
    /**
     *
     */
    public Colision colision;
    
    /**
     *
     * @param app
     */
    public GraficosJuegosSetUp(Application app){
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
        viewPort.setBackgroundColor(new ColorRGBA(0.7f,0.8f,1f,1f));
        //viewPort.setBackgroundColor(ColorRGBA.Blue);
        
        //colisiones
        colision = new Colision(app);
        
        //Sombras Basicas
        setUpShadows();
        
        //Luces basicas
        setUpLight();
        
        //Niebla
        setUpFog();
        
        //Cielo
        //setUpSky();
       
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
        rootNode.setShadowMode(RenderQueue.ShadowMode.Off);
        
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
        FilterPostProcessor fogPPS=new FilterPostProcessor(assetManager);
        FogFilter fog = new FogFilter(ColorRGBA.White, 0.6f, 1000f);
        fogPPS.addFilter(fog);
        viewPort.addProcessor(fogPPS);
    }
    
    private void setUpSky(){
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/skybox_blue_sphere.jpg", true));
    }
    
}

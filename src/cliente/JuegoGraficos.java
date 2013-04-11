/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import bloques.graficos.BloqueGraficos;
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
import com.jme3.shadow.PssmShadowRenderer;
import com.jme3.shadow.PssmShadowRenderer.FilterMode;
import com.jme3.util.SkyFactory;
import personaje.Personaje;

/**
 *
 * @author cyberbask
 */
public class JuegoGraficos {    
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
    
    //Personaje
    /**
     *
     */
    public Personaje personaje;
    
    JuegoStateGui juegoGui;
  
    
    BloqueGraficos bloqueGraficos;
    
    //variable para controlar si posicionamos la camara
    //o activamos el personaje
    int posicionarCamara = 0;  
    int bloqueConMasAltura; //ñapa para posiconar al personaje
    
    //primera carga
    Boolean primeraCarga = true;
    
    /**
     *
     * @param app
     */
    public JuegoGraficos(Application app){
        this.app = (SimpleApplication) app;
        this.rootNode     = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.viewPort     = this.app.getViewPort();
        this.physics      = this.stateManager.getState(BulletAppState.class);
        this.cam          = this.app.getCamera();
        
        
        bloqueGraficos = new BloqueGraficos(this.app);
        
        personaje = new Personaje(app);
        
        juegoGui = new JuegoStateGui(app);
        juegoGui.initPuntoMira();
           
        //cambiamos el color del fondo
        viewPort.setBackgroundColor(new ColorRGBA(0.7f,0.8f,1f,1f));
        //viewPort.setBackgroundColor(ColorRGBA.Blue);
        
        //Sombras Basicas
        setUpShadows();
        
        //Luces basicas
        setUpLight();
        
        //Niebla
        setUpFog();
        
        //Cielo
        setUpSky();
       
    }
    
    private void setUpLight() {
        // We add light so we see the scene
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.55f));
        rootNode.addLight(al);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White.mult(0.85f));
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
        PssmShadowRenderer pssmRenderer = new PssmShadowRenderer(assetManager, 1024, 3);
        pssmRenderer.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal()); // light direction
        pssmRenderer.setShadowIntensity(0.02f);
        pssmRenderer.setEdgesThickness(1);
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
        FogFilter fog = new FogFilter(ColorRGBA.White, 0.8f, 1200f);
        fogPPS.addFilter(fog);
        viewPort.addProcessor(fogPPS);
    }
    
    private void setUpSky(){
        /** /
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/skybox_blue_sphere.jpg", true));
        /**/
    }
    
    /**
    *
    * @param accion
    */
    public void accionBloque(String accion){        
        Vector3f posicionPlayer = personaje.getPosicionPlayer();
        
        bloqueGraficos.accionBloque(accion, "Roca", posicionPlayer);
    }
    
    /**
     *
     * @param tpf
     */
    public void update(float tpf){              
        //la primera vez que se entra aqui se genera el terreno
        if (primeraCarga){
            juegoGui.textoEnPantalla("... Generando Terreno ("+bloqueGraficos.bloqueGeneraTerreno.porcentajeGenerado+"%)... ");
            
            if (bloqueGraficos.generaTerrenoInicial()){
                juegoGui.textoEnPantalla("");
                bloqueConMasAltura = bloqueGraficos.chunks.getBloqueConMasAltura(20, 20);
                primeraCarga = false;   
            }
        }
        
        if (!primeraCarga){
            //actualizamos los chunks
            bloqueGraficos.update(tpf);
            
            if (posicionarCamara == 1){
                //Añadimos el personaje
                personaje.generaPersonaje(20,bloqueConMasAltura+100,20);
                posicionarCamara = 2;
            }
            
            if (posicionarCamara == 0){
                posicionarCamara = personaje.posicionarCamara(bloqueConMasAltura);
            }
            
            //actualizamos la posicion del personaje
            personaje.update(tpf,bloqueGraficos.chunks);            
        }
    }
    /**
     *
     */
    public void destroy() {
        bloqueGraficos.destroy();
    }
    
}

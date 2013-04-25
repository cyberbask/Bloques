/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente.juego;

import bloques.graficos.control.BloquesControl;
import bloques.manejo.utiles.BloquesUtiles;
import cliente.personaje.Personaje;
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
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.shadow.PssmShadowRenderer;
import com.jme3.shadow.PssmShadowRenderer.FilterMode;

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
    
    /**
     *
     */
    protected JuegoStateGui juegoGui;
  
    
    //BloqueGraficos bloqueGraficos;
    /**
     *
     */
    protected BloquesControl bloquesTerrainControl;
    
    private FilterPostProcessor fpp;
    private PssmShadowRenderer pssmRenderer;
    
    //variable para controlar si posicionamos la camara
    //o activamos el personaje
    int posicionarCamara = 0;  
    int bloqueConMasAltura; //ñapa para posiconar al personaje
    
    //primera carga
    private int controlPrimeraCarga = 0;
    
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
        
        rootNode.setShadowMode(RenderQueue.ShadowMode.Off);
        
        bloquesTerrainControl = new BloquesControl(this.app);
        Node terrainNode = new Node("terrainNode");
        terrainNode.addControl(bloquesTerrainControl);
        rootNode.attachChild(terrainNode);
        
        personaje = new Personaje(app);
        
        juegoGui = new JuegoStateGui(app);
        juegoGui.initPuntoMira();
           
        //cambiamos el color del fondo
        viewPort.setBackgroundColor(new ColorRGBA(0.7f,0.8f,1f,1f));
        //viewPort.setBackgroundColor(ColorRGBA.Blue);

        //Luces basicas
        setUpLight();
        
        //Sombras
        setUpShadows();
                
        //Cielo
        setUpSky();
    }
    
    /**
     *
     */
    protected void setUps() {
        fpp=new FilterPostProcessor(assetManager);
               
        //Niebla
        setUpFog();
        
        //Efecto Blur
        setUpBlur();
        
        //celShading
        setUpCellShading();
        
        viewPort.addProcessor(pssmRenderer);
        viewPort.addProcessor(fpp);
    }
    
    /**
     *
     */
    protected void unsetUps(){
        viewPort.removeProcessor(fpp);
        viewPort.removeProcessor(pssmRenderer);
        
        fpp.removeAllFilters();
        fpp.cleanup();
    }
    
    private void setUpLight() {
        // We add light so we see the scene
        /**/
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.55f));
        rootNode.addLight(al);
        /**/

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White.mult(0.85f));
        dl.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal());
        //dl.setDirection(new Vector3f(50f, -50f, -50f).normalizeLocal());
        //dl.setDirection(new Vector3f(-5f,-5f,-5f).normalizeLocal());
        rootNode.addLight(dl);
    }
    
    /**
     *
     */
    private void setUpShadows(){
        /** /
        if (BloquesUtiles.SOMBRAS){
            BasicShadowRenderer bsr = new BasicShadowRenderer(assetManager, 512);
            bsr.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal()); // light direction
            viewPort.addProcessor(bsr);
        }
        /**/
        
        if (BloquesUtiles.SOMBRAS){
            /**/
            pssmRenderer = new PssmShadowRenderer(assetManager, BloquesUtiles.SOMBRAS_CALIDAD1, BloquesUtiles.SOMBRAS_CALIDAD2);
            pssmRenderer.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal()); // light direction
            pssmRenderer.setShadowIntensity(BloquesUtiles.SOMBRAS_INTENSIDAD);
            //pssmRenderer.setEdgesThickness(1);
            //pssmRenderer.setFilterMode(FilterMode.Bilinear);
            pssmRenderer.setFilterMode(FilterMode.PCF4);
            /**/
            
            /** /
            fpp = new FilterPostProcessor(assetManager);
            SSAOFilter ssaoFilter = new SSAOFilter(12.94f, 43.92f, 0.33f, 0.61f);
            fpp.addFilter(ssaoFilter);
            /**/
        }
    }
    
    private void setUpBlur(){
        DepthOfFieldFilter dofFilter = new DepthOfFieldFilter();
        dofFilter.setFocusDistance(0f);
        dofFilter.setFocusRange(BloquesUtiles.BLUR_FOCUS_RANGE);
        dofFilter.setBlurScale(BloquesUtiles.BLUR_SCALE);
        fpp.addFilter(dofFilter);
    }
    
    private void setUpFog(){
        FogFilter fog = new FogFilter(ColorRGBA.White, BloquesUtiles.NIEBLA_INTENSIDAD, BloquesUtiles.NIEBLA_DISTANCIA);
        fpp.addFilter(fog);
    }
    
    private void setUpSky(){
        /** /
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/skybox_blue_sphere.jpg", true));
        /**/
    }
        
    /**
     *
     */
    private void setUpCellShading(){
        /** /        
        fpp.addFilter(new CartoonEdgeFilter());
        /**/
    }
    
    /**
    *
    * @param accion
    */
    public void accionBloque(String accion){        
        Vector3f posicionPlayer = personaje.getPosicionPlayer();
        
        if (accion.equals("colocar")){
            bloquesTerrainControl.nuevoBloque(personaje.nomBloqueSeleccionado, posicionPlayer);
        }else if(accion.equals("destruir")){
            bloquesTerrainControl.quitaBloque(personaje.nomBloqueSeleccionado, posicionPlayer);
        }else if(accion.equals("seleccionar")){
            String accionBloqueClonar = bloquesTerrainControl.seleccionarBloque();
            if (accionBloqueClonar != null){
                personaje.nomBloqueSeleccionado = accionBloqueClonar;
            }
        }
    }
    
    /**
     *
     * @param tpf
     */
    public void update(float tpf){   
        if (controlPrimeraCarga == 0){
            //juegoGui.textoEnPantalla("... Cargando Partida ("+bloquesTerrainControl.getPorcentajeCarga()+"%)... ");
            juegoGui.textoEnPantalla("... Cargando Partida ... ");
            
            if (bloquesTerrainControl.cargaAllChunks()){
                juegoGui.textoEnPantalla("");
                
                if (bloquesTerrainControl.isChunksNull()){
                    controlPrimeraCarga = 1;   
                }else{
                    bloqueConMasAltura = bloquesTerrainControl.getBloqueConMasAltura(60, 60);
                    controlPrimeraCarga = 2;  
                }
            }  
        }
        
        if (controlPrimeraCarga == 1){
            juegoGui.textoEnPantalla("... Generando Terreno ("+bloquesTerrainControl.getPorcentajeGeneradoTerreno()+"%)... ");
            
            if (bloquesTerrainControl.generaTerrenoInicial()){
                juegoGui.textoEnPantalla("");
                bloqueConMasAltura = bloquesTerrainControl.getBloqueConMasAltura(60, 60);
                controlPrimeraCarga = 2;   
            }  
        }
        
        if (controlPrimeraCarga == 2){
            if (posicionarCamara == 1){
                //Añadimos el personaje
                Vector3f coodPersonaje = new Vector3f(60,bloqueConMasAltura + 100,60);
                personaje.generaPersonaje(coodPersonaje);
                posicionarCamara = 2;
            }
            
            if (posicionarCamara == 0){
                posicionarCamara = personaje.posicionarCamara(bloqueConMasAltura);
            }
            
            personaje.update(tpf,bloquesTerrainControl);  
        }
    }
    
    /**
     *
     */
    public void stop(){
        bloquesTerrainControl.stop();
    }
    
    /**
     *
     */
    public void destroy() {
        bloquesTerrainControl.destroy();
    }
    
}

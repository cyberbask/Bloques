/*
 * Manejo del personaje
 */
package cliente.personaje;

import bloques.graficos.control.BloquesControl;
import bloques.manejo.utiles.BloquesUtiles;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

/**
 *
 * @author mcarballo
 */
public class Personaje {
    private SimpleApplication app;
    private Node              rootNode;
    private AppStateManager   stateManager;
    private BulletAppState      physics;
    private Camera       cam;
    
    /**
     *
     */
    public CharacterControl player;
    private Vector3f walkDirection = new Vector3f();
    /**
     *
     */
    public boolean left = false,
    /**
     *
     */
    right = false,
    /**
     *
     */
    up = false,
    /**
     *
     */
    down = false;
    
    private Boolean iniciado = false;
    
    /**
     * La usamos para poner la camara mas alta que el bloque que representa al player
     */
    protected float correcionAlturaPlayer = 3.5f;
    
    /**
     *
     */
    protected Vector3f ultimaPosicionCorrecta = null;
    
    /**
     *
     */
    public Boolean correr = false;
    
    /**
     *
     */
    public String nomBloqueSeleccionado = "Roca";
  
    /**
     *
     * @param app
     */
    public Personaje(Application app){
        this.app = (SimpleApplication) app;
        this.stateManager = this.app.getStateManager();
        this.rootNode     = this.app.getRootNode();
        this.physics      = this.stateManager.getState(BulletAppState.class);
        this.cam          = this.app.getCamera();
    }
    
    /**
     *
     * @param coodPlayer 
     */
    public void generaPersonaje(Vector3f coodPlayer){
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(2f, 7f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(50);
        player.setFallSpeed(4500);
        player.setGravity(180);
        player.setPhysicsLocation(coodPlayer);

        //Añadimos el personaje al espacio de fisicas
        physics.getPhysicsSpace().add(player);
        
        iniciado = true;
    }
    
    /**
     *
     * @param bloqueConMasAltura
     * @return
     */
    public int posicionarCamara(int bloqueConMasAltura){
        if (bloqueConMasAltura > 0){ 
            Vector3f cood = new Vector3f(60,bloqueConMasAltura,60);
            String nombreChunk = BloquesUtiles.generarNombreChunk(cood);
            if (rootNode.getChild(nombreChunk) != null){
                bloqueConMasAltura = bloqueConMasAltura + (60 * BloquesUtiles.TAMANO_BLOQUE * 2);
                cam.setLocation(new Vector3f(10, bloqueConMasAltura, 10));
                cam.setRotation(new Quaternion().fromAngleAxis(90*FastMath.DEG_TO_RAD, new Vector3f(0,1,0)));
                cam.update();
                cam.setRotation(new Quaternion().fromAngleAxis(60*FastMath.DEG_TO_RAD, new Vector3f(0,1,0)));
                cam.update();
                
                return 1;
            }
        }
        
        return 0;
    }
    
    /**
     *
     * @return
     */
    public Vector3f getPosicionPlayer(){
        Vector3f location = cam.getLocation().clone();
        location.y = location.y - correcionAlturaPlayer;
        
        //System.out.println("Player: "+location.x+"-"+location.y+"-"+location.z);
        
        return location;
    }
    
    /**
     *
     * @param tpf
     * @param bloquesTerrainControl  
     */
    public void update(float tpf, BloquesControl bloquesTerrainControl){  
        /**/
        if (iniciado){
            float mov1 = 0.07f;
            float mov2 = 0.035f;
            
            if (correr){
                mov1 = mov1 * 1.75f;
                mov2 = mov2 * 1.75f;
            }
            
            Vector3f camDir = cam.getDirection().clone().multLocal(mov1 * BloquesUtiles.TAMANO_BLOQUE);
            camDir.setY(0f); //evita despegarse del chan :-D
            Vector3f camLeft = cam.getLeft().clone().multLocal(mov2 * BloquesUtiles.TAMANO_BLOQUE);
            walkDirection.set(0, 0, 0);
            if (left)  { walkDirection.addLocal(camLeft); }
            if (right) { walkDirection.addLocal(camLeft.negate()); }
            if (up)    { walkDirection.addLocal(camDir); }
            if (down)  { walkDirection.addLocal(camDir.negate()); }
            player.setWalkDirection(walkDirection);
            
            Vector3f physicsLocation = player.getPhysicsLocation().clone();
            
            
            
            //TODO terminar la colisiones
            //Esta peli es para no meterse dentro de un bloque
            /**/
            if (!player.onGround()){
                float correcion = BloquesUtiles.TAMANO_BLOQUE - 1f;

                Vector3f playerLocation = physicsLocation.clone();
                playerLocation.y = playerLocation.y - correcion; 

                Boolean dentroBloquePlayer = bloquesTerrainControl.calculaDentroBloqueObjeto(playerLocation);

                if (dentroBloquePlayer){
                    physics.getPhysicsSpace().remove(player);

                    float sumaY = 0f;
                    do{
                        sumaY = sumaY + 0.1f;

                        playerLocation = ultimaPosicionCorrecta.clone();
                        playerLocation.y = playerLocation.y - correcion + sumaY; 

                    }while(bloquesTerrainControl.calculaDentroBloqueObjeto(playerLocation));

                    physicsLocation = playerLocation.clone();
                    physicsLocation.y = physicsLocation.y + correcion;

                    generaPersonaje(physicsLocation); 
                }

                ultimaPosicionCorrecta = physicsLocation.clone();
            }
            /**/
            //hasta aqui
            
            

            physicsLocation.y = physicsLocation.y + correcionAlturaPlayer;
            
            cam.setLocation(physicsLocation);
        }
        /**/
    }
}

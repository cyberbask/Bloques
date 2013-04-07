/*
 * Manejo del personaje
 */
package personaje;

import bloques.BloqueChunkUtiles;
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
     * @param posIniX
     * @param posIniY
     * @param posIniZ
     */
    public void generaPersonaje(int posIniX, int posIniY, int posIniZ){
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(2f, 11.8f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(50);
        player.setFallSpeed(4500);
        player.setGravity(180);
        player.setPhysicsLocation(new Vector3f(posIniX, posIniY, posIniZ));

        //Añadimos el personaje al espacio de fisicas
        physics.getPhysicsSpace().add(player);
        
        iniciado = true;
    }
    
    public int posicionarCamara(int bloqueConMasAltura){
        if (bloqueConMasAltura > 0){ 
            String nombreChunk = BloqueChunkUtiles.generarNombreChunk(BloqueChunkUtiles.calculaCoordenadasChunk(20, bloqueConMasAltura , 20));
            if (rootNode.getChild("Chunk: "+nombreChunk) != null){
                bloqueConMasAltura = bloqueConMasAltura + (20 * BloqueChunkUtiles.TAMANO_BLOQUE * 2);
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
     * @param tpf
     */
    public void update(float tpf){  
        /**/
        if (iniciado){
            Vector3f camDir = cam.getDirection().clone().multLocal(0.10f * BloqueChunkUtiles.TAMANO_BLOQUE);
            camDir.setY(0f); //evita despegarse del chan :-D
            Vector3f camLeft = cam.getLeft().clone().multLocal(0.05f * BloqueChunkUtiles.TAMANO_BLOQUE);
            walkDirection.set(0, 0, 0);
            if (left)  { walkDirection.addLocal(camLeft); }
            if (right) { walkDirection.addLocal(camLeft.negate()); }
            if (up)    { walkDirection.addLocal(camDir); }
            if (down)  { walkDirection.addLocal(camDir.negate()); }
            player.setWalkDirection(walkDirection);
            
            Vector3f physicsLocation = player.getPhysicsLocation();
            //physicsLocation = physicsLocation.multLocal(-0.001f,0, 0);
            cam.setLocation(physicsLocation);
        }
        /**/
        
    }
}

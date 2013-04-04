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
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 *
 * @author mcarballo
 */
public class Personaje {
    private SimpleApplication app;
    private AppStateManager   stateManager;
    private BulletAppState      physics;
    private Camera       cam;
    
    public CharacterControl player;
    private Vector3f walkDirection = new Vector3f();
    public boolean left = false, right = false, up = false, down = false;
    
    private Boolean iniciado = false;
    
    
    
    public Personaje(Application app){
        this.app = (SimpleApplication) app;
        this.stateManager = this.app.getStateManager();
        this.physics      = this.stateManager.getState(BulletAppState.class);
        this.cam          = this.app.getCamera();
    }
    
    public void generaPersonaje(int posIniX, int posIniY, int posIniZ){
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(4f, 15.8f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(65);
        player.setFallSpeed(3000);
        player.setGravity(230);
        player.setPhysicsLocation(new Vector3f(posIniX, posIniY, posIniZ));

        //Añadimos el personaje al espacio de fisicas
        physics.getPhysicsSpace().add(player);
        
        iniciado = true;
    }
    
    public void update(float tpf){  
        /**/
        if (iniciado){
            Vector3f camDir = cam.getDirection().clone().multLocal(0.10f * BloqueChunkUtiles.TAMANO_BLOQUE);
            camDir.setY(0f); //evita despegarse del chan :-D
            Vector3f camLeft = cam.getLeft().clone().multLocal(0.05f * BloqueChunkUtiles.TAMANO_BLOQUE);
            walkDirection.set(0, 0, 0);
            if (left)  { 
                walkDirection.addLocal(camLeft); 
            }
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

package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
 
/**
 * Example 9 - How to make walls and floors solid.
 * This collision code uses Physics and a custom Action Listener.
 * @author normen, with edits by Zathras
 */
public class HelloCollision extends SimpleApplication
        implements ActionListener {
 
  private Spatial sceneModel;
  private BulletAppState bulletAppState;
  private RigidBodyControl terrenoControl;
  private CharacterControl player;
  private Vector3f walkDirection = new Vector3f();
  private boolean left = false, right = false, up = false, down = false;
 
  public static void main(String[] args) {
    HelloCollision app = new HelloCollision();
    
    app.setShowSettings(false);
        
    AppSettings settings = new AppSettings(true);
    settings.put("Width", 800);
    settings.put("Height", 600);
    settings.put("Title", "Hello Mundo :-D");
    settings.put("VSync", false);
    //Anti-Aliasing
    settings.put("Samples", 4);

    app.setSettings(settings);
    
    app.start();
  }
 
  public void simpleInitApp() {
    /** Set up Physics */
    bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);
    //bulletAppState.getPhysicsSpace().enableDebug(assetManager);
 
    // We re-use the flyby camera for rotation, while positioning is handled by physics
    viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
    flyCam.setMoveSpeed(100);
    setUpKeys();
    setUpLight();
 
    // We load the scene from the zip file and adjust its size.
    //assetManager.registerLocator("town.zip", ZipLocator.class);
    //sceneModel = assetManager.loadModel("main.scene");
    //sceneModel.setLocalScale(2f);
 
    Node terreno = new Node("terreno");
    rootNode.attachChild(terreno);
    
    CompoundCollisionShape comp_coll = new CompoundCollisionShape();
    
    
    Geometry cubo = makeCube("Cubo", -10f, 2f, 2f);
    Geometry floor = makeFloor();
    
    terreno.attachChild(cubo);
    terreno.attachChild(floor);
    
        
    MeshCollisionShape cuboShape = new MeshCollisionShape(cubo.getMesh());
    MeshCollisionShape floorShape = new MeshCollisionShape(floor.getMesh());
    
    comp_coll.addChildShape(cuboShape, new Vector3f(0, 1, 0));
    comp_coll.addChildShape(floorShape, new Vector3f(0, 1, 0));
    
    
    terrenoControl = new RigidBodyControl(comp_coll, 0);
    terreno.addControl(terrenoControl);
    
 
    
    // We set up collision detection for the player by creating
    // a capsule collision shape and a CharacterControl.
    // The CharacterControl offers extra settings for
    // size, stepheight, jumping, falling, and gravity.
    // We also put the player in its starting position.
    CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
    player = new CharacterControl(capsuleShape, 0.05f);
    player.setJumpSpeed(20);
    player.setFallSpeed(30);
    player.setGravity(30);
    player.setPhysicsLocation(new Vector3f(0, 10, 0));
 
    // We attach the scene and the player to the rootNode and the physics space,
    // to make them appear in the game world.
   
    //bulletAppState.getPhysicsSpace().add(landscape2);
    bulletAppState.getPhysicsSpace().add(terrenoControl);
    bulletAppState.getPhysicsSpace().add(player);
  }
 
  private void setUpLight() {
    // We add light so we see the scene
    AmbientLight al = new AmbientLight();
    al.setColor(ColorRGBA.White.mult(1.3f));
    rootNode.addLight(al);
 
    DirectionalLight dl = new DirectionalLight();
    dl.setColor(ColorRGBA.White);
    dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
    rootNode.addLight(dl);
  }
 
  /** We over-write some navigational key mappings here, so we can
   * add physics-controlled walking and jumping: */
  private void setUpKeys() {
    inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
    inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
    inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
    inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
    inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addListener(this, "Left");
    inputManager.addListener(this, "Right");
    inputManager.addListener(this, "Up");
    inputManager.addListener(this, "Down");
    inputManager.addListener(this, "Jump");
  }
 
  /** These are our custom actions triggered by key presses.
   * We do not walk yet, we just keep track of the direction the user pressed. */
  public void onAction(String binding, boolean value, float tpf) {
    if (binding.equals("Left")) {
      left = value;
    } else if (binding.equals("Right")) {
      right = value;
    } else if (binding.equals("Up")) {
      up = value;
    } else if (binding.equals("Down")) {
      down = value;
    } else if (binding.equals("Jump")) {
      player.jump();
    }
  }
 
  /**
   * This is the main event loop--walking happens here.
   * We check in which direction the player is walking by interpreting
   * the camera direction forward (camDir) and to the side (camLeft).
   * The setWalkDirection() command is what lets a physics-controlled player walk.
   * We also make sure here that the camera moves with player.
   */
  @Override
  public void simpleUpdate(float tpf) {
    Vector3f camDir = cam.getDirection().clone().multLocal(0.3f);
    Vector3f camLeft = cam.getLeft().clone().multLocal(0.2f);
    walkDirection.set(0, 0, 0);
    if (left)  { walkDirection.addLocal(camLeft); }
    if (right) { walkDirection.addLocal(camLeft.negate()); }
    if (up)    { walkDirection.addLocal(camDir); }
    if (down)  { walkDirection.addLocal(camDir.negate()); }
    player.setWalkDirection(walkDirection);
    cam.setLocation(player.getPhysicsLocation());
  }
  
   /** A floor to show that the "shot" can go through several objects. */
  protected Geometry makeFloor() {
    Box box = new Box(Vector3f.ZERO, 30, 1f, 30);
    Geometry floor = new Geometry("the Floor", box);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    mat1.setBoolean("UseMaterialColors", true);
    mat1.setColor("Ambient",  ColorRGBA.Gray);
    mat1.setColor("Diffuse",  ColorRGBA.Gray);
    mat1.setColor("Specular", ColorRGBA.White);
    mat1.setFloat("Shininess", 12);
    
    floor.setMaterial(mat1);
    return floor;
  }
  
  /** A cube object for target practice */
  protected Geometry makeCube(String name, float x, float y, float z) {
    Box box = new Box(new Vector3f(x, y, z), 1, 1, 1);
    Geometry cube = new Geometry(name, box);
    
    Material mat1 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    mat1.setBoolean("UseMaterialColors", true);
    mat1.setColor("Ambient",  ColorRGBA.Blue);
    mat1.setColor("Diffuse",  ColorRGBA.Blue);
    mat1.setColor("Specular", ColorRGBA.White);
    mat1.setFloat("Shininess", 12);

    cube.setMaterial(mat1);
    return cube;
  }
  
  
}
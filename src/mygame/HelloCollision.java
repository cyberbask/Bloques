package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.BufferUtils;
import jme3tools.optimize.GeometryBatchFactory;
 
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
  private DirectionalLight dl;
  private float movSun = -100f;
  private float movSunDire = 1f;
  private boolean left = false, right = false, up = false, down = false;
  Geometry cubo;
  private float cuboTiempo = 0f;
 
  public static void main(String[] args) {
    HelloCollision app = new HelloCollision();
    
    app.setShowSettings(false);
        
    AppSettings settings = new AppSettings(true);
    settings.put("Width", 800);
    settings.put("Height", 576);
    settings.put("Title", "Hello Mundo :-D");
    settings.put("VSync", false);
    //Anti-Aliasing
    settings.put("Samples", 4);

    app.setSettings(settings);
    
    app.start();
  }
 
  public void simpleInitApp() {
    initCrossHairs();  
     
    /** Set up Physics */
    bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);
    //bulletAppState.getPhysicsSpace().enableDebug(assetManager);
 
    // We re-use the flyby camera for rotation, while positioning is handled by physics
    viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
    flyCam.setMoveSpeed(100);
    flyCam.setEnabled(false);//hace vibrar la camara al incrustarse en el suelo
    
    setUpKeys();
    setUpLight();
  
    Node terreno = new Node("terreno");
    Node suelo = new Node("suelo");
       
    //terreno.setCullHint(CullHint.Never);
    
    //CompoundCollisionShape comp_coll = new CompoundCollisionShape();
    
    
    cubo = makeCube("Cubo", 0, 1f, -10);
    Geometry cubo2 = makeCube("Cubo2", 0, 3f, -10);
   
    
    terreno.attachChild(cubo);
    terreno.attachChild(cubo2);
    
    //Geometry floor = makeFloor();
    int tamano = 3;
    
    Geometry floor = makeFloorQuad(tamano);
    floor.setLocalRotation(new Quaternion().fromAngleAxis(180*FastMath.DEG_TO_RAD, new Vector3f(1,0,0)));
    
    for (float j=-30;j<30;j=j+tamano){
        for (float i=-30;i<30;i=i+tamano){
            //floor.setLocalScale(200f);
            Geometry floorclone = floor.clone();
            floorclone.move(j,0,i*-1f);
            suelo.attachChild(floorclone);
            
            //MeshCollisionShape floorShape = new MeshCollisionShape(floorclone.getMesh());
            //comp_coll.addChildShape(floorShape, new Vector3f(j, 0, i*-1f));
        }
    }
    
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    TextureKey key3 = new TextureKey("Textures/Terrain/splat/grass.jpg");
    key3.setGenerateMips(true);
    Texture tex3 = assetManager.loadTexture(key3);
    tex3.setWrap(WrapMode.Repeat);
    mat1.setTexture("ColorMap", tex3);
    suelo.setMaterial(mat1);
    
    terreno.attachChild(suelo);
        
    Spatial optimizado = GeometryBatchFactory.optimize(terreno);
    
    CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node) terreno);
    
    //comp_coll.addChildShape(sceneShape, new Vector3f(0,1,0));
    
    rootNode.attachChild(optimizado);
        
    //MeshCollisionShape cuboShape = new MeshCollisionShape(cubo.getMesh());
    //MeshCollisionShape cubo2Shape = new MeshCollisionShape(cubo2.getMesh());
    
    //comp_coll.addChildShape(cuboShape, new Vector3f(0, 0, 0));
    //comp_coll.addChildShape(cubo2Shape, new Vector3f(0, 0, 0));
    
    terrenoControl = new RigidBodyControl(sceneShape, 0);
    terrenoControl.setFriction(0.0f);
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
   
    bulletAppState.getPhysicsSpace().add(terreno);
    bulletAppState.getPhysicsSpace().add(player);
  }
 
  private void setUpLight() {
    // We add light so we see the scene
    AmbientLight al = new AmbientLight();
    al.setColor(ColorRGBA.White.mult(0.5f));
    rootNode.addLight(al);
 
    dl = new DirectionalLight();
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
    
    /*movSun += tpf*movSunDire*20;
    
    if (movSun < -50f || movSun > 50f){   
        movSunDire = movSunDire * -1f;
        if (movSun < -50f){
            movSun = -50f;
        }else if (movSun > 50f){
            movSun = 50f;
        }
    }
    
    dl.setDirection(new Vector3f(movSun, -2.8f, -2.8f).normalizeLocal());*/
    
    //esto no funcionaria, ya que toda la geometria esta metida en un batch
    //si se saca del batch funciona
    /*cuboTiempo += tpf;
    if (cuboTiempo > 2f){
        Material mat1 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat1.setBoolean("UseMaterialColors", true);
        ColorRGBA color = ColorRGBA.randomColor();
        mat1.setColor("Ambient",  color);
        mat1.setColor("Diffuse",  color);
        mat1.setColor("Specular", ColorRGBA.White);
        mat1.setFloat("Shininess", 12);
        cubo.setMaterial(mat1);

        cuboTiempo = 0f;
    }*/
    
  }
  
  protected void initCrossHairs() {
    guiNode.detachAllChildren();
    guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
    BitmapText ch = new BitmapText(guiFont, false);
    ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
    ch.setText("+"); // crosshairs
    ch.setLocalTranslation( // center
      settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
      settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
    guiNode.attachChild(ch);
  }
  
   /** A floor to show that the "shot" can go through several objects. */
  protected Geometry makeFloor() {
    Box box = new Box(Vector3f.ZERO, 200, 1f, 200);
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
    
    /*Material mat1 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    mat1.setBoolean("UseMaterialColors", true);
    ColorRGBA color = ColorRGBA.randomColor();
    mat1.setColor("Ambient",  color);
    mat1.setColor("Diffuse",  color);
    mat1.setColor("Specular", ColorRGBA.White);
    mat1.setFloat("Shininess", 12);*/
    
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    TextureKey key3 = new TextureKey("Textures/Terrain/Rock/Rock.PNG");
    key3.setGenerateMips(true);
    Texture tex3 = assetManager.loadTexture(key3);
    tex3.setWrap(WrapMode.Repeat);
    mat1.setTexture("ColorMap", tex3);    

    cube.setMaterial(mat1);
    return cube;
  }
  
  protected Geometry makeFloorQuad(int tamano) {
    Mesh m = new Mesh();

    // Vertex positions in space
    Vector3f [] vertices = new Vector3f[4];
    vertices[0] = new Vector3f(0,0,0);
    vertices[1] = new Vector3f(tamano,0,0);
    vertices[2] = new Vector3f(0,0,tamano);
    vertices[3] = new Vector3f(tamano,0,tamano);

    // Texture coordinates
    Vector2f [] texCoord = new Vector2f[4];
    texCoord[0] = new Vector2f(0,0);
    texCoord[1] = new Vector2f(1,0);
    texCoord[2] = new Vector2f(0,1);
    texCoord[3] = new Vector2f(1,1);

    // Indexes. We define the order in which mesh should be constructed
    int [] indexes = {2,0,1,1,3,2};

    // Setting buffers
    m.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
    m.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
    m.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indexes));
    m.updateBound();

    Geometry suelaco = new Geometry("Suelaco", m);
    
    /*Material mat1 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    mat1.setBoolean("UseMaterialColors", true);
    mat1.setColor("Ambient",  ColorRGBA.Gray);
    mat1.setColor("Diffuse",  ColorRGBA.Gray);
    mat1.setColor("Specular", ColorRGBA.White);
    mat1.setFloat("Shininess", 12);*/
    //mat1.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
    
    /*Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setTexture("ColorMap", assetManager.loadTexture("Textures/Terrain/splat/grass.jpg"));
    mat1.setColor("Color", ColorRGBA.Gray);
    
    suelaco.setMaterial(mat1);*/
    
    return suelaco;
  }
  
  
}
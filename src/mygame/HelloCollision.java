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
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
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
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.system.AppSettings;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.BufferUtils;
import jme3tools.optimize.GeometryBatchFactory;
 
/**
 * Example 9 - How to make walls and floors solid.
 * This collision code uses Physics and a custom Action Listener.
 * @author normen, with edits by Zathras
 */
public class HelloCollision extends SimpleApplication implements ActionListener {
 
  private Spatial sceneModel;
  private BulletAppState bulletAppState;
  private RigidBodyControl terrenoControl;
  private CharacterControl player;
  private Vector3f walkDirection = new Vector3f();
  private DirectionalLight dl;
  private float movSun = -100f;
  private boolean vsync = true;
  private float movSunDire = 1f;
  private boolean left = false, right = false, up = false, down = false;
  Geometry cubo;
  private float cuboTiempo = 0f;
  
  protected Vector3f initialUpVec;
  
  
  private int controlmuro = 0;
  //Para el tema de los disparos
  Material wall_mat;
  Material stone_mat;
  Material floor_mat;
  /** Prepare geometries and physical nodes for bricks and cannon balls. */
  private RigidBodyControl    brick_phy;
  private static final Box    box;
  private RigidBodyControl    ball_phy;
  private static final Sphere sphere;
  private RigidBodyControl    floor_phy;
  private static final Box    floor; 
  /** dimensions used for bricks and wall */
  private static final float brickLength = 0.48f;
  private static final float brickWidth  = 0.24f;
  private static final float brickHeight = 0.12f;
  static {
    /** Initialize the cannon ball geometry */
    sphere = new Sphere(32, 32, 0.4f, true, false);
    sphere.setTextureMode(TextureMode.Projected);
    /** Initialize the brick geometry */
    box = new Box(Vector3f.ZERO, brickLength, brickHeight, brickWidth);
    box.scaleTextureCoordinates(new Vector2f(1f, .5f));
    /** Initialize the floor geometry */
    floor = new Box(Vector3f.ZERO, 10f, 0.1f, 5f);
    floor.scaleTextureCoordinates(new Vector2f(3, 6));
  }
  
  
  public static void main(String[] args) {
    HelloCollision app = new HelloCollision();
    
    app.setShowSettings(false);
        
    AppSettings settings = new AppSettings(true);
    settings.put("Width", 1024);
    settings.put("Height", 576);
    settings.put("Title", "Hello Mundo :-D");
    settings.put("VSync", true);
    //Anti-Aliasing
    settings.put("Samples", 4);

    app.setSettings(settings);
    
    app.start();
  }
    
 
  public void simpleInitApp() {
    initialUpVec = cam.getUp().clone();
    
    initCrossHairs();  
    
    /** Set up Physics */
    bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);
    //bulletAppState.getPhysicsSpace().enableDebug(assetManager);
 
    // We re-use the flyby camera for rotation, while positioning is handled by physics
    viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
    //flyCam.setMoveSpeed(100);
    flyCam.setRotationSpeed(3f);
    //flyCam.setEnabled(false);
    
    setUpKeys();
    setUpLight();
    
    initMaterials();
  
    Node terreno = new Node("terreno");
    Node suelo = new Node("suelo");
       
    //terreno.setCullHint(CullHint.Never);
    
    //CompoundCollisionShape comp_coll = new CompoundCollisionShape();
    
    
    cubo = makeCube("Cubo", 0, 1f, -10);
    Geometry cubo2 = makeCube("Cubo2", 0, 3f, -10);
   
    Geometry cubo3 = makeQuad(3);
    cubo3.move(0, 6f, -10);
    
    terreno.attachChild(cubo);
    terreno.attachChild(cubo2);
    terreno.attachChild(cubo3);
    
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
    //mat1.setTexture("ColorMap", atlas.getAtlasTexture("Bloques"));
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
    //terrenoControl.setFriction(0.0f);
    terreno.addControl(terrenoControl);
    
    // We set up collision detection for the player by creating
    // a capsule collision shape and a CharacterControl.
    // The CharacterControl offers extra settings for
    // size, stepheight, jumping, falling, and gravity.
    // We also put the player in its starting position.
    CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
    player = new CharacterControl(capsuleShape, 0.05f);
    player.setJumpSpeed(10);
    player.setFallSpeed(50);
    player.setGravity(30);
    player.setPhysicsLocation(new Vector3f(0, 3f,10f));
 
    // We attach the scene and the player to the rootNode and the physics space,
    // to make them appear in the game world.
   
    bulletAppState.getPhysicsSpace().add(terreno);
    bulletAppState.getPhysicsSpace().add(player);
    
    
    /** Configure cam to look at scene */
    //cam.setLocation(new Vector3f(0, 4f, 6f));
    //cam.lookAt(new Vector3f(2, 2, 0), Vector3f.UNIT_Y);
    
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
    inputManager.addMapping("VSync", new KeyTrigger(KeyInput.KEY_V));
    inputManager.addMapping("Murete", new KeyTrigger(KeyInput.KEY_1));
    
    inputManager.addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
    inputManager.addListener(this, "shoot");
    
   /* inputManager.addMapping("MouseLeft", new MouseAxisTrigger(MouseInput.AXIS_X, true));
    inputManager.addMapping("MouseRigth", new MouseAxisTrigger(MouseInput.AXIS_X, false));
    inputManager.addMapping("MouseUp", new MouseAxisTrigger(MouseInput.AXIS_Y, false) );
    inputManager.addMapping("MouseDown", new MouseAxisTrigger(MouseInput.AXIS_Y, true) );*/
    
    inputManager.addListener(this, "Left");
    inputManager.addListener(this, "Right");
    inputManager.addListener(this, "Up");
    inputManager.addListener(this, "Down");
    inputManager.addListener(this, "Jump");
    inputManager.addListener(this, "VSync");
    inputManager.addListener(this, "Murete");
    
    /*inputManager.addListener(analogListener, "MouseLeft");
    inputManager.addListener(analogListener, "MouseRigth");
    inputManager.addListener(analogListener, "MouseUp");
    inputManager.addListener(analogListener, "MouseDown");*/
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
    } else if (binding.equals("VSync") && !value) {
        vsync = !vsync;
        
        AppSettings Appsett = this.getContext().getSettings();
        Appsett.put("VSync", vsync);
        this.getContext().restart();
        
    } else if (binding.equals("Murete") && !value) {
        for(int i=1;i<100;i++){
            try{
                Spatial child = rootNode.getChild("brick"+String.valueOf(i));
                Geometry cosa = (Geometry) child;
                bulletAppState.getPhysicsSpace().remove(cosa.getControl(0)) ; 
                rootNode.detachChildNamed("brick"+String.valueOf(i));
            }catch(Exception e) {
            
            }
        }
        controlmuro = 1;
    } else if (binding.equals("Jump")) {
      player.jump();
    }
    
    if (binding.equals("shoot") && !value) {
       makeCannonBall();
    }
  }
  
  
  /*private AnalogListener analogListener = new AnalogListener() {
      public void onAnalog(String name, float value, float tpf) {
        if (name.equals("MouseLeft")){
            rotateCamera(value,initialUpVec);
            System.out.println("left:"+value);
        }
        if (name.equals("MouseRigth")){
            rotateCamera(-value,initialUpVec);
            System.out.println("rigth:"+value);
        }
        if (name.equals("MouseUp")){
            rotateCamera(-value,cam.getLeft());
            System.out.println("up:"+value);
        }
        if (name.equals("MouseDown")){
            rotateCamera(value,cam.getLeft());
            System.out.println("down:"+value);
        }
      }
    };*/
 
  /**
   * This is the main event loop--walking happens here.
   * We check in which direction the player is walking by interpreting
   * the camera direction forward (camDir) and to the side (camLeft).
   * The setWalkDirection() command is what lets a physics-controlled player walk.
   * We also make sure here that the camera moves with player.
   */
  @Override
  public void simpleUpdate(float tpf) {    
    Vector3f camDir = cam.getDirection().clone().multLocal(0.25f);
    camDir.setY(0f); //evita despegarse del chan :-D
    Vector3f camLeft = cam.getLeft().clone().multLocal(0.175f);
    walkDirection.set(0, 0, 0);
    if (left)  { walkDirection.addLocal(camLeft); }
    if (right) { walkDirection.addLocal(camLeft.negate()); }
    if (up)    { walkDirection.addLocal(camDir); }
    if (down)  { walkDirection.addLocal(camDir.negate()); }
    player.setWalkDirection(walkDirection);
    cam.setLocation(player.getPhysicsLocation());
    
    if (controlmuro == 1){
        controlmuro = 2;
    }else if(controlmuro == 2){
        controlmuro = 0;
        initWall();
    }
    
    //flyCam.setEnabled(true); 
    //inputManager.setCursorVisible(false);
   
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
    /** /
    cuboTiempo += tpf;
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
    }
    /**/
    
  }
  
    /*protected void rotateCamera(float value, Vector3f axis){
        float rotationSpeed = 2f;

        Matrix3f mat = new Matrix3f();
        mat.fromAngleNormalAxis(rotationSpeed * value, axis);

        Vector3f upp = cam.getUp();
        Vector3f leftt = cam.getLeft();
        Vector3f dirr = cam.getDirection();

        mat.mult(upp, upp);
        mat.mult(leftt, leftt);
        mat.mult(dirr, dirr);

        Quaternion q = new Quaternion();
        q.fromAxes(leftt, upp, dirr);
        q.normalizeLocal();

        cam.setAxes(q);
    }*/
  
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
  
  protected Geometry makeQuad(int tamano) {
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    //TextureKey key3 = new TextureKey("Textures/sphax_ctm.png");
    //key3.setGenerateMips(true);
    //Texture tex3 = assetManager.loadTexture(key3);
    //Texture tex3 = assetManager.loadTexture("Textures/sphax_ctm.png");
    Texture tex3 = assetManager.loadTexture("Textures/sphax_ctm_128.png");
    Image image = tex3.getImage();
    int width = image.getWidth();
    //tex3.setWrap(WrapMode.BorderClamp);
    mat1.setTexture("ColorMap", tex3);    
    mat1.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
      
    float tamanoTile = (float) width / 16f;
    tamanoTile = tamanoTile / (float) width;
    System.out.println("tile: "+tamanoTile);
    
    Mesh m = new Mesh();
    
    // Vertex positions in space
    Vector3f [] vertices = new Vector3f[4];
    vertices[0] = new Vector3f(0,0,0);
    vertices[1] = new Vector3f(tamano,0,0);
    vertices[2] = new Vector3f(0,tamano,0);
    vertices[3] = new Vector3f(tamano,tamano,0);
    
    float x = 12f * tamanoTile; //siempre es uno menos, el cero cuenta
    float y = -5f * tamanoTile; //siempre es uno menos, el cero cuenta
    
    float sumapixel = 0.0005f; //x a la izquierda del cuadrado
    float sumapixel2 = -0.0005f; //y arriba del cuadrado
    float sumapixel3 = 0.0005f;  //y abajo del cuadrado
    float sumapixel4 = -0.0005f; //x a la derecha del cuadrado
    /** /
    sumapixel = 0f;
    sumapixel2 = 0f;
    sumapixel3 = 0f;
    sumapixel4 = 0f;
    /**/
    
    Vector2f [] texCoord = new Vector2f[4];
    texCoord[0] = new Vector2f(0 + x + sumapixel,1 - tamanoTile + y + sumapixel3);
    texCoord[1] = new Vector2f(tamanoTile + x + sumapixel4,1 - tamanoTile + y + sumapixel3);
    texCoord[2] = new Vector2f(0 + x + sumapixel,1 + y + sumapixel2);
    texCoord[3] = new Vector2f(tamanoTile + x + sumapixel4,1 + y + sumapixel2);

    // Indexes. We define the order in which mesh should be constructed
    int [] indexes = {2,0,1,1,3,2};

    // Setting buffers
    m.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
    m.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
    m.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indexes));
    m.updateBound();

    Geometry suelaco = new Geometry("Suelaco", m);   
    
    suelaco.setMaterial(mat1);
    
    return suelaco;
  }
  
  
  /** Initialize the materials used in this scene. */
  public void initMaterials() {
    wall_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    TextureKey key = new TextureKey("Textures/Terrain/BrickWall/BrickWall.jpg");
    key.setGenerateMips(true);
    Texture tex = assetManager.loadTexture(key);
    wall_mat.setTexture("ColorMap", tex);
 
    stone_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    TextureKey key2 = new TextureKey("Textures/Terrain/Rock/Rock.PNG");
    key2.setGenerateMips(true);
    Texture tex2 = assetManager.loadTexture(key2);
    stone_mat.setTexture("ColorMap", tex2);
 
    floor_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    TextureKey key3 = new TextureKey("Textures/Terrain/Pond/Pond.jpg");
    key3.setGenerateMips(true);
    Texture tex3 = assetManager.loadTexture(key3);
    tex3.setWrap(WrapMode.Repeat);
    floor_mat.setTexture("ColorMap", tex3);
  }
  
  /** This loop builds a wall out of individual bricks. */
  public void initWall() {
    int conta = 1;
    float startpt = brickLength / 4;
    float height = 0;
    for (int j = 0; j < 15; j++) {
      for (int i = 0; i < 6; i++) {
        Vector3f vt =
         new Vector3f(i * brickLength * 2 + startpt, brickHeight + height, 0);
        makeBrick(vt,conta);
        conta = conta + 1;
      }
      startpt = -startpt;
      height += 2 * brickHeight;
    }
  }
  
  /** This method creates one individual physical brick. */
  public void makeBrick(Vector3f loc,int conta) {
    /** Create a brick geometry and attach to scene graph. */
    Geometry brick_geo = new Geometry("brick"+String.valueOf(conta), box);
    brick_geo.setMaterial(wall_mat);
    rootNode.attachChild(brick_geo);
    /** Position the brick geometry  */
    brick_geo.setLocalTranslation(loc);
    /** Make brick physical with a mass > 0.0f. */
    brick_phy = new RigidBodyControl(2f);
    /** Add physical brick to physics space. */
    brick_geo.addControl(brick_phy);
    bulletAppState.getPhysicsSpace().add(brick_phy);
  }
  
  /** This method creates one individual physical cannon ball.
   * By defaul, the ball is accelerated and flies
   * from the camera position in the camera direction.*/
   public void makeCannonBall() {
    /** Create a cannon ball geometry and attach to scene graph. */
    Geometry ball_geo = new Geometry("cannon ball", sphere);
    ball_geo.setMaterial(stone_mat);
    rootNode.attachChild(ball_geo);
    
    Vector3f direction = cam.getDirection();
    direction = direction.multLocal(3f);
    Vector3f location = cam.getLocation();
    location.setX(location.getX()+direction.getX());
    location.setY(location.getY()+direction.getY());
    location.setZ(location.getZ()+direction.getZ());
    
    /*Vector3f direction = cam.getDirection();
    System.out.println("x:"+direction.getX());
    System.out.println("y:"+direction.getY());
    System.out.println("z:"+direction.getZ());*/
    
    ball_geo.setLocalTranslation(location);
    /** Make the ball physcial with a mass > 0.0f */
    ball_phy = new RigidBodyControl(5f);
    /** Add physical ball to physics space. */
    ball_geo.addControl(ball_phy);
    bulletAppState.getPhysicsSpace().add(ball_phy);
    /** Accelerate the physcial ball to shoot it. */
    ball_phy.setLinearVelocity(cam.getDirection().multLocal(25));
  }
  
}
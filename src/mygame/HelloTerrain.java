package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.HillHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
 
/** Sample 10 - How to create fast-rendering terrains from heightmaps,
and how to use texture splatting to make the terrain look good.  */
public class HelloTerrain extends SimpleApplication implements ActionListener {
  
  ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(4);
  Future future = null;
  Node terreno = null;
  boolean yasta = false;
  Map<Integer,Geometry> mapaCajitas=new HashMap<Integer, Geometry>();
  Map<Integer,ColorRGBA> colorazos=new HashMap<Integer, ColorRGBA>();
    
  private TerrainQuad terrain;
  Material mat_terrain;
 
  public static void main(String[] args) {
    HelloTerrain app = new HelloTerrain();
    
    /** /
    GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    DisplayMode modes[] = device.getDisplayModes();
    for (int i=0; i < modes.length; i++){
        float j=0;
        //System.out.println(modes<i>.getHeight() + "," + modes</i><i>.getWidth() + "," + modes</i><i>.getBitDepth() + "," + modes</i><i>.getRefreshRate());
    }
    /**/
    
    app.setShowSettings(false);
        
    /**/
    AppSettings settings = new AppSettings(true);
    settings.put("Width", 1024);
    settings.put("Height", 576);
    settings.put("Title", "Hello Mundo :-D");
    settings.put("VSync", true);
    //Anti-Aliasing
    settings.put("Samples", 4);
    
    //settings.put("Width", 1440);
    //settings.put("Height", 900);
    //settings.put("Fullscreen", true);
    //settings.put("setBitsPerPixel", 32);
    
    app.setSettings(settings);
    /**/
    
    app.start();
  }
 
  @Override
  public void simpleInitApp() {
    Logger.getLogger("").setLevel(Level.WARNING);
      
    flyCam.setMoveSpeed(50);
    
    setUpKeys();
    
    /** /
    generateTerrain();
    /**/
    
    /*HillHeightMap heightmap = generateTerrainconReturn();
    try {
        generateTerrainBox(heightmap);
    } catch (InterruptedException ex) {
        Logger.getLogger(HelloTerrain.class.getName()).log(Level.SEVERE, null, ex);
    }*/
            
    //cam.setLocation(new Vector3f(0, 10f, 10f));
    //cam.lookAt(new Vector3f(0, 0, 0f), Vector3f.UNIT_Y);
    
    cam.setLocation(new Vector3f(0, 4f, 6f));
    cam.lookAt(new Vector3f(2, 2, 0), Vector3f.UNIT_Y);
  }
  
  private void setUpKeys() {
      inputManager.addMapping("Regenera", new KeyTrigger(KeyInput.KEY_1));
      inputManager.addListener(this, "Regenera");
  }
  
  public void onAction(String binding, boolean value, float tpf) {
    if (binding.equals("Regenera") && !value) {
        rootNode.detachAllChildren();
        
        /** /
        generateTerrain();
        /**/
        
        /** /
        HillHeightMap heightmap = generateTerrainconReturn();
        generateTerrainBox(heightmap);  
        /**/
    } 
  }
  
  @Override
  public void simpleUpdate(float tpf) {   
        try{
              //If we have no waylist and not started a callable yet, do so!
              if(terreno == null && future == null){
                  future = executor.submit(generaCubos);    //  Thread starts!
              }
              //If we have started a callable already, we check the status
              else if(future != null){
                  //Get the waylist when its done
                  if(future.isDone()){
                      terreno = (Node) future.get();
                      future = null;
                  }
                  else if(future.isCancelled()){
                      //Set future to null. Maybe we succeed next time...
                      future = null;
                  }
              }
          } 
          catch(Exception e){ 
            
          }
          /*if(terreno != null && !yasta){
              yasta = true;
              Spatial optimizado = GeometryBatchFactory.optimize(terreno);
              rootNode.attachChild(optimizado);
          }*/
        
        Integer[] keys = (Integer[])( mapaCajitas.keySet().toArray( new Integer[mapaCajitas.size()] ) );
        
        //System.out.println("claves: "+String.valueOf(keys.length));
        
        for(int i=0; i<keys.length; i++){
            int claveActual = keys[i];
            Geometry cuboActual = mapaCajitas.get(claveActual);
            
            rootNode.attachChild(cuboActual);
            
            mapaCajitas.remove(claveActual);
	}
        
        
      
  }
  
  private void generateTerrain(){
      /** 1. Create terrain material and load four textures into it. */
    mat_terrain = new Material(assetManager, 
            "Common/MatDefs/Terrain/Terrain.j3md");
 
    /** 1.1) Add ALPHA map (for red-blue-green coded splat textures) */
    mat_terrain.setTexture("Alpha", assetManager.loadTexture(
            "Textures/Terrain/splat/alphamap.png"));
 
    /** 1.2) Add GRASS texture into the red layer (Tex1). */
    Texture grass = assetManager.loadTexture(
            "Textures/Terrain/splat/grass.jpg");
    grass.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("Tex1", grass);
    mat_terrain.setFloat("Tex1Scale", 64f);
 
    /** 1.3) Add DIRT texture into the green layer (Tex2) */
    Texture dirt = assetManager.loadTexture(
            "Textures/Terrain/splat/dirt.jpg");
    dirt.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("Tex2", dirt);
    mat_terrain.setFloat("Tex2Scale", 32f);
 
    /** 1.4) Add ROAD texture into the blue layer (Tex3) */
    Texture rock = assetManager.loadTexture(
            "Textures/Terrain/splat/road.jpg");
    rock.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("Tex3", rock);
    mat_terrain.setFloat("Tex3Scale", 128f);
 
    /** 2. Create the height map */
    /** /
    AbstractHeightMap heightmap = null;
    Texture heightMapImage = assetManager.loadTexture(
            "Textures/Terrain/splat/mountains512.png");
    heightmap = new ImageBasedHeightMap(heightMapImage.getImage());
    /**/
    byte[] result= new byte[5];
    Random random= new Random();
    random.nextBytes(result);

    
    HillHeightMap heightmap = null;
    HillHeightMap.NORMALIZE_RANGE = 100; // optional
    try {
        heightmap = new HillHeightMap(513, 1000, 1, 100, result[0]); // byte 3 is a random seed
    } catch (Exception ex) {
        //ex.printStackTrace();
    }
    //heightmap.setHeightScale(0.001f);
    heightmap.load();
 
    /** 3. We have prepared material and heightmap. 
     * Now we create the actual terrain:
     * 3.1) Create a TerrainQuad and name it "my terrain".
     * 3.2) A good value for terrain tiles is 64x64 -- so we supply 64+1=65.
     * 3.3) We prepared a heightmap of size 512x512 -- so we supply 512+1=513.
     * 3.4) As LOD step scale we supply Vector3f(1,1,1).
     * 3.5) We supply the prepared heightmap itself.
     */
    int patchSize = 65;
    //float[] heightMap = heightmap.getHeightMap();
    
    System.out.println("altura:"+heightmap.getScaledHeightAtPoint(1,2));
    System.out.println("altura:"+heightmap.getScaledHeightAtPoint(1,3));
    System.out.println("altura:"+heightmap.getScaledHeightAtPoint(1,4));
    System.out.println("altura:"+heightmap.getScaledHeightAtPoint(1,5));
    System.out.println("altura:"+heightmap.getScaledHeightAtPoint(1,6));
    System.out.println("altura:"+heightmap.getScaledHeightAtPoint(1,7));
    
    System.out.println("altura:"+heightmap.getScaledHeightAtPoint(256,256));
    System.out.println("altura:"+heightmap.getScaledHeightAtPoint(512,512));
    //System.out.println("altura:"+heightmap.getScaledHeightAtPoint(513,513));
    
    /**/
    terrain = new TerrainQuad("my terrain", patchSize, 513, heightmap.getHeightMap());
 
    // 4. We give the terrain its material, position & scale it, and attach it.
    terrain.setMaterial(mat_terrain);
    terrain.setLocalTranslation(0, -100, 0);
    terrain.setLocalScale(2f, 1f, 2f);
    rootNode.attachChild(terrain);
 
    //5. The LOD (level of detail) depends on were the camera is:
    TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
    terrain.addControl(control);
    /**/
      
  }
  
  private HillHeightMap generateTerrainconReturn(){
    byte[] result= new byte[5];
    Random random= new Random();
    random.nextBytes(result);

    
    HillHeightMap heightmap = null;
    HillHeightMap.NORMALIZE_RANGE = 100; // optional
    try {
        heightmap = new HillHeightMap(513, 1000, 1, 100, result[0]); // byte 3 is a random seed
    } catch (Exception ex) {

    }
    //heightmap.setHeightScale(0.001f);
    heightmap.load();
 
    return heightmap;     
  }
  
  protected Geometry makeCube(String name, float x, float y, float z) {
    Box box = new Box(new Vector3f(x, y, z), 1, 1, 1);
    Geometry cube = new Geometry(name, box);
  
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    TextureKey key3 = new TextureKey("Textures/Terrain/Rock/Rock.PNG");
    key3.setGenerateMips(true);
    Texture tex3 = assetManager.loadTexture(key3);
    tex3.setWrap(WrapMode.Repeat);
    mat1.setTexture("ColorMap", tex3);
    
    /*ColorRGBA colorActual;
    
    ColorRGBA foo = colorazos.get((int) y);
    if (foo != null){
       colorActual = foo;
    }else{
       colorActual = ColorRGBA.randomColor();
       colorazos.put((int) y,colorActual);
    }
    
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", colorActual);*/

    cube.setMaterial(mat1);
    return cube;
  }
  
  @SuppressWarnings("SleepWhileInLoop")
  private void generateTerrainBox(HillHeightMap heightmap) throws InterruptedException, ExecutionException{
      terreno = new Node("terreno");
      
      //Node buffer = new Node("buffer");
      int contaBuffer = 0;
      int contaCajas = 0;
      
      int contaBucles;
      
      int maxX = 50;
      int maxZ = 50;
      
      for (int x = 1; x <= maxX; x++){
         for (int z = 1; z <= maxZ; z++){ 
             int y = (int) heightmap.getScaledHeightAtPoint(x,z);
             final Geometry cubo = makeCube("box-"+x+"-"+"z"+"y", x*2, y*2, -z*2);
             
             //mapaCajitas.put(contaCajas, cubo);
             
             final int contaCajasActual = contaCajas;
             
            this.enqueue(new Callable() {
                public Object call() throws Exception {
                    mapaCajitas.put(contaCajasActual, cubo);
                    return null;
                }
            });
             //buffer.attachChild(cubo);
             
             contaBuffer += 1;
             contaCajas += 1;
             
             if (contaBuffer > 100){
                 contaBuffer = 0;
                 //this.rootNode.attachChild(buffer);
                 //buffer = new Node("buffer");
                 
                 int cuentaClaves;
                 
                 contaBucles = 0;
                 
                 do {
                    Thread.sleep(250);
                    contaBucles += 1;
                    
                    cuentaClaves = this.enqueue(new Callable<Integer>() {
                       public Integer call() throws Exception {
                           Integer[] keys = (Integer[])( mapaCajitas.keySet().toArray( new Integer[mapaCajitas.size()] ) );
                           return keys.length;
                       }
                   }).get();
                 } while(cuentaClaves > 0 || contaBucles > 10);
                 
             }
         }
      }
      
      
  }
  
  // A self-contained time-intensive task:
    private Callable<Node> generaCubos = new Callable<Node>(){
        public Node call() throws Exception {

            //Read or write data from the scene graph -- via the execution queue:
            /*Vector3f location = app.enqueue(new Callable<Vector3f>() {
                public Vector3f call() throws Exception {
                    //we clone the location so we can use the variable safely on our thread
                    return mySpatial.getLocalTranslation().clone();
                }
            }).get();
            */

            // This world class allows safe access via synchronized methods
            //Data data = myWorld.getData(); 

            HillHeightMap heightmap = generateTerrainconReturn();
            generateTerrainBox(heightmap);  
            //... Now process data and find the way ...

            return terreno;
        }
    };
  
  @Override
    public void destroy() {
        super.destroy();
        executor.shutdown();
    }
  
}
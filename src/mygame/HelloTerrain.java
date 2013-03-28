package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.HillHeightMap; // for exercise 2
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
 
/** Sample 10 - How to create fast-rendering terrains from heightmaps,
and how to use texture splatting to make the terrain look good.  */
public class HelloTerrain extends SimpleApplication implements ActionListener {
  //private static final Logger logger = Logger.getLogger(HelloTerrain.class.getName());
    
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

    generateTerrain();
    
    cam.setLocation(new Vector3f(0, 60f, 50f));
    //cam.lookAt(new Vector3f(2, 2, 0), Vector3f.UNIT_Y);
  }
  
  private void setUpKeys() {
      inputManager.addMapping("Regenera", new KeyTrigger(KeyInput.KEY_1));
      inputManager.addListener(this, "Regenera");
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
        ex.printStackTrace();
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
    float[] heightMap = heightmap.getHeightMap();
    
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
  
  public void onAction(String binding, boolean value, float tpf) {
    if (binding.equals("Regenera") && !value) {
        rootNode.detachAllChildren();
        generateTerrain();
    } 
  }
}
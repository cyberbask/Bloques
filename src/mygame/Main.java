package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.util.TangentBinormalGenerator;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    public float rotado = 0;
    public float escala = 1.0f;
    public int pulso = 1;
    public Node pivot;
    public Geometry blue;
    public Geometry red;
    public Geometry yellow;
    public DirectionalLight sun;
    
    public static void main(String[] args) {
        Main app = new Main();
        
        app.setShowSettings(false);
        
        AppSettings settings = new AppSettings(true);
        settings.put("Width", 1024);
        settings.put("Height", 768);
        settings.put("Title", "Hello Mundo :-D");
        settings.put("VSync", false);
        //Anti-Aliasing
        settings.put("Samples", 4);
        
        app.setSettings(settings);
        
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Material mat1 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat1.setBoolean("UseMaterialColors", true);
        mat1.setColor("Ambient",  ColorRGBA.Blue);
        mat1.setColor("Diffuse",  ColorRGBA.Blue);
        mat1.setColor("Specular", ColorRGBA.White);
        mat1.setFloat("Shininess", 12);
        
        Material mat2 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat2.setBoolean("UseMaterialColors", true);
        mat2.setColor("Ambient",  ColorRGBA.Red);
        mat2.setColor("Diffuse",  ColorRGBA.Red);
        mat2.setColor("Specular", ColorRGBA.Red);
        mat2.setFloat("Shininess", 12);
        
        Material mat3 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat3.setBoolean("UseMaterialColors", true);
        mat3.setColor("Ambient",  ColorRGBA.Orange);
        mat3.setColor("Diffuse",  ColorRGBA.Orange);
        mat3.setColor("Specular", ColorRGBA.White);
        mat3.setFloat("Shininess", 12);
        
        /** create a blue box at coordinates (1,-1,1) */
        Box box1 = new Box( Vector3f.ZERO, 1,1,1);
        blue = new Geometry("Box", box1);
        blue.setMaterial(mat1);
        blue.move(-3,-2,-4);
 
        /** create a red box straight above the blue one at (1,3,1) */
        Box box2 = new Box( Vector3f.ZERO, 1,1,1);
        red = new Geometry("Box", box2);
        red.setMaterial(mat2);
        red.move(-3,2,-4);
        
        /** create a yellow box straight above the blue one at (1,3,1) */
        Box box3 = new Box( Vector3f.ZERO, 1,1,1);
        yellow = new Geometry("Box", box2);
        yellow.setMaterial(mat3);
        yellow.move(2,2,-4);
 
        /** Create a pivot node at (0,0,0) and attach it to the root node */
        pivot = new Node("pivot");
        rootNode.attachChild(pivot); // put this node in the scene
 
        /** Attach the two boxes to the *pivot* node. */
        pivot.attachChild(blue);
        pivot.attachChild(red);
        pivot.attachChild(yellow);
                
        // You must add a light to make the model visible
        sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1,0,-2).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
       
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        pivot.rotate(0.3f*tpf, 0.15f*tpf, 0.15f*tpf); 
        
        blue.rotate(0.5f*tpf, 0.2f*tpf, 0.2f*tpf); 
        red.rotate(0.5f*tpf, 0.2f*tpf, 0.2f*tpf); 
        yellow.rotate(0.5f*tpf, 0.2f*tpf, 0.2f*tpf); 
        
        
        escala += tpf;
        if (escala < 1) {
            yellow.scale(1 + escala* 0.1f);
        } else if (escala < 2) {
            yellow.scale(1 - escala * 0.1f);
        } else {
            escala = 0;
        }
        
       // sun.setDirection(new Vector3f(1,0,-2).normalizeLocal());
        
        
        /*BitmapText hudText = new BitmapText(guiFont, false);          
        hudText.setSize(guiFont.getCharSet().getRenderedSize());      // font size
        hudText.setColor(ColorRGBA.White);                             // font color
        hudText.setText(String.valueOf(escala));             // the text
        hudText.setLocalTranslation(300, hudText.getLineHeight(), 0); // position
        guiNode.attachChild(hudText);*/
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}

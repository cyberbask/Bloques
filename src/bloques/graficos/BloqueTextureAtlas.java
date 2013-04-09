/*
 * Clase para manejo de las texturas en el juego
 */
package bloques.graficos;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import jme3tools.optimize.TextureAtlas;

/**
 *
 * @author mcarballo
 */
public class BloqueTextureAtlas extends TextureAtlas{
    private SimpleApplication app;
    private AssetManager      assetManager;
    
    private int anchoImagenTextura;
    
    /**
     *
     * @param app
     */
    public BloqueTextureAtlas(Application app){
        super(2048,2048);
        initVarios(app);
    }
    
    /**
     *
     * @param x 
     * @param y
     * @param app  
     */
    public BloqueTextureAtlas(int x, int y,Application app){
        super(x,y);
        initVarios(app);
        
    }
    
    private void initVarios(Application app){
        this.app = (SimpleApplication) app;
        this.assetManager = this.app.getAssetManager();
    }
    
    /**
     * Añade todas las texturas que tenemos al Atlas
     */
    public void setTexturesInAtlas(){
        TextureKey textura = new TextureKey("Textures/bloques1.png");
        textura.setGenerateMips(true);
        Texture text = assetManager.loadTexture(textura);
        
        Image image = text.getImage();
        setAnchoImagenTextura(image.getWidth()); //esto se hace la primera vez para los calculos posteriores
        
        addTexture(text, "bloques1");
    }
    
    /**
     *
     * @return
     */
    public int getAnchoImagenTextura(){
        return anchoImagenTextura;
    }
    
    /**
     *
     * @param width
     */
    public void setAnchoImagenTextura(int width){
        anchoImagenTextura = width;
    }
}

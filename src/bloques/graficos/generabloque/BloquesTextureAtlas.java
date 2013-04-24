/*
 * Clase para manejo de las texturas en el juego, totalmente custom
 */
package bloques.graficos.generabloque;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mcarballo
 */
public class BloquesTextureAtlas{
    private SimpleApplication app;
    private AssetManager      assetManager;
    
    private int anchoImagenTextura;
    
    private Map<String,Texture> texturas;
    
    /**
     *
     * @param app
     */
    public BloquesTextureAtlas(Application app){
        initVarios(app);
    }
    
    private void initVarios(Application app){
        this.app = (SimpleApplication) app;
        this.assetManager = this.app.getAssetManager();
        
        texturas = new HashMap<String, Texture>();
    }
    
    /**
     * Añade todas las texturas que tenemos al Atlas
     */
    public void setTexturesInAtlas(){
        Texture text = assetManager.loadTexture("Textures/bloques1.png");
        text.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
        
        Image image = text.getImage();
        setAnchoImagenTextura(image.getWidth()); //esto se hace la primera vez para los calculos posteriores
        
        texturas.put("bloques1",text);
        
        //segunda textura
        /** /
        textura = new TextureKey("Textures/bloques2.png");
        textura.setGenerateMips(true);
        
        Texture text2 = assetManager.loadTexture(textura);
        addTexture(text2, "bloques2", text);
        /**/
    }
    
    /**
     *
     * @param nomTextura
     * @return
     */
    public Texture getTexture(String nomTextura){
        return texturas.get(nomTextura);
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

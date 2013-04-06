/*
 * Clase donde se configurara la gui durante el juego
 */
package cliente;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import utiles.AppUtiles;

/**
 *
 * @author mcarballo
 */
public class StateJuegoGui {
    private SimpleApplication app;
    private AssetManager      assetManager;
    private BitmapFont guiFont;
    private Node guiNode;
    private BitmapText textoEnPantalla;
    
    /**
     *
     * @param app
     */
    public StateJuegoGui(Application app){
        this.app = (SimpleApplication) app;
        this.assetManager = this.app.getAssetManager();
        
        guiNode = this.app.getGuiNode();
        
        //fuente de letra
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
    }
    
    /**
     * Posiciona el punto de mira en la pantalla
     */
    protected void initPuntoMira() {
        AppSettings settings = AppUtiles.getSettings(app);

        //Por ahora comentamos esta linea ya que machaca las estadisticas laterales
        //guiNode.detachAllChildren();
        
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation( // center
            settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
            settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
        guiNode.attachChild(ch);
    }
    
    protected void textoEnPantalla(String txt){
        if (textoEnPantalla != null){
            guiNode.detachChild(textoEnPantalla);
        }
        if (txt.equals("") && textoEnPantalla != null){
            guiNode.detachChild(textoEnPantalla);
            textoEnPantalla = null;
        }
        textoEnPantalla = new BitmapText(guiFont, false);
        textoEnPantalla.setSize(guiFont.getCharSet().getRenderedSize());
        textoEnPantalla.setText(txt);
        textoEnPantalla.setLocalTranslation(300, textoEnPantalla.getLineHeight(), 0);
        guiNode.attachChild(textoEnPantalla);
    }
}

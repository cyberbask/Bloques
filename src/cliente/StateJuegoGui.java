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
    
    /**
     *
     * @param app
     */
    public StateJuegoGui(Application app){
        this.app = (SimpleApplication) app;
    }
    
    /**
     * Posiciona el punto de mira en la pantalla
     */
    protected void initPuntoMira() {
        Node guiNode = app.getGuiNode();
        AssetManager assetManager = this.app.getAssetManager();
        AppSettings settings = AppUtiles.getSettings(app);

        //Por ahora comentamos esta linea ya que machaca las estadisticas laterales
        //guiNode.detachAllChildren();
        
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation( // center
            settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
            settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
        guiNode.attachChild(ch);
    }
}

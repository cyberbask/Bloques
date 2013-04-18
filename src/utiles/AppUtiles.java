/*
 * Clase para pequeñas funciones
 */
package utiles;

import cliente.MainCliente;
import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import java.io.File;

/**
 *
 * @author mcarballo
 */
public class AppUtiles {
    /**
     *
     */
    public static final String PATH_SAVE = System.getProperty("user.dir")+"/saves/";
    
    /**
     *
     */
    public static final String PATH_SCREENSHOTS = System.getProperty("user.dir")+"/screenshots/";
    
    /**
     *
     * @param app Aplicacion general
     * @return Aplicacion general con los settings configurados
     */
    public static MainCliente initSettings(MainCliente app){
        /** /
        app.setShowSettings(false);
        
        AppSettings settings = new AppSettings(true);
        settings.put("Width", 1024);
        settings.put("Height", 576);
        settings.put("Title", "Bloques");
        settings.put("VSync", true);
        //Anti-Aliasing
        settings.put("Samples", 0);

        app.setSettings(settings);
        /**/
        
        /**/
        app.setShowSettings(true);
        AppSettings settings = new AppSettings(false);
        settings.put("Title", "Bloques");
        settings.setSettingsDialogImage("Interface/splashscreen.png");
        app.setSettings(settings);
        /**/
        
        return app;
    }  
    
    /**
     * Devuelve las settings actuales de la app
     * @param app
     * @return
     */
    public static AppSettings getSettings(SimpleApplication app){
        AppSettings settings = app.getContext().getSettings();
        
        return settings;
    }
    
    /**
     *
     */
    public static void creaCarpetasIniciales(){
        File f = new File(PATH_SCREENSHOTS);
        try{
            if (!f.exists()){
                f.mkdir();
            }
        }catch(Exception e){

        }
        
        //ruta para los savegames
        f = new File(PATH_SAVE);
        try{
            if (!f.exists()){
                f.mkdir();
            }
        }catch(Exception e){

        }
        
    }
}

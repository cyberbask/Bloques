/*
 * Clase para pequeñas funciones
 */
package utiles;

import cliente.MainCliente;
import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;

/**
 *
 * @author mcarballo
 */
public class AppUtiles {
    
    /**
     *
     * @param app Aplicacion general
     * @return Aplicacion general con los settings configurados
     */
    public static MainCliente initSettings(MainCliente app){
        app.setShowSettings(false);
        
        AppSettings settings = new AppSettings(true);
        settings.put("Width", 1024);
        settings.put("Height", 576);
        settings.put("Title", "Bloques");
        settings.put("VSync", true);
        //Anti-Aliasing
        settings.put("Samples", 0);

        app.setSettings(settings);
        
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
}

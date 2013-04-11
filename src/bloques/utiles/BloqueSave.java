/*
 * Clase para guardar y cargar chunks
 * TODO - Acabarla, por ahora se usa SaveGame
 */
package bloques.utiles;

import bloques.manejo.BloqueChunks;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.export.binary.BinaryImporter;
import com.sun.tools.apt.Main;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author mcarballo
 */
public class BloqueSave {
    
    /**
     *
     * @param chunks
     */
    public static void save(BloqueChunks chunks){
        String userHome = System.getProperty("user.home");
        
        OutputStream os = null;
        
        try {
            BinaryExporter exporter = BinaryExporter.getInstance();
            File file = new File(userHome+"/Bloques/terreno/"+"allchunks.dat");
            os = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            
            try {
                exporter.save(chunks, os);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Error: Fallo al guardar los chunks", ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Error: Fallo al crear Ficheros", ex);
        }
        
    }
    
    /**
     *
     * @return
     */
    public static BloqueChunks load(){
        BloqueChunks chunks = null;
                
        String userHome = System.getProperty("user.home");
        
        InputStream is = null;
        
        try {   
            BinaryImporter imp = BinaryImporter.getInstance();
            File file = new File(userHome+"/Bloques/terreno/"+"allchunks.dat");
            
            if(!file.exists()){
                return null;
            }
            
            is = new GZIPInputStream(new BufferedInputStream(new FileInputStream(file)));
            
            try {
                chunks = (BloqueChunks)imp.load(is);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Error: Fallo al leer los chunks", ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Error: Fallo al leer los chunks", ex);
        }
        
        return chunks;
    }
}

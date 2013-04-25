/*
 * Clase para cargar y guardar chunks
 */
package bloques.manejo.utiles;

/**
 *
 * @author mcarballo
 */

import bloques.manejo.chunks.BloquesChunk;
import bloques.manejo.chunks.BloquesChunkDatos;
import bloques.manejo.chunks.BloquesChunks;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.export.Savable;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.export.binary.BinaryImporter;
import com.jme3.math.Vector3f;
import com.jme3.system.Timer;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import utiles.AppUtiles;

/**
 * Tool for saving Savables as SaveGame entries in a system-dependent way.
 * @author normenhansen
 */
public class BloquesSaveLoad {
    /**
     *
     */
    protected SimpleApplication app;
    private ScheduledThreadPoolExecutor executor;
    
    private Future futureCarga = null;
    
    private Future futureGuarda = null;
    
    /**
     *
     */
    public Boolean cargando = false;
    
    /**
     *
     */
    public Boolean guardando = false;
    
    /**
     *
     */
    public int porcentageCargado = 0;
    
    /**
     *
     */
    public int porcentageGuardado = 0;
    
    /**
     *
     * @param app
     * @param executor
     */
    public BloquesSaveLoad(Application app, ScheduledThreadPoolExecutor executor){
        this.app = (SimpleApplication) app;
        
        this.executor = executor;
    }

    /**
     * Saves a savable in a system-dependent way.
     * @param gamePath A unique path for this game, e.g. com/mycompany/mygame
     * @param dataName A unique name for this savegame, e.g. "save_001"
     * @param data The Savable to save
     */
    public static void save(String gamePath, String dataName, Savable data) {
        BinaryExporter ex = BinaryExporter.getInstance();
        OutputStream os = null;
        try {
            File daveFolder = new File(AppUtiles.PATH_SAVE + gamePath.replace('/', File.separatorChar));
            if (!daveFolder.exists() && !daveFolder.mkdirs()) {
                Logger.getLogger(jme3tools.savegame.SaveGame.class.getName()).log(Level.SEVERE, "Error creating save file!");
                throw new IllegalStateException("SaveGame dataset cannot be created");
            }
            File saveFile = new File(daveFolder.getAbsolutePath() + File.separator + dataName);
            if (!saveFile.exists()) {
                if (!saveFile.createNewFile()) {
                    Logger.getLogger(jme3tools.savegame.SaveGame.class.getName()).log(Level.SEVERE, "Error creating save file!");
                    throw new IllegalStateException("SaveGame dataset cannot be created");
                }
            }
            os = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(saveFile)));
            ex.save(data, os);
        } catch (IOException ex1) {
            Logger.getLogger(jme3tools.savegame.SaveGame.class.getName()).log(Level.SEVERE, "Error saving data: {0}", ex1);
            ex1.printStackTrace();
            throw new IllegalStateException("SaveGame dataset cannot be saved");
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException ex1) {
                Logger.getLogger(jme3tools.savegame.SaveGame.class.getName()).log(Level.SEVERE, "Error saving data: {0}", ex1);
                ex1.printStackTrace();
                throw new IllegalStateException("SaveGame dataset cannot be saved");
            }
        }
    }

    /**
     * Loads a savable that has been saved on this system with saveGame() before.
     * @param gamePath A unique path for this game, e.g. com/mycompany/mygame
     * @param dataName A unique name for this savegame, e.g. "save_001"
     * @return The savable that was saved
     */
    public static Savable load(String gamePath, String dataName) {
        return load(gamePath, dataName, null);
    }

    /**
     * Loads a savable that has been saved on this system with saveGame() before.
     * @param gamePath A unique path for this game, e.g. com/mycompany/mygame
     * @param dataName A unique name for this savegame, e.g. "save_001"
     * @param manager Link to an AssetManager if required for loading the data (e.g. models with textures)
     * @return The savable that was saved or null if none was found
     */
    public static Savable load(String gamePath, String dataName, AssetManager manager) {
        InputStream is = null;
        Savable sav = null;
        try {
            File file = new File(AppUtiles.PATH_SAVE + gamePath.replace('/', File.separatorChar) + File.separator + dataName);
            if(!file.exists()){
                return null;
            }
            is = new GZIPInputStream(new BufferedInputStream(new FileInputStream(file)));
            BinaryImporter imp = BinaryImporter.getInstance();
            if (manager != null) {
                imp.setAssetManager(manager);
            }
            sav = imp.load(is);
        } catch (IOException ex) {
            Logger.getLogger(jme3tools.savegame.SaveGame.class.getName()).log(Level.SEVERE, "Error loading data: {0}", ex);
            ex.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    Logger.getLogger(jme3tools.savegame.SaveGame.class.getName()).log(Level.SEVERE, "Error loading data: {0}", ex);
                    ex.printStackTrace();
                }
            }
        }
        return sav;
    }
    
    /**
     *
     * @param chunks
     */
    public static void saveChunks(BloquesChunks chunks){
        if (chunks == null) {
            return;
        }
        
        //TODO ahora se guarda todos los chuunks en el mismo archivo.
        //     habria que guardarlos en archivos independientes o algun sistema similar
        String eol = System.getProperty("line.separator");
        
        BufferedWriter writer = null;
        
        try{            
            File carpeta = new File(AppUtiles.PATH_SAVE + "terreno/");
            if (!carpeta.exists()){
                carpeta.mkdirs();
            }
            
            GZIPOutputStream zip = new GZIPOutputStream(new FileOutputStream(new File(AppUtiles.PATH_SAVE + "terreno/" + "allchunks.txt")),512);
            //writer = new BufferedWriter(new OutputStreamWriter(zip, "UTF-8"));
            writer = new BufferedWriter(new OutputStreamWriter(zip));
            
            String datosEscribir;
            String nomBloque;
            int[] caras;
            Vector3f coordBloque;
            BloquesChunkDatos datosBloque;
            BloquesChunk datosChunk;
            Map<String, BloquesChunkDatos> allBloquesDatos;
            
            for (Map.Entry<String,BloquesChunk> entryChunk : chunks.getAllChunks().entrySet()){
                datosChunk = entryChunk.getValue();
                
                if (datosChunk != null){                    
                    allBloquesDatos = datosChunk.getAllBloquesDatos();
                    
                    if (allBloquesDatos != null){
                        for (Map.Entry<String,BloquesChunkDatos> entryBloquesDatos : allBloquesDatos.entrySet()){
                            nomBloque = entryBloquesDatos.getKey();
                            datosBloque = entryBloquesDatos.getValue();
                            
                            coordBloque = BloquesUtiles.devuelveCoordenadasBloque(nomBloque);
                            
                            datosEscribir = "";
                            
                            //coodBloque - 0
                            datosEscribir += (int)coordBloque.x+"_"+(int)coordBloque.y+"_"+(int)coordBloque.z+ ";";
                            
                            //tipo bloque - 1
                            datosEscribir += datosBloque.getNomBloque() + ";";
                            
                            //mostrar o no mostrar bloque - 2
                            //caras - 3
                            if (datosBloque.getMostrar()){
                                datosEscribir += "1;";
                                
                                caras = datosBloque.getCaras();
                            
                                if (caras != null){
                                    datosEscribir += caras[0]+"-"+caras[1]+"-"+caras[2]+"-"+caras[3]+"-"+caras[4]+"-"+caras[5]+";";
                                }else{
                                    datosEscribir += "null;";
                                }
                                
                            }else{
                                datosEscribir += "0;";
                                datosEscribir += "null;";
                            }   
                            
                            //irompible - 4
                            if (datosBloque.getIrrompible()){
                                datosEscribir += "1;";
                            }else{
                                datosEscribir += "0;";
                            }
                            
                            datosEscribir += eol;

                            writer.append(datosEscribir);
                        }
                    }
                } 
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        finally
        {           
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     *
     * @return
     */
    public BloquesChunks loadChunks(){
        BufferedReader reader = null;
        
        BloquesChunks chunks = null;
        
        try{  
            GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(new File(AppUtiles.PATH_SAVE + "terreno/" + "allchunks.txt")),512);
            reader = new BufferedReader(new InputStreamReader(gzip));
            
            chunks = new BloquesChunks();
            
            String linea;
            String[] lineaSplit;
            String[] coordSplit;
            String[] carasSplit;
            int[] caras;
            Vector3f coord;
            BloquesChunkDatos datosBloque;
            
            while ((linea = reader.readLine()) != null){
                lineaSplit = linea.split(";");
                
                coordSplit = lineaSplit[0].split("_");
                
                coord = new Vector3f(Integer.valueOf(coordSplit[0]), Integer.valueOf(coordSplit[1]), Integer.valueOf(coordSplit[2]));
                
                datosBloque = new BloquesChunkDatos();
                
                datosBloque.setNomBloque(lineaSplit[1]);
                
                if (lineaSplit[2].equals("1")){
                    datosBloque.setMostrar(true);
                    
                    if (!lineaSplit[3].equals("null")){
                        carasSplit = lineaSplit[3].split("-");
                        
                        caras = new int[6];
                        
                        caras[0] = Integer.valueOf(carasSplit[0]);
                        caras[1] = Integer.valueOf(carasSplit[1]);
                        caras[2] = Integer.valueOf(carasSplit[2]);
                        caras[3] = Integer.valueOf(carasSplit[3]);
                        caras[4] = Integer.valueOf(carasSplit[4]);
                        caras[5] = Integer.valueOf(carasSplit[5]);
                        
                        datosBloque.setCaras(caras);
                    }
                }else{
                    datosBloque.setMostrar(false);
                }
                
                if (lineaSplit[4].equals("1")){
                    datosBloque.setIrrompible(true);
                }else{
                    datosBloque.setIrrompible(false);
                }
                
                chunks.setBloque(coord, datosBloque);
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
        finally
        {           
            try {
                if(reader != null){
                    reader.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        return chunks;
    }
    
    private Callable<BloquesChunks> loadAllChunksHilo = new Callable<BloquesChunks>(){
        public BloquesChunks call() throws Exception {
            Timer timer = app.getTimer();
            float totalInicio = timer.getTimeInSeconds();
            
            BloquesChunks chunks = loadChunks();
            
            float totalFin = timer.getTimeInSeconds();
            System.out.println("Terreno Cargado del HD "+(totalFin-totalInicio));
            
            return chunks;
        }
    };
    
    /**
     *
     * @return
     */
    public BloquesChunks loadAllChunks(){      
        try{
            if(futureCarga == null && !cargando){
                futureCarga = executor.submit(loadAllChunksHilo);
                cargando = true;
            }
            else if(futureCarga != null){
                if(futureCarga.isDone()){
                    BloquesChunks get = (BloquesChunks) futureCarga.get();
                    
                    futureCarga = null;
                    cargando = false;
                    
                    return get;
                }
                else if(futureCarga.isCancelled()){
                    futureCarga = null;
                }
            }
        } 
        catch(Exception e){ 
        }
        
        return null;
    }
    
    /**
     *
     */
    public void destroy() {

    }
}
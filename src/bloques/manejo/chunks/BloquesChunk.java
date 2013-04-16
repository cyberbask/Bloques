/*
 * Manejo de un Chunk de Bloques
 */
package bloques.manejo.chunks;

import bloques.manejo.utiles.BloquesUtiles;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mcarballo
 */
public class BloquesChunk implements Savable{
    /**
     * Contiene los datos del bloque segun coordenadas
     */
    private Map<String,BloquesChunkDatos> bloquesDatos = new HashMap<String, BloquesChunkDatos>();
    
    private String nombreChunk = null;

    private Node nodos = new Node();
    
    /**
     * Constructor
     */
    public BloquesChunk(){
    }
    
    /**
     *
     * @param indiceBloque
     * @param datosBloque
     */
    public void setBloque(String indiceBloque, BloquesChunkDatos datosBloque){
        bloquesDatos.put(indiceBloque, datosBloque);
    }
    
    /**
     *
     * @param indiceBloque
     * @return
     */
    public BloquesChunkDatos getBloque(String indiceBloque){
        return bloquesDatos.get(indiceBloque);
    }

    /**
     *
     * @param indiceBloque
     */
    public void quitaBloque(String indiceBloque){ 
        bloquesDatos.remove(indiceBloque);
    }
    
    /**
     *
     * @return
     */
    public Map<String,BloquesChunkDatos> getAllBloquesDatos(){
        return bloquesDatos;
    }
    
    /**
     *
     * @return
     */
    public String getNombreChunk() {
        return nombreChunk;
    }

    /**
     *
     * @param nombreChunk
     */
    public void setNombreChunk(String nombreChunk) {
        this.nombreChunk = nombreChunk;
        this.nodos.setName(nombreChunk);
    }
    
    /**
     *
     * @return
     */
    public Node getAllNodos(){
        return this.nodos;
    }
    
    /**
     *
     * @param coord
     * @param bloque
     */
    public void setNodo(Vector3f coord, Node bloque){
        String nombreNodo = BloquesUtiles.generarNombreBloque(coord);
        setNodo(nombreNodo, bloque);
    }
    
    /**
     *
     * @param nomNodo
     * @param bloque
     */
    public void setNodo(String nomNodo, Node bloque){
        Spatial nodo = getNodo(nomNodo);
        if (nodo != null){
            quitaNodo(nodo);
        }
        
        this.nodos.attachChild(bloque);
    }
    
    /**
     *
     * @param nomNodo
     * @return
     */
    public Spatial getNodo(String nomNodo){
        return this.nodos.getChild(nomNodo); 
    }
    
    /**
     *
     * @param coord
     */
    public void quitaNodo(Vector3f coord){
        String nombreBloque = BloquesUtiles.generarNombreBloque(coord);
        quitaNodo(nombreBloque);
    }
    
    /**
     *
     * @param nomNodo
     */
    public void quitaNodo(String nomNodo){
        Spatial nodo = getNodo(nomNodo);
        if (nodo != null){
            quitaNodo(nodo);
        }
    }
    
    /**
     *
     * @param nodo
     */
    public void quitaNodo(Spatial nodo){
        this.nodos.detachChild(nodo);
    }

    /**
     *
     * @param ex
     * @throws IOException
     */
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        
        int tamano = bloquesDatos.entrySet().size();
        int contador = 0;
        String claveGuardar;
        
        capsule.write(tamano,  "ChunkDatoTamano",  0);
        
        capsule.write(nombreChunk,  "nombreChunk",  null);
               
        capsule.write(nodos,  "ChunkDatoNodos",  null);
        
        for (Map.Entry<String,BloquesChunkDatos> entryBloquesDatos : bloquesDatos.entrySet()){
            claveGuardar = "ChunkDatoClave_"+contador;
            
            capsule.write(entryBloquesDatos.getKey(),  claveGuardar,  null);
            capsule.write(entryBloquesDatos.getValue(),  entryBloquesDatos.getKey(),  null);
            
            contador++;
        }  
    }

    /**
     *
     * @param im
     * @throws IOException
     */
    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        
        String claveGuardar;
        String claveGuardada;
        
        int tamano   = capsule.readInt("ChunkDatoTamano",   0);
        
        nombreChunk = capsule.readString("nombreChunk",   null);
        
        nodos = (Node) capsule.readSavable("ChunkDatoNodos",   null);
        
        for(int i=0;i<tamano;i++){
            claveGuardar = "ChunkDatoClave_"+i;
            
            claveGuardada = capsule.readString(claveGuardar, null);
            if (claveGuardada != null){
                bloquesDatos.put(claveGuardada,(BloquesChunkDatos) capsule.readSavable(claveGuardada,  null));
            }
        }
    }
    
}

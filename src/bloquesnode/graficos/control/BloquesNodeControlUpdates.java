/*
 * Manejo de los updates de los chunks sobre rootNode
 */
package bloquesnode.graficos.control;

import bloquesnode.manejo.chunks.BloquesNodeChunk;
import bloquesnode.manejo.chunks.BloquesNodeChunkDatos;
import bloquesnode.manejo.utiles.BloquesNodeUtiles;
import com.jme3.app.Application;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.Timer;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import jme3tools.optimize.GeometryBatchFactory;

/**
 *
 * @author mcarballo
 */
public class BloquesNodeControlUpdates extends BloquesNodeControlSetterGetter{
    /**
     *
     */
    protected Future future = null;
    /**
     *
     */
    protected Future futureChunkUrgentes = null;
    
    
    //aqui meteremos los updates del terreno nuevo que van sin prisa
    /**
     *
     */
    public Map<Integer,String> updatesChunk=new HashMap<Integer, String>();
    /**
     *
     */
    public int contadorUpdates = 0;
    
    //aqui meteremos los updates urgentes de cambios en los chunks, nuevos bloques ...
    /**
     *
     */
    public Map<Integer,String> updateChunkUrgentes=new HashMap<Integer, String>();
    /**
     *
     */
    public int contadorUpdatesChunkUrgentes = 0;
    
    /**
     *
     * @param app
     */
    public BloquesNodeControlUpdates(Application app){
        super(app);
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        super.controlUpdate(tpf);
        
        //las siguientes veces que se hace update se actualizan los chunks
       try{
            if(future == null){
                future = executor.submit(procesaGraficosUpdates);
            }
            else if(future != null){
                if(future.isDone()){
                    future = null;
                }
                else if(future.isCancelled()){
                    future = null;
                }
            }
        } 
        catch(Exception e){ 

        }

        //updatemos los chunks urgentes
        try{
            if(futureChunkUrgentes == null){
                futureChunkUrgentes = executor.submit(procesaGraficosUpdatesUrgentes);
            }
            else if(futureChunkUrgentes != null){
                if(futureChunkUrgentes.isDone()){
                    futureChunkUrgentes = null;
                }
                else if(futureChunkUrgentes.isCancelled()){
                    futureChunkUrgentes = null;
                }
            }
        } 
        catch(Exception e){ 

        }
        
    }
    
    /**
     *
     * @param updatear
     */
    public void updateaChunks(Map<Integer,BloquesNodeChunk> updatear){
        Timer timer = app.getTimer();
        float totalInicio = timer.getTimeInSeconds();


        for (Map.Entry<Integer,BloquesNodeChunk> entryChunk : updatear.entrySet()){    
            float totalInicioChunk = timer.getTimeInSeconds();
            
            BloquesNodeChunk chunkActual = entryChunk.getValue();
            String claveActual = chunkActual.getNombreChunk();

            Node bloquesMostrar = new Node(claveActual);

            int mostrar = 0;
            
            for (Map.Entry<String,BloquesNodeChunkDatos> entryBloquesDatos : chunkActual.getAllBloquesDatos().entrySet()){ 
                BloquesNodeChunkDatos datosBloque = entryBloquesDatos.getValue();
                
                if (datosBloque != null){
                    String nomDatosBloque = entryBloquesDatos.getKey();

                    Node bloqueClonado;

                    bloqueClonado = bloques.getBloqueGenerado(datosBloque.getNomBloque());

                    //coordenadas reales del cubo, no las del chunk
                    Vector3f coordenadas = BloquesNodeUtiles.devuelveCoordenadasBloque(nomDatosBloque);

                    //le quitamos las caras que no se ven
                    int[] carasbloquesVecinos ;
                    int contaCarasQuitadas = 0;

                    int[][] bloquesVecinos = chunks.getBloquesVecinos(coordenadas);
                    carasbloquesVecinos = chunks.getCarasAPartirDeBloquesVecinos(bloquesVecinos);
                    datosBloque.setCaras(carasbloquesVecinos);

                    for(int h = 0;h<6;h++){
                        if (carasbloquesVecinos[h] == 0){ //si no hay cara
                            bloqueClonado.detachChildNamed("Cara-"+h); 
                            contaCarasQuitadas++;
                        }
                    }

                    if (contaCarasQuitadas < 6){
                        bloqueClonado.setLocalTranslation(coordenadas.x,coordenadas.y,coordenadas.z + BloquesNodeUtiles.TAMANO_BLOQUE);
                        bloquesMostrar.attachChild(bloqueClonado);
                        mostrar = 1;
                    }
                }
            }
            
            final Spatial optimizado = GeometryBatchFactory.optimize(bloquesMostrar);
            final int mostrarFinal = mostrar;

            app.enqueue(new Callable() {
                public Object call() throws Exception {  
                    if (mostrarFinal == 1){
                        CollisionShape bloquesMostrarShape = CollisionShapeFactory.createMeshShape(optimizado);
                        RigidBodyControl bloquesMostrarControl = new RigidBodyControl(bloquesMostrarShape, 0);
                        optimizado.addControl(bloquesMostrarControl);

                        physics.getPhysicsSpace().add(optimizado);
                        spatial.getParent().attachChild(optimizado);
                    }

                    return null;
                }
            });
            
            float totalFinChunk = timer.getTimeInSeconds();
            System.out.println(claveActual+" : "+(totalFinChunk-totalInicioChunk));
        }
        
        float totalFin = timer.getTimeInSeconds();
        System.out.println("Tiempo update"+(totalFin-totalInicio));
        
    }
    
    // A self-contained time-intensive task:
    private Callable<Boolean> procesaGraficosUpdates = new Callable<Boolean>(){
        public Boolean call() throws Exception {
            Map<Integer,BloquesNodeChunk> updatear = app.enqueue(new Callable<Map<Integer,BloquesNodeChunk>>() {
                public Map<Integer,BloquesNodeChunk> call() throws Exception {
                    return getUpdates(true);
                }
            }).get();
            
            if (updatear != null){
                updateaChunks(updatear); //1 es para los updates lentos del terreno
            }
            
            return false;
        }
    };
    
    // A self-contained time-intensive task:
    private Callable<Boolean> procesaGraficosUpdatesUrgentes = new Callable<Boolean>(){
        public Boolean call() throws Exception {
            Map<Integer,BloquesNodeChunk> updatear = app.enqueue(new Callable<Map<Integer,BloquesNodeChunk>>() {
                public Map<Integer,BloquesNodeChunk> call() throws Exception {
                    return getUpdatesUrgentes(true);
                }
            }).get();
            
            if (updatear != null){
                //updateaChunksUrgentes(updatear); //2 es para los rapidos, destruir bloques por ejemplo
            }
            
            return false;
        }
    };
    
    /**
     *
     * @param vaciarUpdate
     * @return
     */
    public Map<Integer,BloquesNodeChunk> getUpdates(Boolean vaciarUpdate){
        if (vaciarUpdate){
            if (updatesChunk == null){
                return null;
            }
            
            int contador = 0;
            
            Map<Integer,BloquesNodeChunk> updatesCopia = new HashMap<Integer, BloquesNodeChunk>();
            
            SortedSet<Integer> keys = new TreeSet<Integer>(updatesChunk.keySet());
            for (Integer key : keys) {
                BloquesNodeChunk chunk = chunks.getChunk(updatesChunk.get(key));
                
                if (chunk != null){
                    updatesCopia.put(contador,chunk);
                    contador++;
                }
                
                updatesChunk.remove(key);
            }
            
            if (contador > 0){
                return updatesCopia;
            }else{
               return null;
            }
        }else{
            //quiza se use mas adelante
            return null;
        }
    }
    
    /**
     *
     * @param vaciarUpdate
     * @return
     */
    public Map<Integer,BloquesNodeChunk> getUpdatesUrgentes(Boolean vaciarUpdate){
        if (vaciarUpdate){
            if (updateChunkUrgentes == null){
                return null;
            }
            
            int contador = 0;
            
            Map<Integer,BloquesNodeChunk> updatesCopia = new HashMap<Integer, BloquesNodeChunk>();
            
            SortedSet<Integer> keys = new TreeSet<Integer>(updateChunkUrgentes.keySet());
            for (Integer key : keys) {
                BloquesNodeChunk chunk = chunks.getChunk(updateChunkUrgentes.get(key));
                
                if (chunk != null){
                    updatesCopia.put(contador,chunk);
                    contador++;
                }
                
                updateChunkUrgentes.remove(key);
            }
            
            if (contador > 0){
                return updatesCopia;
            }else{
               return null;
            }
        }else{
            //quiza se use mas adelante
            return null;
        }
    }
    
    @Override
    public void destroy(){
        super.destroy();
    }
}

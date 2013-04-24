/*
 * Manejo de los updates de los chunks sobre rootNode
 */
package bloques.graficos.control;

import bloques.manejo.chunks.BloquesChunk;
import bloques.manejo.chunks.BloquesChunkDatos;
import bloques.manejo.utiles.BloquesUtiles;
import com.jme3.app.Application;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.Timer;
import com.jme3.util.TangentBinormalGenerator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import jme3tools.optimize.GeometryBatchFactory;

/**
 *
 * @author mcarballo
 */
public class BloquesControlUpdates extends BloquesControlSetterGetter{
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
     */
    protected BloquesChunk chunkActualUC;
    /**
     *
     */
    protected String claveActualUC;
    /**
     *
     */
    protected Node bloquesMostrarUC;
    /**
     *
     */
    protected BloquesChunkDatos datosBloqueUC;
    /**
     *
     */
    protected String nomDatosBloqueUC;
    /**
     *
     */
    protected Node bloqueClonadoUC;
    
    /**
     *
     */
    protected BloquesChunk chunkActualUCU;
    /**
     *
     */
    protected String claveActualUCU;
    /**
     *
     */
    protected Node bloquesMostrarUCU;
    /**
     *
     */
    protected String nomDatosBloqueUCU;
    /**
     *
     */
    protected Node bloqueClonadoUCU;
    /**
     *
     */
    protected BloquesChunkDatos datosBloqueUCU;
    
    private float timer = 0f;
    
    
    /**
     *
     * @param app
     */
    public BloquesControlUpdates(Application app){
        super(app);
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        super.controlUpdate(tpf);
        
        timer += tpf;
        
        //las siguientes veces que se hace update se actualizan los chunks
        if (timer >= 1f){
            timer = 0f;
            
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
     * @throws InterruptedException 
     * @throws ExecutionException  
     */
    public void updateaChunks(Map<Integer,String> updatear) throws InterruptedException, ExecutionException{
        //Timer timer = app.getTimer();
        //float totalInicio = timer.getTimeInSeconds();

        int mostrar;
        
        SortedSet<Integer> keys = new TreeSet<Integer>(updatear.keySet());
        for (Integer key : keys) {
            claveActualUC = updatear.get(key);
            
            chunkActualUC = app.enqueue(new Callable<BloquesChunk>() {
                public BloquesChunk call() throws Exception {
                    return chunks.getChunk(claveActualUC);
                }
            }).get();
                        
            if (chunkActualUC != null){
                Node allNodos = chunkActualUC.getAllNodos();

                if (allNodos.getQuantity() <= 0){
                    bloquesMostrarUC = new Node(claveActualUC);

                    mostrar = 0;

                    for (Map.Entry<String,BloquesChunkDatos> entryBloquesDatos : chunkActualUC.getAllBloquesDatos().entrySet()){ 
                        datosBloqueUC = entryBloquesDatos.getValue();

                        if (datosBloqueUC != null && datosBloqueUC.getMostrar()){
                            nomDatosBloqueUC = entryBloquesDatos.getKey();

                            bloqueClonadoUC = bloques.generaBloqueClonado(nomDatosBloqueUC, datosBloqueUC, chunks,true);
                            if (bloqueClonadoUC != null){
                                bloquesMostrarUC.attachChild(bloqueClonadoUC);

                                //tambien lo guardamos como nodo para mejorar el rendimiento
                                chunkActualUC.setNodo(nomDatosBloqueUC, (Node) bloqueClonadoUC.clone());

                                mostrar = 1;
                            }

                        }
                    }
                }else{
                    bloquesMostrarUC = (Node) allNodos.clone();
                    mostrar = 1;
                }
                
                final Spatial optimizado = GeometryBatchFactory.optimize(bloquesMostrarUC);
                final int mostrarFinal = mostrar;

                app.enqueue(new Callable() {
                    public Object call() throws Exception {  
                        if (mostrarFinal == 1){
                            CollisionShape bloquesMostrarShape = CollisionShapeFactory.createMeshShape(optimizado);
                            RigidBodyControl bloquesMostrarControl = new RigidBodyControl(bloquesMostrarShape, 0);
                            optimizado.addControl(bloquesMostrarControl);

                            physics.getPhysicsSpace().add(optimizado);

                            if (BloquesUtiles.SOMBRAS){
                                optimizado.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
                                TangentBinormalGenerator.generate(optimizado);
                            }

                            spatial.getParent().attachChild(optimizado);
                        }

                        return null;
                    }
                });

                //float totalFinChunk = timer.getTimeInSeconds();
                //System.out.println(chunkActualUC+" : "+(totalFinChunk-totalInicioChunk));
            }
        }
        
        //float totalFin = timer.getTimeInSeconds();
        //System.out.println("Tiempo update"+(totalFin-totalInicio));
        
    }
    
    /**
     *
     * @param updatear
     */
    public void updateaChunksUrgentes(Map<Integer,BloquesChunk> updatear){
        Timer timer = app.getTimer();
        float totalInicio = timer.getTimeInSeconds();

        Map<String, Spatial> nodosUpdatar = new HashMap<String, Spatial>();  
        
        for (Map.Entry<Integer,BloquesChunk> entryChunk : updatear.entrySet()){    
            //float totalInicioChunk = timer.getTimeInSeconds();
            
            chunkActualUCU = entryChunk.getValue();
            claveActualUCU = chunkActualUCU.getNombreChunk();

            Node allNodos = chunkActualUCU.getAllNodos();
            
            if (allNodos.getQuantity() <= 0){
                bloquesMostrarUCU = new Node(claveActualUCU);

                for (Map.Entry<String,BloquesChunkDatos> entryBloquesDatos : chunkActualUCU.getAllBloquesDatos().entrySet()){ 
                    datosBloqueUCU = entryBloquesDatos.getValue();

                    if (datosBloqueUCU != null && datosBloqueUC.getMostrar()){
                        nomDatosBloqueUCU = entryBloquesDatos.getKey();                        

                        bloqueClonadoUCU = bloques.generaBloqueClonado(nomDatosBloqueUCU, datosBloqueUCU, chunks, true);
                        if (bloqueClonadoUCU != null){
                            bloquesMostrarUCU.attachChild(bloqueClonadoUCU);
                            
                            //tambien lo guardamos como nodo para mejorar el rendimiento
                            chunkActualUCU.setNodo(nomDatosBloqueUCU, (Node) bloqueClonadoUCU.clone());
                        }
                    }
                }
            }else{
                bloquesMostrarUCU = (Node) allNodos.clone();
            }
                
            
            nodosUpdatar.put(claveActualUCU,GeometryBatchFactory.optimize(bloquesMostrarUCU));
            
            //float totalFinChunk = timer.getTimeInSeconds();
            //System.out.println(claveActual+" : "+(totalFinChunk-totalInicioChunk));
        }
        
        
        final String[] keysNodosUpdatar = (String[])( nodosUpdatar.keySet().toArray( new String[nodosUpdatar.size()] ) );
        final Map<String, Spatial> nodosUpdatarFinal = nodosUpdatar;

        if (keysNodosUpdatar.length > 0){
            //float totalInicioChunk = timer.getTimeInSeconds();
            app.enqueue(new Callable() {
                public Object call() throws Exception {  
                    Node terreno = spatial.getParent();
                    
                    Spatial nodoOptimizado;
                    CollisionShape bloquesMostrarShape;
                    RigidBodyControl bloquesMostrarControl;
                    
                    for(int j=0; j<keysNodosUpdatar.length; j++){
                        nodoOptimizado = nodosUpdatarFinal.get(keysNodosUpdatar[j]);

                        bloquesMostrarShape = CollisionShapeFactory.createMeshShape((Node) nodoOptimizado);
                        bloquesMostrarControl = new RigidBodyControl(bloquesMostrarShape, 0);
                        nodoOptimizado.addControl(bloquesMostrarControl);

                        physics.getPhysicsSpace().add(nodoOptimizado);

                        //quitamos el chunk anterior y sus fisicas si hace falta
                        Spatial child = terreno.getChild(keysNodosUpdatar[j]);
                        if (child != null){
                            physics.getPhysicsSpace().remove(child.getControl(0)) ; 
                            terreno.detachChild(child);
                        }

                        if (BloquesUtiles.SOMBRAS){
                            nodoOptimizado.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
                            TangentBinormalGenerator.generate(nodoOptimizado);
                        }        
                      
                        terreno.attachChild(nodoOptimizado);
                    }

                    return null;
                }
            });
            //float totalFinChunk = timer.getTimeInSeconds();
            //System.out.println("Refresco Urgente"+" : "+(totalFinChunk-totalInicioChunk));
        }
        
        float totalFin = timer.getTimeInSeconds();
        System.out.println("Tiempo update"+(totalFin-totalInicio));
        
    }
    
    // A self-contained time-intensive task:
    private Callable<Boolean> procesaGraficosUpdates = new Callable<Boolean>(){
        @SuppressWarnings("SleepWhileInLoop")
        public Boolean call() throws Exception {
            int cosa = 0;
            
            while(cosa == 0){
                Map<Integer,String> updatear = app.enqueue(new Callable<Map<Integer,String>>() {
                    public Map<Integer,String> call() throws Exception {
                        return getUpdates(true);
                    }
                }).get();

                if (updatear != null){
                    updateaChunks(updatear); //para los updates lentos del terreno
                }

                Thread.sleep(500);
            }
            
            return false;
        }
    };
    
    // A self-contained time-intensive task:
    private Callable<Boolean> procesaGraficosUpdatesUrgentes = new Callable<Boolean>(){
        public Boolean call() throws Exception {
            Map<Integer,BloquesChunk> updatear = app.enqueue(new Callable<Map<Integer,BloquesChunk>>() {
                public Map<Integer,BloquesChunk> call() throws Exception {
                    return getUpdatesUrgentes(true);
                }
            }).get();
            
            if (updatear != null){
                updateaChunksUrgentes(updatear); //para los rapidos, destruir bloques por ejemplo
            }
            
            return false;
        }
    };
    
    /**
     *
     * @param vaciarUpdate
     * @return
     */
    public Map<Integer,String> getUpdates(Boolean vaciarUpdate){
        if (vaciarUpdate){
            if (updatesChunk == null){
                return null;
            }
            
            int contador = 0;
            
            Map<Integer,String> updatesCopia = new HashMap<Integer, String>();
            
            SortedSet<Integer> keys = new TreeSet<Integer>(updatesChunk.keySet());
            for (Integer key : keys) {
                
                updatesCopia.put(contador,updatesChunk.get(key));
                
                contador++;
                
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
    public Map<Integer,BloquesChunk> getUpdatesUrgentes(Boolean vaciarUpdate){
        if (vaciarUpdate){
            if (updateChunkUrgentes == null){
                return null;
            }
            
            int contador = 0;
            
            Map<Integer,BloquesChunk> updatesCopia = new HashMap<Integer, BloquesChunk>();
            
            SortedSet<Integer> keys = new TreeSet<Integer>(updateChunkUrgentes.keySet());
            for (Integer key : keys) {
                BloquesChunk chunk = chunks.getChunk(updateChunkUrgentes.get(key));
                
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

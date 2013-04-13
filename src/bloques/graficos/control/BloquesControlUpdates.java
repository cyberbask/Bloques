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
import com.jme3.math.Vector3f;
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
     * @param app
     */
    public BloquesControlUpdates(Application app){
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
    public void updateaChunks(Map<Integer,BloquesChunk> updatear){
        //Timer timer = app.getTimer();
        //float totalInicio = timer.getTimeInSeconds();

        BloquesChunk chunkActual;
        String claveActual;
        Node bloquesMostrar;
        
        BloquesChunkDatos datosBloque;
        String nomDatosBloque;
        Node bloqueClonado;
        Vector3f coordenadas;
        int[] carasbloquesVecinos;
        int[][] bloquesVecinos;
        int contaCarasQuitadas;
        int mostrar;
            
        for (Map.Entry<Integer,BloquesChunk> entryChunk : updatear.entrySet()){    
            //float totalInicioChunk = timer.getTimeInSeconds();
            
            chunkActual = entryChunk.getValue();
            claveActual = chunkActual.getNombreChunk();

            bloquesMostrar = new Node(claveActual);
            
            mostrar = 0;
            
            for (Map.Entry<String,BloquesChunkDatos> entryBloquesDatos : chunkActual.getAllBloquesDatos().entrySet()){ 
                datosBloque = entryBloquesDatos.getValue();
                
                if (datosBloque != null){
                    nomDatosBloque = entryBloquesDatos.getKey();
                    
                    bloqueClonado = bloques.getBloqueGenerado(datosBloque.getNomBloque());

                    //coordenadas reales del cubo, no las del chunk
                    coordenadas = BloquesUtiles.devuelveCoordenadasBloque(nomDatosBloque);

                    //le quitamos las caras que no se ven
                    contaCarasQuitadas = 0;
                    bloquesVecinos = chunks.getBloquesVecinos(coordenadas);
                    carasbloquesVecinos = chunks.getCarasAPartirDeBloquesVecinos(bloquesVecinos);
                    datosBloque.setCaras(carasbloquesVecinos);

                    for(int h = 0;h<6;h++){
                        if (carasbloquesVecinos[h] == 0){ //si no hay cara
                            bloqueClonado.detachChildNamed("Cara-"+h); 
                            contaCarasQuitadas++;
                        }
                    }

                    if (contaCarasQuitadas < 6){
                        bloqueClonado.setLocalTranslation(coordenadas.x,coordenadas.y,coordenadas.z + BloquesUtiles.TAMANO_BLOQUE);
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
                        
                        if (BloquesUtiles.SOMBRAS){
                            TangentBinormalGenerator.generate(optimizado);
                            optimizado.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
                        }
                        
                        spatial.getParent().attachChild(optimizado);
                    }

                    return null;
                }
            });
            
            //float totalFinChunk = timer.getTimeInSeconds();
            //System.out.println(claveActual+" : "+(totalFinChunk-totalInicioChunk));
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
        
        BloquesChunk chunkActual;
        String claveActual;
        Node bloquesMostrar;
        
        String nomDatosBloque;
        Node bloqueClonado;
        Vector3f coordenadas;
        int contaCarasQuitadas;
        int[] carasbloquesVecinos;
        BloquesChunkDatos datosBloque;
        
        for (Map.Entry<Integer,BloquesChunk> entryChunk : updatear.entrySet()){    
            //float totalInicioChunk = timer.getTimeInSeconds();
            
            chunkActual = entryChunk.getValue();
            claveActual = chunkActual.getNombreChunk();

            bloquesMostrar = new Node(claveActual);
            
            
            
            for (Map.Entry<String,BloquesChunkDatos> entryBloquesDatos : chunkActual.getAllBloquesDatos().entrySet()){ 
                datosBloque = entryBloquesDatos.getValue();
                
                if (datosBloque != null){
                    nomDatosBloque = entryBloquesDatos.getKey();

                    bloqueClonado = bloques.getBloqueGenerado(datosBloque.getNomBloque());

                    //coordenadas reales del cubo, no las del chunk
                    coordenadas = BloquesUtiles.devuelveCoordenadasBloque(nomDatosBloque);

                    //le quitamos las caras que no se ven
                    contaCarasQuitadas = 0;
                    carasbloquesVecinos = datosBloque.getCaras();

                    for(int h = 0;h<6;h++){
                        if (carasbloquesVecinos[h] == 0){ //si no hay cara
                            bloqueClonado.detachChildNamed("Cara-"+h); 
                            contaCarasQuitadas++;
                        }
                    }

                    if (contaCarasQuitadas < 6){
                        bloqueClonado.setLocalTranslation(coordenadas.x,coordenadas.y,coordenadas.z + BloquesUtiles.TAMANO_BLOQUE);
                        bloquesMostrar.attachChild(bloqueClonado);
                    }
                }
            }
            
            nodosUpdatar.put(claveActual,GeometryBatchFactory.optimize(bloquesMostrar));
            
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
                            TangentBinormalGenerator.generate(nodoOptimizado);
                            nodoOptimizado.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
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
        public Boolean call() throws Exception {
            Map<Integer,BloquesChunk> updatear = app.enqueue(new Callable<Map<Integer,BloquesChunk>>() {
                public Map<Integer,BloquesChunk> call() throws Exception {
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
            Map<Integer,BloquesChunk> updatear = app.enqueue(new Callable<Map<Integer,BloquesChunk>>() {
                public Map<Integer,BloquesChunk> call() throws Exception {
                    return getUpdatesUrgentes(true);
                }
            }).get();
            
            if (updatear != null){
                updateaChunksUrgentes(updatear); //2 es para los rapidos, destruir bloques por ejemplo
            }
            
            return false;
        }
    };
    
    /**
     *
     * @param vaciarUpdate
     * @return
     */
    public Map<Integer,BloquesChunk> getUpdates(Boolean vaciarUpdate){
        if (vaciarUpdate){
            if (updatesChunk == null){
                return null;
            }
            
            int contador = 0;
            
            Map<Integer,BloquesChunk> updatesCopia = new HashMap<Integer, BloquesChunk>();
            
            SortedSet<Integer> keys = new TreeSet<Integer>(updatesChunk.keySet());
            for (Integer key : keys) {
                BloquesChunk chunk = chunks.getChunk(updatesChunk.get(key));
                
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

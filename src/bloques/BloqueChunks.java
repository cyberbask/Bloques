/*
 * Clase para manejar un grupo de chunks
 */
package bloques;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author cyberbask
 */
public class BloqueChunks {
    Map<String,BloqueChunk> chunks = new HashMap<String, BloqueChunk>();
    
    /**
     *
     */
    public BloqueChunks(){
        
    }
    
    /**
     *
     * @param x
     * @param y
     * @param z
     * @param chunk
     */
    public void setChunk(int x, int y, int z, BloqueChunk chunk){
        chunks.put(BloqueChunkUtiles.generarNombreChunk(BloqueChunkUtiles.calculaCoordenadasChunk(x, y, z)), chunk);
    }
    
    /**
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public BloqueChunk getChunk(int x, int y, int z){
        return chunks.get(BloqueChunkUtiles.generarNombreChunk(BloqueChunkUtiles.calculaCoordenadasChunk(x, y, z)));
    }
    
    /**
     *
     * @param x
     * @param y
     * @param z
     * @param datos
     */
    public void setBloque(int x, int y, int z, BloqueChunkDatos datos){
        BloqueChunk chunk = getChunk(x, y, z);
        
        //si es nuevo creamos un nuevo chunk y le asignamos el bloque
        if (chunk == null){ 
            chunk = new BloqueChunk(); 
            setChunk(x, y, z, chunk);
        }
      
        chunk.setDatosBloque(BloqueChunkUtiles.calculaCoordenadasBloqueDentroDeChunk(x, y, z), datos);
    }
    
    /**
     *
     * @param x
     * @param y
     * @param z
     * @param chunk
     * @return
     */
    public BloqueChunkDatos getBloque(int x, int y, int z, BloqueChunk chunk){        
        if (chunk != null){ 
            return chunk.getDatosBloque(BloqueChunkUtiles.calculaCoordenadasBloqueDentroDeChunk(x, y, z));
        }
            
        return null;
    }
    
    /**
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public BloqueChunkDatos getBloquePorCoordenadas(int x, int y, int z){  
        BloqueChunk chunk = getChunk(x, y, z);
        return getBloque(x, y, z,chunk);
    }
    
    /**
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public BloqueChunkDatos getBloqueAPartirDeChunk(int x, int y, int z){
        BloqueChunk chunk = getChunk(x, y, z);
        
        if (chunk != null){ 
            return chunk.getDatosBloque(BloqueChunkUtiles.calculaCoordenadasBloqueDentroDeChunk(x, y, z));
        }
            
        return null;
    }
    
    /**
     *
     * @param x
     * @param z
     * @return
     */
    public BloqueChunks getGrupoChunks(int x, int z){
        int maxbucle = BloqueChunkUtiles.MAX_ALTURA_BLOQUES / BloqueChunkUtiles.TAMANO_CHUNK;
        
        BloqueChunks grupoChunks = new BloqueChunks();
        
        for (int i=0;i<maxbucle;i++){
            int y = i * BloqueChunkUtiles.TAMANO_CHUNK * BloqueChunkUtiles.TAMANO_BLOQUE;
            
            BloqueChunk chunk = getChunk(x, y, z);
            if (chunk != null){
               grupoChunks.setChunk(x, y, z, chunk);
            }
        }

        return grupoChunks;
    }
    
    /**
     *
     * @return
     */
    public Map<String, BloqueChunk> getAllChunks(){
        return chunks;
    }
    
    /**
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public Boolean getBloqueVecino(int x, int y, int z){
        BloqueChunk chunk = getChunk(x, y, z);
        if (chunk == null){
            return false;
        }else{
            BloqueChunkDatos bloque = getBloque(x, y, z, chunk);
            if (bloque != null) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public int[][] getBloquesVecinos(int x, int y, int z){
        //la ultima posicion del array, la 3 nos dice si es de un chunk vecino
        int bloques[][] = new int[6][4]; 
        
        //inicializamos
        for(int i=0;i<6;i++) {
            bloques[i] = null;
        }
        
        BloqueChunkDatos bloque;
        BloqueChunk chunk;
        String nombreChunkOriginal = BloqueChunkUtiles.generarNombreChunk(BloqueChunkUtiles.calculaCoordenadasChunk(x, y, z));
        
        int xVeci = x;
        int yVeci = y;
        int zVeci = z;
        int posicion;
        
        //tenemos que sacar los bloques alrededor al proporcionado
        for(int i=0;i<6;i++) {
            posicion = i;
            
            switch(i){
                case 0:
                  //frontal z+1
                    xVeci = x; yVeci = y; zVeci = z + BloqueChunkUtiles.TAMANO_BLOQUE;  
                break;
                case 1:
                    //lateral derecha x+1
                    xVeci = x + BloqueChunkUtiles.TAMANO_BLOQUE; yVeci = y; zVeci = z;  
                break;
                case 2:
                    //trasera z-1
                    xVeci = x; yVeci = y; zVeci = z - BloqueChunkUtiles.TAMANO_BLOQUE;  
                break;
                case 3:
                    //lateral izquierda x-1
                    xVeci = x - BloqueChunkUtiles.TAMANO_BLOQUE; yVeci = y; zVeci = z;  
                break;
                case 4:
                    //Superior y+1
                    xVeci = x; yVeci = y + BloqueChunkUtiles.TAMANO_BLOQUE; zVeci = z;  
                break;
                case 5:
                    //inferior y-1
                    xVeci = x; yVeci = y - BloqueChunkUtiles.TAMANO_BLOQUE; zVeci = z;  
                break;
            }
            
            chunk = getChunk(xVeci, yVeci, zVeci);
            if (chunk == null){
                //TODO por ahora si no hay chunk vecino, se marca para que no se muestre la cara
                bloques[posicion] = new int[4];
                //bloques[posicion][3] = 2; //sin chunk vecino
            }else{
                bloque = getBloque(xVeci, yVeci, zVeci, chunk);
                if (bloque != null) {
                    bloques[posicion] = new int[4];
                    bloques[posicion][0] = xVeci;
                    bloques[posicion][1] = yVeci;
                    bloques[posicion][2] = zVeci;

                    String nombreChunkVecino = BloqueChunkUtiles.generarNombreChunk(BloqueChunkUtiles.calculaCoordenadasChunk(xVeci, yVeci, zVeci));

                    if (nombreChunkVecino.equals(nombreChunkOriginal)){
                        bloques[posicion][3] = 0;  
                    }else{
                        bloques[posicion][3] = 1; //chunk vecino
                    }
                }
            }
        }
        
        return bloques;
    }  
    
    /**
     *
     * @param bloques
     * @return
     */
    public int[] getCarasAPartirDeBloquesVecinos(int[][] bloques){
        int caras[] = new int[6];
        
        for(int i=0;i<6;i++) {
            caras[i] = 1;
        }
        
        //TODO  Por ahora si el chunk o bloque vecino no existe directamente no se muestra la cara
        //      con esto evitamos que salgan las caras de los bordes del mundo
        
        for(int i=0;i<6;i++) {
            if (bloques[i] != null){ //si hay bloque
                caras[i] = 0;
            }else{
                caras[i] = 1;
            }
        }
        
        return caras;
    }
    
    /**
     *
     * @param x
     * @param z
     * @return
     */
    public int getBloqueConMasAltura(int x, int z){
        int devolverAltura = 0;
        
        int maxbucle = BloqueChunkUtiles.MAX_ALTURA_BLOQUES / BloqueChunkUtiles.TAMANO_CHUNK;
        
        for (int i=(maxbucle - 1);i>=0;i--){
            int a = i * BloqueChunkUtiles.TAMANO_CHUNK * BloqueChunkUtiles.TAMANO_BLOQUE;
            
            BloqueChunk chunk = getChunk(x, a, z);
            if (chunk != null){
               for(int y = (maxbucle - 1);y>=0;y--){
                    int b = y + a;
                    BloqueChunkDatos datosBloque = getBloque(x, y, z, chunk);
                            
                    if (datosBloque != null){
                        devolverAltura = b;
                        return devolverAltura;
                    }
                }
            }
        }
        
        return devolverAltura;
    }
    
    /**
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public Boolean destruyeBloque(int x, int y, int z){
        BloqueChunk chunk = getChunk(x, y, z);
        
        if (chunk != null){
            int[] calculaCoordenadasBloqueDentroDeChunk = BloqueChunkUtiles.calculaCoordenadasBloqueDentroDeChunk(x, y, z);
        
            //System.out.println("destruye: "+calculaCoordenadasBloqueDentroDeChunk[0]+"-"+calculaCoordenadasBloqueDentroDeChunk[1]+"-"+calculaCoordenadasBloqueDentroDeChunk[2]);
            
            chunk.setDatosBloque(calculaCoordenadasBloqueDentroDeChunk, null);
            
            return true;
        }
        
        return false;
    }
    
    /**
     *
     * @param x
     * @param y
     * @param z
     * @param nomBloque
     * @return
     */
    public Boolean colocaBloque(int x, int y, int z, String nomBloque){
        BloqueChunk chunk = getChunk(x, y, z);
        
        if (chunk != null){
            int[] calculaCoordenadasBloqueDentroDeChunk = BloqueChunkUtiles.calculaCoordenadasBloqueDentroDeChunk(x, y, z);
        
            System.out.println("coloca: "+calculaCoordenadasBloqueDentroDeChunk[0]+"-"+calculaCoordenadasBloqueDentroDeChunk[1]+"-"+calculaCoordenadasBloqueDentroDeChunk[2]);
            
            BloqueChunkDatos bloqueChunkDatos = new BloqueChunkDatos();
            bloqueChunkDatos.setNomBloque(nomBloque);
            
            chunk.setDatosBloque(calculaCoordenadasBloqueDentroDeChunk, bloqueChunkDatos);
            
            return true;
        }
        
        return false;
    }
    
    /**
     *
     * @param x
     * @param y
     * @param z
     */
    public void setCarasVecinas(int x, int y, int z){
        int[][] bloquesVecinosDelBloqueVecino = getBloquesVecinos(x, y, z);
        int[] carasbloquesVecinos = getCarasAPartirDeBloquesVecinos(bloquesVecinosDelBloqueVecino);

        //guardamos sus caras
        BloqueChunk chunk = getChunk(x, y, z);
        BloqueChunkDatos datosBloque = chunk.getDatosBloque(BloqueChunkUtiles.calculaCoordenadasBloqueDentroDeChunk(x, y, z));
        datosBloque.setCaras(carasbloquesVecinos);
    }
            
}

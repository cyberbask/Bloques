/*
 * Datos de los bloques, setter/getter, etc..
 */
package bloques;

/**
 *
 * @author mcarballo
 */
public class BloqueGenericosDatos {
    private String nombreTextura; 
    private int[] posicionTexturaX = new int[6];
    private int[] posicionTexturaY = new int[6];
    
    /**
     *
     */
    public BloqueGenericosDatos(){
        
    }
    
    /**
     *
     * @return
     */
    public String getNombreTextura(){
        return nombreTextura;
    }
    
    /**
     *
     * @param nom
     */
    public void setNombreTextura(String nom){
        nombreTextura = nom;
    }
    
    /**
     *
     * @return
     */
    public int[] getPosicionTexturasX(){
        return posicionTexturaX;
    }
    
    /**
     *
     * @param pos
     * @param val
     */
    public void setPosicionTexturasX(int pos, int val){
        posicionTexturaX[pos] = val;
    }
    
    /**
     *
     * @return
     */
    public int[] getPosicionTexturasY(){
        return posicionTexturaY;
    }
    
    /**
     *
     * @param pos
     * @param val
     */
    public void setPosicionTexturasY(int pos, int val){
        posicionTexturaY[pos] = val;
    }
}

/*
 * Datos basicos y por defecto de los bloques
 */
package bloques;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mcarballo
 */
public class Bloques {
    Map<String,BloquesDatos> bloques = new HashMap<String,BloquesDatos>();
    
    /**
     *
     */
    public Bloques(){
        
    }
    
    /**
     * Devuelve los datos del tipo de bloque solicitado
     * @param nomBloque
     * @return
     */
    public BloquesDatos getBloqueTipo(String nomBloque){
        if (bloques.get(nomBloque) != null){
            return bloques.get(nomBloque);
        }else{
            //TODO sacar los datos de algun sitio real
            
            BloquesDatos datosBloque = new BloquesDatos();
            
            //bloque tierra
            if ("Tierra".equals(nomBloque)){
               datosBloque.setNombreTextura("bloques1");
               
               datosBloque.setPosicionTexturasX(0, 1);//frontal
               datosBloque.setPosicionTexturasY(0, 1);
               
               datosBloque.setPosicionTexturasX(1, 1);//lateral derecha
               datosBloque.setPosicionTexturasY(1, 2);
               
               datosBloque.setPosicionTexturasX(2, 1);//trasera
               datosBloque.setPosicionTexturasY(2, 3);
               
               datosBloque.setPosicionTexturasX(3, 1);//lateral izquierda
               datosBloque.setPosicionTexturasY(3, 4);
               
               datosBloque.setPosicionTexturasX(4, 13);//Superior
               datosBloque.setPosicionTexturasY(4, 6);
               
               datosBloque.setPosicionTexturasX(5, 1); //inferior
               datosBloque.setPosicionTexturasY(5, 6);
               
               bloques.put(nomBloque,datosBloque);
               
               return datosBloque;
            }
        }
        
        return null;
    }
   
}

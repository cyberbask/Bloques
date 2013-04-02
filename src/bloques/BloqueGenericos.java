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
public class BloqueGenericos {
    Map<String,BloqueGenericosDatos> bloques = new HashMap<String,BloqueGenericosDatos>();
    
    /**
     *
     */
    public BloqueGenericos(){
        
    }
    
    /**
     * Devuelve los datos del tipo de bloque solicitado
     * @param nomBloque
     * @return
     */
    public BloqueGenericosDatos getBloqueTipo(String nomBloque){
        if (bloques.get(nomBloque) != null){
            return bloques.get(nomBloque);
        }else{
            //TODO sacar los datos de algun sitio real
            
            BloqueGenericosDatos datosBloque = new BloqueGenericosDatos();
            
            //bloque tierra
            if ("Tierra".equals(nomBloque)){
               datosBloque.setNombreTextura("bloques1");
               
               datosBloque.setPosicionTexturasX(0, 1);//frontal
               datosBloque.setPosicionTexturasY(0, 1);
               
               datosBloque.setPosicionTexturasX(1, 2);//lateral derecha
               datosBloque.setPosicionTexturasY(1, 1);
               
               datosBloque.setPosicionTexturasX(2, 3);//trasera
               datosBloque.setPosicionTexturasY(2, 1);
               
               datosBloque.setPosicionTexturasX(3, 4);//lateral izquierda
               datosBloque.setPosicionTexturasY(3, 1);
               
               datosBloque.setPosicionTexturasX(4, 5);//Superior
               datosBloque.setPosicionTexturasY(4, 1);
               
               datosBloque.setPosicionTexturasX(5, 6); //inferior
               datosBloque.setPosicionTexturasY(5, 1);
               
               bloques.put(nomBloque,datosBloque);
               
               return datosBloque;
            }
            
            //bloque tierra
            if ("Roca".equals(nomBloque)){
               datosBloque.setNombreTextura("bloques1");
               
               datosBloque.setPosicionTexturasX(0, 1);//frontal
               datosBloque.setPosicionTexturasY(0, 2);
               
               datosBloque.setPosicionTexturasX(1, 2);//lateral derecha
               datosBloque.setPosicionTexturasY(1, 2);
               
               datosBloque.setPosicionTexturasX(2, 3);//trasera
               datosBloque.setPosicionTexturasY(2, 2);
               
               datosBloque.setPosicionTexturasX(3, 4);//lateral izquierda
               datosBloque.setPosicionTexturasY(3, 2);
               
               datosBloque.setPosicionTexturasX(4, 5);//Superior
               datosBloque.setPosicionTexturasY(4, 2);
               
               datosBloque.setPosicionTexturasX(5, 6); //inferior
               datosBloque.setPosicionTexturasY(5, 2);
               
               bloques.put(nomBloque,datosBloque);
               
               return datosBloque;
            }
        }
        
        return null;
    }
   
}

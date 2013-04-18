/*
 * Datos basicos y por defecto de los bloques
 */
package bloques.graficos.generabloque;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mcarballo
 */
public class BloquesGenericos {
    Map<String,BloquesGenericosDatos> bloques = new HashMap<String,BloquesGenericosDatos>();
    
    /**
     *
     */
    public BloquesGenericos(){
        
    }
    
    /**
     * Devuelve los datos del tipo de bloque solicitado
     * @param nomBloque
     * @return
     */
    public BloquesGenericosDatos getBloqueTipo(String nomBloque){
        if (bloques.get(nomBloque) != null){
            return bloques.get(nomBloque);
        }else{
            //TODO sacar los datos de algun sitio real
            
            BloquesGenericosDatos datosBloque = new BloquesGenericosDatos();
            
            //bloque especial WireFrame
            if ("WireFrame".equals(nomBloque)){
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
            
            //bloque tierra
            if ("Tierra".equals(nomBloque)){
               datosBloque.setNombreTextura("bloques1");
               
               datosBloque.setPosicionTexturasX(0, 3);//frontal
               datosBloque.setPosicionTexturasY(0, 1);
               
               datosBloque.setPosicionTexturasX(1, 3);//lateral derecha
               datosBloque.setPosicionTexturasY(1, 1);
               
               datosBloque.setPosicionTexturasX(2, 3);//trasera
               datosBloque.setPosicionTexturasY(2, 1);
               
               datosBloque.setPosicionTexturasX(3, 3);//lateral izquierda
               datosBloque.setPosicionTexturasY(3, 1);
               
               datosBloque.setPosicionTexturasX(4, 3);//Superior
               datosBloque.setPosicionTexturasY(4, 1);
               
               datosBloque.setPosicionTexturasX(5, 3); //inferior
               datosBloque.setPosicionTexturasY(5, 1);
               
               bloques.put(nomBloque,datosBloque);
               
               return datosBloque;
            }
            
            //bloque roca
            if ("Roca".equals(nomBloque)){
               datosBloque.setNombreTextura("bloques1");
               
               datosBloque.setPosicionTexturasX(0, 1);//frontal
               datosBloque.setPosicionTexturasY(0, 2);
               
               datosBloque.setPosicionTexturasX(1, 1);//lateral derecha
               datosBloque.setPosicionTexturasY(1, 2);
               
               datosBloque.setPosicionTexturasX(2, 1);//trasera
               datosBloque.setPosicionTexturasY(2, 2);
               
               datosBloque.setPosicionTexturasX(3, 1);//lateral izquierda
               datosBloque.setPosicionTexturasY(3, 2);
               
               datosBloque.setPosicionTexturasX(4, 1);//Superior
               datosBloque.setPosicionTexturasY(4, 2);
               
               datosBloque.setPosicionTexturasX(5, 1); //inferior
               datosBloque.setPosicionTexturasY(5, 2);
               
               bloques.put(nomBloque,datosBloque);
               
               return datosBloque;
            }
            
            //bloque arena
            if ("Arena".equals(nomBloque)){
               datosBloque.setNombreTextura("bloques1");
               
               datosBloque.setPosicionTexturasX(0, 7);//frontal
               datosBloque.setPosicionTexturasY(0, 15);
               
               datosBloque.setPosicionTexturasX(1, 7);//lateral derecha
               datosBloque.setPosicionTexturasY(1, 15);
               
               datosBloque.setPosicionTexturasX(2, 7);//trasera
               datosBloque.setPosicionTexturasY(2, 15);
               
               datosBloque.setPosicionTexturasX(3, 7);//lateral izquierda
               datosBloque.setPosicionTexturasY(3, 15);
               
               datosBloque.setPosicionTexturasX(4, 7);//Superior
               datosBloque.setPosicionTexturasY(4, 15);
               
               datosBloque.setPosicionTexturasX(5, 7); //inferior
               datosBloque.setPosicionTexturasY(5, 15);
               
               bloques.put(nomBloque,datosBloque);
               
               return datosBloque;
            }
            
            //bloque Hierba
            if ("Hierba".equals(nomBloque)){
               datosBloque.setNombreTextura("bloques1");
               
               datosBloque.setPosicionTexturasX(0, 4);//frontal
               datosBloque.setPosicionTexturasY(0, 1);
               
               datosBloque.setPosicionTexturasX(1, 4);//lateral derecha
               datosBloque.setPosicionTexturasY(1, 1);
               
               datosBloque.setPosicionTexturasX(2, 4);//trasera
               datosBloque.setPosicionTexturasY(2, 1);
               
               datosBloque.setPosicionTexturasX(3, 4);//lateral izquierda
               datosBloque.setPosicionTexturasY(3, 1);
               
               datosBloque.setPosicionTexturasX(4, 2);//Superior
               datosBloque.setPosicionTexturasY(4, 10);
               
               datosBloque.setPosicionTexturasX(5, 3); //inferior
               datosBloque.setPosicionTexturasY(5, 1);
               
               bloques.put(nomBloque,datosBloque);
               
               return datosBloque;
            }
            
            //bloque Madera
            if ("Madera".equals(nomBloque)){
               datosBloque.setNombreTextura("bloques1");
               
               datosBloque.setPosicionTexturasX(0, 5);//frontal
               datosBloque.setPosicionTexturasY(0, 2);
               
               datosBloque.setPosicionTexturasX(1, 5);//lateral derecha
               datosBloque.setPosicionTexturasY(1, 2);
               
               datosBloque.setPosicionTexturasX(2, 5);//trasera
               datosBloque.setPosicionTexturasY(2, 2);
               
               datosBloque.setPosicionTexturasX(3, 5);//lateral izquierda
               datosBloque.setPosicionTexturasY(3, 2);
               
               datosBloque.setPosicionTexturasX(4, 6);//Superior
               datosBloque.setPosicionTexturasY(4, 2);
               
               datosBloque.setPosicionTexturasX(5, 6); //inferior
               datosBloque.setPosicionTexturasY(5, 2);
               
               bloques.put(nomBloque,datosBloque);
               
               return datosBloque;
            }
            
            //bloque Hojas
            if ("Hojas".equals(nomBloque)){
               datosBloque.setNombreTextura("bloques1");
               
               datosBloque.setPosicionTexturasX(0, 6);//frontal
               datosBloque.setPosicionTexturasY(0, 9);
               
               datosBloque.setPosicionTexturasX(1, 6);//lateral derecha
               datosBloque.setPosicionTexturasY(1, 9);
               
               datosBloque.setPosicionTexturasX(2, 6);//trasera
               datosBloque.setPosicionTexturasY(2, 9);
               
               datosBloque.setPosicionTexturasX(3, 6);//lateral izquierda
               datosBloque.setPosicionTexturasY(3, 9);
               
               datosBloque.setPosicionTexturasX(4, 6);//Superior
               datosBloque.setPosicionTexturasY(4, 9);
               
               datosBloque.setPosicionTexturasX(5, 6); //inferior
               datosBloque.setPosicionTexturasY(5, 9);
               
               bloques.put(nomBloque,datosBloque);
               
               return datosBloque;
            }
        }
        
        return null;
    }
   
}

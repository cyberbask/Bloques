/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bloquesnode.graficos.control;

import com.jme3.app.Application;

/**
 *
 * @author mcarballo
 */
public class BloquesNodeControlSetterGetter extends BloquesNodeControlBasic{
    public BloquesNodeControlSetterGetter(Application app){
        super(app);
    }
    
    public int getPorcentajeGeneradoTerreno(){
        return bloqueGeneraTerreno.porcentajeGenerado;
    }
}

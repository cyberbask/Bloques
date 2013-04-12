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
public class BloquesNodeControlUpdates extends BloquesNodeControlSetterGetter{
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
    }
    
    @Override
    public void destroy(){
        super.destroy();
    }
}

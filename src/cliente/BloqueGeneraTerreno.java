/*
 * Aqui se generaran los chunks y el terreno en general
 */
package cliente;

import bloques.BloquesDatos;
import com.jme3.app.Application;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.heightmap.HillHeightMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author mcarballo
 */
public class BloqueGeneraTerreno extends BloqueGeneraBloque{
    /**
     *
     * @param app
     */
    public BloqueGeneraTerreno(Application app){
        super(app);
    }
    
    private HillHeightMap generateTerrainconReturn(){
        byte[] result= new byte[5];
        Random random= new Random();
        random.nextBytes(result);


        HillHeightMap heightmap = null;
        HillHeightMap.NORMALIZE_RANGE = 100; // optional
        try {
            heightmap = new HillHeightMap(513, 1000, 20, 40, result[0]); // byte 3 is a random seed
        } catch (Exception ex) {

        }
        //heightmap.setHeightScale(0.001f);
        heightmap.load();

        return heightmap;     
    }
        
    /**
     *
     * @return
     */
    public Node generaTerreno(){
        Node bloquesFinales = new Node("bloques");
        Map<String,Node> bloquesGenerados = new HashMap<String,Node>();
        
        //TODO material con iluminacion
        
        //tierra
        BloquesDatos bloquesDatos = bloques.getBloqueTipo("Tierra");
        
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setTexture("ColorMap", atlas.getAtlasTexture(bloquesDatos.getNombreTextura()));    
        mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha); //transparencia
        
        BloqueGeneraBloque generaBloque = new BloqueGeneraBloque(app);
        Node bloque = generaBloque.makeBloque(1,"Tierra");
        
        bloquesGenerados.put("Tierra",bloque);
        
        //roca
        bloquesDatos = bloques.getBloqueTipo("Roca");
        
        mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setTexture("ColorMap", atlas.getAtlasTexture(bloquesDatos.getNombreTextura()));    
        mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha); //transparencia
        
        generaBloque = new BloqueGeneraBloque(app);
        bloque = generaBloque.makeBloque(1,"Roca");
        
        bloquesGenerados.put("Roca",bloque);
        
        
        HillHeightMap heightmap = generateTerrainconReturn();
        
        int conta = 0;
        int y = 0;
        for (int x = 0;x<200;x++){
            for (int z = 0;z<200;z++){
                int originalY = y;
                y = (int) heightmap.getScaledHeightAtPoint(x,z);
                if (y == 0){
                    y = originalY;
                }

                Spatial bloqueClonado;
                if (conta == 0){ //tierra
                    bloqueClonado = bloquesGenerados.get("Tierra").clone();
                    conta = 1;
                }else{ //roca
                    bloqueClonado = bloquesGenerados.get("Roca").clone();
                    conta = 0;
                }

                bloqueClonado.move(x,y,z);

                bloquesFinales.attachChild(bloqueClonado);
            }
        }
        
        bloquesFinales.setMaterial(mat1);
        
        return bloquesFinales;
    }
}

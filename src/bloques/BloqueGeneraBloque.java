/*
 * Clase para generar un bloque mediante triangulos, sus caras, las texturas de cada cara ...
 */
package bloques;

import cliente.TextureAtlasJuego;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

/**
 * 
 * @author mcarballo
 */
public class BloqueGeneraBloque {
    /**
     *
     */
    protected SimpleApplication app;
    /**
     *
     */
    protected AssetManager      assetManager;
    
    /**
     *
     */
    protected TextureAtlasJuego atlas;
    /**
     *
     */
    protected BloqueGenericos bloques;
    
    /**
     *
     * @param app
     */
    public BloqueGeneraBloque(Application app){
        this.app = (SimpleApplication) app;
        this.assetManager = this.app.getAssetManager();
        
        atlas = new TextureAtlasJuego(app);
        atlas.setTexturesInAtlas();
        
        bloques = new BloqueGenericos();
    }
    
    /**
     *
     * @param tamano
     * @param tipo 
     * @return
     */
    protected Node makeBloque(int tamano,String tipo) {
        //TODO Pasar datos de bloques adyacentes para no generar las caras que no se van a usar
        
        Node bloque = new Node("bloque");
        BloqueGenericosDatos bloquesDatos = bloques.getBloqueTipo(tipo);
        
        float tamanoTile = (float) atlas.getAnchoImagenTextura() / 16f;
        tamanoTile = tamanoTile / (float) atlas.getAnchoImagenTextura();
        
        for (int i = 0; i<6; i++){
            Mesh m = new Mesh();

            // Vertices, posiciones en el espacio
            Vector3f [] vertices = new Vector3f[4];
            vertices[0] = new Vector3f(0,0,0);
            vertices[1] = new Vector3f(tamano,0,0);
            vertices[2] = new Vector3f(0,tamano,0);
            vertices[3] = new Vector3f(tamano,tamano,0);

            //Posiciones de las texturas
            float x = (( (float) bloquesDatos.getPosicionTexturasX()[i] ) - 1 ) * tamanoTile; //siempre es uno menos, el cero cuenta
            float y = ((( (float) bloquesDatos.getPosicionTexturasY()[i] ) - 1 ) * -1) * tamanoTile; //siempre es uno menos, el cero cuenta

            //correciones para las posiciones de las texturas - revisar mas adelante
            float sumapixel = 0.0005f; //x a la izquierda del cuadrado
            float sumapixel2 = -0.0005f; //y arriba del cuadrado
            float sumapixel3 = 0.0005f;  //y abajo del cuadrado
            float sumapixel4 = -0.0005f; //x a la derecha del cuadrado
            
            /** /
            sumapixel = 0;
            sumapixel2 = 0;
            sumapixel3 = 0;
            sumapixel4 = 0;
            /**/

            Vector2f [] texCoord = new Vector2f[4];
            texCoord[0] = new Vector2f(x + sumapixel,1 - tamanoTile + y + sumapixel3);
            texCoord[1] = new Vector2f(tamanoTile + x + sumapixel4,1 - tamanoTile + y + sumapixel3);
            texCoord[2] = new Vector2f(x + sumapixel,1 + y + sumapixel2);
            texCoord[3] = new Vector2f(tamanoTile + x + sumapixel4,1 + y + sumapixel2);

            // Indices de los vertices, en que orden se construyen
            int [] indexes = {2,0,1,1,3,2};

            // Setting buffers
            m.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
            m.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
            m.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indexes));
            m.updateBound();

            Geometry cara = new Geometry("Cara-"+String.valueOf(i)+"-Bloque-x-y", m);   

            //colocamos las caras en su sitio
            switch(i){
                case 0: //cara 1
                    //esta ya va bien de serie
                break;
                case 1: //cara 2
                   cara.move(1,0,0); 
                   cara.setLocalRotation(new Quaternion().fromAngleAxis(90*FastMath.DEG_TO_RAD, new Vector3f(0,1,0)));
                break;
                case 2: //cara 3
                   cara.move(1,0,-1); 
                   cara.setLocalRotation(new Quaternion().fromAngleAxis(180*FastMath.DEG_TO_RAD, new Vector3f(0,1,0)));
                break;
                case 3: //cara 4
                   cara.move(0,0,-1); 
                   cara.setLocalRotation(new Quaternion().fromAngleAxis(-90*FastMath.DEG_TO_RAD, new Vector3f(0,1,0)));
                break;
                case 4: //cara 5
                    cara.move(0,1,0); 
                    cara.setLocalRotation(new Quaternion().fromAngleAxis(-90*FastMath.DEG_TO_RAD, new Vector3f(1,0,0)));
                break;
                case 5: //cara 6
                    cara.move(0,0,-1); 
                    cara.setLocalRotation(new Quaternion().fromAngleAxis(90*FastMath.DEG_TO_RAD, new Vector3f(1,0,0)));
                break;
                
            }
            
            bloque.attachChild(cara); 
        }
        
        return bloque;
    }
}

/*
 * Clase para generar un bloque mediante triangulos, sus caras, las texturas de cada cara ...
 */
package cliente;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
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
public class GeneraBloque {
    private SimpleApplication app;
    private AssetManager      assetManager;
    
    private StateJuegoTextureAtlas atlas;
    
    /**
     *
     * @param app
     */
    public GeneraBloque(Application app){
        this.app = (SimpleApplication) app;
        this.assetManager = this.app.getAssetManager();
        
        atlas = new StateJuegoTextureAtlas(app);
        atlas.setTexturesInAtlas();
    }
    
    /**
     *
     * @param tamano
     * @return
     */
    protected Node makeQuad(int tamano) {
        Node bloque = new Node("bloque");
        
        //TODO Pasar datos de bloques adyacentes
        //TODO Pasar datos del bloque para saber que textura aplicar
        
        System.out.println(atlas.getAnchoImagenTextura());
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat1.setTexture("ColorMap", atlas.getAtlasTexture("Bloques1"));    
        mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        float tamanoTile = (float) atlas.getAnchoImagenTextura() / 16f;
        tamanoTile = tamanoTile / (float) atlas.getAnchoImagenTextura();

        Mesh m = new Mesh();

        // Vertices, posiciones en el espacio
        Vector3f [] vertices = new Vector3f[4];
        vertices[0] = new Vector3f(0,0,0);
        vertices[1] = new Vector3f(tamano,0,0);
        vertices[2] = new Vector3f(0,tamano,0);
        vertices[3] = new Vector3f(tamano,tamano,0);

        //Posiciones de las texturas
        float x = 12f * tamanoTile; //siempre es uno menos, el cero cuenta
        float y = -5f * tamanoTile; //siempre es uno menos, el cero cuenta

        float sumapixel = 0.0005f; //x a la izquierda del cuadrado
        float sumapixel2 = -0.0005f; //y arriba del cuadrado
        float sumapixel3 = 0.0005f;  //y abajo del cuadrado
        float sumapixel4 = -0.0005f; //x a la derecha del cuadrado

        Vector2f [] texCoord = new Vector2f[4];
        texCoord[0] = new Vector2f(0 + x + sumapixel,1 - tamanoTile + y + sumapixel3);
        texCoord[1] = new Vector2f(tamanoTile + x + sumapixel4,1 - tamanoTile + y + sumapixel3);
        texCoord[2] = new Vector2f(0 + x + sumapixel,1 + y + sumapixel2);
        texCoord[3] = new Vector2f(tamanoTile + x + sumapixel4,1 + y + sumapixel2);

       
        // Indices de los vertices, en que orden se construyen
        int [] indexes = {2,0,1,1,3,2};

        // Setting buffers
        m.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        m.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        m.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indexes));
        m.updateBound();

        Geometry cara = new Geometry("Suelaco", m);   

        cara.setMaterial(mat1);

        bloque.attachChild(cara);
        
        return bloque;
    }
}

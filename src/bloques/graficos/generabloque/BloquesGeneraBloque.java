/*
 * Clase para generar un bloque mediante triangulos, sus caras, las texturas de cada cara ...
 */
package bloques.graficos.generabloque;

import bloques.manejo.chunks.BloquesChunkDatos;
import bloques.manejo.chunks.BloquesChunks;
import bloques.manejo.utiles.BloquesUtiles;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.debug.WireBox;
import com.jme3.util.BufferUtils;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author mcarballo
 */
public class BloquesGeneraBloque {
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
    public BloquesTextureAtlas atlas;
    /**
     *
     */
    public BloquesGenericos bloquesGenericos;
    
    
    /**
     *
     */
    protected Map<String,Node> bloquesGenerados = new HashMap<String,Node>();
    
    /**
     *
     * @param app
     */
    public BloquesGeneraBloque(Application app){
        this.app = (SimpleApplication) app;
        this.assetManager = this.app.getAssetManager();
        
        atlas = new BloquesTextureAtlas(app);
        atlas.setTexturesInAtlas();
        
        bloquesGenericos = new BloquesGenericos();
    }
    
    /**
     *
     * @param tamano
     * @param tipo 
     * @return
     */
    public Node makeBloque(int tamano,String tipo) {        
        Node bloque = new Node("bloque");
        BloquesGenericosDatos bloquesDatos = bloquesGenericos.getBloqueTipo(tipo);
        
        float tamanoTile = (float) atlas.getAnchoImagenTextura() / 16f;
        tamanoTile = tamanoTile / (float) atlas.getAnchoImagenTextura();
        
        for (int i = 0; i<6; i++){
            Mesh m = new Mesh();

            //VERTICES -  posiciones en el espacio
            Vector3f [] vertices = new Vector3f[4];
            vertices[0] = new Vector3f(0,0,0);
            vertices[1] = new Vector3f(tamano,0,0);
            vertices[2] = new Vector3f(0,tamano,0);
            vertices[3] = new Vector3f(tamano,tamano,0);

            //TEXTURAS - Posiciones de las texturas
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

            //NORMALES - para la iluminacion
            float[] normals = new float[]{0,0,1, 0,0,1, 0,0,1, 0,0,1};
            
            //INDICES -  Indices de los vertices, en que orden se construyen
            int [] indexes = {2,0,1,1,3,2};
            
            // Setting buffers
            m.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
            m.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
            m.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indexes));
            m.setBuffer(VertexBuffer.Type.Normal, 3, BufferUtils.createFloatBuffer(normals));
            m.updateBound();
            m.setStatic();

            //Geometry cara = new Geometry("Cara-"+String.valueOf(i)+"-Bloque-x-y", m);   
            Geometry cara = new Geometry("Cara-"+String.valueOf(i), m);   

            //colocamos las caras en su sitio
            switch(i){
                case 0: //cara 1
                    //esta ya va bien de serie
                break;
                case 1: //cara 2
                   cara.move(tamano,0,0); 
                   cara.setLocalRotation(new Quaternion().fromAngleAxis(90*FastMath.DEG_TO_RAD, new Vector3f(0,tamano,0)));
                break;
                case 2: //cara 3
                   cara.move(tamano,0,-tamano); 
                   cara.setLocalRotation(new Quaternion().fromAngleAxis(180*FastMath.DEG_TO_RAD, new Vector3f(0,tamano,0)));
                break;
                case 3: //cara 4
                   cara.move(0,0,-tamano); 
                   cara.setLocalRotation(new Quaternion().fromAngleAxis(-90*FastMath.DEG_TO_RAD, new Vector3f(0,tamano,0)));
                break;
                case 4: //cara 5
                    cara.move(0,tamano,0); 
                    cara.setLocalRotation(new Quaternion().fromAngleAxis(-90*FastMath.DEG_TO_RAD, new Vector3f(tamano,0,0)));
                break;
                case 5: //cara 6
                    cara.move(0,0,-tamano); 
                    cara.setLocalRotation(new Quaternion().fromAngleAxis(90*FastMath.DEG_TO_RAD, new Vector3f(tamano,0,0)));
                break;
                
            }
            
            bloque.attachChild(cara); 
        }
        
        return bloque;
    }
    
    /**
     *
     * @return
     */
    public Geometry makeWireBloque(){
         WireBox wireBox = new WireBox(BloquesUtiles.TAMANO_BLOQUE / 2, BloquesUtiles.TAMANO_BLOQUE / 2, BloquesUtiles.TAMANO_BLOQUE / 2); 
         wireBox.setLineWidth(2f);

         Geometry wireBloque = new Geometry("WireBloque", wireBox); 
         
         return wireBloque;
    }
    
    /**
     *
     * @param nomBloque
     * @return
     */
    public Node getBloqueGenerado(String nomBloque){        
        Node bloqueGenerado = bloquesGenerados.get(nomBloque);
        
        if (bloqueGenerado != null){
            return (Node) bloqueGenerado.clone();
        }else{
            BloquesGenericosDatos bloquesDatos = bloquesGenericos.getBloqueTipo(nomBloque);
            
            Node bloque;
            
            Material mat1;
            
            if (nomBloque.equals("WireFrame")){
                mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");   
                mat1.setColor("Color", ColorRGBA.Black);
                
                bloque = new Node("WireFrame");
                
                Geometry gWireBloque = makeWireBloque();
                
                gWireBloque.setMaterial(mat1);
                
                bloque.attachChild(gWireBloque);
            }else{
                mat1 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
                mat1.setTexture("DiffuseMap", atlas.getTexture(bloquesDatos.getNombreTextura()));
                //mat1.setTexture("ColorMap", atlas.getTexture(bloquesDatos.getNombreTextura())); 
                
                //luces
                mat1.setBoolean("UseMaterialColors", true);
                //mat1.setBoolean("UseVertexColor", true);
                mat1.setColor("Ambient",  ColorRGBA.White);
                mat1.setColor("Diffuse",  ColorRGBA.White);
                mat1.setColor("Specular", ColorRGBA.White);
                mat1.setFloat("Shininess", 1f);
                
                bloque = makeBloque(BloquesUtiles.TAMANO_BLOQUE,nomBloque);
                
                bloque.setMaterial(mat1);
            }
            
            //mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha); //transparencia

            bloquesGenerados.put(nomBloque,bloque);

            return (Node) bloque.clone();
        }
        
    }
    
    /**
     *
     * @param nomBloqueFinal
     * @param datosBloque
     * @param chunks
     * @param conReturn 
     * @return
     */
    public Node generaBloqueClonado(String nomBloqueFinal, BloquesChunkDatos datosBloque, BloquesChunks chunks, Boolean conReturn){
        if (datosBloque != null){
            Node bloqueClonado = null;
            if (conReturn){
                bloqueClonado = getBloqueGenerado(datosBloque.getNomBloque());
                bloqueClonado.setName(nomBloqueFinal);
            }

            //coordenadas reales del cubo, no las del chunk
            Vector3f coordenadas = BloquesUtiles.devuelveCoordenadasBloque(nomBloqueFinal);

            //le quitamos las caras que no se ven
            int contaCarasQuitadas = 0;

            int[] carasbloquesVecinos;
            if (datosBloque.getMostrar() && datosBloque.getCaras() != null){
                carasbloquesVecinos = datosBloque.getCaras();
            }else{
                int[][] bloquesVecinos = chunks.getBloquesVecinos(coordenadas);
                carasbloquesVecinos = chunks.getCarasAPartirDeBloquesVecinos(bloquesVecinos);
            }
            datosBloque.setCaras(carasbloquesVecinos);

            for(int h = 0;h<6;h++){
                if (carasbloquesVecinos[h] == 0){ //si no hay cara
                    if (conReturn){
                        bloqueClonado.detachChildNamed("Cara-"+h); 
                    }
                    contaCarasQuitadas++;
                }
            }

            if (contaCarasQuitadas < 6){
                datosBloque.setMostrar(true);
                if (conReturn){
                    bloqueClonado.setLocalTranslation(coordenadas.x,coordenadas.y,coordenadas.z + BloquesUtiles.TAMANO_BLOQUE);
                    return bloqueClonado;
                }else{
                    return null;   
                }
            }else{
                datosBloque.setMostrar(false);
                datosBloque.setCaras(null);
            }
        }
        
        return null;
    }

}

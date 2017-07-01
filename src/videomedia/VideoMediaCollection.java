package videomedia;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import jmr.video.Video;


/**
 * (@link VideoMedia) implementado como colección de fotogramas 
 * @author Juan Jesús Tomás Rojas
 * @version 1.0 - Marzo de 2017
 */

/**
 *
 * @author Asus
 */
public class VideoMediaCollection implements Video {

    /**
     * Colección de (@link BufferedImage) que representan los fotogramas de un
     * vídeo
     */
    private ArrayList<BufferedImage> collection;    
    
    /**
     * Constructor vacío que inicializa la colección vacía
     */
    public VideoMediaCollection(){
        collection = new ArrayList<>();
    }
    
    /**
     * Constructor que inicializa la colección a fotogramas con las características
     * aportadas
     * @param nframes Número de frames del vídeo
     * @param ancho Ancho de la imagen
     * @param alto Alto de la imagen
     * @param imgType Tipo de la imagen
     */
    public VideoMediaCollection(int nframes, int ancho, int alto, int imgType){
        collection = new ArrayList<>();
        BufferedImage bf = new BufferedImage(alto, ancho, imgType);
        for(int i = 0; i < nframes; i ++){
            collection.add(bf);
        }
    }
    
    /**
     * Constructor que inicializa la colección a fotogramas con las características
     * aportadas
     * @param nframes Número de frames
     * @param ancho Ancho de la imagen
     * @param alto  Alto de la imagen
     */
    public VideoMediaCollection(int nframes, int ancho, int alto){
        this(nframes, ancho, alto, BufferedImage.TYPE_INT_RGB);
    }  

    /**
     * @inheritDoc
     */
    @Override
    public int getHeight() {
        if(collection.isEmpty()){
            return 0;
        }else{
            return collection.get(0).getHeight();
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public int getWidth() {
         if(collection.isEmpty()){
            return 0;
        }else{
            return collection.get(0).getWidth();
         }
    }
    
    /**
     * @inheritDoc
     */
    @Override
    public BufferedImage getFrame(int nframe) {
        if(nframe>=0 && nframe<getNumberOfFrames()){
            return collection.get(nframe);
        }else{
            throw new IllegalArgumentException("Frame " + nframe + " out of range");
        }
    }

    /**
     * Añade un frame a la colección
     * @param frame Frame a añadir
     */
    public void addFrame(BufferedImage frame) {
        collection.add(frame);
    }

    /**
     * Asigna un frame a la posición indicada
     * @param nframe Posición donde se desea asignar el frame
     * @param frame Frame a asignar
     */
    public void setFrame(int nframe, BufferedImage frame) {
        if(nframe>=0 && nframe<getNumberOfFrames()){
            collection.set(nframe, frame);
        }else{
            throw new IllegalArgumentException("Frame " + nframe + "out of range");
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public int getNumberOfFrames() {
        return collection.size();
    }

    /**
     * @inheritDoc
     */
    @Override
    public int getFrameRate() {
        throw new UnsupportedOperationException("VideoMediaCollection does not have framerate yet."); 
    }
    
}

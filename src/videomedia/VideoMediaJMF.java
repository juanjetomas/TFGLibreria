package videomedia;


import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.URLDataSource;
import javax.media.Buffer;
import javax.media.CannotRealizeException;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoDataSourceException;
import javax.media.NoPlayerException;
import javax.media.NoProcessorException;
import javax.media.NotConfiguredError;
import javax.media.Player;
import javax.media.Processor;
import javax.media.control.FrameGrabbingControl;
import javax.media.control.FramePositioningControl;
import javax.media.format.VideoFormat;
import javax.media.protocol.DataSource;
import javax.media.util.BufferToImage;
import jmr.video.Video;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * (@link VideoMedia) implementado usando la JMF 
 * @author Juan Jesús Tomás Rojas
 * @version 1.0 - Marzo de 2017
 */
public class VideoMediaJMF implements Video {

    /**
     * Reproductor usado para leer el contenido
     */
    private Player pl;
    /**
     * Usado para posicionar el reproductor en el frame deseado
     */
    private FramePositioningControl fpc;
    /**
     * Usado para extraer el fotograma deseado
     */
    private FrameGrabbingControl fgc;
    /**
     * Número de frames del medio
     */
    private int nframes;
    /**
     * Ancho de la imagen
     */
    private int width;
    /**
     * Alto de la imgagen
     */
    private int height;

    /**
     * Constructor vacío que inicializa el reproductor a nulo
     */
    public VideoMediaJMF(){
        pl = null;
    }
    
    /**
     * Constructor que asigna un reproductor 
     * @param player Reproductor a asignar
     */
    public VideoMediaJMF(Player player){
        this.setPlayer(player);
    }
    
    /**
     * Devuelve el reproductor asociado al vídeo
     * @return Reproductor asociado al vídeo
     */
    public Player getPlayer(){
        return pl;
    }
    
    /**
     * Source of the media
     */
    private MediaLocator mediaLocator;
    
    /**
     * Asigna un reproductor e inicializa los atributos necesarios
     * @param player Reproductor a asignar
     */
    public void setPlayer(Player player){
        pl = player;
        fpc = (FramePositioningControl) pl.getControl("javax.media.control.FramePositioningControl");
        fgc = (FrameGrabbingControl) pl.getControl("javax.media.control.FrameGrabbingControl");
        //pl.prefetch();
        nframes = fpc.mapTimeToFrame(pl.getDuration());
        Component video = pl.getVisualComponent();
        width = video.getWidth();
        height = video.getHeight();
    }
    
    /**
     * Devuelve la duración en segundos del medio
     * @return Duración en segundos del medio
     */
    public double getDuracion(){
        return pl.getDuration().getSeconds();        
    }
    
    /**
     * Sets the the MediaLocator
     * @param ml MediaLocator
     */
    public void setMediaLocator(MediaLocator ml){
        if(ml!=null){
            mediaLocator = ml;
        }else{
            throw new IllegalArgumentException("MediaLocator is null");
        }
    }
    

    /**
     * @inheritDoc
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * @inheritDoc
     */
    @Override
    public int getWidth() {
        return width;
    }
    
    /**
     * Devuelve el frame actual. Se usa principalmente cuando el reproductor
     * que representa el vídeo está asociado a una webcam, es decir, para la consulta
     * en tiempo real
     * @return Fotograma actual
     */
    public BufferedImage getFrame(){ 
        pl.start();
        Buffer buf = fgc.grabFrame();
        VideoFormat vf = (VideoFormat) buf.getFormat();
        BufferToImage converter = new BufferToImage(vf);
        Image image = converter.createImage(buf);
        width = image.getWidth(null); 
        height = image.getHeight(null);
        int type = BufferedImage.TYPE_INT_RGB;
        BufferedImage buffered = new BufferedImage(width, height, type);

        Graphics2D g2 = buffered.createGraphics();
        g2.drawImage(image, null, null);
        g2.dispose();
        buffered.flush();

        pl.start();
        pl.close();

        return buffered;
    }

    /**
     * @inheritDoc
     */
    @Override
    public BufferedImage getFrame(int nframe) {
        if(nframe>=0 && nframe <nframes){
            fpc.seek(nframe);
            return this.getFrame();            
        }else{
            throw new IllegalArgumentException("Frame " + nframe + "out of range");
        }
    }    

    @Override
    public int getNumberOfFrames() {
        return nframes;
    }

    @Override
    public int getFrameRate() {
        int frameRate = -1;
        if (mediaLocator != null) {
            try {
                Processor myProcessor = Manager.createProcessor(mediaLocator);
                Format relax = myProcessor.getContentDescriptor().relax();
                if (relax instanceof VideoFormat) {
                    frameRate = (int) ((VideoFormat) relax).getFrameRate();
                }
            } catch (NotConfiguredError e) {
            } catch (IOException | NoProcessorException ex) {
                Logger.getLogger(VideoMediaJMF.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            throw new NullPointerException("MediaLocator is null");
        }
        if(frameRate!=-1){
            return frameRate;
        }else{
            throw new IllegalStateException("Framerate not found.");
        }
    }
}

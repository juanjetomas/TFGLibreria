/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videomedia;

import org.bytedeco.javacv.Frame;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import jmr.video.Video;

/**
 * (@link VideoMedia) implementado usando JavaCV (que a su vez usa OpenCV) 
 * @author Juan Jesús Tomás Rojas
 * @version 1.0 - Marzo de 2017
 */
public class VideoMediaOpenCV implements Video {

    /**
     * Utilizado para extraer fotogramas del vídeo
     */
    private FFmpegFrameGrabber fg;
    
    /**
     * Constructor vacío que inicializa el framegrabber a nulo
     */
    public VideoMediaOpenCV(){
        fg = null;
    }
    
    /**
     * Constructor que asigna un FrameGrabber a la clase
     * @param framegrabber FrameGrabber que se desea asignar
     * @throws org.bytedeco.javacv.FrameGrabber.Exception 
     */
    public VideoMediaOpenCV(FFmpegFrameGrabber framegrabber) throws FrameGrabber.Exception{
        setFrameGrabber(framegrabber);
    }
    
    /**
     * Asigna un FrameGrabber a la clase
     * @param framegrabber FrameGrabber a asignar
     * @throws org.bytedeco.javacv.FrameGrabber.Exception 
     */
    public void setFrameGrabber(FFmpegFrameGrabber framegrabber) throws FrameGrabber.Exception{
        fg = framegrabber;
        fg.start();
        fg.flush();
    }
    
    /**
     * Devuelve el FrameGrabber asociado a la clase
     * @return FrameGrabber asociado a la clase
     */
    public FFmpegFrameGrabber getFrameGrabber(){
        return fg;
    }    

    /**
     * @inheritDoc
     */
    @Override
    public int getHeight() {
        return fg.getImageHeight();
    }

    /**
     * @inheritDoc
     */
    @Override
    public int getWidth() {
        return fg.getImageWidth();
    }

    /**
     * @inheritDoc
     */
    @Override
    public BufferedImage getFrame(int nframe) {
        if(nframe>=0 && nframe <getNumberOfFrames()){
            Java2DFrameConverter converter = new Java2DFrameConverter();
            Frame fr = null;            
            try {                           
                fg.setFrameNumber(nframe);
                fr = fg.grabImage();                
            } catch (FrameGrabber.Exception ex) {
                Logger.getLogger(VideoMediaOpenCV.class.getName()).log(Level.SEVERE, null, ex);
            }            
            return converter.convert(fr);
        }else{
            throw new IllegalArgumentException("Frame " + nframe + "out of range");
        }
    }    
    
    /**
     * @inheritDoc
     */
    @Override
    public int getNumberOfFrames() {
        return fg.getLengthInFrames();
    }

    /**
     * @inheritDoc
     */    
    @Override
    public int getFrameRate() {
        return (int) fg.getFrameRate();
    }
}

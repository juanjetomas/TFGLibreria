/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videomedia;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

/**
 * Lee y escribe de disco objetos del tipo (@link VideoMediaOpenCV)
 * @author Juan Jesús Tomás Rojas
 * @version 1.0 - Marzo de 2017
 */
public class VideoMediaOpenCVIO {
    
    /**
     * Devuelve un (@link VideoMediaOpenCV) a partir de un archivo
     * @param f Fichero que se desea cargar
     * @return (@link VideoMediaOpenCV) asociado al fichero indicado
     * @throws org.bytedeco.javacv.FrameGrabber.Exception 
     */
    public static VideoMediaOpenCV read(File f) throws FrameGrabber.Exception{
        FFmpegFrameGrabber fg = new FFmpegFrameGrabber(f);
        VideoMediaOpenCV vmocv = new VideoMediaOpenCV();       
        vmocv.setFrameGrabber(fg);
        Java2DFrameConverter converter = new Java2DFrameConverter();
        
        return vmocv;
    }
}

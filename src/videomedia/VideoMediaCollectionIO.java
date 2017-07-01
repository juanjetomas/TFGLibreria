package videomedia;


import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.CannotRealizeException;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.control.FrameGrabbingControl;
import javax.media.control.FramePositioningControl;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Lee y escribe de disco objetos del tipo (@link VideoMediaCollection)
 * @author Juan Jesús Tomás Rojas
 * @version 1.0 - Marzo de 2017
 */

public class VideoMediaCollectionIO {
    /**
     * A partir de un fichero, inicializa un (@link VideoMediaCollection)
     * @param file Archivo que apunta al medio a leer
     * @return (@link VideoMediaCollection) asignado a dicho file
     * @throws IOException
     * @throws NoPlayerException
     * @throws CannotRealizeException 
     */
    public static VideoMediaCollection read(File file) throws IOException, NoPlayerException, CannotRealizeException{
        VideoMediaCollection vmc = new VideoMediaCollection();
        readRecursive(vmc, file);
        return vmc;        
    }
    
    /**
     * Carga en el (@link VideoMediaCollection) el contenido de la carpeta
     * @param vmc (@link VideoMediaCollection) donde se carga
     * @param file Fichero que apunta a la carpeta
     * @throws IOException
     * @throws NoPlayerException
     * @throws CannotRealizeException 
     */
    private static void readFolder(VideoMediaCollection vmc,File file) throws IOException, NoPlayerException, CannotRealizeException{
        File[] listOfFiles = file.listFiles();
        for(int i = 0; i < listOfFiles.length; i++){
            readRecursive(vmc, listOfFiles[i]);
        }
    }
    
    /**
     * Carga en el (@link VideoMediaCollection) una imagen
     * @param vmc (@link VideoMediaCollection) en el que se desea cargar
     * @param file Fichero que apunta a la imagen
     * @throws IOException 
     */
    private static void readImage(VideoMediaCollection vmc,File file) throws IOException{
        vmc.addFrame(ImageIO.read(file));
    }
    
    /**
     * Carga el (@link VideoMediaCollection) con el vídeo usando la JMF
     * @param vmc (@link VideoMediaCollection) en el que se desea cargar el vídeo
     * @param file Fichero que apunta al vídeo
     * @throws IOException
     * @throws NoPlayerException
     * @throws CannotRealizeException 
     */
    private static void readJMFVideo(VideoMediaCollection vmc,File file) throws IOException, NoPlayerException, CannotRealizeException{
        MediaLocator ml = new MediaLocator("file:" + file.getAbsolutePath());
        Player pl = Manager.createRealizedPlayer(ml);
        FramePositioningControl fpc = (FramePositioningControl) pl.getControl("javax.media.control.FramePositioningControl");
        FrameGrabbingControl fgc = (FrameGrabbingControl) pl.getControl("javax.media.control.FrameGrabbingControl");
        pl.prefetch();
        int nframes = fpc.mapTimeToFrame(pl.getDuration());
        Component video = pl.getVisualComponent();
        Buffer buf;
        VideoFormat vf;
        BufferToImage converter;
        Image image;
        int width, height, type;
        BufferedImage buffered;
        Graphics2D g2;
        
        for(int i = 0; i< nframes; i++){
            fpc.seek(i);
            buf = fgc.grabFrame();
            vf = (VideoFormat) buf.getFormat();
            converter = new BufferToImage(vf);
            image = converter.createImage(buf);
            width = image.getWidth(null); 
            height = image.getHeight(null);
            type = BufferedImage.TYPE_INT_RGB;
            buffered = new BufferedImage(width, height, type);

            g2 = buffered.createGraphics();
            g2.drawImage(image, null, null);
            g2.dispose();
            buffered.flush();
            
            vmc.addFrame(buffered);
            pl.close();
        }
    }
    
    /**
     * Carga el (@link VideoMediaCollection) con el vídeo usando OpenCV
     * @param vmc (@link VideoMediaCollection)a cargar
     * @param file Archivo que apunta al vídeo
     * @throws org.bytedeco.javacv.FrameGrabber.Exception 
     */
    private static void readOpenCVVideo(VideoMediaCollection vmc,File file) throws FrameGrabber.Exception{
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(file);
        Frame frame;
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage bi;
        frameGrabber.start();
        int nframes = frameGrabber.getLengthInFrames();
        
        try {
            
            frame = frameGrabber.grabImage();
            while(frame!=null){
                bi = converter.convert(frame);
                vmc.addFrame(bi);
                frame = frameGrabber.grabImage();   
            }
            
            /*
            for(int i = 0; i< nframes; i++){
                frameGrabber.setFrameNumber(i);
                frame = frameGrabber.grabImage();
                bi = converter.convert(frame);
                vmc.addFrame(bi);
            }
            */

            frameGrabber.stop();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * Lee los medios en el (@link VideoMediaCollection) dependiendo de su tipo
     * @param vmc (@link VideoMediaCollection) en el que se carga el contenido
     * @param file (@link VideoMediaCollection) que apunta al medio
     * @throws IOException
     * @throws NoPlayerException
     * @throws CannotRealizeException 
     */
    private static void readRecursive(VideoMediaCollection vmc, File file) throws IOException, NoPlayerException, CannotRealizeException{
        if(file.isFile()){ //Si es un archivo
            String[] extensionesJMF = {"avi", "mpg", "mpeg", "mov"};
            String extension = "";
            int i = file.getName().lastIndexOf('.');
            if(i>0){
                extension = file.getName().substring(i+1);
            }
            if(Arrays.asList(ImageIO.getReaderFormatNames()).contains(extension)){ //Si es una imagen
                readImage(vmc, file);
            }else if(Arrays.asList(extensionesJMF).contains(extension)){ //Si es un video soportado por la JMF
                readJMFVideo(vmc, file);
            }else{
                readOpenCVVideo(vmc, file);
            }
        }else{ //Si es un directorio
            readFolder(vmc, file);
        }
    }
}

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
import javax.media.CannotRealizeException;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;

/**
 * Lee y escribe de disco objetos del tipo (@link VideoMediaJMF)
 * @author Juan Jesús Tomás Rojas
 * @version 1.0 - Marzo de 2017
 */
public class VideoMediaJMFIO {
    /**
     * Crea un (@link VideoMediaJMF) a partir de un archivo
     * @param file Archivo que apunta al medio que se desea cargar
     * @return (@link VideoMediaJMF) asociado al medio
     * @throws IOException
     * @throws NoPlayerException
     * @throws CannotRealizeException 
     */
    public static VideoMediaJMF read(File file) {
        try {
            String sfichero = "file:" + file.getAbsolutePath();
            MediaLocator ml = new MediaLocator(sfichero);
            Player pl = Manager.createRealizedPlayer(ml);
            VideoMediaJMF vm = new VideoMediaJMF(pl);
            vm.setMediaLocator(ml);
            
            return vm;
        } catch (IOException ex) {
            Logger.getLogger(VideoMediaJMFIO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoPlayerException ex) {
            Logger.getLogger(VideoMediaJMFIO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CannotRealizeException ex) {
            Logger.getLogger(VideoMediaJMFIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}

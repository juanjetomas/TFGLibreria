package videomedia;


import java.awt.image.BufferedImage;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Interfaz que representa un vídeo con sus metodos básicos
 * @author Juan Jesús Tomás Rojas
 * @version 1.0 - Marzo de 2017
 */

public interface VideoMedia {
    /**
     * Devuelve el número de frames del vídeo
     * @return Número de frames
     */
    public int getNumFrames();
    /**
     * Devuelve la altura en píxeles del vídeo
     * @return Altura del vídeo
     */
    public int getHeight();
    /**
     * Devuelve la anchura en píxeles del vídeo
     * @return anchura del vídeo
     */
    public int getWidth();
    /**
     * Devuelve el fotograma indicado mediante un (@link BufferedImage)
     * @param nframe Número de fotograma a devolver
     * @return Fotograma en la posición nframe
     */
    public BufferedImage getFrame(int nframe);
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videomedia.comparator;

import java.util.ArrayList;
import jmr.descriptor.Comparator;
import jmr.video.KeyFrameDescriptor;


/**
 * Comparator class wich returns the medium distance between every descriptor
 * from the first KeyFrameDescriptor and every descriptor at the second
 * KeyFrameDescriptor minimum distances, and the maximum between this and the 
 * resverse operation.
 *
 * @author Juan Jesús Tomás Rojas
 * @version 1.0 - Marzo de 2017
 */
public class MedianEqualComparator implements Comparator<KeyFrameDescriptor, Double> {
    @Override
    public Double apply(KeyFrameDescriptor t1, KeyFrameDescriptor t2) {
        Double median1, median2;
        median1 = new MedianComparator().apply(t1, t2);
        median2 = new MedianComparator().apply(t2, t1);
        return Math.max(median1, median2);
    }
}

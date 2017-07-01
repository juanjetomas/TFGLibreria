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
 * Comparator class wich returns the median distance between every descriptor
 * from the first KeyFrameDescriptor and every descriptor at the second
 * KeyFrameDescriptor minimum distances calculating them respecting
 * the order
 *
 * @author Juan Jesús Tomás Rojas
 * @version 1.0 - Marzo de 2017
 */
public class MedianOrderedComparator implements Comparator<KeyFrameDescriptor, Double> {
    @Override
    public Double apply(KeyFrameDescriptor t1, KeyFrameDescriptor t2) {
        ArrayList<Double> distances = ComparatorTools.getMinDistances(t1, t2, true);
        return ComparatorTools.getMedian(distances);
    }
}

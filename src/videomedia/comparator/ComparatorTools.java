/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videomedia.comparator;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jmr.descriptor.MediaDescriptor;
import jmr.video.KeyFrameDescriptor;

/**
 * It includes functions used by <code>Comparator</code> classes.
 *
 *
 * @author Juan Jesús Tomás Rojas
 * @version 1.0 - Marzo de 2017
 */
public class ComparatorTools {

    /**
     * Returns a list of minimum distances between the
     * <code>KeyFrameDescriptor</code>
     *
     * @param kf1 First <code>KeyFrameDescriptor</code>
     * @param kf2 Second <code>KeyFrameDescriptor</code>
     * @param inOrder If true, each element of the first argument
     * <code>KeyFrameDescriptor</code> is compared starting at the las position
     * of the second one where a minimum was found. This means minimum distances
     * between <code>kf1</code> and <code>kf2</code> cannot be taken at crossed
     * positions.
     * @return ArrayList of minimum distances between the
     * <code>KeyFrameDescriptor</code>
     */
    static public ArrayList<Double> getMinDistances(KeyFrameDescriptor kf1, KeyFrameDescriptor kf2, boolean inOrder) {
        ArrayList<Double> distances = new ArrayList<>();
        List<MediaDescriptor> l1, l2;
        l1 = (List<MediaDescriptor>) kf1.getDescriptors();
        l2 = (List<MediaDescriptor>) kf2.getDescriptors();
        Double minDistance;
        Double currentDistance;
        try {
            if (!inOrder) {
                for (int i = 0; i < l1.size(); i++) {
                    minDistance = Double.MAX_VALUE;
                    for (int j = 0; j < l2.size(); j++) {
                        currentDistance = (Double) l1.get(i).compare(l2.get(j));
                        minDistance = Math.min(minDistance, currentDistance);
                    }
                    distances.add(minDistance);
                }
            } else {
                int lastMinPosition = 0;
                for (int i = 0; i < l1.size(); i++) {
                    minDistance = Double.MAX_VALUE;
                    for (int j = lastMinPosition; j < l2.size(); j++) {
                        currentDistance = (Double) l1.get(i).compare(l2.get(j));
                        if (currentDistance < minDistance) {
                            minDistance = currentDistance;
                            lastMinPosition = j;
                        }
                    }
                    distances.add(minDistance);
                }
            }
        } catch (ClassCastException e) {
            throw new InvalidParameterException("The comparision between descriptors is not interpetrable as a double value.");
        } catch (Exception e) {
            throw new InvalidParameterException("The descriptors are not comparables.");
        }
        return distances;
    }
    
    /**
     * Returns the minimum value of the ArrayList
     * @param values ArrayList to find the minium
     * @return Minimum value of the ArrayList
     */
    static Double getMin(ArrayList<Double> values){
        Double min = Double.MAX_VALUE;
        for (Double value : values) {
            min = Math.min(min, value);
        }
        return min;
    }
    
        /**
     * Returns the maximum value of the ArrayList
     * @param values ArrayList to find the maximum
     * @return Maximum value of the ArrayList
     */
    static Double getMax(ArrayList<Double> values){
        Double max = Double.MIN_NORMAL;
        for (Double value : values) {
            max = Math.max(max, value);
        }
        return max;
    }
    
    /**
     * Returns the median value from a ArrayList
     * @param values Arralist to obtain the median
     * @return The median value
     */
    static Double getMedian(ArrayList<Double> values){
        Collections.sort(values);
        return(values.get(values.size()/2));
    }
    
    static double getAverage(ArrayList<Double> values){
        Double average = 0d;
        for (Double value : values) {
            average+=value;
        }
        return average/values.size();
    }
}

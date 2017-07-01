/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videomedia;

import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import jmr.descriptor.MediaDescriptor;
import jmr.video.Video;

/**
 * This is the SegmentationOp class implemented using the called single change
 * method
 *
 * @author Juan Jesús Tomás Rojas
 * @version 1.0 - Marzo de 2017
 */
public class SegmentationOpSingleChange extends SegmentationOp {

    /**
     * Interval or distance betwen checked frames (window's size)
     */
    private int k;

    /**
     * Number between 0 and 1 wich determines the minimum value of a frame
     * change (variation) in relation with the maximum, to be considered a key
     * frame.
     */
    private double threshold;

    /**
     * Indicates if frame variations are smoothed or not
     */
    private Boolean smoothVariations;

    /**
     * Defines the value of sigma used when the gaussian filter is applied. It
     * must be between 0 and 1
     */
    private double sigma;

    /**
     * Defines the default value of sigma.
     */
    public static final double DEFAULT_SIGMA = 0.8d;

    /**
     * Default value of K
     */
    public static final int DEFAULT_K = 5;

    /**
     * Default threshold
     */
    public static final double DEFAULT_THRESHOLD = 0.8d;

    /**
     * Full constructor
     *
     * @param vmedia Segmentation will be done at this media
     * @param md Class of the MediaDescriptor used to compare frames
     * @param fpsVal Number of analyzed frames per second
     * @param resFrame Size of the resized frames to analyze. 0 means the frame
     * is not resized
     * @param kVal K value, distance between analyzed frames
     * @throws java.lang.NoSuchMethodException when the descriptor doesn't have
     * a BufferedImage based constructor
     */
    public SegmentationOpSingleChange(Video vmedia, Class<? extends MediaDescriptor> md, int fpsVal, int resFrame, int kVal) throws NoSuchMethodException {
        super(vmedia, md, fpsVal, resFrame);

        setK(kVal);

        setSigma(DEFAULT_SIGMA);

        threshold = DEFAULT_THRESHOLD;
        smoothVariations = true;
    }

    /**
     * Constructor with important parameters
     *
     * @param vmedia Segmentation will be done at this media
     * @param md Class of the MediaDescriptor used to compare frames
     * @throws java.lang.NoSuchMethodException when the descriptor doesn't have
     * a BufferedImage based constructor
     */
    public SegmentationOpSingleChange(Video vmedia, Class<? extends MediaDescriptor> md) throws NoSuchMethodException {
        this(vmedia, md, DEFAULT_FPS, DEFAULT_RESIZEDFRAME, DEFAULT_K);
    }

    /**
     * Empty constructor
     */
    public SegmentationOpSingleChange() {
    }

    /**
     * @inheritDoc
     */
    @Override
    public void calcVariations() {
        ArrayList<Double> variations = getVariations();
        ArrayList<Boolean> keyFrames = getKeyframes();

        //Just in case variations were calculated before
        variations.clear();
        keyFrames.clear();

        int frameJump = getFrameJump();
        MediaDescriptor mdfirst, mdsecond;
        Double max = -1d;
        Double variation;
        int numNeighbours = (k - 1) / 2;

        mdfirst = mdsecond = null;
        if (getVm().getNumberOfFrames() == 1) { //If there is only one frame, is added as keyFrame
            keyFrames.add(Boolean.TRUE);
            variations.add(0d);
        } else {

            //Sets the variations to 0 so the list has always the same size even
            //framejump is not 1.
            for (int i = 0; i < getVm().getNumberOfFrames() - 1; i++) {
                variations.add(0d);
                keyFrames.add(Boolean.FALSE);
            }

            //First, we obtain all the variations
            for (int i = 0; i < variations.size() - k; i += frameJump) {
                try {
                    mdfirst = getDescriptor(i);
                    mdsecond = getDescriptor(i + k);
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(SegmentationOpSingleChange.class.getName()).log(Level.SEVERE, null, ex);
                }
                variation = (Double) mdfirst.compare(mdsecond);
                variations.set((i + i + k) / 2, variation);

                if (variation > max) {
                    max = variation;
                }

                setPercentageDone((int) (((double) i / (variations.size() - k - frameJump)) * 100));
            }

            
            Double firstVal, secondVal, division, accumulated;

            //Then, we fill the intermediate values due to the framejump
            for (int i = k / 2; i < variations.size() - 2 * frameJump; i += frameJump) {
                firstVal = variations.get(i);
                secondVal = variations.get(i + frameJump);
                division = (Math.abs(firstVal - secondVal)) / frameJump * 1.0d;
                accumulated = firstVal + division;
                for (int j = i + 1; j < i + frameJump; j++) {
                    variations.set(j, accumulated);
                    accumulated += division;
                }
            }

            //If applicable, variation are smoothed
            if (getSmoothVariations()&& getVm().getNumberOfFrames()>1) {
                smoothVariations();
            }
            

            //Then, we check if they are peaks and if they are big enough
            for (int i = numNeighbours; i < keyFrames.size() - numNeighbours; i += frameJump) {
                //If value is a peak and it is big enough, we add it
                if (((variations.get(i - numNeighbours) - variations.get(i + numNeighbours))
                        * (variations.get(i + numNeighbours) - variations.get(i - numNeighbours))) < 0
                        && variations.get(i) > threshold * max) {
                    keyFrames.set(i, Boolean.TRUE);
                }
            }
        }

    }

    /**
     * Makes the variation dataset smoother applying a media k dimension filter
     * *
     */
    public void smoothVariationsMedia() {
        ArrayList<Double> newVariations = new ArrayList();
        ArrayList<Double> variations = getVariations();
        Double sum;
        int numNeighbours = k / 2;

        //First, we add the border neighbours without any smoothed value
        for (int i = 0; i < numNeighbours && i < variations.size(); i++) {
            newVariations.add(variations.get(i));
        }

        //Then, we assign the average
        for (int i = numNeighbours; i < variations.size() - numNeighbours; i++) {
            sum = 0d;
            for (int j = i - numNeighbours; j <= i + numNeighbours; j++) {
                sum += variations.get(j);
            }
            newVariations.add(sum / k);
        }

        //Finally, we add the last non smoothed values
        for (int i = variations.size() - numNeighbours; i < variations.size(); i++) {
            newVariations.add(variations.get(i));
        }

        setVariations(newVariations);
    }

    /**
     * Makes the variation dataset smoother applying a gaussian 1D filter
     */
    public void smoothVariations() {
        ArrayList<Double> newVariations = new ArrayList();
        ArrayList<Double> variations = getVariations();

        int radius = k / 2;
        //The radius must be 1 at least
        if (radius < 1) {
            radius = 1;
        }
        double newVariation;
        double sqsigma = sigma * sigma;
        double scale = 1.0 / (Math.sqrt(2 * Math.PI) * sigma);
        int kernelPosition;
        int size = 2 * radius + 1;
        ArrayList<Double> kernel = new ArrayList();

        //The kernel is calculated
        for (int i = 0; i < size; i++) {
            double x = radius - i;
            kernel.add(scale * Math.exp(-0.5 * (x * x) / sqsigma));
        }

        //Values not smoothed are added without modification
        for (int i = 0; i < radius && i < variations.size(); i++) {
            newVariations.add(variations.get(i));
        }

        //Applying the kernel
        for (int i = radius; i < variations.size() - radius; i++) {
            newVariation = 0d;
            kernelPosition = 0;
            for (int j = i - radius; j <= i + radius; j++) {
                newVariation += kernel.get(kernelPosition) * variations.get(j);

                kernelPosition++;
            }

            newVariations.add(newVariation);
        }

        //Finally, we add the last non smoothed values
        for (int i = variations.size() - radius; i < variations.size(); i++) {
            newVariations.add(variations.get(i));
        }

        setVariations(newVariations);
    }

    /**
     * Sets the value of k
     *
     * @param kval New value of k
     */
    public void setK(int kval) {
        if (kval > 0) {
            k = kval;
        } else {
            throw new IllegalArgumentException("k must be greater than 0");
        }
    }

    /**
     * Sets the threshold
     *
     * @param thres Threshold
     */
    public void setThreshold(double thres) {
        if (thres > 0.0d && thres <= 1.0d) {
            threshold = thres;
        } else {
            throw new IllegalArgumentException("Threshold must be between 0.0 and 1.0");
        }
    }

    /**
     * Returns the threshold
     *
     * @return Threshold
     */
    public double getThreshold() {
        return threshold;
    }

    /**
     * Returns if variations should be smoothed or not
     *
     * @return the smoothVariations
     */
    public Boolean getSmoothVariations() {
        return smoothVariations;
    }

    /**
     * Activates or desactivates the variation smoothing
     *
     * @param bl Boolean which sets the smoothing
     */
    public void setSmoothVariations(Boolean bl) {
        smoothVariations = bl;
    }

    public void setSigma(double sig) {
        if (sig >= 0.0d && sig <= 1.0d) {
            sigma = sig;
        } else {
            throw new IllegalArgumentException("Sigma must be between 0.0 and 1.0");
        }
    }

}

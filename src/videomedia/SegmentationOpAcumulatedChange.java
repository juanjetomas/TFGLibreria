/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videomedia;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmr.descriptor.MediaDescriptor;
import jmr.video.Video;


/**
 * This is the SegmentationOp class implemented using the called acumulated change
 * method
 * @author Juan Jesús Tomás Rojas
 * @version 1.0 - Marzo de 2017
 */
public class SegmentationOpAcumulatedChange extends SegmentationOp{

    /**
     * Set the variation limit to consider a keyframe in absolute value
     */
    private double threshold;
    
    /**
     * Suggested variation threshold: very high
     */
    static public final Double TH_VERY_HIGH= 0.07d;
    
    /**
     * Suggested variation threshold: high
     */
    static public final Double TH_HIGH= 0.05d;
    
    /**
     * Suggested variation threshold: medium
     */
    static public final Double TH_MEDIUM= 0.03d;
    
    /**
     * Suggested variation threshold: low
     */
    static public final Double TH_LOW= 0.01d;
    
    /**
     * Suggested variation threshold: very low
     */
    static public final Double TH_VERY_LOW= 0.005d;
    
    /**
     * Default threshold
     */
    static public final Double DEFAULT_THRESHOLD = TH_MEDIUM;
    
    /**
     * Full constructor
     * @param vmedia Segmentation will be done at this media
     * @param md Class of the MediaDescriptor used to compare frames
     * @param fpsVal Number of analyzed frames per second
     * @param resFrame Size of the resized frames to analyze. 0 means the frame is not resized
     * @param thres If variation between two frames exceeds this value, a key frame is added
     * @throws java.lang.NoSuchMethodException when the descriptor doesn't have
     * a BufferedImage based constructor
     */
    public SegmentationOpAcumulatedChange(Video vmedia, Class<?extends MediaDescriptor> md, int fpsVal, int resFrame, double thres) throws NoSuchMethodException{
        super(vmedia, md, fpsVal, resFrame);        
        
        setThreshold(thres);
    }
    
    /**
     * Constructor with important parameters
     * @param vmedia Segmentation will be done at this media
     * @param md Class of the MediaDescriptor used to compare frames
     * @throws java.lang.NoSuchMethodException when the descriptor doesn't have
     * a BufferedImage based constructor
     */
    public SegmentationOpAcumulatedChange(Video vmedia, Class<?extends MediaDescriptor> md) throws NoSuchMethodException{
            this(vmedia, md, DEFAULT_FPS, DEFAULT_RESIZEDFRAME, DEFAULT_THRESHOLD);
    }
    
    /**
     * Empty constructor
     */
    public SegmentationOpAcumulatedChange(){}
    
    /**
     * @inheritDoc
     */
    @Override
    public void calcVariations() {
        MediaDescriptor mdfirst, mdsecond;
        mdfirst = mdsecond = null;
        ArrayList<Double> variations = getVariations();
        ArrayList<Boolean> keyFrames = getKeyframes();
        int frameJump = getFrameJump();
        Double variation;
        int mdFirstPosition = 0;
        
        //Just in case variations were calculated before
        variations.clear();
        keyFrames.clear();
        
        //Sets the variations to 0 so the list has always the same size even
        //framejump is not 1.
        for(int i = 0; i < getVm().getNumberOfFrames()-1; i++){
                variations.add(0d);
                keyFrames.add(Boolean.FALSE);
        }
        
        try {
            mdfirst = getDescriptor(0);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(SegmentationOpAcumulatedChange.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for(int i = 1; i < variations.size() ; i+=frameJump){
            try {
                mdsecond = getDescriptor(i);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(SegmentationOpAcumulatedChange.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            variation = (Double) mdfirst.compare(mdsecond);
            variations.set(i, variation);
            if (variation > threshold) {
                mdfirst = mdsecond;
                keyFrames.set((i+mdFirstPosition)/2, true); //Variations index is one frame below the frames index
                mdFirstPosition = i;
            }
            
            setPercentageDone((int)(((double)i/(variations.size()-frameJump))*100));
        }
        
        Double firstVal, secondVal, division, accumulated;
        
        //If framejump is not 1, we fulfill the intermediate values
        if (frameJump != 1) {
            for (int i = 0; i <= variations.size() - 2*frameJump; i += frameJump) {
                firstVal = variations.get(i);
                secondVal = variations.get(i + frameJump);
                division = (Math.abs(firstVal - secondVal)) / frameJump * 1.0d;
                accumulated = firstVal + division;
                for (int j = i + 1; j < i + frameJump; j++) {
                    variations.set(j, accumulated);
                    accumulated += division;
                }
            }
        }
    }
    
    /**
     * Sets the variation threshold
     * @param thr Threshold
     */
    public void setThreshold(double thr){
        if(thr>0.0d){
            threshold = thr;
        }else{
            throw new IllegalArgumentException("Threshold must be greater than 0.0");
        }
    }
    
    /**
     * Return the variation threshold
     * @return Threshold
     */
    public double getThreshold(){
        return threshold;
    }
    
}

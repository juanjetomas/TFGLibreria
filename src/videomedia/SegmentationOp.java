/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videomedia;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JProgressBar;
import jmr.descriptor.MediaDescriptor;
import jmr.descriptor.color.MPEG7ColorStructure;
import jmr.video.Video;
import jmr.video.VideoIterator;

/**
 * This class contains segmentation operations in order to extract key frames,
 * check frame variation, etc.
 * @author Juan Jesús Tomás Rojas
 * @version 1.0 - Marzo de 2017
 */

public abstract class SegmentationOp implements Runnable, VideoIterator<BufferedImage> {
    /**
     * Media which will be segmented
     */
    private Video vm;
    /**
     * Type of descriptor used to compare between frames
     */
    private Class<?extends MediaDescriptor> dt;
    /**
     * Variations between frames
     */
    private ArrayList<Double> variations;
    
    /**
     * Indicates the distance between analysed frames. Increasing this value
     * reduces the processing time but makes the analysis less sensible to changes
     */
    private int frameJump;    
    
    /**
     * Frames per second analyzed
     */
    private int fps;
    
    /**
     * List of values which are key frames
     */
    private ArrayList<Boolean> keyframes;  
    
    /**
     * Sets the size of the frames when they are resized so calculations
     * may be faster. 0 value denotes that the analyzed frame is not resized
     */
    private int resizedFrame;  
    
    /**
     * Defines the default value of FPS
     */
    public static final int DEFAULT_FPS = 24;
    
    /**
     * Default class of the descriptor used to compare between frames
     */
    public static final Class DEFAULT_DESCRIPTOR = MPEG7ColorStructure.class;

    /**
     * Default value of the frame resizing. It means there is resizing.
     */
    public static final int DEFAULT_RESIZEDFRAME = 0;
    
    /**
     * ArrayList of BufferedImages which are keyframes. 
     */
    private ArrayList<BufferedImage> imageKeyFrames;
    
    /**
     * Indicates the completed percentage of {@link #calcVariations()} method
     * from 0 to 100.
     */
    volatile private int pertentageDone;
    
    /**
     * Iterator wich goes over the key frames of the video
     */
    private Iterator<BufferedImage> it;
    

    
    /**
     * 
     * @param vmedia Segmentation will be done at this media
     * @param md Class of the MediaDescriptor used to compare frames
     * @param fpsVal Number of analyzed frames per second
     * @param resFrame Size of the resized frames to analyze. 0 means the frame is not resized
     */
    public SegmentationOp(Video vmedia, Class<?extends MediaDescriptor> md, int fpsVal, int resFrame) throws NoSuchMethodException{
        if(vmedia==null){
            throw new IllegalArgumentException("VideoMedia is null");
        }else{
            vm = vmedia;
        }
        
        variations = new ArrayList<>();
        keyframes = new ArrayList<>();
        
        Constructor<?> cons = md.getConstructor(BufferedImage.class);
        if(cons.isAccessible()){
            dt = md;
        }else{
            dt = MPEG7ColorStructure.class;
        }

        setFps(fpsVal);
        
        setResizedFrame(resFrame);    
        
        imageKeyFrames = null;
        pertentageDone = 0;
    }
    
    /**
     * Empty constructor
     */
    public SegmentationOp(){}
    
    /**
     * Returns the MediaDescriptor asociated with the indicated frame
     *
     * @param nframe Number of the frame
     * @return MediaDescriptor based on the choosen frame
     * @throws java.lang.NoSuchMethodException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     */
    public MediaDescriptor getDescriptor(int nframe) throws NoSuchMethodException, 
            InstantiationException, IllegalAccessException, IllegalArgumentException, 
            InvocationTargetException {
        
        BufferedImage frame;

        //If resizedFrame (size) is not 0, the frame is resized
        if (resizedFrame != 0) {
            frame = new BufferedImage(resizedFrame, resizedFrame, BufferedImage.TYPE_INT_RGB);
            Graphics g = frame.createGraphics();
            g.drawImage(getVm().getFrame(nframe), 0, 0, resizedFrame, resizedFrame, null);
            g.dispose();            
        } else {
            frame = getVm().getFrame(nframe);
        }
        
        Constructor<?> cons = dt.getConstructor(BufferedImage.class);
        
        return (MediaDescriptor) cons.newInstance(frame);
    }
    
    /**
     * It performs the calculations to know the differences between frames
     * and stores it in the variations array
     */
    abstract public void calcVariations();
    
    /**
     * Returns a list of keyframes
     * @return ArrayList of BufferedIMages as keyframes
     */
    public ArrayList<BufferedImage> apply(){
        if(variations.isEmpty()){
            calcVariations();
        }
        
        ArrayList<BufferedImage> kframes = new ArrayList<>();
        
        int keyframesSize = 0;
        
        for (Boolean keyframe : keyframes) {
            if(keyframe){
                keyframesSize++;
            }
        }
        
        //If any keyframe is detected, the middle one is added
        if(keyframesSize==0){
            kframes.add(getVm().getFrame(vm.getNumberOfFrames()/2));
        }else{
            for (int i = 0; i < keyframes.size(); i++) {
                if(keyframes.get(i)){
                    kframes.add(vm.getFrame(i)); //If the frame is key, it's returned
                }
            }
        }
        
        imageKeyFrames = kframes;
        
        return kframes;
    }
    
    /**
     * Returns the frame's variation
     * @return String with format frame_number variation \n
     */
    @Override
    public String toString(){
        if(getVariations().isEmpty()){
            throw new ArrayIndexOutOfBoundsException("Not initialized object");
        }
        
        String ret = "";
        
        for(int i = 0; i < getVariations().size(); i++){
            ret+= i + " " + getVariations().get(i) + "\n";
        }
        
        return ret;
    }
    
    /**
     * Returns the list of frame variations
     * @return Array of variations between frames
     */
    public ArrayList<Double> getVariations(){
        return variations;
    }   
  

    /**
     *  Sets the media which will be segmented
     * @param vm the Video to set
     */
    public void setVm(Video vm) {
        this.vm = vm;
    }

    /**
     * Sets the type of descriptor used to compare between frames
     * @param dt the dt to set
     */
    public void setDt(Class<?extends MediaDescriptor> dt) {
        this.dt = dt;
    }
    
    /**
     * Sets the variations array to a new one
     * @param newVars New array of variations
     */
    public void setVariations(ArrayList<Double> newVars){
        variations = new ArrayList<>(newVars);
    }

    /**
     * Sets the frames per second analyzed
     * @param fps the fps to set. It must be greater than 0
     */
    public void setFps(int fpsVal) {
        if(fpsVal<=0){
            throw new IllegalArgumentException("Fps must be grater than 0");
        }else if(fpsVal>24){
            fps = DEFAULT_FPS;
        } else{
            fps = fpsVal;
        }

        frameJump = 24/fps;
        if(frameJump<=0){
            frameJump = 1;
        }
    }
    /**
     * Returns the frames per second analysed
     * @return FPS
     */
    public int getFps(){
        return fps;
    }

    /**
     * Sets the size of the frames when they are resized so calculations
     * may be faster. 0 means frames are not resized
     * @param resizedFrame the resizedFrame to set
     */
    public void setResizedFrame(int rFrame) {
        if(rFrame<0){
            throw new IllegalArgumentException("resizedFrame size cannot be lower than 0");
        }else{
            resizedFrame = rFrame;
        }
    }

    /**
     * Returns Video which will be segmented
     * @return the Video
     */
    public Video getVm() {
        return vm;
    }

    /**
     * Returns the distance between analysed frames. Increasing this value
     * reduces the processing time but makes the analysis less sensible to changes
     * @return the frameJump
     */
    public int getFrameJump() {
        return frameJump;
    }

    /**
     * Returns a list of values which are key frames
     * @return the keyframes
     */
    public ArrayList<Boolean> getKeyframes() {
        return keyframes;
    }
    
    /**
     * Returns the list of images wich are keyframes
     * @return ArrayList of keyframes
     */
    public ArrayList<BufferedImage> getImageKeyframes(){
        return imageKeyFrames;
    }

    /**
     * Method executed when we launch a new thread
     */
    @Override
    public void run() {
        this.init();
    }
    
    /**
     * Returns the pertentage completed of ({@link #calcVariations()}
     * @return Percentage
     */
    public int getPercentageDone(){
        return pertentageDone;
    }
    
    /**
     * Sets the completed percentage of ({@link #calcVariations()}
     * @param pDone Percentage
     */
    void setPercentageDone(int pDone){
        pertentageDone = pDone;
    }

    /**
     * It sets a new video to the class
     * @param video Video set to the class
     */
    @Override
    public void setVideo(Video video) {
        if(video!=null){
            vm = video;
            keyframes = new ArrayList<>();
            imageKeyFrames = new ArrayList<>();
            pertentageDone = 0;
            it = null;
            init();
        }else{
            throw new NullPointerException("Video cannot be null");
        }
    }

    /**
     * Returns the video associated with the class
     * @return The video associated with the class
     */
    @Override
    public Video getVideo() {
        return vm;
    }

    /**
     * Initializes the SegmentationOp so the iterator can be used 
     */
    @Override
    public void init() {
        this.apply();
        it = imageKeyFrames.iterator();
    }
    
    /**
     * Returns true if the iteration has more elements.
     * @return true if the iteration has more elements.
     */
    @Override
    public boolean hasNext(){
        return it.hasNext();
    }
    
    /**
     * Returns the next element in the iteration.
     * @return the next element in the iteration.
     */
    @Override
    public BufferedImage next(){
        return it.next();
    }
}

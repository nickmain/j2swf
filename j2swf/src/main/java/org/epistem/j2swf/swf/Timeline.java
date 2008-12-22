package org.epistem.j2swf.swf;

import java.io.IOException;
import java.util.*;

import com.anotherbigidea.flash.interfaces.SWFTagTypes;

/**
 * A timeline
 *
 * @param <TAGTYPE> the type of tag (either ControlTag or Tag) that
 *                  can be in the frame
 * @author nickmain
 */
public final class Timeline <TAGTYPE extends Tag> 
    implements Iterable<Frame<TAGTYPE>> {

    private SortedMap<Integer,Frame<TAGTYPE>> frames = new TreeMap<Integer, Frame<TAGTYPE>>();
    private Map<String, Frame<TAGTYPE>> framesByLabel = new HashMap<String, Frame<TAGTYPE>>();
    
    /**
     * Get the last frame - adding a frame if there are none
     */
    public Frame<TAGTYPE> lastFrame() {
        if( frames.isEmpty() ) {
            return addFrame();
        }
        
        return frames.get( frames.lastKey() );
    }
    
    /**
     * Add a new frame at the end of the movie
     * 
     * @return the new frame
     */
    public Frame<TAGTYPE> addFrame() {
        int num = frames.isEmpty() ? 0 : frames.lastKey() + 1;
        return getFrame( num );
    }
    
    /**
     * Get the frame with the given number - adding it if necessary
     * 
     * @param number greater or equal to zero
     */
    public Frame<TAGTYPE> getFrame( int number ) {
        Frame<TAGTYPE> frame = frames.get( number );
        if( frame == null ) {
            frame = new Frame<TAGTYPE>( number, this );
            frames.put( number, frame );
        }
        
        return frame;
    }

    /**
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<Frame<TAGTYPE>> iterator() {
        return frames.values().iterator();
    }     
 
    /**
     * Get the frame with the given label.
     * 
     * @param label the label to retrieve
     * @return null if no frame has the given label
     */
    public Frame<TAGTYPE> getFrame( String label ) {
        return framesByLabel.get( label );
    }

    /**
     * Register a frame label
     */
    /*pkg*/ void registerFrame( String label, Frame<TAGTYPE> frame ) {
        framesByLabel.put( label, frame );
    }
    
    /**
     * Deregister a frame label
     */
    /*pkg*/ void deregisterFrame( String label ) {
        framesByLabel.remove( label );
    }
    
    //write the timeline
    /*pkg*/ void write( SWFTagTypes tags ) throws IOException {
     
        Frame<TAGTYPE> lastFrame = lastFrame();        
        int maxFrame = lastFrame.getNumber();
        
        for( int i = 0; i <= maxFrame; i++ ) {
            Frame<TAGTYPE> frame = frames.get( i );
            if( frame != null ) {                
                frame.write( tags );
            }
            
            tags.tagShowFrame();
        }
    }
    
    //write the contents of the first frame
    /*pkg*/ void writeFirst( SWFTagTypes tags ) throws IOException {     
        Frame<TAGTYPE> firstFrame = lastFrame();        
        firstFrame.write( tags );
    }

    //write the close of the first frame and then write remaining frames
    /*pkg*/ void writeRest( SWFTagTypes tags ) throws IOException {

        tags.tagShowFrame();
        
        Frame<TAGTYPE> lastFrame = lastFrame();        
        int maxFrame = lastFrame.getNumber();
        
        for( int i = 1; i <= maxFrame; i++ ) {
            Frame<TAGTYPE> frame = frames.get( i );
            if( frame != null ) {                
                frame.write( tags );
            }
            
            tags.tagShowFrame();
        }
    }
}

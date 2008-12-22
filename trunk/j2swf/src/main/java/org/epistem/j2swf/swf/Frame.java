package org.epistem.j2swf.swf;

import java.io.IOException;
import java.util.*;

import com.anotherbigidea.flash.interfaces.SWFTagTypes;

/**
 * A frame
 *
 * @param <TAGTYPE> the type of tag (either ControlTag or Tag) that
 *                  can be in the frame
 * @author nickmain
 */
public final class Frame<TAGTYPE extends Tag> implements Iterable<TAGTYPE>{

    private boolean isAnchor;
    private String label;
    private int number;
    private Timeline<TAGTYPE> timeline;
    private Collection<TAGTYPE> tags;
    
    /**
     * Get the frame number (zero based)
     */
    public int getNumber() {
        return number;
    }
    
    /**
     * @return the frame label - may be null
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * Whether this frame is a named anchor. This only has meaning if the frame
     * label is not null
     */
    public boolean isAnchor() {
        return isAnchor;
    }

    /**
     * Whether this frame is a named anchor. This only has meaning if the frame
     * label is not null
     */
    public void isAnchor( boolean isAnchor ) {
        this.isAnchor = isAnchor;
    }
    
    /**
     * @param number the frame number
     */
    /*pkg*/ Frame( int number, Timeline<TAGTYPE> timeline ) {
        this.number   = number;
        this.timeline = timeline;
    }
    
    /**
     * Set or clear the the frame label
     * 
     * @param label null to clear the label
     */
    public void setLabel( String label ) {
        if( this.label != null ) {
            timeline.deregisterFrame( this.label );                
        }

        this.label = label;

        if( label != null ) {
            timeline.registerFrame( label, this );
        }
    }
 
    /**
     * Add a tag to this frame
     */
    public void addTag( TAGTYPE tag ) {
        if( tags == null ) tags = new ArrayList<TAGTYPE>();
        tags.add( tag );
    }
    
    /**
     * Get an iterator over the tags in this frame
     * 
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<TAGTYPE> iterator() {
        if( tags == null ) {
            Set<TAGTYPE> set = Collections.emptySet();
            return set.iterator();
        }
        
        return tags.iterator();
    }
    
    //write the contents of a frame (but not the show-frame)
    /*pkg*/ void write( SWFTagTypes tags ) throws IOException {
        
        if( this.tags != null ) {
            for( TAGTYPE tag : this ) {
                tag.write( tags );
            }
        }
        
        if( label != null ) {
            tags.tagFrameLabel( label, isAnchor );
        }        
    }
}

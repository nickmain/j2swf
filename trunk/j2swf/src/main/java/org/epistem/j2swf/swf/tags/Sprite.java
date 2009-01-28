package org.epistem.j2swf.swf.tags;

import java.io.IOException;

import org.epistem.j2swf.swf.ControlTag;
import org.epistem.j2swf.swf.DefinitionTag;
import org.epistem.j2swf.swf.Timeline;

import com.anotherbigidea.flash.interfaces.SWFTagTypes;

/**
 * A sprite definition
 *
 * @author nickmain
 */
public class Sprite extends DefinitionTag {

    /**
     * The sprite timeline
     */
    public final Timeline<ControlTag> timeline = new Timeline<ControlTag>();

    /** @see org.epistem.j2swf.swf.Tag#write(com.anotherbigidea.flash.interfaces.SWFTagTypes) */
    @Override
    protected void write( SWFTagTypes tags ) throws IOException {
        // TODO Auto-generated method stub        
    }
}

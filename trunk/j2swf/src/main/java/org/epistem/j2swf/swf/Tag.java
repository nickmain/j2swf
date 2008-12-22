package org.epistem.j2swf.swf;

import java.io.IOException;

import com.anotherbigidea.flash.interfaces.SWFTagTypes;

/**
 * Base for tags
 *
 * @author nickmain
 */
public abstract class Tag {

    /**
     * Write the tag
     */
    protected abstract void write( SWFTagTypes tags ) throws IOException;

    
}

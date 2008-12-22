package org.epistem.j2swf.ant;

import org.epistem.j2swf.swf.SWFFile;

/**
 * Implemented by ANT tasks that operate on a SWF file. These tasks should
 * only be used inside a SWFBuilderTask container.
 *
 * @author nickmain
 */
public interface SWFTask {

    /**
     * Set the SWF to operate on
     */
    public void setSWFBuilder( SWFFile swfBuilder );
}

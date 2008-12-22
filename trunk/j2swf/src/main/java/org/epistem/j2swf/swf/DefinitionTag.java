package org.epistem.j2swf.swf;

/**
 * Base for definition tags
 *
 * @author nickmain
 */
public abstract class DefinitionTag extends Tag {

    private int symbolId = -1;
    
    /**
     * Get the symbol id
     */
    public final int symbolId() {
        //TODO: this should be generated in coordination with the symbol dictionary
        
        return symbolId;
    }
}

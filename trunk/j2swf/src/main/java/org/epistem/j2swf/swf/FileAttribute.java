package org.epistem.j2swf.swf;

import org.epistem.util.Flag;

import com.anotherbigidea.flash.SWFConstants;

/**
 * The SWF file attributes
 *
 * @author nickmain
 */
public enum FileAttribute {

    @Flag(SWFConstants.FILE_ATTRIBUTES_USE_DIRECT_BLT) 
    UseDirectBlt,
    
    @Flag(SWFConstants.FILE_ATTRIBUTES_USE_GPU)
    UseGPU,
        
    @Flag(SWFConstants.FILE_ATTRIBUTES_HAS_METADATA)
    HasMetaData,
    
    @Flag(SWFConstants.FILE_ATTRIBUTES_AS3)
    ActionScript3,
    
    @Flag(SWFConstants.FILE_ATTRIBUTES_USE_NETWORK)
    UseNetwork;
     
}

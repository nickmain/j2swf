package com.anotherbigidea.flash.avm2.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.anotherbigidea.flash.avm2.ABC;
import com.anotherbigidea.flash.avm2.model.AVM2ABCFile;
import com.anotherbigidea.flash.avm2.model.io.AVM2ABCBuilder;
import com.anotherbigidea.flash.interfaces.SWFTagTypes;
import com.anotherbigidea.flash.interfaces.SWFTags;
import com.anotherbigidea.flash.readers.SWFReader;
import com.anotherbigidea.flash.readers.TagParser;
import com.anotherbigidea.flash.writers.SWFTagTypesImpl;

/**
 * Utilities for loading ABC files
 *
 * @author nickmain
 */
public class AVM2ABCFileLoader {

    /**
     * Extract the ABC files from a SWF 
     */
    public static Collection<AVM2ABCFile> abcFilesFromExistingSWF( File swfFile ) throws IOException {
        
        final List<AVM2ABCFile> files = new ArrayList<AVM2ABCFile>();
        
        SWFTagTypes tags = new SWFTagTypesImpl( null ) {
            /** @see com.anotherbigidea.flash.writers.SWFTagTypesImpl#tagDoABC() */
            @Override
            public ABC tagDoABC( int flags, String name ) throws IOException {
                return new AVM2ABCBuilder() {

                    /** @see com.anotherbigidea.flash.avm2.model.io.AVM2ABCBuilder#done() */
                    @Override
                    public void done() {
                        files.add( this.file );
                        super.done();
                    }                    
                };
            }
        };
        
        FileInputStream in = new FileInputStream( swfFile );        
        SWFTags tagparser = new TagParser( tags );        
        SWFReader reader = new SWFReader( tagparser, in );        
        reader.readFile();
        
        return files;
    }
    
}

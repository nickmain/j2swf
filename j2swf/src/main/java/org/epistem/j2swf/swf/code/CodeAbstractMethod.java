package org.epistem.j2swf.swf.code;

import com.anotherbigidea.flash.avm2.model.AVM2Code;
import com.anotherbigidea.flash.avm2.model.AVM2MethodSlot;
import com.anotherbigidea.flash.avm2.model.AVM2Name;

/**
 * An abstract method
 *
 * @author nickmain
 */
public class CodeAbstractMethod extends CodeMethod {
    
    /*pkg*/ CodeAbstractMethod( CodeClass codeClass, AVM2MethodSlot methodSlot, AVM2Name... paramTypes ) {
        super( codeClass, methodSlot, paramTypes );
    }
    
    /** @see org.epistem.j2swf.swf.code.CodeMethod#makeCode(com.anotherbigidea.flash.avm2.model.AVM2Name[]) */
    @Override
    protected AVM2Code makeCode( AVM2Name... paramTypes ) {
        return null; //since abstract
    }
    
    /**
     * Prepare for writing
     */
    @Override
    /*pkg*/ void prepareForWriting() {
        // nada
    }
}

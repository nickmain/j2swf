package org.epistem.j2swf.swf.code;

import com.anotherbigidea.flash.avm2.model.AVM2MethodSlot;

/**
 * An AVM2 method
 *
 * @author nickmain
 */
public class CodeMethod {

    private final CodeClass codeClass;
    private final AVM2MethodSlot methodSlot;
    
    /*pkg*/ CodeMethod( CodeClass codeClass, AVM2MethodSlot methodSlot ) {
        this.codeClass  = codeClass;
        this.methodSlot = methodSlot;
    }
    
}

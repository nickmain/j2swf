package org.epistem.j2swf.swf.code;

import com.anotherbigidea.flash.avm2.model.AVM2Slot;

/**
 * An AVM2 field
 *
 * @author nickmain
 */
public class CodeField {

    protected final CodeClass codeClass;
    protected final AVM2Slot slot;
    protected final boolean isStatic;
    protected final boolean isConstant;
    
    public CodeField( CodeClass codeClass, AVM2Slot slot, 
                      boolean isStatic, boolean isConstant ) {
        this.codeClass  = codeClass;
        this.slot       = slot;
        this.isStatic   = isStatic;
        this.isConstant = isConstant; 
    }
}

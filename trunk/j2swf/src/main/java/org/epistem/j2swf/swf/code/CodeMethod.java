package org.epistem.j2swf.swf.code;

import com.anotherbigidea.flash.avm2.model.AVM2Code;
import com.anotherbigidea.flash.avm2.model.AVM2MethodSlot;
import com.anotherbigidea.flash.avm2.model.AVM2Name;
import com.anotherbigidea.flash.avm2.model.AVM2QName;

/**
 * An AVM2 method
 *
 * @author nickmain
 */
public class CodeMethod {

    private final CodeClass codeClass;
    private final AVM2MethodSlot methodSlot;
    private final AVM2Code code;
    
    /*pkg*/ CodeMethod( CodeClass codeClass, AVM2MethodSlot methodSlot, AVM2Name... paramTypes ) {
        this.codeClass  = codeClass;
        this.methodSlot = methodSlot;
        
        for( int i = 0; i < paramTypes.length; i++ ) {
            methodSlot.method.addParameter( "arg" + i, paramTypes[i], null );
        }
        
        code = new AVM2Code( methodSlot.method.methodBody, null, paramTypes.length, false );
        code.setupInitialScope();
    }
    
    /**
     * Prepare for writing
     */
    /*pkg*/ void prepareForWriting() {
        code.analyze();
    }
    
    /**
     * Get the code of the method.
     */
    public AVM2Code code() {
        return code;
    }
    
    /**
     * Get the method name
     */
    public AVM2QName name() {
        return methodSlot.name;
    }
}

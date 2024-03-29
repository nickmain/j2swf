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

    protected final CodeClass codeClass;
    protected final AVM2MethodSlot methodSlot;
    protected final AVM2Code code;
    
    public CodeMethod( CodeClass codeClass, AVM2MethodSlot methodSlot, AVM2Name... paramTypes ) {
        this.codeClass  = codeClass;
        this.methodSlot = methodSlot;
        
        if( methodSlot != null ) {
            for( int i = 0; i < paramTypes.length; i++ ) {
                methodSlot.method.addParameter( "arg" + i, paramTypes[i], null );
            }
        }
        
        code = makeCode( paramTypes );
    }

    protected AVM2Code makeCode( AVM2Name... paramTypes ) {
        return new AVM2Code( methodSlot.method.methodBody, null, paramTypes.length, false );        
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

    /** @see java.lang.Object#equals(java.lang.Object) */
    @Override
    public boolean equals( Object obj ) {
        if( obj == null || !( obj instanceof CodeMethod ) ) return false;
        
        return ((CodeMethod) obj).name().equals( name() );
    }

    /** @see java.lang.Object#hashCode() */
    @Override
    public int hashCode() {
        return name().hashCode();
    }
}

package org.epistem.j2swf.swf.code;

import com.anotherbigidea.flash.avm2.model.AVM2Code;
import com.anotherbigidea.flash.avm2.model.AVM2Name;
import com.anotherbigidea.flash.avm2.model.AVM2QName;

/**
 * The static initializer for a class
 *
 * @author nickmain
 */
public class CodeClassInitializer extends CodeMethod {

    CodeClassInitializer( CodeClass codeClass  ) {
        super( codeClass, null );
    }

    /** @see org.epistem.j2swf.swf.code.CodeMethod#makeCode(com.anotherbigidea.flash.avm2.model.AVM2Name[]) */
    @Override
    protected AVM2Code makeCode( AVM2Name... paramTypes ) {
        return AVM2Code.startStaticInitializer( codeClass.avm2class, codeClass.scopeDepth );
    }

    /** @see org.epistem.j2swf.swf.code.CodeMethod#name() */
    @Override
    public AVM2QName name() {
        return new AVM2QName( "<static-initializer>" );
    }
}

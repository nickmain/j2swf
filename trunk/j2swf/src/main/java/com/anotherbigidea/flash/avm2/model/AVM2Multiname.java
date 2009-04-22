package com.anotherbigidea.flash.avm2.model;

import java.util.Arrays;
import java.util.List;

import com.anotherbigidea.flash.avm2.MultiNameKind;

/**
 * A name qualified by a set of namespaces
 *
 * @author nickmain
 */
public class AVM2Multiname extends AVM2Name {

    /**
     * @param name the simple name
     * @param namespaceSet the set of namespaces
     */
    public AVM2Multiname( String name, List<AVM2Namespace> namespaceSet ) {
        super( MultiNameKind.Multiname, null, name, namespaceSet );
    }
    
    /**
     * @param name the simple name
     * @param namespaces the set of namespaces
     */
    public AVM2Multiname( String name, AVM2Namespace... namespaces ) {
        super( MultiNameKind.Multiname, null, name, Arrays.asList( namespaces ));
    }
}

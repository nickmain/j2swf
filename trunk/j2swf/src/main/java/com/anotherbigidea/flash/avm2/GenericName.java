package com.anotherbigidea.flash.avm2;

import java.util.Arrays;

/**
 * A generic type name
 *
 * @author nickmain
 */
public class GenericName extends MultiName {

    /**
     * The generic type
     */
    public final MultiName type;
    
    /**
     * The type parameters
     */
    public final MultiName[] typeParams;
    
    /**
     * Index of the type
     */
    public final int typeIndex;
    
    /**
     * Indices of the type parameters
     */
    public final int[] typeParamIndices;
    
    public GenericName( MultiNameKind kind, int index, MultiName type, MultiName[] typeParams, int typeIndex, int...typeParamIndices ) {
        super( kind, index, null, 0, null, 0, null );
        this.typeIndex        = typeIndex;
        this.typeParamIndices = typeParamIndices;
        this.type             = type;
        this.typeParams       = typeParams;
    }
    
    @Override
    public String toString() {
        return kind.name() + ":" + typeIndex + ":" + Arrays.toString( typeParamIndices );
    }
}

package com.anotherbigidea.flash.avm2.model;

import java.util.Arrays;

import org.epistem.io.IndentingPrintWriter;

import com.anotherbigidea.flash.avm2.MultiNameKind;
import com.anotherbigidea.flash.avm2.model.AVM2ABCFile.WriteContext;
import com.anotherbigidea.flash.avm2.model.io.ConstantPool;

/**
 * A generic type name
 *
 * @author nickmain
 */
public class AVM2GenericName extends AVM2Name {

    /**
     * The generic type
     */
    public final AVM2Name type;
    
    /**
     * The type parameters
     */
    public final AVM2Name[] typeParams;
    
    public AVM2GenericName( AVM2Name type, AVM2Name...typeParams ) {
        super( MultiNameKind.GenericName, null );
        this.type       = type;
        this.typeParams = typeParams;
    }

    /** @see com.anotherbigidea.flash.avm2.model.AVM2Name#dump(org.epistem.io.IndentingPrintWriter) */
    @Override
    public void dump( IndentingPrintWriter out ) {
        type.dump( out );
        out.print( ".<" );
        for( int i = 0; i < typeParams.length; i++ ) {
            if( i > 0 ) out.print( "," );
            typeParams[i].dump( out );
        }
        out.print( ">" );
    }

    /** @see com.anotherbigidea.flash.avm2.model.AVM2Name#compareTo(com.anotherbigidea.flash.avm2.model.AVM2Name) */
    @Override
    public int compareTo( AVM2Name o ) {
        if( o == null ) return -1;
        if(! (o instanceof AVM2Name) ) return -1;
        
        AVM2Name other = (AVM2Name) o;
        
        //sort by kind first
        if( other.kind != kind ) return kind.compareTo( other.kind );
        
        AVM2GenericName otherGen = (AVM2GenericName) other;
        int comp = type.compareTo( otherGen.type );
        if( comp != 0 ) return comp;

        for( int i = 0; i < typeParams.length; i++ ) {
            if( otherGen.typeParams.length <= i ) return 1;
            
            comp = typeParams[i].compareTo( otherGen.typeParams[i] );
            if( comp != 0 ) return comp;
        }
        
        if( otherGen.typeParams.length > typeParams.length ) return -1;        
        return 0;
    }

    /** @see com.anotherbigidea.flash.avm2.model.AVM2Name#equals(java.lang.Object) */
    @Override
    public boolean equals( Object obj ) {
        if( obj == null ) return false;
        if( ! (obj instanceof AVM2GenericName)) return false;
        
        AVM2GenericName other = (AVM2GenericName) obj;
        
        if( ! type.equals( other.type ) ) return false;
        
        if( typeParams.length != other.typeParams.length ) return false;
        for( int i = 0; i < typeParams.length; i++ ) {
            if( ! typeParams[i].equals( other.typeParams[i] ) ) return false;
        }
        
        return true;
    }

    /** @see com.anotherbigidea.flash.avm2.model.AVM2Name#indexIn(com.anotherbigidea.flash.avm2.model.io.ConstantPool) */
    @Override
    public int indexIn( ConstantPool pool ) {

        int typeIndex = type.indexIn( pool );
        int[] typeParamIndices = new int[ typeParams.length ];
        for( int i = 0; i < typeParams.length; i++ ) {
            typeParamIndices[i] = typeParams[i].indexIn( pool );
        }
        
        return pool.nameIndex( kind, typeIndex, typeParamIndices ).poolIndex;
    }

    /** @see com.anotherbigidea.flash.avm2.model.AVM2Name#initPool(com.anotherbigidea.flash.avm2.model.AVM2ABCFile.WriteContext) */
    @Override
    public void initPool( WriteContext context ) {
        indexIn( context.pool );
    }
    
    /** @see java.lang.Object#hashCode() */
    @Override
    public int hashCode() {
        return type.hashCode() * Arrays.hashCode( typeParams ) ;
    }
}

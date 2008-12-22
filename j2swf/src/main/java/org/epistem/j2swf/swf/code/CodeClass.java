package org.epistem.j2swf.swf.code;

import static com.anotherbigidea.flash.avm2.Operation.*;
import static com.anotherbigidea.flash.avm2.model.AVM2StandardNamespace.EmptyPackage;

import org.epistem.code.LocalValue;

import com.anotherbigidea.flash.avm2.NamespaceKind;
import com.anotherbigidea.flash.avm2.instruction.Instruction;
import com.anotherbigidea.flash.avm2.model.*;

/**
 * A class
 *
 * @author nickmain
 */
public class CodeClass {

    private final AVM2ABCFile abcFile;    
    private final AVM2Class avm2class;
    private final int scopeDepth;
    
    /**
     * @param name the fully qualified class name
     * @param superclasses the names of the superclasses in order from top to bottom
     * @param isSealed whether the class is sealed (true) or dynamic
     * @param isFinal whether the class is final
     * @param isInterface whether the class is an interface
     * @return the new class
     */
    /*pkg*/ CodeClass( AVM2ABCFile abcFile,
                       String name,
                       boolean isSealed, boolean isFinal, boolean isInterface,
                       String... superclasses ) {

        this.abcFile = abcFile;
        String superclass = superclasses[ superclasses.length - 1 ];
       
        //protected ns is pkg:shortname
        String pkg = "";
        String shortName = name;
        int period = name.lastIndexOf( "." );
        if( period >= 0 ) {
            pkg = name.substring( 0, period );
            shortName = name.substring( period + 1 );            
        }
       
        AVM2QName qname      = new AVM2QName( name );
        AVM2QName qnameSuper = new AVM2QName( superclass );
        AVM2Namespace protNS = 
            new AVM2Namespace( NamespaceKind.ProtectedNamespace, 
                               pkg + ":" + shortName );      
       
        avm2class = abcFile.addClass( qname, qnameSuper, 
                                      isSealed, isFinal, isInterface, 
                                      protNS );
        
        //create the initialization script
        AVM2Code.ClassInitializationScript script = 
            AVM2Code.classInitializationScript( avm2class, true );
        for( String supclass : superclasses ) {
            script.addSuperclass( supclass );
        }
        scopeDepth = script.finish();
        
        //create the static initializer
        AVM2Code.defaultStaticInit( avm2class, scopeDepth );
        //TODO:expose non-default static init
        
        //no-arg constructor
        AVM2Code cons = AVM2Code.startNoArgConstructor( avm2class );
        THIS_IS_ONLY_FOR_DEV_PURPOSES( cons );
    }
    
    private void THIS_IS_ONLY_FOR_DEV_PURPOSES( AVM2Code cons ) {
        
        cons.trace( "In Constructor" );
        cons.getLocal( cons.thisValue );
        cons.getProperty( "graphics" );
        cons.coerceTo( "flash.display.Graphics" );

        LocalValue<Instruction> g = cons.newLocal();
        cons.setLocal( g );

        cons.callPropVoid( g, "beginFill", 0x888800 );
        cons.callPropVoid( g, "lineStyle", 5, 0x000088 );
        cons.callPropVoid( g, "moveTo", 10, 10 );
        cons.callPropVoid( g, "lineTo", 90, 10 );
        cons.callPropVoid( g, "lineTo", 90, 90 );
        cons.callPropVoid( g, "lineTo", 10, 90 );
        cons.callPropVoid( g, "lineTo", 10, 10 );
        cons.callPropVoid( g, "endFill" );

        cons.trace( "At end of Constructor" );
        
        cons.returnVoid();    
        cons.analyze();
    }
    
    /**
     * Prepare the class for writing - perform any code analysis etc
     */
    /*pkg*/ void prepareForWriting() {
        //TODO: prepare all methods
        //TODO: prepare static init
        //TODO: prepare constructor
    }
    
    /**
     * Get the class name
     */
    public String getName() {
        return avm2class.name.toQualString();
    }
    
    /**
     * Get the superclass name
     */
    public String getSuperclass() {
        return avm2class.superclass.toString();
    }
    
}

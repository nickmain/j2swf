package org.epistem.j2swf.swf.code;

import java.util.HashSet;
import java.util.Set;

import com.anotherbigidea.flash.avm2.NamespaceKind;
import com.anotherbigidea.flash.avm2.Operation;
import com.anotherbigidea.flash.avm2.instruction.Instruction;
import com.anotherbigidea.flash.avm2.model.*;

/**
 * A class
 *
 * This is still half baked - fix it up later
 *
 * @author nickmain
 */
public class CodeClass {

    public final AVM2ABCFile abcFile; //TODO: this should be hidden 
    public final AVM2Class avm2class; //TODO: this should be hidden
    
    protected final int scopeDepth;
    private final Set<CodeMethod> methods = new HashSet<CodeMethod>();
    
    private CodeClassInitializer staticInit;
    
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
                       String initCall,
                       boolean isSealed, boolean isFinal, boolean isInterface,                       
                       String... superclasses ) {

        this.abcFile = abcFile;
        String superclass = (superclasses.length > 0) ? 
                                superclasses[ superclasses.length - 1 ] :
                                null;
       
        //protected ns is pkg:shortname
        String pkg = "";
        String shortName = name;
        int period = name.lastIndexOf( "." );
        if( period >= 0 ) {
            pkg = name.substring( 0, period );
            shortName = name.substring( period + 1 );            
        }
       
        AVM2QName qname      = new AVM2QName( name );
        AVM2QName qnameSuper = (superclass != null) ?
                                   new AVM2QName( superclass ) :
                                   null;
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
        
        AVM2QName initCallName = (initCall != null) ? new AVM2QName( initCall ) : null;
        scopeDepth = script.finish( initCallName );

        AVM2Code.defaultStaticInit( avm2class, scopeDepth );
    }
    
    /**
     * Wrap another class for overriding purposes
     */
    protected CodeClass( CodeClass other ) {
        this.abcFile    = other.abcFile;
        this.avm2class  = other.avm2class;
        this.scopeDepth = other.scopeDepth;
    }
    
    /**
     * Get the static initializer
     */
    public CodeClassInitializer getStaticInitializer() {
        if( staticInit == null ) {
            staticInit = new CodeClassInitializer( this );
            methods.add( staticInit );
        }
        
        return staticInit;
    }
    
    /**
     * Prepare the class for writing - perform any code analysis etc
     */
    /*pkg*/ void prepareForWriting() {
        
        //make sure that the static initializer is terminated
        if( staticInit != null ) {
            Instruction last = staticInit.code.instructions.last();
            if( last == null || last.operation != Operation.OP_returnvoid ) {
                staticInit.code.returnVoid();
            }
        }
        
        for( CodeMethod method : methods ) method.prepareForWriting();
        
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
    
    /**
     * Add an instance method
     * 
     * @param name the method name
     * @param returnType the return type
     * @param isFinal whether final
     * @param isOverride whether an override method
     * @param paramTypes the parameters types
     */
    public CodeMethod addInstanceMethod( AVM2QName name, AVM2Name returnType,
                                         boolean isFinal, boolean isOverride, 
                                         AVM2Name... paramTypes ) {
        
        AVM2Method method = new AVM2Method( returnType, null );
        method.methodBody.scopeDepth = scopeDepth + 1;
        AVM2MethodSlot slot = avm2class.traits.addMethod( name, method, isFinal, isOverride );
        CodeMethod cm = new CodeMethod( this, slot, paramTypes );
        methods.add( cm );
        return cm;
    }
    
    /**
     * Add an abstract method
     * 
     * @param name the method name
     * @param returnType the return type
     * @param isOverride whether an override method
     * @param paramTypes the parameters types
     */
    public CodeMethod addAbstractMethod( AVM2QName name, AVM2Name returnType,
                                         boolean isOverride, 
                                         AVM2Name... paramTypes ) {
        
        AVM2Method method = new AVM2Method( returnType, null );
        AVM2MethodSlot slot = avm2class.traits.addMethod( name, method, false, isOverride );
        CodeMethod cm = new CodeAbstractMethod( this, slot, paramTypes );
        methods.add( cm );
        return cm;
    }
    
    /**
     * Add a static method
     * 
     * @param name the method name
     * @param paramTypes the parameters types
     * @param returnType the return type
     */
    public CodeMethod addStaticMethod( AVM2QName name, AVM2Name returnType, 
                                       AVM2Name... paramTypes ) {
        
        AVM2Method method = new AVM2Method( returnType, null );
        method.methodBody.scopeDepth = scopeDepth;
        AVM2MethodSlot slot = avm2class.staticTraits.addMethod( name, method, false, false );
        CodeMethod cm = new CodeMethod( this, slot, paramTypes );
        methods.add( cm );
        return cm;
    }
}

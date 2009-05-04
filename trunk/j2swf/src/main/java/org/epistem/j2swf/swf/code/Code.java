package org.epistem.j2swf.swf.code;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.epistem.j2swf.swf.ControlTag;

import com.anotherbigidea.flash.avm2.ABC;
import com.anotherbigidea.flash.avm2.MethodInfoFlags;
import com.anotherbigidea.flash.avm2.model.AVM2ABCFile;
import com.anotherbigidea.flash.avm2.model.AVM2Method;
import com.anotherbigidea.flash.avm2.model.AVM2Name;
import com.anotherbigidea.flash.interfaces.SWFTagTypes;

/**
 * A chunk of AVM2/ABC bytecode data (see AVM2 Overview).
 *
 * @author nickmain
 */
public class Code extends ControlTag {

    private boolean lazyInit = true;
    private final Map<String, CodeClass> classes = new LinkedHashMap<String, CodeClass>();
    private final Map<String, CodeInterface> interfaces = new LinkedHashMap<String, CodeInterface>();
    
    /**
     * The name of this code chunk
     */
    public final String name;
    
    /**
     * @param name the code name
     */
    public Code( String name ) {
        this.name = name;
    }
    
    /**
     * Whether this code is initialized lazily (default = true )
     */
    public boolean isLazyInit() {
        return lazyInit;
    }
    
    /**
     * Set whether this code is initialized lazily.
     */
    public void isLazyInit( boolean lazy ) {
        this.lazyInit = lazy;
    }
    
    /**
     * Add a new class
     * 
     * @param name the fully qualified class name
     * @param initCall the name of a void/no-arg static init method to call - null for none
     * @param superclasses the names of the superclasses in order from top to bottom
     * @param isSealed whether the class is sealed (true) or dynamic
     * @param isFinal whether the class is final
     * @return the new class
     */
    public CodeClass addClass( String name, String initCall,
                               boolean isSealed, boolean isFinal, 
                               String... superclasses ) {

        CodeClass cc = new CodeClass( name, initCall,
                                      isSealed, isFinal,
                                      superclasses );
        classes.put( name, cc );        
        return cc;
    }

    /**
     * Add a new interface
     * 
     * @param name the fully qualified class name
     * @param superIfaces the names of the super-interfaces
     * @return the new interface
     */
    public CodeInterface addInterface( String name, String...superIfaces ) {
        CodeInterface iface = new CodeInterface( name, superIfaces );
        interfaces.put( name, iface );
        return iface;
    }
    
    /**
     * Add a standalone function to the code
     * 
     * TODO: wrap AVM2Method in CodeMethod or create a CodeFunction ?
     * 
     * @param retType the function return type
     * @return the function method
     */
    public AVM2Method addFunction( AVM2Name retType ) {        
        return abcFile.addFunctionClosure( retType, 
                                           EnumSet.noneOf( MethodInfoFlags.class ) );        
    }
    
    /**
     * Get a class
     * @param name the class name
     * @return null if no such class exists
     */
    public CodeClass getClass( String name ) {
        return classes.get( name ); 
    }
    
    /**
     * Get an iterator over the classes defined in this code tag
     */
    public Iterator<CodeClass> classes() {
        return classes.values().iterator();
    }
    
    /**
     * @see org.epistem.j2swf.swf.Tag#write(com.anotherbigidea.flash.interfaces.SWFTagTypes)
     */
    protected void write( SWFTagTypes tags ) throws IOException {    
        
        AVM2ABCFile abcFile = new AVM2ABCFile();
        
        for( CodeClass cc : classes.values() ) {
            cc.prepareForWriting();
        }
       
        prepareABCforWriting( abcFile );
        
        ABC abc = tags.tagDoABC( lazyInit ? 1 : 0, name );
        abcFile.write( abc );
    }
    
    /**
     * Override to allow the ABC file to be manipulated before writing
     */
    protected void prepareABCforWriting( AVM2ABCFile abc ) {
        //to be overridden
    }
}

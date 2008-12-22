package org.epistem.j2swf.swf;

import static org.epistem.j2swf.swf.FileAttribute.ActionScript3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import org.epistem.j2swf.swf.code.CodeClass;
import org.epistem.util.FlagIO;

import com.anotherbigidea.flash.SWFConstants;
import com.anotherbigidea.flash.interfaces.SWFTagTypes;
import com.anotherbigidea.flash.structs.Color;
import com.anotherbigidea.flash.writers.SWFWriter;
import com.anotherbigidea.flash.writers.TagWriter;

/**
 * A SWF file.
 * 
 * @author nickmain
 */
public final class SWFFile {
    
    private int width      = 500;
    private int height     = 450;
    private int frameRate  = 12;
    private int version    = 9;
    private Color background = Color.WHITE;
    
    private final SortedMap<Integer,String> sceneNames = new TreeMap<Integer, String>(); 
    private final Map<String, Integer> scenes = new HashMap<String, Integer>();
    private final Map<String,DefinitionTag> symbolClasses = new HashMap<String, DefinitionTag>();
    private String mainClass;
    private int maxRecursionDepth = -1;
    private int scriptTimeoutSecs = -1;
    
    /**
     * The file attributes (mutable). By default ActionScript3 is set.
     */
    public final Collection<FileAttribute> attributes = EnumSet.of( ActionScript3 );
    
    /**
     * The main timeline
     */
    public final Timeline<Tag> timeline = new Timeline<Tag>();
    
    /**
     * @param width the movie width in pixels
     */
    public void setWidth( int width ) {
        this.width = width;
    }

    /**
     * @param height the movie height in pixels
     */
    public void setHeight( int height ) {
        this.height = height;
    }

    /**
     * @param frameRate the frames per second
     */
    public void setFrameRate( int frameRate ) {
        this.frameRate = frameRate;
    }

    /**
     * @param version the flash version
     */
    public void setVersion( int version ) {
        this.version = version;
    }

    /**
     * Set the execution limits for code.
     * 
     * @param maxRecursionDepth the maximum recursion depth, > 0
     * @param timeoutSecs the number of seconds before timeout, > 0
     */
    public void setCodeLimits( int maxRecursionDepth, int timeoutSecs ) {
        this.maxRecursionDepth = maxRecursionDepth;
        this.scriptTimeoutSecs = timeoutSecs;
    }
    
    /**
     * @param background the RGB background color
     */
    public void setBackground( Color background ) {
        this.background = background;
    }

    /**
     * The movie width in pixels
     */
    public int getWidth() {
        return width;
    }

    /**
     * The movie height in pixels
     */
    public int getHeight() {
        return height;
    }

    /**
     * The frames per second
     */
    public int getFrameRate() {
        return frameRate;
    }

    /**
     * The flash version
     */
    public int getVersion() {
        return version;
    }

    /**
     * The RGB background color
     */
    public Color getBackground() {
        return background;
    }

    /**
     * Get the first frame of a scene
     * 
     * @param sceneName the scene name
     * @return null if the scene does not exist
     */
    public Frame<Tag> getScene( String sceneName ) {
        Integer frameNum = scenes.get( sceneName );
        if( frameNum == null ) return null;
        return timeline.getFrame( frameNum );
    }
    
    /**
     * Get the scene that a frame belongs to. The frame need not be the
     * first frame of the scene.
     * 
     * @param frame the frame
     * @return null if there is no scene containing the frame
     */
    public String getScene( Frame<Tag> frame ) {
        int num = frame.getNumber();
        String scene = sceneNames.get( num );
        if( scene != null ) return scene;

        SortedMap<Integer,String> head = sceneNames.headMap( num );
        if( head.isEmpty() ) return null;
        return sceneNames.get( head.lastKey() );
    }
    
    /**
     * Get all the known scene names
     * @return immutable collection which may be empty
     */
    public Collection<String> getSceneNames() {
        return Collections.unmodifiableSet( scenes.keySet());
    }
    
    /**
     * Start a new scene (or move an existing scene)
     * 
     * @param name the name of the scene
     * @param firstFrame the first frame of the scene
     */
    public void startScene( String name, Frame<Tag> firstFrame ) {
        int frameNumber = firstFrame.getNumber();
        
        Integer existingFrameNum = scenes.get( name );
        if( existingFrameNum != null ) {
            sceneNames.remove( existingFrameNum );
        }
        
        scenes.put( name, frameNumber );
        sceneNames.put( frameNumber, name );
    }
    
    /**
     * Set the main class for the SWF
     */
    public void setMainClass( CodeClass clazz ) {
        mainClass = clazz.getName();
    }
    
    /**
     * Write the SWF that is currently specified to the given file.
     * 
     * @param swfFile the file to create.
     * @param compressed whether the file is compressed or not
     */
    public void write( File swfFile, boolean compressed ) throws IOException {
        FileOutputStream out = new FileOutputStream( swfFile );
        try {
            write( out, compressed );
        } finally {
            out.close();
        }
    }
    
    /**
     * Write the currently specified SWF to the given output stream. The
     * stream is not closed.
     * 
     * @param out the target stream.
     * @param compressed whether the file is compressed or not
     */
    public void write( OutputStream out, boolean compressed ) throws IOException {
        SWFWriter swfWriter = new SWFWriter( out );
        swfWriter.setCompression( compressed );
        
        TagWriter tags = new TagWriter( swfWriter );
        write( tags );
    }
    
    /**
     * Write the header and tags to a SWFTagTypes implementation.
     */
    public void write( SWFTagTypes swf ) throws IOException {
        swf.header( version, -1, 
                    width * SWFConstants.TWIPS, 
                    height * SWFConstants.TWIPS, 
                    frameRate, -1 );
        
        swf.tagFileAttributes( FlagIO.toFlags( FileAttribute.class, attributes ));
        
        if( maxRecursionDepth > 0 ) {
            swf.tagScriptLimits( maxRecursionDepth, scriptTimeoutSecs );
        }
        
        //TODO: write metadata
        
        swf.tagSetBackgroundColor( background );

        timeline.writeFirst( swf );
        
        //emit class mappings at end of first frame
        Map<Integer,String> classes = new TreeMap<Integer, String>();
        if( mainClass != null ) classes.put( 0, mainClass );
        for( Map.Entry<String,DefinitionTag> e : symbolClasses.entrySet() ) {
            classes.put( e.getValue().symbolId(), e.getKey() );            
        }        
        swf.tagSymbolClass( classes );
        
        timeline.writeRest( swf );
        
        //TODO: file info
        //TODO: emit scenes
                
        swf.tagEnd();
    }
}

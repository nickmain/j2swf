/****************************************************************
 * Copyright (c) 2001, David N. Main, All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the 
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the following 
 * disclaimer. 
 * 
 * 2. Redistributions in binary form must reproduce the above 
 * copyright notice, this list of conditions and the following 
 * disclaimer in the documentation and/or other materials 
 * provided with the distribution.
 * 
 * 3. The name of the author may not be used to endorse or 
 * promote products derived from this software without specific 
 * prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A 
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ****************************************************************/
package com.anotherbigidea.flash.writers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.epistem.io.InStream;
import org.epistem.io.IndentingPrintWriter;
import org.epistem.util.CommandLineArgs;
import org.epistem.util.Hex;

import com.anotherbigidea.flash.SWFConstants;
import com.anotherbigidea.flash.avm2.ABC;
import com.anotherbigidea.flash.avm2.model.io.AVM2ABCBuilder;
import com.anotherbigidea.flash.interfaces.SWFActions;
import com.anotherbigidea.flash.interfaces.SWFFileSignature;
import com.anotherbigidea.flash.interfaces.SWFShape;
import com.anotherbigidea.flash.interfaces.SWFTagTypes;
import com.anotherbigidea.flash.interfaces.SWFTags;
import com.anotherbigidea.flash.interfaces.SWFText;
import com.anotherbigidea.flash.interfaces.SWFVectors;
import com.anotherbigidea.flash.readers.SWFReader;
import com.anotherbigidea.flash.readers.TagParser;
import com.anotherbigidea.flash.structs.AlphaColor;
import com.anotherbigidea.flash.structs.AlphaTransform;
import com.anotherbigidea.flash.structs.ButtonRecord;
import com.anotherbigidea.flash.structs.ButtonRecord2;
import com.anotherbigidea.flash.structs.Color;
import com.anotherbigidea.flash.structs.ColorTransform;
import com.anotherbigidea.flash.structs.Matrix;
import com.anotherbigidea.flash.structs.Rect;
import com.anotherbigidea.flash.structs.SoundInfo;
import com.anotherbigidea.flash.video.ScreenVideoPacket;

/**
 * An implementation of the SWFTagTypes interface that produces a debug dump
 */
public class SWFTagDumper 
	implements SWFTagTypes, SWFShape, SWFText, SWFFileSignature 
{
    protected PrintWriter writer;
    protected String dashes = "---------------";
    protected boolean dumpHex;
    protected String indent = "";
    protected boolean dumpActions = false;
    
    /** 
     * Dump to System.out
     * @param dumpHex if true then binary data will dumped as hex - otherwise skipped
     */
    public SWFTagDumper( boolean dumpHex, boolean decompileActions )
    {
        this( System.out, dumpHex, decompileActions );
    }
    
    /**
     * Dump to the given output stream
     * @param dumpHex if true then binary data will dumped as hex - otherwise skipped
     */
    public SWFTagDumper( OutputStream out, 
                         boolean dumpHex, 
                         boolean dumpActions )
    {
        writer = new PrintWriter( out );
        this.dumpHex = dumpHex;
        this.dumpActions = dumpActions;
    }
    
    public SWFTagDumper( PrintWriter writer, 
                         boolean dumpHex, 
                         boolean dumpActions )
    {
        this.writer = writer;
        this.dumpHex = dumpHex;
        this.dumpActions = dumpActions;
    }

	/**
	 * @see SWFFileSignature#signature(String)
	 */
	public void signature( String sig ) {
		println( "signature: " + sig );
	}

    protected void println( String line )
    {
        writer.println( indent + line );
    }
    
    protected void print( String s )
    {
        writer.print( s );
    }
    
    /**
     * SWFTags interface
     */    
    public void tag( int tagType, boolean longTag, byte[] contents ) 
        throws IOException
    {
        println( "Tag " + tagType + " length=" + contents.length );
        
        if( dumpHex )
        {
            Hex.dump( writer, contents, 0L, indent + "    ", false );
            println( dashes );
        }
    }

    /**
     * SWFHeader interface.
     */
    public void header( int version, long length,
                        int twipsWidth, int twipsHeight,
                        int frameRate, int frameCount ) throws IOException
    {
        println( "header: version=" + version + 
                 " length=" + length + " width=" + twipsWidth +
                 " height=" + twipsHeight + " rate=" + frameRate +
                 " frame-count=" + frameCount );
    }
    
    /**
     * SWFTagTypes interface
     */
    public void tagEnd() throws IOException
    {
        println( "end" );
        println( dashes );
    }

    
    
    /** @see com.anotherbigidea.flash.interfaces.SWFSpriteTagTypes#tagDoABC(int, java.lang.String) */
    public ABC tagDoABC(int flags, String filename) throws IOException {

        println( "ABC File '" + filename + "' " + (flags != 0 ? "lazy-initialization" : "" ));
        
        if( ! dumpActions ) {
            return null;
        }
        
        return new AVM2ABCBuilder() {

            /** @see com.anotherbigidea.flash.avm2.model.io.AVM2ABCBuilder#done() */
            @Override
            public void done() {
                super.done();

                IndentingPrintWriter ipw = new IndentingPrintWriter( writer );
                ipw.setIndentLevel( indent.length() / 4 );
                ipw.setIndent( "  " );
                
                this.file.dump( ipw );
            }
        };
    }

    /** @see com.anotherbigidea.flash.interfaces.SWFSpriteTagTypes#tagSymbolClass(java.util.Map) */
    public void tagSymbolClass(Map<Integer, String> classes) throws IOException {
        println( "symbol classes:" );
        
        for( Map.Entry<Integer, String> entry : classes.entrySet() ) {
            println( "  " + entry.getKey() + " --> " + entry.getValue() );
        }
    }
    
    /**
     * SWFTagTypes interface
     */
    public void tagStartSound( int soundId, SoundInfo info ) throws IOException
        println( "start-sound id=" + soundId + " " + info );
    
    /**
     * SWFTagTypes interface
     */
    public void tagSoundStreamHead( 
        printSoundStreamHead( "sound-stream-head", 
            playbackFrequency, playback16bit, playbackStereo,
    
    /**
     * SWFTagTypes interface
     */
    public void tagSoundStreamHead2( 
        printSoundStreamHead( "sound-stream-head-2", 
            playbackFrequency, playback16bit, playbackStereo,
    
    public void printSoundStreamHead( String name,
        if( playbackFrequency == SWFConstants.SOUND_FREQ_11KHZ ) playFreq = "11";
        if( streamFrequency == SWFConstants.SOUND_FREQ_11KHZ ) streamFreq = "11";
        if( streamFormat == SWFConstants.SOUND_FORMAT_ADPCM ) format = "ADPCM";
        println( name + " play at " + playFreq + "kHz stereo=" + playbackStereo +
                 " 16bit=" + playback16bit + " | Stream at " + streamFreq +
                 "kHz format=" + format + " stereo=" + streamStereo +
                 " 16bit=" + stream16bit + " Avg-Samples=" + averageSampleCount );
    
    
     * SWFTagTypes interface
     */
    public void tagSoundStreamBlock( byte[] soundData ) throws IOException
        println( "sound-stream-block" );
        
        if( dumpHex )
        {
            Hex.dump( writer, soundData, 0L, indent + "    ", false );
            println( dashes );
        }        
     * SWFTagTypes interface
     */
    public void tagSerialNumber( byte[] serialNumber ) throws IOException
        println( "serial number:\n" + Hex.dump( serialNumber, 0, "  " ) );       
                                                              
     * SWFTagTypes interface
     */
    public void tagGenerator( byte[] data ) throws IOException
        println( "generator tag" );
        
        if( dumpHex )
        {
            Hex.dump( writer, data, 0L, indent + "    ", false );
            println( dashes );
        }        
     * SWFTagTypes interface
     */
    public void tagGeneratorText( byte[] data ) throws IOException
        println( "generator text" );
        
        if( dumpHex )
        {
            Hex.dump( writer, data, 0L, indent + "    ", false );
            println( dashes );
        }        
    
     * SWFTagTypes interface
     */
    public void tagGeneratorFont( byte[] data ) throws IOException
        println( "generator font" );
        
        if( dumpHex )
        {
            Hex.dump( writer, data, 0L, indent + "    ", false );
            println( dashes );
        }        
    /**
     * SWFTagTypes interface
     */
    public void tagGeneratorCommand( byte[] data ) throws IOException
        println( "generator command" );
        
        if( dumpHex )
        {
            Hex.dump( writer, data, 0L, indent + "    ", false );
            println( dashes );
        }        

    /**
     * SWFTagTypes interface
     */
    public void tagNameCharacter( byte[] data ) throws IOException
        println( "generator name character" );
        
        if( dumpHex )
        {
            Hex.dump( writer, data, 0L, indent + "    ", false );
            println( dashes );
        }        
     * SWFTagTypes interface
     */
    public void tagDefineBits( int id, byte[] imageData ) throws IOException
        println( "jpeg bits" );
        
        if( dumpHex )
        {
            Hex.dump( writer, imageData, 0L, indent + "    ", false );
            println( dashes );
        }        
     * SWFTagTypes interface
     */
    public void tagJPEGTables( byte[] jpegEncodingData ) throws IOException
        println( "jpeg encoding data" );
        
        if( dumpHex )
        {
            Hex.dump( writer, jpegEncodingData, 0L, indent + "    ", false );
            println( dashes );
        }        

     * SWFTagTypes interface
     */
    public void tagDefineBitsJPEG3( int id, byte[] imageData, byte[] alphaData ) throws IOException
        println( "jpeg with alpha" );
        
        if( dumpHex )
        {
            Hex.dump( writer, imageData, 0L, indent + "    ", false );
            println( "--- Alpha Channel follows ---" );
            Hex.dump( writer, alphaData, 0L, indent + "    ", false );
            println( dashes );
        }         
    
    /**
     * SWFTagTypes interface
     */
    public void tagDefineSound( int id, int format, int frequency,
                                boolean bits16, boolean stereo,
                                int sampleCount, byte[] soundData ) throws IOException
        if( frequency == SWFConstants.SOUND_FREQ_11KHZ ) freq = "11";
        if( format == SWFConstants.SOUND_FORMAT_ADPCM ) formatS = "ADPCM";
        println( "define sound: id=" + id + " format=" + formatS + 
        
        if( dumpHex )
        {
            Hex.dump( writer, soundData, 0L, indent + "    ", false );
            println( dashes );
        }        
     * SWFTagTypes interface
     */
    public void tagDefineButtonSound( int buttonId,
        println( "define button sound: id=" + buttonId );
        println( "    roll-over sound=" + rollOverSoundId + " " + rollOverSoundInfo );
        println( "    roll-out  sound=" + rollOutSoundId  + " " + rollOutSoundInfo );
        println( "    press     sound=" + pressSoundId    + " " + pressSoundInfo );
        println( "    release   sound=" + releaseSoundId  + " " + releaseSoundInfo );       
    
    /**
     * SWFTagTypes interface
     */
    public void tagShowFrame() throws IOException
    {
        println( "---------- frame ----------" );
    /**
     * SWFTagTypes interface
     */
    public SWFActions tagDoAction() throws IOException
        if( ! dumpActions ) {
            println( "AVM1 actions" );
            return null;
        }

        println( "AVM1 actions:" );
        
        ActionTextWriter acts = new ActionTextWriter( writer );
        acts.indent = "    " + indent ;
        return acts;
    }
    
	/**
	 * SWFTagTypes interface
	 */
	public SWFActions tagDoInitAction( int spriteId ) throws IOException
	{
        if( ! dumpActions ) {
            println( "AVM1 init actions" );
            return null;
        }
	    
		println( "init actions for sprite " + spriteId + ":" );
        
		ActionTextWriter acts = new ActionTextWriter( writer );
		acts.indent = "    " + indent ;
		return acts;
	}
    
     * SWFTagTypes interface
     */
    public SWFShape tagDefineShape( int id, Rect outline ) throws IOException
        println( "shape id=" + id + "   " + outline );
        return this;
    
    
     * SWFTagTypes interface
     */
    public SWFShape tagDefineShape2( int id, Rect outline ) throws IOException
        return this;
    
     * SWFTagTypes interface
     */
    public SWFShape tagDefineShape3( int id, Rect outline ) throws IOException
        println( "shape3 id=" + id + "   " + outline );
        return this;
    
    /**
     * SWFTagTypes interface
     */    
    public void tagFreeCharacter( int charId ) throws IOException 
    {
        println( "free character id=" + charId );
    }
    
    /**
     * SWFTagTypes interface
     */    
    public void tagPlaceObject( int charId, int depth, 
    {
    }
    /**
     * SWFTagTypes interface
     */    
    public SWFActions tagPlaceObject2( boolean isMove,
                                       int clipDepth,
                                       int depth,
                                       int charId,
                                       Matrix matrix,
                                       AlphaTransform cxform,
                                       int ratio,
                                       String name,
                                       int clipActionFlags )  
        throws IOException    
    {
        println( "place-object2 move=" + isMove +
                 " id=" + charId +                         
                 " ratio=" + ratio +
                 " name=" + name +
        
        if( clipActionFlags != 0 )
        {
            println( "  clip-actions: " + Integer.toBinaryString( clipActionFlags ) );
            acts.indent = "    " + indent ;
            return acts;
        }
        
        return null;
    }
        
    /**
     * SWFTagTypes interface
     */ 
    {
    }
        
    /**
     * SWFTagTypes interface
     */ 
    {
    }

    /**
     * SWFTagTypes interface
     */ 
    {
        println( "background-color  " + color );        
    }

    /**
     * SWFTagTypes interface
     */ 
        println( "frame-label " + label );
    
	/**
	 * SWFTagTypes interface
	 */     
	public void tagFrameLabel( String label, boolean isAnchor ) throws IOException {    
		println( "frame-label " + label + ( isAnchor ? " (anchor)" : "") );
	}     
    
    /**
     * SWFTagTypes interface
     */ 
    {
        println( "sprite id=" + id );
        
        SWFTagDumper dumper = new SWFTagDumper( writer, dumpHex, dumpActions );
        dumper.indent = indent + "    ";
        return dumper;
    
    /**
     * SWFTagTypes interface
     */ 
    {
    }
    /**
     * SWFTagTypes interface
     */ 
    {
        println( "enable-debug" );        
	/**
	 * SWFTagTypes interface
	 */ 
	public void tagEnableDebug2( byte[] password ) throws IOException
	{
		println( "enable-debug-2" );        
	}
        
    /**
     * SWFTagTypes interface
     */ 
    {
        return this;

    /**
     * SWFTagTypes interface
     */ 
        throws IOException
    {
        println( "font-info id=" + fontId + " name=" + fontName + 
                 " flags=" + Integer.toBinaryString( flags ) + " codes=" + codes.length );
	/**
	 * SWFTagTypes interface
	 */ 
	public void tagDefineFontInfo2( int fontId, String fontName, int flags, int[] codes, int languageCode )
		throws IOException
	{
		println( "font-info2 id=" + fontId + " name=" + fontName + 
				 " flags=" + Integer.toBinaryString( flags ) + 
                 " codes=" + codes.length + " language=" + languageCode );
	}
    
    /**
     * SWFTagTypes interface
     */ 
                                      int ascent, int descent, int leading,
                                      int[] codes, int[] advances, Rect[] bounds,
                                      int[] kernCodes1, int[] kernCodes2,
                                      int[] kernAdjustments ) throws IOException
    {
        println( "font2 id=" + id + " flags=" + Integer.toBinaryString( flags ) +
                 " name=" + name + " ascent=" + ascent + " descent=" + descent +
        
        return this;
    
    /**
     * SWFTagTypes interface
     */ 
                    String initialText, Rect boundary, int flags,
                    AlphaColor textColor, int alignment, int fontId, int fontSize, 
                    int charLimit, int leftMargin, int rightMargin, int indentation,
                    int lineSpacing ) 
        throws IOException
    {
    	if( initialText != null ) {
    		initialText = initialText.replace( '\r', ' ' );
			initialText = initialText.replace( '\n', ' ' );
			initialText = initialText.replace( '\b', ' ' );
     	}
    	
        println( "edit-field id=" + fieldId + " name=" + fieldName +
                 " flags=" + Integer.toBinaryString( flags ) +
    }

    /**
     * SWFTagTypes interface
     */ 
        throws IOException
    { 
        println( "text id=" + id + " " + bounds + " " + matrix );
        return this;
     * SWFTagTypes interface
     */ 
    { 
        println( "text2 id=" + id + " " + bounds + " " + matrix );
        return this;
    /**
     * SWFTagTypes interface
     */ 
        throws IOException
    {
        println( "button id=" + id );
        
        for( ButtonRecord rec : buttonRecords ) {
        }

        acts.indent = "    " + indent ;
        return acts;
    }
    
    /**
     * SWFTagTypes interface
     */ 
        throws IOException
    {
        println( "button-cxform id=" + buttonId + "  " + transform );
    /**
     * SWFTagTypes interface
     */ 
        throws IOException
    {
        
        for( ButtonRecord2 rec : buttonRecord2s ) {
        }

        acts.indent = "    " + indent ;
        return acts;
            
    }
    /**
     * SWFTagTypes interface
     */ 
    {
        println( "export" );
        
        for( int i = 0; i < names.length && i < ids.length; i++ )
        {
            println( "  id=" + ids[i] + " name=" + names[i] );
        }
    }
    
    /**
     * SWFTagTypes interface
     */ 
        throws IOException
    {
        println( "import library-movie=" + movieName );
        
        for( int i = 0; i < names.length && i < ids.length; i++ )
        {
            println( "  id=" + ids[i] + " name=" + names[i] );
        }
    }
    
    /** @see com.anotherbigidea.flash.interfaces.SWFTagTypes#tagImportAssets2(java.lang.String, java.lang.String[], int[]) */
    public void tagImportAssets2( String movieName, String[] names, int[] ids )
            throws IOException {
        println( "import-assets-2 url=" + movieName );
        
        for( int i = 0; i < names.length && i < ids.length; i++ )
        {
            println( "  id=" + ids[i] + " name=" + names[i] );
        }
    }

    /**
     * SWFTagTypes interface
     */ 
    {
    }
    /**
     * SWFTagTypes interface
     */ 
    {
        println( "jpeg2 id=" + id );

        if( dumpHex )
        {
            Hex.dump( writer, data, 0L, indent + "    ", false );
            println( dashes );
        }
    }
     * SWFTagTypes interface
     */ 
    {
        println( "jpeg2 id=" + id + " (from input stream)" );        
    
     * SWFTagTypes interface
     */ 
    {
        println( "morph-shape id=" + id + " start: " + 
                 startBounds + "  end: " + endBounds );
        return this;
    
    /**
     * SWFTagTypes interface
     */ 
    {
    
     * SWFTagTypes interface
     */ 
    public void tagDefineBitsLossless2( int id, int format, int width, int height,
    {
        dumpBitsLossless( "bits-lossless2", id, format, width, height, colors, imageData );
    
    public void dumpBitsLossless( String name, int id, int format, 
                                  int width, int height,
    {
        int size = 0;
        if     ( format == SWFConstants.BITMAP_FORMAT_8_BIT  ) size = 8;
        else if( format == SWFConstants.BITMAP_FORMAT_16_BIT ) size = 16;
        else if( format == SWFConstants.BITMAP_FORMAT_32_BIT ) size = 32;
        
        println( name + " id=" + id + " bits=" + size + 
                 " width=" + width + " height=" + height );
        
        if( dumpHex )
        {
            for( int i = 0; i < colors.length; i++ )
            {
                println( "    " + i + ": " + colors[i] );
            }
            
            Hex.dump( writer, imageData, 0L, indent + "    ", false );
            println( dashes );
        }
    }
    
    /**
     * SWFVectors interface
     * SWFText interface
     */     
    public void done() throws IOException
    {
        println( "    " + dashes );
    }
    
    /**
     * SWFVectors interface
     */     
    public void line( int dx, int dy ) throws IOException
    {
        println( "    line  " + dx + "," + dy );        
    }
    
    /**
     * SWFVectors interface
     */     
    public void curve( int cx, int cy, int dx, int dy ) throws IOException
    {
        println( "    curve " + cx + "," + cy + " - " + dx + "," + dy );                
    }
    
    /**
     * SWFVectors interface
     */     
    public void move( int x, int y ) throws IOException
    {
        println( "    move  " + x + "," + y );        
    }
    
    /**
     * SWFShape interface
     */     
    public void setFillStyle0( int styleIndex ) throws IOException
    {
        println( "    fill0 = " + styleIndex );
    }
    
    /**
     * SWFShape interface
     */     
    public void setFillStyle1( int styleIndex ) throws IOException
    {
        println( "    fill1 = " + styleIndex );
    }    
    
    /**
     * SWFShape interface
     */     
    public void setLineStyle( int styleIndex ) throws IOException
    {
        println( "    line  = " + styleIndex );
    }    

    /**
     * SWFShape interface
     */     
    public void defineFillStyle( Color color ) throws IOException
    {
        println( "    fill " + color );
    }    

    /**
     * SWFShape interface
     */     
    public void defineFillStyle( Matrix matrix, int[] ratios,
                                 Color[] colors, boolean radial )
        throws IOException
    {
        println( "    fill radial=" + radial + "  " + matrix );
        
        for( int i = 0; i < ratios.length && i < colors.length; i++ )
        {
            if( colors[i] == null ) continue;
            println( "         ratio=" + ratios[i] + " " + colors[i] );
        }
    }    

    /**
     * SWFShape interface
     */     
    public void defineFillStyle( int bitmapId, Matrix matrix, boolean clipped, boolean smoothed )
        throws IOException
    {
        println( "    fill clipped=" + clipped + " smoothed=" + smoothed + " image=" + bitmapId + " " + matrix );
    }
    
    /**
     * SWFShape interface
     */     
    public void defineLineStyle( int width, Color color ) throws IOException
    {
        println( "    line-style width=" + width + "  " + color );
    }
    
    /**
     * SWFText interface
     */     
    public void font( int fontId, int textHeight ) throws IOException
    {
        println( "    font id=" + fontId + " size=" + textHeight );
    }
    
    /**
     * SWFText interface
     */     
    public void color( Color color ) throws IOException
    {
        println( "    color " + color );
    }
    
    /**
     * SWFText interface
     */     
    public void setX( int x ) throws IOException
    {
        println( "    x = " + x );
    }

    /**
     * SWFText interface
     */     
    public void setY( int y ) throws IOException
    {
        println( "    y = " + y );
    }
    
    /**
     * SWFText interface
     */     
    public void text( int[] glyphIndices, int[] glyphAdvances ) throws IOException
    {
        StringBuffer buff1 = new StringBuffer();
        StringBuffer buff2 = new StringBuffer();
        
        buff1.append( "(" );
        buff2.append( "(" );
        
        for( int i = 0; i < glyphIndices.length && i < glyphAdvances.length; i++ )
        {
            buff1.append( " " );
            buff2.append( " " );
            
            buff1.append( glyphIndices[i] );
            buff2.append( glyphAdvances[i] );
        }
        
        buff1.append( " )" );
        buff2.append( " )" );

        println( "    text" );
        println( "        glyph indices = " + buff1 );
        println( "        advances      = " + buff2 );
    }
    
    public void flush() throws IOException
    {
        writer.flush();
    }
    
    /**
     * Command line dumper.
     * 
     * The first argument is the name of the file to dump.  Other options (of
     * the form name=value follow):
     * 
     *   hex          - if this appears then binary blobs are hex dumped
     *   acts         - if this appears then actions are dumped
     *   out=filename - file to dump to, sysout otherwise
     * 
     */
    public static void main( String[] args ) throws IOException {

        Properties options = CommandLineArgs.parse( args );

        String  inFile   = args[0];
        String  outFile  = options.getProperty( "out" );
        boolean dumpHex  = options.containsKey( "hex" );
        boolean dumpActs = options.containsKey( "acts" );
        
        OutputStream out = (outFile != null) ?
                               new FileOutputStream( outFile ) :
                               null;
        
        SWFTagDumper dumper = (out != null) ? 
                                  new SWFTagDumper( out, dumpHex, dumpActs ) :
                                  new SWFTagDumper( dumpHex, dumpActs );
        
        FileInputStream in  = new FileInputStream( inFile );
        SWFTags   tagparser = new TagParser( dumper );
        SWFReader reader    = new SWFReader( tagparser, in );
        
        try {
            reader.readFile();
        } finally {        
            dumper.flush();
            in.close();
            if( out != null ) out.close();
        }        
    }
    
    
    
    /** @see com.anotherbigidea.flash.interfaces.SWFTagTypes#tagFileAttributes(int) */
    public void tagFileAttributes(int flags) throws IOException {
        print( "file-attributes" );
        if(( flags & SWFConstants.FILE_ATTRIBUTES_HAS_METADATA ) != 0 ) print( " has-metadata" );
        if(( flags & SWFConstants.FILE_ATTRIBUTES_ALLOW_AS3    ) != 0 ) print( " allow-as3" );
        if(( flags & SWFConstants.FILE_ATTRIBUTES_USE_NETWORK  ) != 0 ) print( " use-network" );
        println( "" );
    }

    /**
     * @see com.anotherbigidea.flash.interfaces.SWFTagTypes#tagScriptLimits(int, int)
     */
    public void tagScriptLimits(int maxRecursionDepth, int scriptTimeoutSecs)
        throws IOException {
        println( "script-limits  max-recursion-depth=" + maxRecursionDepth + " script-timeout=" + scriptTimeoutSecs + " secs" );
    }

    /**
     * @see com.anotherbigidea.flash.interfaces.SWFSpriteTagTypes#tagTabOrder(int, int)
     */
    public void tagTabOrder(int depth, int tabOrder) throws IOException {
        println( "tab-order  depth=" + depth + " order=" + tabOrder );
    }

    /** @see com.anotherbigidea.flash.interfaces.SWFTagTypes#tagDefineVideoStream(int, int, int, int, int, int) */
    public void tagDefineVideoStream(int id, int numFrames, int width,
            int height, int flags, int codec) throws IOException {
        
        String codecName = "codec=" + codec;
        if     ( codec == SWFConstants.VIDEO_CODEC_SCREEN_VIDEO  ) codecName = "codec=screen-video";
        else if( codec == SWFConstants.VIDEO_CODEC_SORENSON_H263 ) codecName = "codec=sorenson-H.263";

        String deblocking = "deblocking-in-packet";
        if((flags & SWFConstants.VIDEO_STREAM_DEBLOCKING_ON) != 0 ) {
            deblocking = "deblocking-on";
        } else if((flags & SWFConstants.VIDEO_STREAM_DEBLOCKING_OFF) != 0 ) {
            deblocking = "deblocking-off";
        } 
        
        String smoothing = ((flags & SWFConstants.VIDEO_STREAM_SMOOTHING_ON) != 0 ) ?
                               "smoothing-on" :
                               "smoothing-off";
        
        println( "define video stream, id=" + id + ", frames=" + numFrames +
                 ", (" + width + "x" + height + "), " + codecName +
                 ", " + deblocking + ", " + smoothing );        
    }
    
    /** @see com.anotherbigidea.flash.interfaces.SWFTagTypes#tagVideoFrame(int, int, byte[]) */
    public void tagVideoFrame(int streamId, int seqNum, int frameType, int codec, byte[] videoPacket)
            throws IOException {
        
        String codecName = " codec=" + codec;
        if     ( codec == SWFConstants.VIDEO_CODEC_SCREEN_VIDEO  ) codecName = " codec=screen-video";
        else if( codec == SWFConstants.VIDEO_CODEC_SORENSON_H263 ) codecName = " codec=sorenson-H.263";
        
        String type = " type=" + frameType;
        if     ( frameType == SWFConstants.VIDEO_FRAME_KEYFRAME   ) type = " type=key-frame";
        else if( frameType == SWFConstants.VIDEO_FRAME_INTERFRAME ) type = " type=inter-frame";
        
        println( "video frame #" + seqNum + " - streamId=" + streamId + codecName + type );
                
        if( codec == SWFConstants.VIDEO_CODEC_SCREEN_VIDEO ) {
            ScreenVideoPacket svp = new ScreenVideoPacket( new InStream( videoPacket ));
            println( "    packet: image(" +                     
                svp.getImageWidth() + "x" + svp.getImageHeight() +
                ") block(" + svp.getBlockWidth() + "x" + svp.getBlockHeight() +
                ") #blocks=" + svp.getImageBlocks().length );
        }
        
        if( dumpHex ) {
            Hex.dump( writer, videoPacket, 0L, indent + "    ", false );
            println( dashes );
        }  
    }

    /** @see com.anotherbigidea.flash.interfaces.SWFTagTypes#tagDefineBinaryData(int, byte[]) */
    public void tagDefineBinaryData(int id, byte[] data) throws IOException {
        println( "define-binary-data id=" + id + " length=" + data.length );
        if( dumpHex ) {
            Hex.dump( writer, data, 0L, indent + "    ", false );
            println( dashes );
        }          
    }
    
    
}
package org.epistem.util;

import java.io.FileWriter;
import java.io.IOException;

import org.epistem.io.IndentingPrintWriter;

/**
 * Utility for creating a FreeMind mindmap
 *
 * @author nickmain
 */
public class FreeMindWriter {

    public static enum Posn { LEFT, RIGHT }
    
    /** The underlying output */
    public final IndentingPrintWriter out;
    
    private int nodeDepth = 0;
    private int nodeId = 1;
    private boolean tagOpen = false;
    
    /**
     * @param file the output file
     */
    public FreeMindWriter( String file ) throws IOException {
        out = new IndentingPrintWriter( new FileWriter( file ) );
        out.println( "<map version=\"0.9.0\">" );
        out.println( "<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->" );
    }
    
    /**
     * End the mind map and close the file
     * 
     * @throws IllegalStateException if there are still open nodes
     */
    public void close() {
        if( nodeDepth != 0 ) throw new IllegalStateException( "There are still open nodes" );
        
        out.println( "</map>" );
        out.flush();
        out.close();
    }

    /**
     * Start a node
     */
    public FreeMindWriter startNode() {
        tagClose();

        nodeDepth++;
        out.print( "<node" );
        out.print( " CREATED='" + System.currentTimeMillis() + "'" );
        out.print( " MODIFIED='" + System.currentTimeMillis() + "'" );
        out.print( " ID='ID_" + (nodeId++) + "'" );
        tagOpen = true;
        
        return this;
    }
    
    /**
     * Write the node text attribute
     */
    public FreeMindWriter text( String text ) {
        if( ! tagOpen ) throw new IllegalStateException( "Node start element is no longer open" );
        
        out.print( " TEXT='" );
        out.writeEscapedXMLString( text, true );
        out.print( "'" );        
        return this;
    }

    /**
     * Write the node color attribute
     */
    public FreeMindWriter color( int color ) {
        if( ! tagOpen ) throw new IllegalStateException( "Node start element is no longer open" );
        
        String c = Integer.toHexString( color );
        while( c.length() < 6 ) c = "0" + c;        
        out.print( " COLOR='#" + c + "'" );
        return this;
    }

    /**
     * Write the node background color attribute
     */
    public FreeMindWriter backColor( int color ) {
        if( ! tagOpen ) throw new IllegalStateException( "Node start element is no longer open" );
        
        String c = Integer.toHexString( color );
        while( c.length() < 6 ) c = "0" + c;        
        out.print( " BACKGROUND_COLOR='#" + c + "'" );
        return this;
    }

    /**
     * Write the bubble style attribute
     */
    public FreeMindWriter bubbleStyle() {
        if( ! tagOpen ) throw new IllegalStateException( "Node start element is no longer open" );
        
        out.print( " STYLE='bubble'" );
        return this;        
    }

    /**
     * Write the folded attribute
     */
    public FreeMindWriter folded() {
        if( ! tagOpen ) throw new IllegalStateException( "Node start element is no longer open" );
        
        out.print( " FOLDED='true'" );
        return this;        
    }
    
    /**
     * Write the position attribute
     * 
     * @throws IllegalStateException if the node is not a second level node
     */
    public FreeMindWriter position( Posn posn ) {
        if( ! tagOpen ) throw new IllegalStateException( "Node start element is no longer open" );
        if( nodeDepth != 2 ) throw new IllegalStateException( "Position only valid on 2nd level nodes" );

        out.print( " POSITION='" + posn.name().toLowerCase() + "'" );
        return this;        
    }
    
    /**
     * Write the font element - this will close the opening node tag
     */
    public FreeMindWriter font( String name, String size ) {
        tagClose();
        out.println( "<font NAME='" + name + "' SIZE='" + size + "'/>" );
        return this;        
    }

    /**
     * Write an attribute element - this will close the opening node tag
     */
    public FreeMindWriter attribute( String name, String value ) {
        tagClose();
        out.println( "<attribute NAME='" + name + "' VALUE='" + value + "'/>" );
        return this;        
    }    

    /**
     * Write a cloud element - this will close the opening node tag
     */
    public FreeMindWriter cloud( int color ) {
        tagClose();

        String c = Integer.toHexString( color );
        while( c.length() < 6 ) c = "0" + c;        

        out.println( "<cloud COLOR='#" + c + "'/>" );
        return this;        
    }    
    
    /**
     * Write an HTML text element - this will close the opening node tag
     * 
     * @param text the html body text
     */
    public FreeMindWriter richText( String text ) {
        tagClose();
        out.println( "<richcontent TYPE='NODE'><html>\n  <head>\n\n  </head>\n  <body>" );
        out.println( text );
        out.println( "  </body>\n</html>\n</richcontent>" );
        return this;        
    }        

    /**
     * Write an HTML text element - this will close the opening node tag
     * 
     * @param text the text to be xml-escaped and enclosed in pre elements
     */
    public FreeMindWriter richPreText( String text ) {
        tagClose();
        out.print( "<richcontent TYPE='NODE'><html>\n  <head>\n\n  </head>\n  <body><pre>" );
        out.writeEscapedXMLString( text, true );
        out.print( "</pre></body>\n</html>\n</richcontent>" );
        return this;        
    }        

    /**
     * Start an HTML text element - this will close the opening node tag
     */
    public FreeMindWriter startRichText() {
        tagClose();
        out.println( "<richcontent TYPE='NODE'><html>\n  <head>\n\n  </head>\n  <body>" );
        return this;        
    }        

    /**
     * End an HTML text element
     */
    public FreeMindWriter endRichText() {
        out.println( "  </body>\n</html>\n</richcontent>" );
        return this;        
    }        
    
    //close any open tag
    private void tagClose() {
        if( tagOpen ) {
            out.println( ">" );            
            tagOpen = false;
        }
    }
    
    /**
     * Close the currently open node
     * 
     * @throws IllegalStateException if there are no open nodes
     */
    public FreeMindWriter endNode() {
        if( nodeDepth < 1 ) throw new IllegalStateException( "There are no open nodes" );
        tagClose();
        out.println( "</node>" );
        nodeDepth--;
        
        return this;
    }
}

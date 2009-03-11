package org.epistem.io;

import java.io.*;
	
/**
 * Threaded copier - from an input stream to an output stream
 *
 * @author nickmain
 */
public class StreamCopier implements Runnable 
{
	protected InputStream in;
	protected OutputStream out;
	protected int buffSize;
	protected boolean isDone = false;
		
	public StreamCopier( InputStream in, OutputStream out )
	{
		this( in, out, 100 );
	}
		
	public StreamCopier( InputStream in, OutputStream out, int bufferSize )
	{
		this.in  = in;
		this.out = out;
		this.buffSize = bufferSize;
	}
		
	public void startThread( )
	{
		new Thread( this ).start();	
	}
	
	public synchronized void waitUntilDone()
	{
		if( isDone ) return;
		try{ wait(); } catch( Exception ex ) {}
	}
		
	public synchronized void run()
	{
		try
		{
			int r = 0;
			byte[] buff = new byte[ buffSize ];
				
			while( ( r = in.read( buff ) ) > 0 )
			{
				out.write( buff, 0, r );
				out.flush();				
			}
		}
		catch( IOException ioe ){}
			
		isDone = true;
		notifyAll();
			
		//System.out.println( "StreamCopier ended" );
	}
}

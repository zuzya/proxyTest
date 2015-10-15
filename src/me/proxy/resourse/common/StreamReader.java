package me.proxy.resourse.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class StreamReader implements Runnable{
	
	protected OutputStream streamTo;
	protected boolean isRequest;	

	public StreamReader(OutputStream streamTo, boolean isRequest) {
		super();
		this.streamTo = streamTo;
		this.isRequest = isRequest;
	}
	
	private void pullFromResource(OutputStream streamTo, boolean isRequest){			

		byte[] reply = new byte[4096];
	    InputStream streamFromResource = readRow(); 
		
		// Read the server's responses
        // and pass them back to the client.
        int bytesRead;
        try {
          while ((bytesRead = streamFromResource.read(reply)) != -1) {
        	  streamTo.write(reply, 0, bytesRead);
        	  streamTo.flush();
          }
        } catch (IOException e) {
        }
        
	}
	
	public void run() {		
		pullFromResource(streamTo, isRequest);
	}
	
	public abstract InputStream readRow();


}

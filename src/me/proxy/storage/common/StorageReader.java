package me.proxy.storage.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class StorageReader implements Runnable{
	
	protected OutputStream streamTo;
	protected boolean isRequest;	

	public StorageReader(OutputStream streamTo, boolean isRequest) {
		super();
		this.streamTo = streamTo;
		this.isRequest = isRequest;
	}
	
	public InputStream getStreamFromStorage(){			

	    return readRow();         
	}	
	
	public void writeDataFromStorage(OutputStream streamTo, InputStream streamFromResource){			

		byte[] reply = new byte[4096];
		
		// Read the server's responses
        // and pass them back to the client.
        int bytesRead;
        try {
          while ((bytesRead = streamFromResource.read(reply)) != -1) {
        	  
        	  String msg = "=========";
        	  if(isRequest)
        		  msg += " REQUEST ";
        	  else
        		  msg += "ANSWER ";
        	  msg += "=========";
        	  
        	  System.out.println(msg);
        	  System.out.println(new String(reply));
        	  
        	  streamTo.write(reply, 0, bytesRead);
        	  streamTo.flush();
          }          
          
          
          System.out.println("end of client write");
        } catch (IOException e) {
        	e.printStackTrace();
        }
        
	}	
	
	
	protected abstract InputStream readRow();
	
	public void run() {		
		InputStream streamFrom = getStreamFromStorage();
		writeDataFromStorage(streamTo, streamFrom);
	}


}

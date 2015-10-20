package me.proxy.storage.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class StorageReader implements Runnable{
	
	protected OutputStream streamTo;
	protected boolean isRequest;

	public StorageReader(OutputStream streamTo, boolean isRequest) {
		super();
		this.streamTo = streamTo;
		this.isRequest = isRequest;
	}
	
	public StorageReader(boolean isRequest) {
		super();
		this.isRequest = isRequest;
	}
	
	public InputStream getStreamFromStorage(){			

	    return readRow();         
	}	
	
	
	public List<byte[]> writeDataFromStorage(InputStream streamFromResource){			

		List<byte[]> output = new ArrayList<byte[]>();
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
        	  
        	  output.add(trim(reply));
          }          
          
          
          System.out.println("end of client write");
        } catch (IOException e) {
        	e.printStackTrace();
        }
        
        return output;
	}
       
	static byte[] trim(byte[] bytes)
	{
	    int i = bytes.length - 1;
	    while (i >= 0 && bytes[i] == 0)
	    {
	        --i;
	    }

	    return Arrays.copyOf(bytes, i + 1);
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
	
	public List<byte[]> run1() {		
		InputStream streamFrom = getStreamFromStorage();
		return writeDataFromStorage(streamFrom);
	}


}

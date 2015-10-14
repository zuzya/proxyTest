package me.proxy.resourse.request;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import me.proxy.resourse.common.IResourse;
import me.proxy.resourse.common.ResourceDBStorage;

public class ResourceDBStorage_ToClient  extends  ResourceDBStorage{

	public InputStream getInputStream() throws IOException {
		
		return readRow(null, true);
	}

	public OutputStream getOutputStream() throws IOException {
	
		return outputStream;
	}

	public void writeRequestToResource(final InputStream streamFrom, final boolean isRequestStram) throws IOException {
		
	    final byte[] request = new byte[1024];
	    
		
		  // a thread to read the client's requests and pass them
       // to the server. A separate thread for asynchronous.
       Thread t = new Thread() {
         public void run() {
           int bytesRead;
           try {
             while ((bytesRead = streamFrom.read(request)) != -1) {
           	  		            	  
            	try {
					insertRow(new String(request), isRequestStram);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
           	  
               System.out.println(new String(request));
             }
           } catch (IOException e) {
           }

           // the client closed the connection to us, so close our
           // connection to the server.         
         }
       };
       
       t.start();
		
	}

	public void readResponseFromResource(OutputStream streamTo, final boolean isRequestStream) throws IOException {
			
			byte[] reply = new byte[4096];
		    final InputStream streamFromResource = readRow(null, isRequestStream); 
			
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

}

package me.proxy.resourse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ResourceSocketServer implements IResourse {

	private Socket server;
	
	public ResourceSocketServer(Socket server) {
		this.server = server;
	}

	public InputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return server.getInputStream();
	}

	public OutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return server.getOutputStream();	
	}

	public void writeRequestToResource(final InputStream streamFromClient) throws IOException {
		
	    final byte[] request = new byte[1024];
	    
	     // Get server streams.
 
        final OutputStream streamToResource = getOutputStream();
		
		  // a thread to read the client's requests and pass them
        // to the server. A separate thread for asynchronous.
        Thread t = new Thread() {
          public void run() {
            int bytesRead;
            try {
              while ((bytesRead = streamFromClient.read(request)) != -1) {
            	  		            	  
            	  streamToResource.write(request, 0, bytesRead);
            	  streamToResource.flush();
            	  
                System.out.println(new String(request));
              }
            } catch (IOException e) {
            }

            // the client closed the connection to us, so close our
            // connection to the server.
            try {
            	streamToResource.close();
            } catch (IOException e) {
            }
          }
        };
        
        t.start();
		
	}

	public void readResponseFromResource(OutputStream streamToClient) throws IOException {
		
		byte[] reply = new byte[4096];
	    final InputStream streamFromResource = getInputStream(); 
		
		// Read the server's responses
        // and pass them back to the client.
        int bytesRead;
        try {
          while ((bytesRead = streamFromResource.read(reply)) != -1) {
            streamToClient.write(reply, 0, bytesRead);
            streamToClient.flush();
          }
        } catch (IOException e) {
        }
		
	}

}

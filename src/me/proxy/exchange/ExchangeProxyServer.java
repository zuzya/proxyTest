package me.proxy.exchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import me.proxy.resourse.IResourse;
import me.proxy.resourse.ResourceDatabaseStorage;
import me.proxy.resourse.ResourceSocketServer;

public class ExchangeProxyServer {

	 public static void main(String[] args) throws IOException {
		    try {
		    	
		    //TODO:  config
		      String host = "localhost";
		      int remoteport = 8080;
		      int localport = 8088;
		      // Print a start-up message
		      System.out.println("Starting proxy for " + host + ":" + remoteport
		          + " on port " + localport);
		      // And start running the server
		      runServer(host, remoteport, localport); // never returns
		    } catch (Exception e) {
		      System.err.println(e);
		    }
		  }

		  /**
		   * runs a single-threaded proxy server on
		   * the specified local port. It never returns.
		   */
		  public static void runServer(String host, int remoteport, int localport)
		      throws IOException {
		    // Create a ServerSocket to listen for connections with
		    ServerSocket ss = new ServerSocket(localport);

		    IResourse resource = null;
		    SocketExchange socketExchenge = new SocketExchange();
		    
		    int n = 0;
		    
		    while (true) {
		      Socket client = null, server = null;
		      try {
		        // Wait for a connection on the local port
		        client = ss.accept();
		        
		        final InputStream streamFromClient = client.getInputStream();
		        final OutputStream streamToClient = client.getOutputStream();
		       
//		        resource = new ResourceSocketServer(socketExchenge.createRemoteSocket(host, remoteport, streamToClient, client));
		        resource = new ResourceDatabaseStorage();
		        
		        resource.writeRequestToResource(streamFromClient);	    

		    	byte[] reply = new byte[4096];
			    final InputStream streamFromResource = resource.getInputStream(); 
				
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
		        
		        // The server closed its connection to us, so we close our
		        // connection to our client.
		        streamToClient.close();
		      } catch (IOException e) {
		        System.err.println(e);
		      } finally {
		        try {
		          if (server != null)
		            server.close();
		          if (client != null)
		            client.close();
		        } catch (IOException e) {
		        }
		      }
		    }
		  }
}

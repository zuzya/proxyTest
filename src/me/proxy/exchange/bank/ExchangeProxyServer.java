package me.proxy.exchange.bank;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import me.proxy.resourse.common.IResourse;
import me.proxy.resourse.request.ResourceWithDBStorage;
import me.proxy.resourse.request.ResourceSocketServer;

public class ExchangeProxyServer {

	 public static void main(String[] args) throws IOException {
		    try {
		    	
		    //TODO:  config
		      String host = "okp-vm-sumzport";
		      int remoteport = 8080;
		      // Print a start-up message
		      System.out.println("Starting proxy for " + host + ":" + remoteport);
		      // And start running the server
		      runServer(host, remoteport); // never returns
		    } catch (Exception e) {
		    	e.printStackTrace();
		      System.err.println(e);
		    }
		  }

		  /**
		   * runs a single-threaded proxy server on
		   * the specified local port. It never returns.
		   */
		  public static void runServer(String host, int remoteport)
		      throws IOException {

		    IResourse resource = null;
		    SocketExchange socketExchenge = new SocketExchange();
		    
		    final byte[] request = new byte[1024];
	    	byte[] reply = new byte[4096];
	    	
		    int n = 0;
		    
		    while (true) {
		      Socket server = null;
		      try {
		        // Wait for a connection on the local port		      	
		    	  
		        resource = new ResourceWithDBStorage();
		        
		        final InputStream streamFromClient = resource.getInputStream();
		        final OutputStream streamToClient = resource.getOutputStream();		       	
		        
		    	// Make a connection to the real server.
		        // If we cannot connect to the server, send an error to the
		        // client, disconnect, and continue waiting for connections.
		        try {
				        server = new Socket(host, remoteport);
				        server.setSendBufferSize(512);
		        } catch (IOException e) {
				        PrintWriter out = new PrintWriter(streamToClient);
				        out.print("Proxy server cannot connect to " + host + ":"
				            + remoteport + ":\n" + e + "\n");
				        out.flush();				  
				        continue;
		         }

		          // Get server streams.
		        final InputStream streamFromServer = server.getInputStream();
		        final OutputStream streamToServer = server.getOutputStream();
		        
		        // a thread to read the client's requests and pass them
		        // to the server. A separate thread for asynchronous.
		        Thread t = new Thread() {
		          public void run() {
		            int bytesRead;
		            try {
		              while ((bytesRead = streamFromClient. read(request)) != -1) {
		                streamToServer.write(request, 0, bytesRead);
		                
		                System.out.println("====REQUEST=====");
		                System.out.println(new String(request));
		                streamToServer.flush();
		              }
		            } catch (IOException e) {
		            }

		            // the client closed the connection to us, so close our
		            // connection to the server.
//		            try {
////		              streamToServer.close();
//		            } catch (IOException e) {
//		            }
		          }
		        };
		        
		        t.start();
		        resource.writeRequestToResource_From_InputStream(streamFromServer, false);	    

		        
//		     // Read the server's responses
//		        // and pass them back to the client.
//		        int bytesRead;
//		        try {
//		          while ((bytesRead = streamFromServer.read(reply)) != -1) {
////		            streamToClient.write(reply, 0, bytesRead);
//		            
//		        	  
//		            System.out.println("====REPLY=====");
//		            System.out.println(new String(reply));
////		            streamToClient.flush();
//		          }
//		        } catch (IOException e) {
//		        	e.printStackTrace();
//		        }catch (Exception e) {
//		        	e.printStackTrace();
//		        }
		        
		        // The server closed its connection to us, so we close our
		        // connection to our client.
		        if(streamToClient != null)
		        	streamToClient.close();
		      } catch (IOException e) {
		        System.err.println(e);
		      } finally {
		        try {
		          if (server != null)
		            server.close();
		          } catch (IOException e) {
		        }
		      }
		    }
		  }
}

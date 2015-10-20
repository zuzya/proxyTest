package me.proxy.exchange.dmz;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import me.proxy.storage.common.IStorage;
import me.proxy.storage.common.StorageReader;
import me.proxy.storage.common.StorageWriter;
import me.proxy.storage.impl.DBStorageReader;
import me.proxy.storage.impl.DBStorageWriter;
import me.proxy.storage.impl.ResourceSocketServer;

public class ExchangeProxyDMZ {

	 public static void main(String[] args) throws IOException {
		    try {
		    	
		      int localport = 8080;
		      runServer(localport); 
		    } catch (Exception e) {
		    	e.printStackTrace();
		      System.err.println(e);
		    }	
	 }

	  /**
	   * runs a single-threaded proxy server on
	   * the specified local port. It never returns.
	   */
	  public static void runServer(int localport)
	      throws IOException {
	    // Create a ServerSocket to listen for connections with
	    ServerSocket ss = new ServerSocket(localport);
	    StorageWriter writer = null;
	    StorageReader reader = null;
	    
	    while (true) { 
	    	
	      try(Socket client = ss.accept()) {
	        // Wait for a connection on the local port
	      
	    	OutputStream streamToClient = client.getOutputStream();
  	        InputStream streamFromClient = client.getInputStream();
  	      
			
    		//TODO: сделать фабрику
   	     
	        //пишем поток клиента  в ресурс
	        writer = new DBStorageWriter(streamFromClient, true);
	        new Thread(writer).start();
		 
	        
	        //забираем данные из ресурса и пишем в поток клиента
	        reader = new DBStorageReader(streamToClient, false);	        
	        reader.run();
        	
	        System.out.println("end of coonection");	    	  

	       
	       
	        // The server closed its connection to us, so we close our
	        // connection to our client.
	
//	        streamFromClient.close();
//	        streamToClient.close();
	      } catch (IOException e) {
	        System.err.println(e);
	      }
	    }
	  }
}

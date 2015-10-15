package me.proxy.exchange.dmz;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import me.proxy.resourse.common.IResourse;
import me.proxy.resourse.request.ResourceWithDBStorage;
import me.proxy.resourse.request.ResourceSocketServer;

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
	    IResourse resource = null;
	    
	    while (true) { 
	      Socket client = null, server = null;
	      try {
	        // Wait for a connection on the local port
	        client = ss.accept();
	        
	        //берем потоки клиента
	        InputStream streamFromClient = client.getInputStream();
	        OutputStream streamToClient = client.getOutputStream();
	       
	        //TODO: сделать фабрику
	        //выбираем имлементацию ресурса
	        resource = new ResourceWithDBStorage();
	        
	        //TODO: !!!!!!!!!!!!!!!!!!!!!!!!!! THREAD POOL HERE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	       
	        //пишем поток клиента  в ресурс
	        resource.writeRequestToResource_From_InputStream(streamFromClient, true);	
	        //забираем данные из ресурса и пишем в поток клиента
		    resource.readResponseFromResource_To_OutputStream(streamToClient, false);			
        
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

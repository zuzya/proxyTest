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
	      Socket client = null;
	      try {
	        // Wait for a connection on the local port
	        client = ss.accept();
	        
	        //берем потоки клиента
	        InputStream streamFromClient = client.getInputStream();
	        OutputStream streamToClient = client.getOutputStream();
	       
	        //TODO: сделать фабрику
	     
	        //пишем поток клиента  в ресурс
	        writer = new DBStorageWriter(streamFromClient, true);
	        new Thread(writer).start();
//	        writer.run();
	        
	        //забираем данные из ресурса и пишем в поток клиента
	        reader = new DBStorageReader(streamToClient, false);	        
	        reader.run();
	        
	        
	        // The server closed its connection to us, so we close our
	        // connection to our client.
	        try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//	        streamFromClient.close();
	        streamToClient.close();
	      } catch (IOException e) {
	        System.err.println(e);
	      } finally {
	        try {
	          if (client != null)
	            client.close();
	        } catch (IOException e) {
	        }
	      }
	    }
	  }
}

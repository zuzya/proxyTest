package me.proxy.exchange.bank;

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

public class ExchangeProxyServer {

	 private static final String ADRESS = "okp-vm-sumzport";

	public static void main(String[] args) throws IOException {
		    try {
		    	
		    //TODO:  config
		      String host = ADRESS;
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
		  public static void runServer(String host, int remoteport) throws IOException {

			System.out.println("starting connect to remote server");  
			  
			StorageWriter writer =  null;
			StorageReader reader = null;		
		    
		    final byte[] request = new byte[1024];
	  		    
		    while (true) {
		    	try (Socket server = new Socket(host, remoteport)) {
		    		  
		    		final InputStream streamFromServer = server.getInputStream();
				    final OutputStream streamToServer = server.getOutputStream(); 
		    			
    			  	reader = new DBStorageReader(streamToServer, true);	
			        reader.run();     		        
	
					writer =  new DBStorageWriter(streamFromServer, false);		
					new Thread(writer).start();						
				
					System.out.println("end of connection to remote server");
					
//					streamToServer.close();
				
		       } catch (IOException e) {
		          System.err.println(e);
		       }
		    }
		        
		  }
	}
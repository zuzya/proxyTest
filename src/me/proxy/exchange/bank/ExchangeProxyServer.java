package me.proxy.exchange.bank;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

import me.proxy.storage.common.ChangeRequest;
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

		      System.out.println("Starting proxy for " + host + ":" + remoteport);
	
		      
//			  ExecutorService service = Executors.newFixedThreadPool(3);
		      new ExchangeProxyServer().runServer(host, remoteport); // never returns
		    } catch (Exception e) {
		    	e.printStackTrace();
		      System.err.println(e);
		    }
	 }
	


		  /**
		   * runs a single-threaded proxy server on
		   * the specified local port. It never returns.
		   */
		  public  void runServer(String host, int remoteport) throws IOException {
			  
			
			
			final SocketChannel sc = SocketChannel.open();
	        sc.connect(new InetSocketAddress(host, remoteport));
			sc.configureBlocking( false );
			
			Selector selector = Selector.open();
			sc.register( selector, SelectionKey.OP_CONNECT ); 
			
			ExecutorService service = Executors.newFixedThreadPool(1);

		
			// Waiting for the connection
			while (true) {

				selector.select();
				
			  // Get keys
			  Set keys = selector.selectedKeys();
			  Iterator i = keys.iterator();

			  // For each key...
			  while (i.hasNext()) {
			    SelectionKey key = (SelectionKey)i.next();

			    // Remove the current key
			    i.remove();
			    
			    // Get the socket channel held by the key
			    SocketChannel channel = (SocketChannel)key.channel();
			    
			 // Attempt a connection
			    if (key.isConnectable()) {

				      // Connection OK
				      System.out.println("Server Found");
	
				      // Close pendent connections
				      if (channel.isConnectionPending())
				        channel.finishConnect();
			      
			    }
			    
			

			  }
			  
			  break;
			} 

			
			
			while(! sc.finishConnect()){
				System.out.println("connecting to server...");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			while(true){
				
			
				//single thread  yet
				List<byte[]> data = null;
				StorageReader reader = new DBStorageReader(true);		
				
//				(FutureTask<List<byte[]>>) service.submit(reader);
				FutureTask<List<byte[]>> task = new FutureTask(reader);
				
				Thread t = new Thread(task);
				t.start();
				
				try {
					data = task.get();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					e1.printStackTrace();
				}
					
				final List<byte[]> finaldata = data;			
				//писать на сервер					
				processWrite(sc, finaldata);
			
				//взять ответ сервера
				List<byte[]> result = new ArrayList<>(); 
//				 while(true){
				    	
				        if(result == null || result.isEmpty()){
				        	System.out.println("------------------- empty ----------------------");//								        
					        result =  processRead(sc);	
				        } else 
				        	break;							        	
//				 }			

		        
		        //писать в харнилище
		        StorageWriter writer = new DBStorageWriter(result, false);
		        Thread writterThread = new Thread(writer);		
		        writterThread.start();
		        
		        try {
					writterThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			
			
	
	 }		  
		  

    
    /**
     * 
     * @param sc
     * @return
     */
	private  List<byte[]> processRead(SocketChannel sc) {
			
		List<byte[]> result = new ArrayList<byte[]>();
		
	    ByteBuffer buffer = ByteBuffer.allocate(1024);
        int numRead = -1;

        try {
        	
        	System.out.println("======= pre read buffer =========");
            while( (numRead = sc.read(buffer)) != -1){
            	
            	System.out.println("======= IN read buffer ========="); 
            	
                byte[] data = new byte[numRead];
                System.arraycopy(buffer.array(), 0, data, 0, numRead);

                System.out.println(new String(data));
                result.add(data);
                
                if (numRead == -1) {
            		try {
    					sc.close();
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
//    	            key.cancel();
                return null;
                }
            }
            
         	System.out.println("======= after read buffer =========");
        }
        catch (IOException e) {
            e.printStackTrace();
        }


      
        
        return result;
			
	}

	private void processWrite(java.nio.channels.SocketChannel channel, List<byte[]> data) throws IOException {
		
        List<byte[]> pendingData = data;
        
        Iterator<byte[]> items = pendingData.iterator();
        while (items.hasNext()) {
            byte[] item = items.next();
            items.remove();
            
//            System.out.println(new String(item));
            channel.write(ByteBuffer.wrap(item));
        }
        
		
	}
	}
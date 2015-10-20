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
		      // Print a start-up message
		      System.out.println("Starting proxy for " + host + ":" + remoteport);
		      // And start running the server
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
			  
			Selector selector = Selector.open();
			
			SocketChannel ssc = SocketChannel.open();
	        ssc.connect(new InetSocketAddress(host, remoteport));
			ssc.configureBlocking( false );
			ssc.register( selector, SelectionKey.OP_CONNECT ); 
			
		
			
			StorageReader reader = new DBStorageReader(true);
			
			while(true){
				
				List<byte[]> data = reader.run1();		
				 
				processWrite(ssc, data);   
		        List<byte[]> result =  processRead(ssc);
		        
		        StorageWriter writer = new DBStorageWriter(result, false);
		        writer.run1();
			}

           
			
		/*	while(true){
								
				Set keys = selector.keys();
				Iterator it = keys.iterator();
				
				while(it.hasNext()){
					
					SelectionKey key = (SelectionKey) it.next();
					if(key.isAcceptable()){
						System.out.println("accept");
						
					}else
					
					if(key.isConnectable()){			
						
						connect(key);					
						
					} else if(key.isReadable()){
						processRead(key);
					} else if(key.isWritable()){
						processWrite(key, data);
					}
				}
				
			}*/
		  
		        
	 }		  
		  

		  
		  
		  
    private static void connect(SelectionKey key) {
    	java.nio.channels.SocketChannel sChannel = (java.nio.channels.SocketChannel)key.channel();

        boolean success = false;
        try {
            success = sChannel.finishConnect();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        if (!success) {
            // An error occurred; handle it

            // Unregister the channel with this selector
            key.cancel();
        }
        int ops = key.interestOps();

        if((ops & SelectionKey.OP_WRITE) != 0)
        {
            key.interestOps(SelectionKey.OP_WRITE);
        }
        else
        {
            key.interestOps(SelectionKey.OP_READ);
        }
    }

	private  List<byte[]> processRead(SocketChannel sc) {
			
		List<byte[]> result = new ArrayList<byte[]>();
		
	    ByteBuffer buffer = ByteBuffer.allocate(1024);
        int numRead = -1;

        try {
            while( (numRead = sc.read(buffer)) > 0){
            	
            	  
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
            
            System.out.println(new String(item));
            channel.write(ByteBuffer.wrap(item));
        }
		
	}
	}
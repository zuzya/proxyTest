package me.proxy.exchange.dmz;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import me.proxy.storage.common.IStorage;
import me.proxy.storage.common.StorageReader;
import me.proxy.storage.common.StorageWriter;
import me.proxy.storage.impl.DBStorageReader;
import me.proxy.storage.impl.DBStorageWriter;
import me.proxy.storage.impl.ResourceSocketServer;

public class ExchangeProxyDMZ {

	private List<byte[]> output;
	
	 public static void main(String[] args) throws IOException {
		    try {
		    	
		      int localport = 8080;
		      new ExchangeProxyDMZ().runServer(localport); 
		    } catch (Exception e) {
		    	e.printStackTrace();
		      System.err.println(e);
		    }	
	 }

	  /**
	   * runs a single-threaded proxy server on
	   * the specified local port. It never returns.
	   */
	  public void runServer(int localport)
	      throws IOException {
		  
		Selector selector = Selector.open();
		
		ServerSocketChannel ssc = ServerSocketChannel.open();
        InetSocketAddress listenAddr = new InetSocketAddress("127.0.0.1", localport);
        ssc.socket().bind(listenAddr);
		ssc.configureBlocking( false );
		ssc.register( selector, SelectionKey.OP_ACCEPT ); 			
	    
	    while (true) { 
	    	
	        // Проверяем, если ли какие-либо активности -
	        // входящие соединения или входящие данные в
	        // существующем соединении.
	        int num = selector.select();

	        // Если никаких активностей нет, выходим из цикла
	        // и снова ждём.
	        if (num == 0) {
	           continue;
	        }
	        
	        // Получим ключи, соответствующие активности,
	        // которые могут быть распознаны и обработаны один за другим.
	        Set keys = selector.selectedKeys();
	        Iterator it = keys.iterator();
	        
	        while(it.hasNext()){
	        	
		        // Получим ключ, представляющий один из битов
		        // активности ввода/вывода.
		        SelectionKey key = (SelectionKey)it.next();
		        
		        if (key.isAcceptable()) {

		        	    // Принимаем входящее соединение
		        	    SocketChannel sc = ssc.accept();
		        	    
		        	    // Необходимо сделать его неблокирующим,
		        	    // чтобы использовать Selector для него.		        	   
		        	    sc.configureBlocking( false );

		        	    // Регистрируем его в Selector для чтения.
		        	    sc.register( selector, SelectionKey.OP_READ );
		        	    
		        }  else if (key.isReadable()) {

		            processInput( key);

		        }  else if (key.isWritable()) {

		            processOutput( key);

		        }
		        
		        
		        
	        }

	        keys.clear();

	    }
	  }

	private void processOutput(SelectionKey key) throws IOException {
		
    	java.nio.channels.SocketChannel channel = (java.nio.channels.SocketChannel) key.channel();
        List<byte[]> pendingData = output;
        
        Iterator<byte[]> items = pendingData.iterator();
        while (items.hasNext()) {
            byte[] item = items.next();
            items.remove();
            
            System.out.println(new String(item));
            channel.write(ByteBuffer.wrap(item));
        }
        key.interestOps(SelectionKey.OP_READ);
		
	}

	private void processInput(SelectionKey key) throws IOException {		
		

        SocketChannel sc = (SocketChannel)key.channel();
	    ByteBuffer buffer = ByteBuffer.allocate(1024);
        int numRead = -1;

        List<byte[]> dataList = new ArrayList<byte[]>();
        
        try {
        	
        	   while( (numRead = sc.read(buffer)) > 0){
               	
                   
        	        if (numRead == -1) {
        	        	sc.close();
//        	            key.cancel();
        	            return;
        	        }
        	        
        	        byte[] data = new byte[numRead];
        	        System.arraycopy(buffer.array(), 0, data, 0, numRead);

        	        System.out.println(new String(data));
        	        dataList.add(data);
        	   }
        }
        catch (IOException e) {
            e.printStackTrace();
        }


        
        StorageWriter writer = new DBStorageWriter(dataList, true);
        Thread t = new Thread(writer);
        t.start();
        
        try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        StorageReader reader = new DBStorageReader(false, output);
        output = reader.call();
        
       
        
        SelectionKey destkey = sc.keyFor(key.selector());
        destkey.interestOps(SelectionKey.OP_WRITE);
        
        System.out.println("end");
		
	}	
	


}

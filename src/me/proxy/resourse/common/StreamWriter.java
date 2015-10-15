package me.proxy.resourse.common;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public abstract class StreamWriter implements Runnable {

	protected InputStream streamFrom;
	protected boolean isRequest;	

	public StreamWriter(InputStream streamFrom, boolean isRequest) {
		super();
		this.streamFrom = streamFrom;
		this.isRequest = isRequest;
	}
	
	/**
	 * Фигачим в хранилище
	 * 
	 * @param streamFrom
	 * @param isRequest
	 */
	private void pushToResource(InputStream streamFrom){
		
		
	     final byte[] request = new byte[1024];
		 int bytesRead;
         try {
           while ((bytesRead = streamFrom.read(request)) != -1) {
         	  		            	  
          	try {
          			//собственно сам метод вставки
					insertRow(request);
				} catch (Exception e) {
					e.printStackTrace();
				} 
         	  
             System.out.println(new String(request));
           }
         } catch (IOException e) {
         }
	}
	

	public void run() {		
		pushToResource(streamFrom);
	}
	
	public abstract void insertRow(byte[] request);


}

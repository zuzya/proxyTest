package me.proxy.storage.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

public abstract class StorageWriter  implements Runnable{


	protected InputStream streamFrom;
	protected boolean isRequest;	

	public StorageWriter(InputStream streamFrom, boolean isRequest) {
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
	private void pushToStorage(InputStream streamFrom){		
		
	     final byte[] request = new byte[1024];
		 int bytesRead;
         try {
           while ((bytesRead = streamFrom.read(request)) != -1) {
         	  		            	  
          	try {
          		
//          			byte[] newrequest;
//          			newrequest = replaceHTTPHost(request, "ya.ru");
//          			System.out.println("===========new REQUST============");
//          			System.out.println(new String(newrequest));
          			
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

	
	private byte[] replaceHTTPHost(byte[] request, String string) {
		
		String msg = new String(request);
		
		BufferedReader bufReader = new BufferedReader(new StringReader(msg));
		StringBuffer sb = new StringBuffer();
		String line=null;
		try {
			while( (line=bufReader.readLine()) != null )
			{
					if(line.startsWith("Host")){
						System.out.println(line);
						line = "Host: ya.ru";
					}
					sb.append(line);
					sb.append("\r\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sb.toString().getBytes();
	}

	protected abstract void insertRow(byte[] request);
	
	
	public void run() {
		pushToStorage(streamFrom);	
	}

}

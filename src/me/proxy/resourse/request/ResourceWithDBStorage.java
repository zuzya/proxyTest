package me.proxy.resourse.request;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import me.proxy.resourse.common.DBStorageReader;
import me.proxy.resourse.common.IResourse;
import me.proxy.resourse.common.DBStorageWriter;
import me.proxy.resourse.common.StreamReader;
import me.proxy.resourse.common.StreamWriter;

public class ResourceWithDBStorage  implements IResourse{

	public InputStream getInputStream() throws IOException {
		
		return null;
	}

	public OutputStream getOutputStream() throws IOException {
	
		return null;
	}

	public void writeRequestToResource_From_InputStream(InputStream streamFrom, boolean isRequestStram) throws IOException {
		
		for(int i=0; i<1; i++){
			StreamWriter sw = new DBStorageWriter(streamFrom, isRequestStram);
			sw.run();
		}
		
	}

	public void readResponseFromResource_To_OutputStream(OutputStream streamTo, final boolean isRequest) throws IOException {
			
			StreamReader sr =  new DBStorageReader(streamTo, isRequest); 
			sr.run();
		
		}

}

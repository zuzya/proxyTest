package me.proxy.resourse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IResourse {

	public InputStream getInputStream() throws IOException;
	
	public OutputStream getOutputStream() throws IOException;

	public void writeRequestToResource(InputStream streamFromClient) throws IOException;

	public void readResponseFromResource(OutputStream streamToClient) throws IOException;
}

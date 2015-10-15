package me.proxy.exchange.bank;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import me.proxy.storage.impl.ResourceSocketServer;

public class SocketExchange {

	
	public Socket createRemoteSocket(String host, int remoteport, OutputStream streamToClient, Socket client) throws IOException{
		
		Socket server = null;
		
		 // Make a connection to the real server.
        // If we cannot connect to the server, send an error to the
        // client, disconnect, and continue waiting for connections.
        try {
          server = new Socket(host, remoteport);
        } catch (IOException e) {
          PrintWriter out = new PrintWriter(streamToClient);
          out.print("Proxy server cannot connect to " + host + ":"
              + remoteport + ":\n" + e + "\n");
          out.flush();
          client.close();
//          continue;
        }
        
        return server;
	}
		
	
}

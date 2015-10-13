package me.proxy.resourse;

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

public class ResourceDatabaseStorage implements IResourse {

	private InputStream inputStream;
	private OutputStream outputStream;
	
	private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/proxydb";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "";
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	public static void insertRow(String body) throws SQLException {

		Connection dbConnection = null;
		PreparedStatement  preparedStatement  = null;

		String insertTableSQL = "INSERT INTO proxydb.REQUEST"
				+ "(BODY) VALUES"
				+ "(?)";
		
		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection.prepareStatement(insertTableSQL);
			preparedStatement.setString(1, body);

			System.out.println(insertTableSQL);

			// execute insert SQL stetement
			preparedStatement.executeUpdate();

			System.out.println("Record is inserted into REQUEST table!");

		} catch (SQLException e) {

			System.out.println(e.getMessage());

		} finally {

			if (preparedStatement != null) {
				preparedStatement.close();
			}

			if (dbConnection != null) {
				dbConnection.close();
			}

		}

	}
	
	public static InputStream readRow(String sessionId)  {

		Connection dbConnection = null;
		PreparedStatement  preparedStatement  = null;
		ResultSet rs = null;
		InputStream stream = null;
		String insertTableSQL = "SELECT * FROM proxydb.REQUEST";
		
		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection.prepareStatement(insertTableSQL);
	
			// execute insert SQL stetement
			rs = preparedStatement.executeQuery();
			while(rs.next()){
				
				String body = rs.getString(3);
				
				try {
					stream = new ByteArrayInputStream(body.getBytes("UTF_8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println(body);
			}
						
			System.out.println("Record is readed into REQUEST table!");

			return stream;
		} catch (SQLException e) {

			System.out.println(e.getMessage());

		} finally {

			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (dbConnection != null) {
				try {
					dbConnection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		return stream;

	}


	private static Connection getDBConnection() {

		Connection dbConnection = null;

		try {

			Class.forName(DB_DRIVER);

		} catch (ClassNotFoundException e) {

			System.out.println(e.getMessage());

		}

		try {

			dbConnection = DriverManager.getConnection(
                               DB_CONNECTION, DB_USER,DB_PASSWORD);
			return dbConnection;

		} catch (SQLException e) {

			System.out.println(e.getMessage());

		}

		return dbConnection;

	}

	private static String getCurrentTimeStamp() {

		java.util.Date today = new java.util.Date();
		return dateFormat.format(today.getTime());

	}

	public InputStream getInputStream() throws IOException {
		
		return readRow(null);
	}

	public OutputStream getOutputStream() throws IOException {
	
		return outputStream;
	}

	public void writeRequestToResource(final InputStream streamFromClient) throws IOException {
		
	    final byte[] request = new byte[1024];
	    
		
		  // a thread to read the client's requests and pass them
       // to the server. A separate thread for asynchronous.
       Thread t = new Thread() {
         public void run() {
           int bytesRead;
           try {
             while ((bytesRead = streamFromClient.read(request)) != -1) {
           	  		            	  
            	try {
					insertRow(new String(request));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
           	  
               System.out.println(new String(request));
             }
           } catch (IOException e) {
           }

           // the client closed the connection to us, so close our
           // connection to the server.         
         }
       };
       
       t.start();
		
	}

	public void readResponseFromResource(OutputStream streamToClient) throws IOException {

	
		
	}

	


}

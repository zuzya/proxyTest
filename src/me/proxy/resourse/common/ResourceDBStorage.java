package me.proxy.resourse.common;

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

public abstract class ResourceDBStorage  implements IResourse  {

	protected InputStream inputStream;
	protected OutputStream outputStream;
	
	private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/proxydb";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "";
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	public abstract InputStream getInputStream() throws IOException;

	public abstract OutputStream getOutputStream() throws IOException;

	public abstract void writeRequestToResource(InputStream streamFromClient, boolean isRequestStram)
			throws IOException;

	public abstract void readResponseFromResource(OutputStream streamToClient, boolean isRequestStram)
			throws IOException;
			
	
	
	public static void insertRow(String body, boolean isRequestStram) throws SQLException {

		Connection dbConnection = null;
		PreparedStatement  preparedStatement  = null;

		String tableName = null;
		if(isRequestStram){
			tableName = "REQUEST";
		} else {
			tableName = "ANSWER";
		}
		
		String insertTableSQL = "INSERT INTO proxydb." + tableName
				+ " (BODY) VALUES"
				+ " (?)";
		
		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection.prepareStatement(insertTableSQL);
			preparedStatement.setString(1, body);

			System.out.println(insertTableSQL);

			// execute insert SQL stetement
			preparedStatement.executeUpdate();

			System.out.println("Record is inserted into "+tableName+" table!");

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
	
	public static InputStream readRow(String sessionId, boolean isRequestStram)   {

		Connection dbConnection = null;
		PreparedStatement  preparedStatement  = null;
		Statement statement = null;
		ResultSet rs = null;
		InputStream stream = null;
		
		String tableName = null;
		if(isRequestStram){
			tableName = "REQUEST";
		} else {
			tableName = "ANSWER";
		}
		
		String insertTableSQL = "SELECT * FROM proxydb." +tableName+ " r WHERE r.Readed = 0 ORDER BY r.DATE LIMIT 1";
		
		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection.prepareStatement(insertTableSQL);
	
			while(true){				
				// execute insert SQL stetement
				rs = preparedStatement.executeQuery();				
				
				if(rs.isBeforeFirst())
					break;
			}
			
			while(rs.next()){
				
				int id = rs.getInt(1);
				byte[] body = rs.getBytes(3);
				
				stream = new ByteArrayInputStream(body);
				
				System.out.println(body);
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				String updateTableSQL = "UPDATE proxydb." +tableName+ " SET Readed = 1  WHERE ID = " + id;
				statement = dbConnection.createStatement();
				int res = statement.executeUpdate(updateTableSQL);
				
				
				break;
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
			
			
}
package me.proxy.storage.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import me.proxy.storage.common.StorageReader;

public class DBStorageReader extends StorageReader  {

	public DBStorageReader(OutputStream streamFrom, boolean isRequest) {
		super(streamFrom, isRequest);
	}
	
	public DBStorageReader(boolean isRequest) {
		super(isRequest);
	}

	protected InputStream inputStream;
	protected ByteArrayOutputStream outputStream;
	
	private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/proxydb";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "";
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");	
			
	
	@Override
	public InputStream readRow()   {

		Connection dbConnection = null;
		PreparedStatement  preparedStatement  = null;
		Statement statement = null;
		ResultSet rs = null;
		InputStream stream = null;
		
		outputStream = new ByteArrayOutputStream();
		
		String tableName = null;
		if(isRequest){
			tableName = "REQUEST";
		} else {
			tableName = "ANSWER";
		}
		
		String insertTableSQL = "SELECT * FROM proxydb." +tableName+ " r WHERE r.Readed = 0 ORDER BY r.DATE ";
		
		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection.prepareStatement(insertTableSQL,ResultSet.TYPE_SCROLL_SENSITIVE,
	                   ResultSet.CONCUR_UPDATABLE);
	
			while(true){				
				// execute insert SQL stetement
				rs = preparedStatement.executeQuery();				
				
				if(rs.isBeforeFirst())
					break;
			}
			
			while(rs.next()){
				
				int id = rs.getInt(1);
				byte[] body = rs.getBytes(3);
//				Blob blob = rs.getBlob(3);
//				stream = blob.getBinaryStream();
//				
//				stream = new ByteArrayInputStream(body); 
				
				try {
					outputStream.write(body);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				
				System.out.println("Body:  ");
				System.out.println(new String(body));
				
				boolean b = rs.getBoolean(5);
				rs.updateBoolean("Readed", true);
				rs.updateRow();
				
			}
			

			
						
			System.out.println("Record is readed into REQUEST table!");

			return new ByteArrayInputStream(outputStream.toByteArray());
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
		return new ByteArrayInputStream(outputStream.toByteArray());

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

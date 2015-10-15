package me.proxy.storage.impl;

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

import me.proxy.storage.common.StorageWriter;

public class DBStorageWriter extends StorageWriter  {

	public DBStorageWriter(InputStream streamFrom, boolean isRequest) {
		super(streamFrom, isRequest);
	}

	protected InputStream inputStream;
	protected OutputStream outputStream;
	
	private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/proxydb";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "";
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");	
			
	
	@Override
	public void insertRow(byte[] request) {

		Connection dbConnection = null;
		PreparedStatement  preparedStatement  = null;

		String tableName = null;
		if(isRequest){
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
			preparedStatement.setBytes(1, request);

			// execute insert SQL stetement
			preparedStatement.executeUpdate();

			System.out.println("Record is inserted into "+tableName+" table!");

		} catch (SQLException e) {

			System.out.println(e.getMessage());

		} finally {

			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (dbConnection != null) {
				try {
					dbConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

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

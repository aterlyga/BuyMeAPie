package com.buymeapie;

import java.sql.*;


public class DatabaseConnection {	
	
	private DatabaseConnection() {
		// closing external access to constructor
	};

	private static Connection connection = null;

	public static Connection getConnect() throws ClassNotFoundException, SQLException, NullPointerException {

		String databaseUrl = "jdbc:mysql://localhost:3306/buymeapie";
		String user = "root";
		String password = "2020";
		
		Class.forName("com.mysql.jdbc.Driver");
		
		if (connection == null) {
			connection = DriverManager.getConnection(databaseUrl, user, password);
		}

		return connection;
	}
}
package com.BuyMeAPie;

import java.sql.*;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;


public class BuyMeAPieServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Serializes to JSON and adds error object to response
	 * @param errorCode The number of an error
	 * @param response The HttpServletResponse instance, to which the error has to be added
	 */
	protected void processError(Integer errorCode, HttpServletResponse response) throws IOException {
		
		// create error object
		Error error = new Error(errorCode);
		
		// serialize and print to response
		GsonParser gsonParser = GsonParser.getGsonParserInstance();
		String errorJsonResponse = gsonParser.createJsonForResponse(error);
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.print(errorJsonResponse);
	}
	
	/**
	* Checks whether the product with specified name already exists in the DB
	* and if not - creates appropriate record
	* 
	* @param productName
	* 			The name of the product to check / create
	* @param connection
	* 			The valid open database connection
	*/
	protected void createProductIfNotExists(String productName, Connection connection) 
		throws Exception {					
		// Product related statements
		PreparedStatement checkNameStatement = 
			connection.prepareStatement("SELECT name from item WHERE name=?;");
		PreparedStatement insertNewNameStatement = 
			connection.prepareStatement("INSERT INTO item (name) VALUES (?);");
			
		// Check if there is already product with this name in the DB
		checkNameStatement.setString(1, productName);
		ResultSet checkedNameResultSet = checkNameStatement.executeQuery();

		// If there is no product with this name yet, create it
		if (!checkedNameResultSet.first()) {
			insertNewNameStatement.setString(1, productName);
			insertNewNameStatement.executeUpdate();
		}		
	}
};

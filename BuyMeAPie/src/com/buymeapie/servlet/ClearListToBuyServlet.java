package com.BuyMeAPie;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* Deletes all the item to buy records from the database
*/
public class ClearListToBuyServlet extends BuyMeAPieServlet {
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {

		Statement truncateTableStatement = null;
		
		try {
			Connection connection = DatabaseConnection.getConnect();
			
			// Truncating table
			String sql = "TRUNCATE item_to_buy";
			truncateTableStatement = connection.createStatement();
			truncateTableStatement.executeUpdate(sql);
		
			String jsonResponse = GsonParser.getInstance().toJson(new String[0]);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.print(jsonResponse);
			out.flush();
			out.close();

		} catch (Exception e){
			e.printStackTrace();

			processError(Error.ERROR_INTERNAL_SERVER, response);
		};
		
	};
};

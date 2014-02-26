package com.buymeapie;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class ClearListToBuyServlet extends BuyMeAPie {
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Instance for serializing and deserializing json objects
		Gson gson = new Gson();

		Statement truncateTable = null;
		
		try {
			Connection connection = DatabaseConnection.getConnect();
			
			//truncating table
			String sql = "TRUNCATE item_to_buy";
			truncateTable = connection.createStatement();
			truncateTable.executeUpdate(sql);
		
			String jsonResponse = gson.toJson(new String[0]);
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

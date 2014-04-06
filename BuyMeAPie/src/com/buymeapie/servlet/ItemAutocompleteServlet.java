package com.BuyMeAPie;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* Returns a list of products in order to fill teh autocomplete list
*/
public class ItemAutocompleteServlet extends BuyMeAPieServlet {
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {

		String searchValue = request.getParameter("name");

		ArrayList<Item> itemNamesArrayResponse = new ArrayList<Item>();

		try {
			// Connecting to DB
			Connection connection = DatabaseConnection.getConnect();

			// Retrieving the matching items from DB
			PreparedStatement selectItemNamesStatement = 
				connection.prepareStatement("SELECT name FROM item WHERE name LIKE ?;");
			selectItemNamesStatement.setString(1, "%" + searchValue + "%");
			ResultSet resultSet = selectItemNamesStatement.executeQuery();

			while (resultSet.next()) {
				Item item = new Item();
				item.setName(resultSet.getString("name"));
				itemNamesArrayResponse.add(item);
			}

			String itemNamesResponse = GsonParser.getInstance().toJson(itemNamesArrayResponse);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.print(itemNamesResponse);
			out.flush();
			out.close();

		} catch (Exception e) {
			// write to log file
			e.printStackTrace();

			processError(Error.ERROR_INTERNAL_SERVER, response);
		}
	};
}

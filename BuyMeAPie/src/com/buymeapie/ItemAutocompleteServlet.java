package com.buymeapie;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class ItemAutocompleteServlet extends BuyMeAPieServlet {
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Instance for serializing and deserializing json objects
		Gson gson = new Gson();

		// Serializing json using gson
		ArrayList<Item> forNameResponse = new ArrayList<Item>();

		// Deserializing json using gson
		String itemName = gson.fromJson(request.getParameter("items_to_buy"),
				String.class);

		if (ValidateRequestParameter.validateItemToBuy(itemName) == 1) {

			PreparedStatement selectName = null;

			ResultSet selectedNames = null;

			try {
				// Connecting to DB
				Connection connection = DatabaseConnection.getConnect();

				// selecting match in autocomplete widget
				selectName = connection
						.prepareStatement("SELECT name FROM item WHERE name LIKE ?;");
				selectName.setString(1, "%" + itemName + "%");
				selectedNames = selectName.executeQuery();

				while (selectedNames.next()) {
					String name = selectedNames.getString("name");
					Item item = new Item();
					item.setName(name);
					forNameResponse.add(item);
				}

				String nameResponse = gson.toJson(forNameResponse);
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				out.print(nameResponse);
				out.flush();
				out.close();

			} catch (Exception e) {
				// write to log file
				e.printStackTrace();

				processError(Error.ERROR_INTERNAL_SERVER, response);
			}
		} else {
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
				processError(Error.ERROR_SOME_OTHER, response);
			}
		}
	};
}

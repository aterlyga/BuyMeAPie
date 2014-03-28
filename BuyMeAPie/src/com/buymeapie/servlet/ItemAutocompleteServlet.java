package com.buymeapie.servlet;

import java.io.IOException;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buymeapie.*;
import com.buymeapie.object.*;
import com.buymeapie.Error;

public class ItemAutocompleteServlet extends BuyMeAPieServlet {
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		GsonParser gsonParser = GsonParser.getGsonParserInstance();

		Item item = gsonParser.getItem();
		String itemName = item.getName();

		ArrayList<Item> itemNamesArrayResponse = new ArrayList<Item>();

		PreparedStatement selectName = null;

		ResultSet selectedNames = null;

		try {
			// Connecting to DB
			Connection connection = DatabaseConnection.getConnect();

			// selecting match in autocomplete widget
			selectName = connection.prepareStatement("SELECT name FROM item WHERE name LIKE ?;");

			selectName.setString(1, "%" + itemName + "%");
			selectedNames = selectName.executeQuery();

			while (selectedNames.next()) {
				String name = selectedNames.getString("name");
				Item newItem = new Item();
				newItem.setName(name);
				itemNamesArrayResponse.add(newItem);
			}

			String itemNamesResponse = gsonParser.createJsonForResponse(itemNamesArrayResponse);
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

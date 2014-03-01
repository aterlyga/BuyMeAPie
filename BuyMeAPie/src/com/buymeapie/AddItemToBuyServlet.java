package com.buymeapie;

import java.util.*;
import java.io.*;
import java.lang.reflect.Type;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.*;
import com.google.gson.reflect.*;

// Add new item to ITEM_TO_BUY from input fields

public class AddItemToBuyServlet extends BuyMeAPieServlet {
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Instance for serializing and deserializing json objects
		Gson gson = new Gson();

		// Serializing json using gson
		Collection<ItemToBuy> forJsonResponse = new ArrayList<ItemToBuy>();

		// Deserializing json using gson
		Type collectionType = new TypeToken<Collection<ItemToBuy>>() {}.getType();
		Collection<ItemToBuy> items = gson.fromJson(request.getParameter("items_to_buy"), collectionType);

		// validation
		if (ValidateRequestParameter.validateItemToBuy(items) == 1) {

			PreparedStatement insertNewItem = null;
			PreparedStatement selectNewItem = null;
			PreparedStatement checkName = null;
			PreparedStatement insertNewName = null;

			ResultSet checkedName = null;
			ResultSet newItemToBuyFromDb = null;

			try {

				// Connecting to DB
				Connection connection = DatabaseConnection.getConnect();

				connection.setAutoCommit(false);

				checkName = connection
						.prepareStatement("SELECT name from item WHERE name=?;");
				insertNewName = connection
						.prepareStatement("INSERT INTO item (name) VALUES (?);");

				insertNewItem = connection
						.prepareStatement("INSERT INTO item_to_buy (name, amount) VALUES (?,?);");
				selectNewItem = connection
						.prepareStatement("SELECT * from item_to_buy ORDER BY id DESC LIMIT 1;");

				// Iterating through array of instances and executing statement
				Iterator<ItemToBuy> iterator = items.iterator();
				while (iterator.hasNext()) {
					ItemToBuy itemToBuy = iterator.next();
					checkName.setString(1, itemToBuy.getName());
					checkedName = checkName.executeQuery();

					if (checkedName.first() == false) {
						insertNewName.setString(1, itemToBuy.getName());
						insertNewName.executeUpdate();
					}

					insertNewItem.setString(1, itemToBuy.getName());
					insertNewItem.setString(2, itemToBuy.getAmount());
					insertNewItem.executeUpdate();
				}

				// Executing statement and iterating through ResultSet
				newItemToBuyFromDb = selectNewItem.executeQuery();
				while (newItemToBuyFromDb.next()) {
					Integer id = newItemToBuyFromDb.getInt("id");
					String name = newItemToBuyFromDb.getString("name");
					String amount = newItemToBuyFromDb.getString("amount");
					Integer purchased = newItemToBuyFromDb.getInt("purchased");

					ItemToBuy itemToBuy = new ItemToBuy();
					itemToBuy.setId(id);
					itemToBuy.setName(name);
					itemToBuy.setAmount(amount);
					itemToBuy.setPurchased(purchased);

					forJsonResponse.add(itemToBuy);
				}

				connection.commit();

				String jsonResponse = gson.toJson(forJsonResponse);
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				out.print(jsonResponse);
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
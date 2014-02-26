package com.buymeapie;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class EditItemToBuyServlet extends BuyMeAPie {
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Instance for serializing and deserializing json objects
		Gson gson = new Gson();

		// Serializing json using gson
		Collection<ItemToBuy> forJsonResponse = new ArrayList<ItemToBuy>();

		// Deserializing json using gson
		Type collectionType = new TypeToken<Collection<ItemToBuy>>() {}.getType();
		Collection<ItemToBuy> items = gson.fromJson(request.getParameter("items_to_buy"), collectionType);

		if (ValidateRequestParameter.validateItemToBuy(items) == 1) {
			PreparedStatement insertNewName = null;
			PreparedStatement checkName = null;
			PreparedStatement updateItem = null;
			PreparedStatement selectUpdatedItem = null;

			ResultSet checkedName = null;
			ResultSet updatedItem = null;

			try {
				// Connecting to DB
				Connection connection = DatabaseConnection.getConnect();

				connection.setAutoCommit(false);

				checkName = connection
						.prepareStatement("SELECT name from item WHERE name=?;");
				insertNewName = connection
						.prepareStatement("INSERT INTO item (name) VALUES (?);");

				updateItem = connection
						.prepareStatement("UPDATE item_to_buy SET name=?, amount=? WHERE ID=?;");
				selectUpdatedItem = connection
						.prepareStatement("SELECT id, name, amount from item_to_buy WHERE ID=?;");

				// Iterating through array of gotten instances and executing
				// statement
				Iterator<ItemToBuy> iterator = items.iterator();

				while (iterator.hasNext()) {
					ItemToBuy itemToBuy = iterator.next();

					checkName.setString(1, itemToBuy.getName());
					checkedName = checkName.executeQuery();

					if (checkedName.first() == false) {
						insertNewName.setString(1, itemToBuy.getName());
						insertNewName.executeUpdate();
					}

					updateItem.setString(1, itemToBuy.getName());
					updateItem.setString(2, itemToBuy.getAmount());
					updateItem.setInt(3, itemToBuy.getId());
					updateItem.executeUpdate();

					selectUpdatedItem.setInt(1, itemToBuy.getId());
				}

				// Executing statement and iterating through ResultSet
				updatedItem = selectUpdatedItem.executeQuery();
				while (updatedItem.next()) {
					Integer id = updatedItem.getInt("id");
					String name = updatedItem.getString("name");
					String amount = updatedItem.getString("amount");

					ItemToBuy itemToBuy = new ItemToBuy();
					itemToBuy.setId(id);
					itemToBuy.setName(name);
					itemToBuy.setAmount(amount);

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

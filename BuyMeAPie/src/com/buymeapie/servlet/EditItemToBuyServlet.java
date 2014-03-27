package com.BuyMeAPie;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class EditItemToBuyServlet extends BuyMeAPieServlet {
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		GsonParser gsonParser = GsonParser.getGsonParserInstance();
		Collection<ItemToBuy> items = gsonParser.getItemToBuy();

		Collection<ItemToBuy> itemsToBuyCollectionResponse = new ArrayList<ItemToBuy>();

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

			checkName = connection.prepareStatement("SELECT name from item WHERE name=?;");
			insertNewName = connection.prepareStatement("INSERT INTO item (name) VALUES (?);");

			updateItem = connection.prepareStatement("UPDATE item_to_buy SET name=?, amount=? WHERE ID=?;");
			selectUpdatedItem = connection.prepareStatement("SELECT id, name, amount from item_to_buy WHERE ID=?;");

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

				itemsToBuyCollectionResponse.add(itemToBuy);
			}

			connection.commit();

			String jsonResponse = gsonParser.createJsonForResponse(itemsToBuyCollectionResponse);
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
		// } else {
		// try {
		// throw new Exception();
		// } catch (Exception e) {
		// e.printStackTrace();
		// processError(Error.ERROR_SOME_OTHER, response);
		// }
	};
}

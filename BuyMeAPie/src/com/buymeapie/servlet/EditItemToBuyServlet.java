package com.buymeapie.servlet;

import com.buymeapie.*;
import com.buymeapie.object.*;

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

/**
* Modifies a collection of items to buy in the DB using the data passed from client-side
*/
public class EditItemToBuyServlet extends BuyMeAPieServlet {
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {

		// Retrieve the passed instances of items to buy
		Collection<ItemToBuy> items = 
			GsonParser.getInstance().deserializeItemsToBuy(
				request.getParameter("items_to_buy")
				);

		// This variable will be used to store the successfully updated items to buy
		Collection<ItemToBuy> updatedItemsToBuy = new ArrayList<ItemToBuy>();
		
		try {
			// Connecting to DB
			Connection connection = DatabaseConnection.getConnect();
			connection.setAutoCommit(false);

			PreparedStatement updateItemStatement = connection.prepareStatement("UPDATE item_to_buy SET name=?, amount=? WHERE ID=?;");
			PreparedStatement selectUpdatedItemStatement = connection.prepareStatement("SELECT id, name, amount, purchased from item_to_buy WHERE ID=?;");

			// Iterating through array of available instances and executing
			// statement
			Iterator<ItemToBuy> iterator = items.iterator();
			while (iterator.hasNext()) {
				ItemToBuy itemToBuy = iterator.next();

				// Validate if product name already exists and create
				// a new product in the opposite case
				createProductIfNotExists(itemToBuy.getName(), connection);

				// Update an item to buy
				updateItem.setString(1, itemToBuy.getName());
				updateItem.setString(2, itemToBuy.getAmount());
				updateItem.setInt(3, itemToBuy.getId());
				updateItem.executeUpdate();

				selectUpdatedItemStatement.setInt(1, itemToBuy.getId());
			}

			// Executing statement and iterating through ResultSet
			ResultSet updatedItemsResultSet = selectUpdatedItemStatement.executeQuery();
			while (updatedItemsResultSet.next()) {
				ItemToBuy itemToBuy = new ItemToBuy();
				itemToBuy.setId(updatedItemsResultSet.getInt("id"));
				itemToBuy.setName(updatedItemsResultSet.getString("name"));
				itemToBuy.setAmount(updatedItemsResultSet.getString("amount"));
				itemToBuy.setPurchased(updatedItemsResultSet.getInt("purchased"));
				updatedItemsToBuy.add(itemToBuy);
			}

			connection.commit();

			String jsonResponse = gsonParser.createJsonForResponse(updatedItemsToBuy);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.print(jsonResponse);
			out.flush();
			out.close();

		} catch (Exception e) {
			// Rolling back the transaction
			try {
                connection.rollback();
            } catch(SQLException e1) {
                // write to log file
				e1.printStackTrace();
            }
			
			// write to log file
			e.printStackTrace();

			processError(Error.ERROR_INTERNAL_SERVER, response);
		} finally {
			// Restoring the auto-commit mode
			connection.setAutoCommit(true);
		}
	};
}

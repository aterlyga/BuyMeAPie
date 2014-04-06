package com.buymeapie.servlet;

import com.buymeapie.*;
import com.buymeapie.object.*;

import java.util.*;
import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Adds into the database a collection of items to buy. 
// The collection of items to buy is passed from the client side
// via HTTP POST.
public class AddItemToBuyServlet extends BuyMeAPieServlet {
	
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

		// Retrieve the passed instances of items to buy
		Collection<ItemToBuy> items = 
			GsonParser.getInstance().deserializeItemsToBuy(
				request.getParameter("items_to_buy")
				);

		PreparedStatement insertNewItemStatement = null;
		PreparedStatement selectInsertedItemStatement = null;

		// This variable will be used to store the successfully inserted items to buy
		Collection<ItemToBuy> insertedItemsToBuy = new ArrayList<ItemToBuy>();
		
		try {
			// Initializing DB connection
			Connection connection = DatabaseConnection.getConnect();
			connection.setAutoCommit(false);

			// Item to buy related statements
			insertNewItemStatement = 
				connection.prepareStatement("INSERT INTO item_to_buy (name, amount) VALUES (?,?);");
			selectInsertedItemStatement = 
				connection.prepareStatement("SELECT * from item_to_buy ORDER BY id DESC LIMIT 1;");

			// Iterating through collection of instances and executing statements
			Iterator<ItemToBuy> iterator = items.iterator();
			while (iterator.hasNext()) {
				ItemToBuy itemToBuy = iterator.next();
				
				// Validate if product name already exists and create
				// a new product in the opposite case
				createProductIfNotExists(itemToBuy.getName(), connection);
				
				// Insert new item to buy
				insertNewItemStatement.setString(1, itemToBuy.getName());
				insertNewItemStatement.setString(2, itemToBuy.getAmount());
				insertNewItemStatement.executeUpdate();
			}
			
			// Retrieve the inserted items to buy from the database together with their Ids
			ResultSet insertedItemsResultSet = selectInsertedItemStatement.executeQuery();
			while (insertedItemsResultSet.next()) {
				ItemToBuy itemToBuy = new ItemToBuy();
				itemToBuy.setId(insertedItemsResultSet.getInt("id"));
				itemToBuy.setName(insertedItemsResultSet.getString("name"));
				itemToBuy.setAmount(insertedItemsResultSet.getString("amount"));
				itemToBuy.setPurchased(insertedItemsResultSet.getInt("purchased"));
				insertedItemsToBuy.add(itemToBuy);
			}

			connection.commit();

			String jsonResponse = GsonParser.getInstance().toJson(insertedItemsToBuy);
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